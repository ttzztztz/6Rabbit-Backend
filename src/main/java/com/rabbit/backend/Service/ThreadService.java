package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Forum.Forum;
import com.rabbit.backend.Bean.Thread.*;
import com.rabbit.backend.DAO.ForumDAO;
import com.rabbit.backend.DAO.PostDAO;
import com.rabbit.backend.DAO.StaticDAO;
import com.rabbit.backend.DAO.ThreadDAO;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ThreadService {
    private PostDAO postDAO;
    private ThreadDAO threadDAO;
    private StaticDAO staticDAO;
    private ForumDAO forumDAO;
    private StringRedisTemplate stringRedisTemplate;

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    @Autowired
    public ThreadService(PostDAO postDAO, ThreadDAO threadDAO, StaticDAO staticDAO, ForumDAO forumDAO,
                         StringRedisTemplate stringRedisTemplate) {
        this.postDAO = postDAO;
        this.threadDAO = threadDAO;
        this.staticDAO = staticDAO;
        this.forumDAO = forumDAO;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Transactional
    public void modify(List<String> tidList, String key, String value) {
        for (String tid : tidList) {
            threadDAO.modify(tid, key, value);
        }
    }

    @Transactional
    public void delete(String tid) {
        String fid = threadDAO.fid(tid);
        staticDAO.decrement("forum", "threads", "fid", fid, 1);
        threadDAO.delete(tid);
        threadDAO.deletePostCASCADE(tid);
    }

    public ThreadItem info(String tid) {
        ThreadItem threadItem = threadDAO.find(tid);
        if (threadItem == null) {
            throw new NotFoundException(-1, "Thread not found.");
        }
        return threadItem;
    }

    @Transactional
    public void reply(String tid, PostEditorForm form, String uid) {
        form.setTid(tid);
        form.setUid(uid);
        postDAO.insertWithPostEditorForm(form);

        String pid = form.getPid();
        threadDAO.updateLastReply(tid, pid, uid, new Date());
        staticDAO.increment("thread", "posts", "tid", tid, 1);
    }

    @Transactional
    public void update(String tid, String newFid, String newSubject, String newContent) {
        String firstPid = postDAO.firstPid(tid);
        threadDAO.update(tid, newSubject, newFid);
        postDAO.update(firstPid, newContent);
    }

    @Transactional
    public String insert(String uid, ThreadEditorForm form) {
        form.setUid(uid);
        threadDAO.insert(form);
        String tid = form.getTid();
        postDAO.insertWithThreadEditorForm(form);
        threadDAO.updateFirstPid(tid, form.getFirstpid(), uid);
        staticDAO.increment("forum", "threads", "fid", form.getFid(), 1);
        return tid;
    }

    public Forum forum(String fid) {
        return forumDAO.find(fid);
    }

    public List<ThreadListItem> list(String fid, Integer page) {
        List<ThreadListItem> list = new ArrayList<>();
        if (page == 1) {
            list.addAll(threadDAO.globalTopThread());
            list.addAll(threadDAO.forumTopThreadByFid(fid));
        }

        list.addAll(threadDAO.listWithoutTop(fid, (page - 1) * PAGESIZE, page * PAGESIZE));
        return list;
    }

    public List<ThreadListImageItem> imageList(String fid, Integer page) {
        final Pattern pattern = Pattern.compile("<img(?= )[^>]* src=(['\"])(.*?)\\1[^>]*>");
        List<ThreadListImageItem> list = threadDAO.listImageItem(fid, (page - 1) * PAGESIZE, page * PAGESIZE);

        for (ThreadListImageItem item : list) {
            String tid = item.getTid();
            String key = "thread:image:" + tid;
            String cachedImage = stringRedisTemplate.boundValueOps(key).get();
            if (cachedImage == null) {
                ThreadMessageItem threadMessageItem = threadDAO.getMessage(tid);
                Matcher matcher = pattern.matcher(threadMessageItem.getMessage());
                String result = "";
                if (matcher.find()) {
                    result = matcher.group(2);
                }

                item.setImage(result);
                stringRedisTemplate.boundValueOps(key).set(result, 60 * 60 * 1000, TimeUnit.MILLISECONDS);
                // cache 1h
            } else {
                item.setImage(cachedImage);
            }
        }

        return list;
    }

    public List<ThreadListItem> listByUser(String uid, Integer page) {
        return threadDAO.listByUser(uid, (page - 1) * PAGESIZE, page * PAGESIZE);
    }

    public String uid(String tid) {
        return threadDAO.authorUid(tid);
    }

    public Integer userThreads(String uid) {
        return threadDAO.userThreads(uid);
    }

    public List<SearchItem> search(String keywords, Integer page) {
        return threadDAO.search(keywords, (page - 1) * PAGESIZE, page * PAGESIZE);
    }

    public Integer searchCount(String keywords) {
        return threadDAO.searchCount(keywords);
    }

    public ThreadListItem findWithThreadListItem(String tid) {
        return threadDAO.findWithThreadListItem(tid);
    }
}

package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Forum.Forum;
import com.rabbit.backend.Bean.Thread.PostEditorForm;
import com.rabbit.backend.Bean.Thread.ThreadEditorForm;
import com.rabbit.backend.Bean.Thread.ThreadItem;
import com.rabbit.backend.Bean.Thread.ThreadListItem;
import com.rabbit.backend.DAO.ForumDAO;
import com.rabbit.backend.DAO.PostDAO;
import com.rabbit.backend.DAO.StaticDAO;
import com.rabbit.backend.DAO.ThreadDAO;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Vector;

@Service
public class ThreadService {
    private PostDAO postDAO;
    private ThreadDAO threadDAO;
    private StaticDAO staticDAO;
    private ForumDAO forumDAO;

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    @Autowired
    public ThreadService(PostDAO postDAO, ThreadDAO threadDAO, StaticDAO staticDAO, ForumDAO forumDAO) {
        this.postDAO = postDAO;
        this.threadDAO = threadDAO;
        this.staticDAO = staticDAO;
        this.forumDAO = forumDAO;
    }

    public void modify(String tid, String key, String value) {
        threadDAO.modify(tid, key, value);
    }

    @Transactional
    public void delete(String tid) {
        String fid = threadDAO.fid(tid);
        staticDAO.decrement("forum", "threads", "fid", fid, 1);
        threadDAO.delete(tid);
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
        postDAO.insert(form);

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
        postDAO.insert(form);
        threadDAO.updateFirstPid(tid, form.getFirstpid());
        staticDAO.increment("forum", "threads", "fid", form.getFid(), 1);
        return tid;
    }

    public Forum forum(String fid) {
        return forumDAO.find(fid);
    }

    public List<ThreadListItem> list(String fid, Integer page) {
        List<ThreadListItem> list = new Vector<>();
        if (page == 1) {
            list.addAll(threadDAO.globalTopThread());
            list.addAll(threadDAO.forumTopThreadByFid(fid));
        }

        list.addAll(threadDAO.listWithoutTop(fid, (page - 1) * PAGESIZE, page * PAGESIZE));
        return list;
    }
}

package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Thread.Post;
import com.rabbit.backend.DAO.PostDAO;
import com.rabbit.backend.DAO.StaticDAO;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    private PostDAO postDAO;
    private StaticDAO staticDAO;

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    @Autowired
    public PostService(PostDAO postDAO, StaticDAO staticDAO) {
        this.postDAO = postDAO;
        this.staticDAO = staticDAO;
    }

    @Transactional
    public void delete(String pid) {
        postDAO.delete(pid);
        String tid = postDAO.tid(pid);
        staticDAO.decrement("thread", "posts", "tid", tid, 1);
    }

    public void update(String pid, String content) {
        postDAO.update(pid, content);
    }

    public List<Post> list(String tid, Integer page) {
        return postDAO.list(tid, (page - 1) * PAGESIZE, page * PAGESIZE);
    }

    public Post firstPost(String tid) {
        return postDAO.firstPost(tid);
    }

    public Post find(String pid) {
        Post post = postDAO.find(pid);
        if (post == null) {
            throw new NotFoundException(-1, "Post not found.");
        }
        return post;
    }

    public String uid(String pid) {
        return postDAO.authorUid(pid);
    }
}

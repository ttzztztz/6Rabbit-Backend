package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Thread.Post;
import com.rabbit.backend.DAO.PostDAO;
import com.rabbit.backend.DAO.ThreadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private PostDAO postDAO;
    private ThreadDAO threadDAO;

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    @Autowired
    public PostService(PostDAO postDAO, ThreadDAO threadDAO) {
        this.postDAO = postDAO;
        this.threadDAO = threadDAO;
    }

    public void delete(String pid) {
        postDAO.delete(pid);
    }

    public void reply() {

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
        return postDAO.find(pid);
    }
}

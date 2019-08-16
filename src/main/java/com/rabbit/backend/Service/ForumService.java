package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Forum.Forum;
import com.rabbit.backend.DAO.ForumDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumService {
    private ForumDAO forumDAO;

    @Autowired
    public ForumService(ForumDAO forumDAO) {
        this.forumDAO = forumDAO;
    }

    public List<Forum> list() {
        return forumDAO.list();
    }

    public Forum find(String fid) {
        return forumDAO.find(fid);
    }
}

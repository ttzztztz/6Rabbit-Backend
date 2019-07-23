package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Thread.PostEditorForm;
import com.rabbit.backend.Bean.Thread.ThreadItem;
import com.rabbit.backend.DAO.PostDAO;
import com.rabbit.backend.DAO.ThreadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThreadService {
    private PostDAO postDAO;
    private ThreadDAO threadDAO;

    @Autowired
    public ThreadService(PostDAO postDAO, ThreadDAO threadDAO) {
        this.postDAO = postDAO;
        this.threadDAO = threadDAO;
    }

    public void modify(String tid, String key, String value) {
        threadDAO.modify(tid, key, value);
    }

    public void delete(String tid) {
        threadDAO.delete(tid);
    }

    public ThreadItem info(String tid) {
        return threadDAO.find(tid);
    }

    @Transactional
    public String reply(String tid, PostEditorForm form, String uid) {
        postDAO.insert(tid, form, uid);
        String pid = form.getPid();

        // todo: update lastpid & lastuser

        return pid;
    }
}

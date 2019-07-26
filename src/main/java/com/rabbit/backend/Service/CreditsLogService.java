package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Credits.CreditsLog;
import com.rabbit.backend.DAO.CreditsLogDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditsLogService {
    private CreditsLogDAO DAO;

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    @Autowired
    public CreditsLogService(CreditsLogDAO creditsLogDAO) {
        this.DAO = creditsLogDAO;
    }

    public Integer count(String uid) {
        return DAO.count(uid);
    }

    public List<CreditsLog> list(String uid, Integer page) {
        return DAO.list(uid, (page - 1) * PAGESIZE, page * PAGESIZE);
    }
}

package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Credits.CreditsLog;
import com.rabbit.backend.Bean.Credits.CreditsLogForm;
import com.rabbit.backend.DAO.CreditsLogDAO;
import com.rabbit.backend.DAO.DepositDAO;
import com.rabbit.backend.DAO.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DepositService {
    private CreditsLogDAO creditsLogDAO;
    private DepositDAO depositDAO;
    private UserDAO userDAO;
    private StringRedisTemplate stringRedisTemplate;

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    @Autowired
    public DepositService(CreditsLogDAO creditsLogDAO, DepositDAO depositDAO, UserDAO userDAO,
                          StringRedisTemplate stringRedisTemplate) {
        this.creditsLogDAO = creditsLogDAO;
        this.depositDAO = depositDAO;
        this.userDAO = userDAO;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public List<CreditsLog> creditsLogList(Integer page) {
        return depositDAO.unverifiedDeposit((page - 1) * PAGESIZE, page * PAGESIZE);
    }

    @Transactional
    public void setDeposit(String cid, Integer status) {
        CreditsLog creditsLog = creditsLogDAO.find(cid);
        depositDAO.setDeposit(cid, status);
        if (creditsLog.getStatus() != 1 && status == 1) {
            userDAO.increaseCredits(creditsLog.getUser().getUid(), creditsLog.getCreditsType().toString(),
                    creditsLog.getCredits());
        }
    }

    public Boolean submitIsFrequent(String uid) {
        String key = "pay:deposit:" + uid;
        String previousPay = stringRedisTemplate.boundValueOps(key).get();
        return previousPay != null && previousPay.equals("1");
    }

    public void submitFrequentSet(String uid) {
        String key = "pay:deposit:" + uid;
        stringRedisTemplate.boundValueOps(key).set("1",
                10 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    public String submitDeposit(Integer rmbs, String uid) {
        CreditsLogForm creditsLogForm = new CreditsLogForm();

        creditsLogForm.setCreditsType(3);
        creditsLogForm.setCredits(rmbs);
        creditsLogForm.setUid(uid);
        creditsLogForm.setStatus("0");

        creditsLogDAO.insert(creditsLogForm);
        return creditsLogForm.getCid();
    }
}

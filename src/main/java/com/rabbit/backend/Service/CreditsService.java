package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Credits.CreditsRule;
import com.rabbit.backend.DAO.UserDAO;
import com.rabbit.backend.Utilities.TimestampUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CreditsService {
    private StringRedisTemplate stringRedisTemplate;
    private ApplicationContext applicationContext;
    private UserDAO userDAO;

    @Autowired
    public CreditsService(StringRedisTemplate stringRedisTemplate, UserDAO userDAO, ApplicationContext applicationContext) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.userDAO = userDAO;
        this.applicationContext = applicationContext;
    }
    public void applyRule(String uid, String ruleName) {
        CreditsRule rule = applicationContext.getBean("CreditsAction_" + ruleName, CreditsRule.class);
        if (rule == null) {
            return;
        }

        String key = "rule:" + uid + ":" + ruleName;
        if (rule.getDailyLimit() != -1) {
            String currentHit = stringRedisTemplate.boundValueOps(key).get();
            if (currentHit != null && Integer.parseInt(currentHit) >= rule.getDailyLimit()) {
                return;
            }
        }
        stringRedisTemplate.boundValueOps(key).increment();
        stringRedisTemplate.boundValueOps(key).expire(TimestampUtil.getDayEndTimestamp() - System.currentTimeMillis()
                , TimeUnit.MILLISECONDS);
        userDAO.applyRule(uid, rule);
    }
}

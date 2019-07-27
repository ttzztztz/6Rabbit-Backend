package com.rabbit.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class FrequentService {
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public FrequentService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public Boolean check(String key, Integer seconds) {
        String previousPay = stringRedisTemplate.boundValueOps(key).get();
        if (previousPay != null && previousPay.equals("1")) {
            return false;
        }

        stringRedisTemplate.boundValueOps(key).set("1",
                seconds * 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}

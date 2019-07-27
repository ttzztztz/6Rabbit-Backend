package com.rabbit.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerifyService {
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public VerifyService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private int generateRandom() {
        Random rand = new Random(System.currentTimeMillis());
        int result = rand.nextInt(899999);
        return result + 100000;
    }

    public void setRandom(String key) {
        int random = generateRandom();
        stringRedisTemplate.boundValueOps(key).set(String.valueOf(random));
        stringRedisTemplate.boundValueOps(key).expire(5 * 60 * 1000,
                TimeUnit.MILLISECONDS);
    }

    public boolean verifyRandom(String key, String code) {
        String realCode = stringRedisTemplate.boundValueOps(key).get();
        return realCode != null && realCode.equals(code);
    }
}

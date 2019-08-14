package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Thread.ThreadListItem;
import com.rabbit.backend.Bean.User.*;
import com.rabbit.backend.DAO.UserDAO;
import com.rabbit.backend.Security.JWTUtils;
import com.rabbit.backend.Security.PasswordUtils;
import com.rabbit.backend.Utilities.TimestampUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private UserDAO DAO;
    private StringRedisTemplate stringRedisTemplate;

    @Value("${rabbit.limit.login}")
    private int loginLimitPerIP;

    @Value("${rabbit.limit.register}")
    private int registerLimitPerIP;

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    @Autowired
    public UserService(UserDAO userDAO, StringRedisTemplate stringRedisTemplate) {
        this.DAO = userDAO;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public User selectUser(String key, String value) {
        return DAO.find(key, value);
    }

    public MyUser selectMyUser(String key, String value) {
        return DAO.findMy(key, value);
    }

    public OtherUser selectOtherUserByUid(String uid) {
        return DAO.findOther("uid", uid);
    }

    public Boolean exist(String key, String value) {
        Integer val = DAO.exist(key, value);
        return val != null;
    }

    public String register(String username, String password, String email) {
        String salt = PasswordUtils.generateSalt();
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(PasswordUtils.generatePassword(password, salt));
        newUser.setSalt(salt);
        newUser.setEmail(email);
        DAO.insert(newUser);

        return newUser.getUid();
    }

    public void updatePassword(String uid, String password) {
        String salt = PasswordUtils.generateSalt();
        DAO.updatePassword(uid, PasswordUtils.generatePassword(password, salt), salt);
    }

    @Transactional
    public void updateProfile(String uid, UpdateProfileForm form) {
        DAO.updateFields(uid, form);
    }

    private boolean limitCheck(String rule, String IP, int limit) {
        String key = "limit:" + rule + ":" + IP;
        String currentHit = stringRedisTemplate.boundValueOps(key).get();
        return currentHit == null || Integer.parseInt(currentHit) < limit;
    }

    private void limitIncrement(String rule, String IP) {
        String key = "limit:" + rule + ":" + IP;
        stringRedisTemplate.boundValueOps(key).increment();
        stringRedisTemplate.boundValueOps(key).expire(TimestampUtil.getDayEndTimestamp() - System.currentTimeMillis()
                , TimeUnit.MILLISECONDS);
    }

    public void loginLimitIncrement(String IP) {
        limitIncrement("login", IP);
    }

    public void registerLimitIncrement(String IP) {
        limitIncrement("register", IP);
    }

    public boolean loginLimitCheck(String IP) {
        return limitCheck("login", IP, loginLimitPerIP);
    }

    public boolean registerLimitCheck(String IP) {
        return limitCheck("register", IP, registerLimitPerIP);
    }

    public UserLoginResponse loginResponse(User user) {
        String token = JWTUtils.sign(user.getUid(), user.getUsername(), user.getUsergroup().getIsAdmin());

        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setUid(user.getUid());
        response.setIsAdmin(user.getUsergroup().getIsAdmin());

        return response;
    }

    public List<ThreadListItem> purchasedList(String uid, Integer page) {
        return DAO.purchasedList(uid, (page - 1) * PAGESIZE, page * PAGESIZE);
    }

    public Integer purchasedListCount(String uid) {
        return DAO.purchasedListCount(uid);
    }
}
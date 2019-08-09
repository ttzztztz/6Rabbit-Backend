package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.User.MyUser;
import com.rabbit.backend.Bean.User.OtherUser;
import com.rabbit.backend.Bean.User.UpdateProfileForm;
import com.rabbit.backend.Bean.User.User;
import com.rabbit.backend.DAO.UserDAO;
import com.rabbit.backend.Security.JWTUtils;
import com.rabbit.backend.Security.PasswordUtils;
import com.rabbit.backend.Utilities.TimestampUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private UserDAO DAO;
    private StringRedisTemplate stringRedisTemplate;

    @Value("${rabbit.limit.login}")
    private int loginLimitPerIP;

    @Value("${rabbit.limit.register}")
    private int registerLimitPerIP;

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

    public Map<String, Object> loginResponse(User user) {
        String token = JWTUtils.sign(user.getUid(), user.getUsername(), user.getUsergroup().getIsAdmin());
        Map<String, Object> response = new HashMap<>();

        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("uid", user.getUid());
        response.put("isAdmin", user.getUsergroup().getIsAdmin());
        return response;
    }
}
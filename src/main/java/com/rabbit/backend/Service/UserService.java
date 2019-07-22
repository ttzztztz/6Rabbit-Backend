package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.User.OtherUser;
import com.rabbit.backend.Bean.User.User;
import com.rabbit.backend.DAO.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Random;

@Service
public class UserService {
    private UserDAO DAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.DAO = userDAO;
    }

    public User selectUser(String key, String value) {
        return DAO.find("key", value);
    }

    public OtherUser selectOtherUserByUid(String uid) {
        return DAO.findOther("uid", uid);
    }

    public Boolean usernameExist(String username) {
        Integer val = DAO.usernameExist(username);
        return val != null;
    }

    public void register(String username, String password, String email) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        Random random = new Random();
        StringBuilder saltBuilder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(str.length());
            saltBuilder.append(str.charAt(number));
        }

        String salt = saltBuilder.toString();
        DAO.insert(username, email, DigestUtils.md5DigestAsHex((password + salt).getBytes()), salt);
    }

    public Boolean login(String username, String password) {
        User user = DAO.find("username", username);
        if (user == null) {
            return false;
        }

        return DigestUtils.md5DigestAsHex((password + user.getSalt()).getBytes()).equals(
                DigestUtils.md5DigestAsHex((user.getPassword() + user.getSalt()).getBytes())
        );
    }
}

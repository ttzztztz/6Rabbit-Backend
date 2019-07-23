package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.User.MyUser;
import com.rabbit.backend.Bean.User.OtherUser;
import com.rabbit.backend.Bean.User.UpdateProfileForm;
import com.rabbit.backend.Bean.User.User;
import com.rabbit.backend.DAO.UserDAO;
import com.rabbit.backend.Security.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.rabbit.backend.Security.PasswordUtils.generatePassword;

@Service
public class UserService {
    private UserDAO DAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.DAO = userDAO;
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

    public void register(String username, String password, String email) {
        String salt = PasswordUtils.generateSalt();
        DAO.insert(username, email, generatePassword(password, salt), salt);
    }

    public void updatePassword(String uid, String password) {
        String salt = PasswordUtils.generateSalt();
        DAO.updatePassword(uid, generatePassword(password, salt), salt);
    }

    public void updateProfile(String uid, String[] fields, String[] values) {
        DAO.updateFields(fields, values, uid);
    }
}
package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.User;
import com.rabbit.backend.DAO.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public User selectUserByUid(String uid) {
        return userDAO.findUserByUid(uid);
    }
}

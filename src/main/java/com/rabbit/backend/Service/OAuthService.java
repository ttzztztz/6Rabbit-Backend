package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.OAuth.OAuth;
import com.rabbit.backend.DAO.OAuthDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OAuthService {
    private OAuthDAO oAuthDAO;

    @Autowired
    public OAuthService(OAuthDAO oAuthDAO) {
        this.oAuthDAO = oAuthDAO;
    }

    public void delete(String oid) {
        oAuthDAO.delete(oid);
    }

    public List<OAuth> list(String uid) {
        return oAuthDAO.listByUid(uid);
    }

    public void insert(OAuth oAuth) {
        oAuthDAO.insert(oAuth);
    }

    public OAuth find(String oid) {
        return oAuthDAO.findByOid(oid);
    }
}

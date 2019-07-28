package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.OAuth.OAuth;
import com.rabbit.backend.Bean.OAuth.OAuthUserInfo;
import com.rabbit.backend.Bean.OAuth.OAuthWebsite;
import com.rabbit.backend.DAO.OAuthDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class OAuthService {
    private OAuthDAO oAuthDAO;
    private ApplicationContext applicationContext;

    @Autowired
    public OAuthService(OAuthDAO oAuthDAO, ApplicationContext applicationContext) {
        this.oAuthDAO = oAuthDAO;
        this.applicationContext = applicationContext;
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

    public OAuth findByOid(String oid) {
        return oAuthDAO.findByOid(oid);
    }

    public OAuth find(String uid, String platform) {
        return oAuthDAO.find(uid, platform);
    }

    public OAuth findByOpenid(String openid, String platform) {
        return oAuthDAO.findByOpenid(openid, platform);
    }

    public String getURL(String platform, String redirect) {
        OAuthWebsite oAuthWebsite = applicationContext.getBean("OAuth_" + platform, OAuthWebsite.class);
        return oAuthWebsite.buildLoginURL(redirect);
    }

    public OAuthUserInfo callback(String platform, HttpServletRequest request) {
        OAuthWebsite oAuthWebsite = applicationContext.getBean("OAuth_" + platform, OAuthWebsite.class);
        String code = oAuthWebsite.getCode(request);
        String access_token = oAuthWebsite.getAccessToken(code);
        return oAuthWebsite.getUserInfo(access_token);
    }
}

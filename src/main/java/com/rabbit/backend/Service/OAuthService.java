package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.OAuth.OAuth;
import com.rabbit.backend.Bean.OAuth.OAuthUserInfo;
import com.rabbit.backend.Bean.OAuth.OAuthWebsite;
import com.rabbit.backend.DAO.OAuthDAO;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OAuthService {
    private OAuthDAO oAuthDAO;
    private ApplicationContext applicationContext;
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public OAuthService(OAuthDAO oAuthDAO, ApplicationContext applicationContext, StringRedisTemplate stringRedisTemplate) {
        this.oAuthDAO = oAuthDAO;
        this.applicationContext = applicationContext;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private String redisKeyGenerator(String platform, String code) {
        return "oauth:" + platform + ":" + code;
    }

    public void deleteTokenFromCache(String platform, String code) {
        stringRedisTemplate.delete(redisKeyGenerator(platform, code));
    }

    private void setAccessTokenToCache(String platform, String code, String accessToken) {
        String token = redisKeyGenerator(platform, code);
        stringRedisTemplate.boundValueOps(token).set(accessToken);
        stringRedisTemplate.boundValueOps(token).expire(10 * 60 * 1000L, TimeUnit.MILLISECONDS);
    }

    private String getAccessTokenFromCache(String platform, String code) {
        return stringRedisTemplate.boundValueOps(redisKeyGenerator(platform, code)).get();
    }

    public void delete(String oid) {
        oAuthDAO.delete(oid);
    }

    public void deleteByUser(String platform, String uid) {
        oAuthDAO.deleteByUser(platform, uid);
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

    public String getURL(String platform) {
        OAuthWebsite oAuthWebsite = applicationContext.getBean("OAuth_" + platform, OAuthWebsite.class);
        return oAuthWebsite.buildLoginURL();
    }

    public OAuthUserInfo callback(String platform, String code) {
        OAuthWebsite oAuthWebsite = applicationContext.getBean("OAuth_" + platform, OAuthWebsite.class);

        String access_token = getAccessTokenFromCache(platform, code);
        if (access_token == null || access_token.equals("")) {
            access_token = oAuthWebsite.getAccessToken(code);
            setAccessTokenToCache(platform, code, access_token);
        }
        OAuthUserInfo info = oAuthWebsite.getUserInfo(access_token);
        if (info == null || info.getOpenid() == null) {
            throw new NotFoundException(404, "Invalid code & platform.");
        }
        return info;
    }

    public Boolean userBindOtherPlatformExist(String platform, String uid) {
        // check if user bind other platforms
        String oid = oAuthDAO.findOidByUidAndPlatform(uid, platform);
        return oid != null && !oid.equals("");
    }

    public Boolean openidBindOtherUserExist(String openid, String platform) {
        // check if openid binded by another user
        OAuth currentOauth = findByOpenid(openid, platform);
        return currentOauth != null;
    }

    public void bind(String platform, String code, String uid, String openid) {
        OAuth oAuth = new OAuth();
        oAuth.setOpenid(openid);
        oAuth.setPlatform(platform);
        oAuth.setUid(uid);
        insert(oAuth);
        deleteTokenFromCache(platform, code);
    }

    public String findUid(String platform, String openid) {
        return oAuthDAO.findUid(platform, openid);
    }
}

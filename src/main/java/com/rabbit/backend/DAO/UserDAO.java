package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Credits.CreditsRule;
import com.rabbit.backend.Bean.Thread.ThreadListItem;
import com.rabbit.backend.Bean.User.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserDAO {
    @Select("SELECT * FROM user WHERE ${key} = #{value} LIMIT 1")
    @Results({
            @Result(property = "usergroup", column = "gid", one = @One(select = "com.rabbit.backend.DAO.GroupDAO.findByGid"))
    })
    User find(@Param("key") String key, @Param("value") String value);

    @Select("SELECT username, uid, gid FROM user WHERE ${key} = #{value} LIMIT 1")
    @Results({
            @Result(property = "usergroup", column = "gid", one = @One(select = "com.rabbit.backend.DAO.GroupDAO.findByGid"))
    })
    OtherUser findOther(@Param("key") String key, @Param("value") String value);

    @Select("SELECT username, uid, gid FROM user WHERE uid = #{uid} LIMIT 1")
    @Results({
            @Result(property = "usergroup", column = "gid", one = @One(select = "com.rabbit.backend.DAO.GroupDAO.findByGid"))
    })
    OtherUser findOtherByUid(@Param("uid") String uid);

    @Select("SELECT * FROM user WHERE ${key} = #{value} LIMIT 1")
    @Results({
            @Result(property = "usergroup", column = "gid", one = @One(select = "com.rabbit.backend.DAO.GroupDAO.findByGid"))
    })
    MyUser findMy(@Param("key") String key, @Param("value") String value);

    @Insert("INSERT INTO user (username, email, password, salt) VALUES (#{username}, #{email}, #{password}, #{salt})")
    @Options(keyColumn = "uid", useGeneratedKeys = true, keyProperty = "uid")
    Integer insert(User user);

    @Select("SELECT 1 FROM user WHERE ${key} = #{value}")
    Integer exist(@Param("key") String key, @Param("value") String value);

    @Update("UPDATE user SET password = #{password}, salt = #{salt} WHERE uid = #{uid}")
    void updatePassword(@Param("uid") String uid, @Param("password") String password, @Param("salt") String salt);

    @Update("UPDATE user SET " +
            "realname = #{form.realname}, gender = #{form.gender}, email = #{form.email}, qq = #{form.qq}," +
            " mobile = #{form.mobile}, wechat = #{form.wechat}, signature = #{form.signature}" +
            " WHERE uid = #{uid}")
    void updateFields(@Param("uid") String uid, @Param("form") UpdateProfileForm form);

    @Update("UPDATE user SET " +
            "credits = credits + ${rule.credits}, " +
            "golds = golds + ${rule.golds}, " +
            "rmbs = rmbs + ${rule.rmbs}" +
            " WHERE uid = #{uid}")
    void applyRule(@Param("uid") String uid, @Param("rule") CreditsRule rule);

    @Update("UPDATE user SET ${creditsType} = ${creditsType} - #{credits} WHERE uid = #{uid}")
    void decreaseCredits(@Param("uid") String uid, @Param("creditsType") String creditsType, @Param("credits") Integer credits);

    @Update("UPDATE user SET ${creditsType} = ${creditsType} + #{credits} WHERE uid = #{uid}")
    void increaseCredits(@Param("uid") String uid, @Param("creditsType") String creditsType, @Param("credits") Integer credits);

    @Select("SELECT uid, credits, golds, rmbs FROM user WHERE uid = #{uid}")
    UserCredits readCredits(@Param("uid") String uid);

    @Select("SELECT DISTINCT `thread`.*, `temp`.`createDate` AS `purchasedDate` FROM " +
            "(SELECT `attach`.`tid`, `attach_pay_log`.`createDate` FROM `attach_pay_log` " +
            "INNER JOIN attach ON `attach_pay_log`.`aid` = `attach`.`aid` " +
            "WHERE `attach_pay_log`.`uid` = #{uid}) temp " +
            "INNER JOIN thread ON `temp`.`tid` = `thread`.`tid` " +
            "ORDER BY `temp`.`createDate` DESC " +
            "LIMIT ${from},${to}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> purchasedList(@Param("uid") String uid, @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT COUNT(DISTINCT `temp`.`tid`) FROM " +
            "(SELECT `attach`.`tid`, `attach_pay_log`.`createDate` FROM `attach_pay_log` " +
            "INNER JOIN attach ON `attach_pay_log`.`aid` = `attach`.`aid` " +
            "WHERE `attach_pay_log`.`uid` = #{uid}) temp")
    Integer purchasedListCount(@Param("uid") String uid);
}
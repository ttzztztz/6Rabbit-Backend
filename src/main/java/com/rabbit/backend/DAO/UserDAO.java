package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.User.MyUser;
import com.rabbit.backend.Bean.User.OtherUser;
import com.rabbit.backend.Bean.User.UpdateProfileForm;
import com.rabbit.backend.Bean.User.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

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
    void insert(@Param("username") String username, @Param("email") String email, @Param("password") String password, @Param("salt") String salt);

    @Select("SELECT 1 FROM user WHERE ${key} = #{value}")
    Integer exist(@Param("key") String key, @Param("value") String value);

    @Update("UPDATE user SET password = #{password}, salt = #{salt} WHERE uid = #{uid}")
    void updatePassword(@Param("uid") String uid, @Param("password") String password, @Param("salt") String salt);

    @Update("UPDATE user SET " +
            "realname = #{form.realname}, gender = #{form.gender}, email = #{form.email}, qq = #{form.qq}," +
            " mobile = #{form.mobile}, wechat = #{form.wechat}, signature = #{form.signature}" +
            " WHERE uid = #{uid}")
    void updateFields(@Param("uid") String uid, @Param("form") UpdateProfileForm form);
}
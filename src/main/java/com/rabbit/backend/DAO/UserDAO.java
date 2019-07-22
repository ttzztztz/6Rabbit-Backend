package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.User.OtherUser;
import com.rabbit.backend.Bean.User.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UserDAO {
    @Select("SELECT * FROM user WHERE #{key} = #{value};")
    @Results({
            @Result(property = "usergroup", column = "gid", one = @One(select = "com.rabbit.backend.DAO.GroupDAO.findByGid"))
    })
    User find(@Param("key") String key, @Param("value") String value);

    @Select("SELECT username, uid, gid FROM user WHERE #{key} = #{value};")
    @Results({
            @Result(property = "usergroup", column = "gid", one = @One(select = "com.rabbit.backend.DAO.GroupDAO.findByGid"))
    })
    OtherUser findOther(@Param("key") String key, @Param("value") String value);

    @Insert("INSERT INTO user (username, email, password, salt) VALUES (#{username}, #{email}, #{password}, #{salt});")
    void insert(String username, String email, String password, String salt);

    @Select("SELECT 1 FROM user WHERE username = #{username}")
    Integer usernameExist(String username);
}

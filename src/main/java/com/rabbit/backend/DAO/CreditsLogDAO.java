package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.User.CreditsLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CreditsLogDAO {
    @Select("SELECT * FROM credits_type WHERE uid = #{uid} LIMIT ${from},${to} ORDER BY cid DESC")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<CreditsLog> list(@Param("uid") String uid, @Param("from") Integer from, @Param("to") Integer to);

    @Delete("DELETE FROM credits_type WHERE cid = #{cid}")
    void delete(@Param("cid") String cid);

    @Insert("INSERT INTO credits_type (uid, status, type, description, creditsType, credits) " +
            "VALUES (#{uid}, #{status}, #{type}, #{description}, #{creditsType}, #{credits})")
    void insert(@Param("uid") String uid, @Param("status") Integer status, @Param("type") Integer type, @Param("description") String description,
                @Param("creditsType") Integer creditsType, @Param("credits") Integer credits);

    @Select("SELECT COUNT(*) FROM credits_type WHERE uid = #{uid}")
    Integer count(@Param("uid") String uid);
}

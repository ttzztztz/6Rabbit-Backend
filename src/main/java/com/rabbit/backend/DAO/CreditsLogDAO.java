package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Credits.CreditsLog;
import com.rabbit.backend.Bean.Credits.CreditsLogForm;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CreditsLogDAO {
    @Select("SELECT * FROM credits_log WHERE uid = #{uid} ORDER BY cid DESC LIMIT ${from},${to}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<CreditsLog> list(@Param("uid") String uid, @Param("from") Integer from, @Param("to") Integer to);

    @Delete("DELETE FROM credits_log WHERE cid = #{cid}")
    void delete(@Param("cid") String cid);

    @Insert("INSERT INTO credits_log (uid, status, type, description, creditsType, credits) " +
            "VALUES (#{uid}, #{status}, #{type}, #{description}, #{creditsType}, #{credits})")
    @Options(keyProperty = "cid", keyColumn = "cid", useGeneratedKeys = true)
    void insert(CreditsLogForm form);

    @Select("SELECT COUNT(*) FROM credits_log WHERE uid = #{uid}")
    Integer count(@Param("uid") String uid);


    @Select("SELECT * FROM credits_log WHERE cid = #{cid}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    CreditsLog find(@Param("cid") String cid);
}

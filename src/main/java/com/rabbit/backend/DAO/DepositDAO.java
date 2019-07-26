package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Credits.CreditsLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DepositDAO {
    @Select("SELECT * FROM credits_log WHERE type = 'deposit' AND status = 0 ORDER BY cid LIMIT ${from}, ${to}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<CreditsLog> unverifiedDeposit(@Param("from") Integer from, @Param("to") Integer to);

    @Update("UPDATE credits_log SET status = #{status} WHERE cid = #{cid}")
    void setDeposit(@Param("cid") String cid, @Param("status") Integer status);
}

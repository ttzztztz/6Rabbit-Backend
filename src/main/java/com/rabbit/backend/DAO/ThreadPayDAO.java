package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Credits.CreditsPay;
import com.rabbit.backend.Bean.Credits.ThreadPayLog;
import com.rabbit.backend.Bean.Thread.ThreadListItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ThreadPayDAO {
    @Select("SELECT * FROM thread_pay_log WHERE bid = #{bid}")
    ThreadPayLog find(@Param("bid") String bid);

    @Select("SELECT 1 FROM thread_pay_log WHERE tid = #{tid}, uid = #{uid}")
    Integer isPay(@Param("tid") String tid, @Param("uid") String uid);

    @Insert("INSERT INTO thread_pay_log(uid, tid, creditsType, credits) " +
            "VALUES (#{uid}, #{tid}, #{creditsType}, #{credits})")
    @Options(keyColumn = "bid", keyProperty = "bid", useGeneratedKeys = true)
    void insert(ThreadPayLog threadPayLog);

    @Select("SELECT COUNT(*) FROM thread_pay_log WHERE tid = #{tid}")
    Integer countByTid(@Param("tid") String tid);

    @Select("SELECT thread.* FROM thread_pay_log " +
            "INNER JOIN thread ON `thread_pay_log`.`tid` = `thread`.`tid` " +
            "WHERE thread_pay_log.uid = #{uid} ORDER BY thread_pay_log.createDate LIMIT ${from},${to}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> findByUid(@Param("uid") String uid,
                                   @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT creditsType, credits FROM thread WHERE tid = #{tid}")
    CreditsPay creditsPay(@Param("tid") String tid);
}

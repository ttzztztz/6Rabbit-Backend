package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Attach.AttachListItem;
import com.rabbit.backend.Bean.Credits.AttachPayLog;
import com.rabbit.backend.Bean.Credits.CreditsPay;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AttachPayDAO {
    @Select("SELECT * FROM attach_pay_log WHERE bid = #{bid}")
    AttachPayLog find(@Param("bid") String bid);

    @Select("SELECT 1 FROM attach_pay_log WHERE aid = #{aid}, uid = #{uid}")
    Integer isPay(@Param("aid") String aid, @Param("uid") String uid);

    @Insert("INSERT INTO attach_pay_log(uid, aid, creditsType, credits) " +
            "VALUES (#{uid}, #{aid}, #{creditsType}, #{credits})")
    @Options(keyColumn = "bid", keyProperty = "bid", useGeneratedKeys = true)
    void insert(AttachPayLog attachPayLog);

    @Select("SELECT attach.* FROM attach_pay_log " +
            "INNER JOIN attach ON `attach_pay_log`.`aid` = `attach`.`aid` " +
            "WHERE attach_pay_log.uid = #{uid} ORDER BY attach_pay_log.createDate LIMIT ${from},${to}")
    List<AttachListItem> findByUid(@Param("uid") String uid,
                                   @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT creditsType, credits FROM attach WHERE aid = #{aid}")
    CreditsPay creditsPay(@Param("aid") String aid);
}

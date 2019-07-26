package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Attach.AttachPayListItem;
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

    @Select("SELECT aid FROM attach_pay_log WHERE uid = #{uid} ORDER BY createDate LIMIT ${from},${to}")
    @Results({
            @Result(column = "aid", property = "aid", one = @One(select = "com.rabbit.backend.DAO.AttachDAO.findAttachPayListItem"))
    })
    List<AttachPayListItem> findByUid(@Param("uid") String uid,
                                      @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT creditsType, credits FROM attach WHERE aid = #{aid}")
    CreditsPay creditsPay(@Param("aid") String aid);
}

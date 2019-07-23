package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Notification.Notification;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NotificationDAO {
    @Select("SELECT * FROM notification WHERE toUid = #{toUid} LIMIT ${from},${to} ORDER BY nid DESC")
    @Results({
            @Result(property = "fromUser", column = "fromUid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "toUser", column = "toUid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<Notification> list(@Param("toUid") String toUid, @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT COUNT(*) FROM notification WHERE toUid = #{toUid}")
    Integer totalCount(@Param("toUid") String toUid);

    @Select("SELECT COUNT(*) FROM notification WHERE toUid = #{toUid} AND isRead = 0")
    Integer unreadCount(@Param("toUid") String toUid);

    @Update("UPDATE notification SET isRead = 1 WHERE nid = #{nid}")
    void setOneRead(@Param("nid") String nid);

    @Update("UPDATE notification SET isRead = 1 WHERE toUid = #{toUid}")
    void setAllRead(@Param("toUid") String toUid);

    @Delete("DELETE FROM notification WHERE nid = #{nid}")
    void deleteOne(@Param("nid") String nid);

    @Delete("DELETE FROM notification WHERE toUid = #{toUid}")
    void deleteAll(@Param("toUid") String toUid);

    @Select("SELECT * FROM notification WHERE nid = #{nid} LIMIT 1")
    @Results({
            @Result(property = "fromUser", column = "fromUid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "toUser", column = "toUid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    Notification read(@Param("nid") String nid);

    @Select("SELECT toUid FROM notification WHERE nid = #{nid} LIMIT 1")
    String toUid(@Param("nid") String nid);

    @Insert("INSERT INTO notification (fromUid, toUid, content, link) VALUES (#{fromUid}, #{toUid}, #{content}, #{link})")
    void insert(@Param("fromUid") String fromUid, @Param("toUid") String toUid, @Param("content") String content, @Param("link") String link);
}
package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Thread.ThreadItem;
import com.rabbit.backend.Bean.Thread.ThreadListItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ThreadDAO {
    @Update("UPDATE thread SET ${key} = #{value} WHERE tid = #{tid}")
    void modify(@Param("tid") String tid, @Param("key") String key, @Param("value") String value);

    @Delete("DELETE FROM thread WHERE tid = #{tid}")
    void delete(@Param("tid") String tid);

    @Select("SELECT * FROM thread WHERE tid = #{tid}")
    @Results({
            @Result(property = "forum", column = "fid", one = @One(select = "com.rabbit.backend.DAO.ForumDAO.find")),
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
    })
    ThreadItem find(@Param("tid") String tid);

    @Select("SELECT * FROM thread WHERE fid = #{fid} LIMIT ${from},${to} ORDER BY lastpid DESC")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> list(@Param("fid") String fid, @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT COUNT(*) FROM post WHERE tid = #{tid}")
    Integer postsCount(@Param("tid") String tid);

    @Select("SELECT uid FROM thread WHERE tid = #{tid}")
    String authorUid(@Param("tid") String tid);

    @Select("SELECT * FROM thread WHERE fid = #{fid} AND isTop = 1 LIMIT ${from},${to} ORDER BY lastpid DESC")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> forumTopThreadByFid(@Param("fid") String fid);

    @Select("SELECT * FROM thread WHERE isTop = 2 LIMIT ${from},${to} ORDER BY lastpid DESC")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> globalTopThreadByFid();
}

package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Thread.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
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

    @Select("SELECT * FROM thread WHERE tid = #{tid}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    ThreadListItem findWithThreadListItem(@Param("tid") String tid);

    @Select("SELECT * FROM thread WHERE fid = #{fid} ORDER BY lastpid DESC LIMIT ${from},${to}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> listWithTop(@Param("fid") String fid, @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT * FROM thread WHERE isTop = 0 AND fid IN (1, 2, 3) ORDER BY lastpid DESC LIMIT ${from},${to}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> listNewWithoutTop(@Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT COUNT(1) FROM thread WHERE isTop = 0 AND fid IN (1, 2, 3)")
    Integer listNewCount();

    @Select("SELECT * FROM thread WHERE fid = #{fid} AND isTop = 0 ORDER BY lastpid DESC LIMIT ${from},${to}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> listWithoutTop(@Param("fid") String fid, @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT * FROM thread WHERE fid = #{fid} ORDER BY lastpid DESC LIMIT ${from},${to}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListImageItem> listImageItem(@Param("fid") String fid, @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT * FROM thread WHERE uid = #{uid} ORDER BY tid DESC LIMIT ${from},${to}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> listByUser(@Param("uid") String uid, @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT COUNT(1) FROM thread WHERE uid = #{uid}")
    Integer userThreads(@Param("uid") String uid);

    @Select("SELECT COUNT(*) FROM post WHERE tid = #{tid}")
    Integer postsCount(@Param("tid") String tid);

    @Select("SELECT uid FROM thread WHERE tid = #{tid}")
    String authorUid(@Param("tid") String tid);

    @Select("SELECT * FROM thread WHERE fid = #{fid} AND isTop = 1 ORDER BY lastpid DESC")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> forumTopThreadByFid(@Param("fid") String fid);

    @Select("SELECT * FROM thread WHERE isTop = 2 ORDER BY lastpid DESC")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "lastUser", column = "lastuid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<ThreadListItem> globalTopThread();

    @Update("UPDATE thread SET lastpid = #{lastpid}, lastuid = #{lastuid}, replyDate = #{replyDate} WHERE tid = #{tid}")
    void updateLastReply(@Param("tid") String tid, @Param("lastpid") String lastpid, @Param("lastuid") String lastuid,
                         @Param("replyDate") Date replyDate);

    @Update("UPDATE thread SET subject = #{subject}, fid = #{fid} WHERE tid = #{tid}")
    void update(@Param("tid") String tid, @Param("subject") String subject, @Param("fid") String fid);

    @Insert("INSERT INTO thread(fid, uid, subject) VALUES (#{fid}, #{uid}, #{subject})")
    @Options(keyProperty = "tid", keyColumn = "tid", useGeneratedKeys = true)
    void insert(ThreadEditorForm thread);

    @Update("UPDATE thread SET firstpid = #{firstpid}, lastpid = #{firstpid}, lastuid = #{uid} WHERE tid = #{tid}")
    void updateFirstPid(@Param("tid") String tid, @Param("firstpid") String firstpid, @Param("uid") String uid);

    @Select("SELECT fid FROM thread WHERE tid = #{tid}")
    String fid(@Param("tid") String tid);

    @Select("SELECT `temp`.`tid`, `temp`.`pid`, `temp`.`uid`, `user`.`username`, `temp`.`createDate`,`temp`.`message`,`temp`.`isFirst` FROM " +
            "(SELECT uid, tid, pid, message, createDate, isFirst FROM post WHERE MATCH(`message`) AGAINST(#{keywords} IN NATURAL LANGUAGE MODE) " +
            "UNION " +
            "SELECT uid, tid, firstpid AS pid, subject AS message, createDate, 1 AS isFirst FROM thread WHERE MATCH(`subject`) AGAINST(#{keywords} IN NATURAL LANGUAGE MODE)) temp " +
            "INNER JOIN `user` ON `temp`.`uid`=`user`.`uid` " +
            "ORDER BY `temp`.`pid` DESC " +
            "LIMIT ${from}, ${to}")
    @Results({
            @Result(property = "thread", column = "tid", one = @One(select = "com.rabbit.backend.DAO.ThreadDAO.findWithThreadListItem"))
    })
    List<SearchItem> search(@Param("keywords") String keywords, @Param("from") Integer from, @Param("to") Integer to);

    @Select("SELECT COUNT(1) FROM " +
            "(SELECT uid, tid, pid, message, createDate, isFirst FROM post WHERE MATCH(`message`) AGAINST(#{keywords} IN NATURAL LANGUAGE MODE) " +
            "UNION " +
            "SELECT uid, tid, firstpid AS pid, subject AS message, createDate, 1 AS isFirst FROM thread WHERE MATCH(`subject`) AGAINST(#{keywords} IN NATURAL LANGUAGE MODE)) temp ")
    Integer searchCount(@Param("keywords") String keywords);

    @Delete("DELETE FROM post WHERE tid = #{tid}")
    void deletePostCASCADE(@Param("tid") String tid);

    @Select("SELECT `thread`.`tid`, `post`.`message`, `thread`.`firstpid` FROM `thread` " +
            "INNER JOIN `post` ON `thread`.`firstpid`=`post`.`pid` " +
            "WHERE `thread`.`tid` = #{tid}")
    ThreadMessageItem getMessage(@Param("tid") String tid);
}

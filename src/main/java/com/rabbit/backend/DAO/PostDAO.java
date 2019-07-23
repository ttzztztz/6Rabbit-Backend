package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Thread.Post;
import com.rabbit.backend.Bean.Thread.PostEditorForm;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PostDAO {
    @Select("SELECT * FROM post WHERE pid = #{pid}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
    })
    Post find(@Param("pid") String pid);

    @Select("SELECT * FROM post WHERE tid = #{tid} AND isFirst = 0 LIMIT ${from},${to} ORDER BY pid")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
    })
    List<Post> list(@Param("tid") String tid, @Param("from") Integer from, @Param("to") Integer to);

    @Delete("DELETE FROM post WHERE pid = #{pid}")
    void delete(@Param("pid") String pid);

    @Select("SELECT isFirst FROM post WHERE pid = #{pid}")
    Boolean isFirst(@Param("pid") String pid);

    @Select("SELECT * FROM post WHERE tid = #{tid} AND isFirst = 1 LIMIT 1")
    Post firstPost(@Param("tid") String tid);

    @Update("UPDATE post SET content = #{content} WHERE pid = #{pid}")
    void update(@Param("pid") String pid, @Param("content") String content);

    @Insert("INSERT INTO post (uid, tid, quotepid, content) VALUES (#{uid}, #{tid}, #{form.quotepid}, #{form.content})")
    @Options(keyProperty = "form.pid", keyColumn = "pid", useGeneratedKeys = true)
    void insert(@Param("tid") String tid, @Param("form") PostEditorForm form, @Param("uid") String uid);
}

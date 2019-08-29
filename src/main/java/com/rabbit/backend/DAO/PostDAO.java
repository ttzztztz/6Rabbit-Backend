package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Thread.Post;
import com.rabbit.backend.Bean.Thread.PostEditorForm;
import com.rabbit.backend.Bean.Thread.ThreadEditorForm;
import com.rabbit.backend.Bean.Thread.UserPost;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PostDAO {
    @Select("SELECT * FROM post WHERE pid = #{pid}")
    @Results({
            @Result(id = true, property = "pid", column = "pid"),
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "attachList", column = "pid", many = @Many(select = "com.rabbit.backend.DAO.AttachDAO.findByPidWithoutUser"))
    })
    Post find(@Param("pid") String pid);

    @Select("SELECT * FROM post WHERE tid = #{tid} AND isFirst = 0 ORDER BY pid LIMIT ${from},${to}")
    @Results({
            @Result(id = true, property = "pid", column = "pid"),
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid")),
            @Result(property = "attachList", column = "pid", many = @Many(select = "com.rabbit.backend.DAO.AttachDAO.findByPidWithoutUser"))
    })
    List<Post> list(@Param("tid") String tid, @Param("from") Integer from, @Param("to") Integer to);

    @Delete("DELETE FROM post WHERE pid = #{pid}")
    void delete(@Param("pid") String pid);

    @Select("SELECT isFirst FROM post WHERE pid = #{pid}")
    Boolean isFirst(@Param("pid") String pid);

    @Select("SELECT * FROM post WHERE tid = #{tid} AND isFirst = 1 LIMIT 1")
    Post firstPost(@Param("tid") String tid);

    @Select("SELECT pid FROM post WHERE tid = #{tid} AND isFirst = 1 LIMIT 1")
    String firstpid(@Param("tid") String tid);

    @Update("UPDATE post SET message = #{message} WHERE pid = #{pid}")
    void update(@Param("pid") String pid, @Param("message") String message);

    @Insert("INSERT INTO post (uid, tid, quotepid, message) VALUES (#{uid}, #{tid}, #{quotepid}, #{message})")
    @Options(keyProperty = "pid", keyColumn = "pid", useGeneratedKeys = true)
    void insertWithPostEditorForm(PostEditorForm form);

    @Insert("INSERT INTO post (uid, tid, isFirst, message) VALUES (#{uid}, #{tid}, 1, #{message})")
    @Options(keyProperty = "firstpid", keyColumn = "pid", useGeneratedKeys = true)
    void insertWithThreadEditorForm(ThreadEditorForm form);

    @Select("SELECT tid FROM post WHERE pid = #{pid}")
    String tid(@Param("pid") String pid);

    @Select("SELECT uid FROM post WHERE pid = #{pid}")
    String authorUid(@Param("pid") String pid);

    @Select("SELECT COUNT(1) FROM post WHERE uid = #{uid}")
    Integer userPosts(@Param("uid") String uid);

    @Select("SELECT * FROM post WHERE uid = #{uid} ORDER BY pid DESC LIMIT ${from},${to}")
    @Results({
            @Result(property = "thread", column = "tid", one = @One(select = "com.rabbit.backend.DAO.ThreadDAO.findWithThreadListItem"))
    })
    List<UserPost> listByUser(@Param("uid") String uid, @Param("from") Integer from, @Param("to") Integer to);
}

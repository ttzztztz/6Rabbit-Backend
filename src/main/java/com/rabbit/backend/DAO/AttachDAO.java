package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Attach.Attach;
import com.rabbit.backend.Bean.Attach.AttachUpload;
import com.rabbit.backend.Bean.Attach.ThreadAttach;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AttachDAO {
    @Select("SELECT uid FROM attach WHERE aid = #{aid}")
    String uid(@Param("aid") String aid);

    @Select("SELECT tid FROM attach WHERE aid = #{aid}")
    String tid(@Param("aid") String aid);

    @Select("SELECT * FROM attach WHERE aid = #{aid}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    Attach find(@Param("aid") String aid);

    @Select("SELECT * FROM attach WHERE tid = #{tid}")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<Attach> findByTid(@Param("tid") String tid);

    @Select("SELECT * FROM attach WHERE tid = #{tid}")
    List<ThreadAttach> findByTidInThreadlist(@Param("tid") String tid);

    @Select("SELECT * FROM attach WHERE uid = #{uid} AND tid IS NULL")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<Attach> findUnused(@Param("uid") String uid);

    @Select("SELECT * FROM attach WHERE tid IS NULL")
    @Results({
            @Result(property = "user", column = "uid", one = @One(select = "com.rabbit.backend.DAO.UserDAO.findOtherByUid"))
    })
    List<Attach> findAllUnused();

    @Select("SELECT COUNT(*) FROM attach WHERE tid = #{tid}")
    Integer threadAttachCount(@Param("tid") String tid);

    @Delete("DELETE FROM attach WHERE aid = #{aid}")
    void delete(@Param("aid") String aid);

    @Delete("DELETE FROM attach WHERE tid = #{tid}")
    void deleteByTid(@Param("tid") String tid);

    @Update("UPDATE attach SET tid = #{tid} WHERE aid = #{aid}")
    void updateAttachThread(@Param("aid") String aid, @Param("tid") String tid);

    @Insert("INSERT INTO attach(uid, fileSize, fileName, originalName) VALUES (#{uid}, #{fileSize}, #{fileName}, #{originalName})")
    @Options(keyColumn = "aid", keyProperty = "aid", useGeneratedKeys = true)
    void insert(AttachUpload attachUpload);
}

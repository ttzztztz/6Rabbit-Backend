package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Forum.Forum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ForumDAO {
    @Select("SELECT * FROM forum WHERE fid = #{fid}")
    Forum find(@Param("fid") String fid);

    @Select("SELECT * FROM forum")
    List<Forum> list();
}

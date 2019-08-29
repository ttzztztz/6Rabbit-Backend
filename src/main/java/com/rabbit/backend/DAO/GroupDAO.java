package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.Group.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GroupDAO {
    @Select("SELECT * FROM user_group WHERE gid = #{gid}")
    Group findByGid(@Param("gid") String gid);

    @Select("SELECT * FROM user_group")
    List<Group> list();
}

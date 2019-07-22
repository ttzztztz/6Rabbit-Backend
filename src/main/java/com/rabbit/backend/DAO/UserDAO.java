package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDAO {
    @Select("SELECT * FROM user WHERE uid = #{uid}")
    User findUserByUid(@Param("uid") String uid);
}

package com.rabbit.backend.DAO;

import com.rabbit.backend.Bean.OAuth.OAuth;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OAuthDAO {
    @Select("SELECT * FROM oauth WHERE oid = #{oid}")
    OAuth findByOid(@Param("oid") String oid);

    @Select("SELECT * FROM oauth WHERE uid = #{uid}, platform = #{platform} LIMIT 1")
    OAuth find(@Param("uid") String uid, @Param("platform") String platform);

    @Delete("DELETE FROM oauth WHERE oid = #{oid}")
    void delete(@Param("oid") String oid);

    @Select("SELECT * FROM oauth WHERE uid = #{uid}")
    List<OAuth> listByUid(@Param("uid") String uid);

    @Insert("INSERT INTO oauth(uid, platform, openid) VALUES (#{uid}, #{platform}, #{openid})")
    @Options(keyColumn = "oid", keyProperty = "oid", useGeneratedKeys = true)
    void insert(OAuth item);
}

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

    @Select("SELECT * FROM oauth WHERE uid = #{uid} AND platform = #{platform} LIMIT 1")
    OAuth find(@Param("uid") String uid, @Param("platform") String platform);

    @Select("SELECT * FROM oauth WHERE openid = #{openid} AND platform = #{platform} LIMIT 1")
    OAuth findByOpenid(@Param("openid") String openid, @Param("platform") String platform);

    @Delete("DELETE FROM oauth WHERE oid = #{oid}")
    void delete(@Param("oid") String oid);

    @Delete("DELETE FROM oauth WHERE platform = #{platform} AND uid = #{uid}")
    void deleteByUser(@Param("platform") String platform, @Param("uid") String uid);

    @Select("SELECT * FROM oauth WHERE uid = #{uid}")
    List<OAuth> listByUid(@Param("uid") String uid);

    @Select("SELECT oid FROM oauth WHERE uid = #{uid} AND platform = #{platform}")
    String findOidByUidAndPlatform(@Param("uid") String uid, @Param("platform") String platform);

    @Insert("INSERT INTO oauth(uid, platform, openid) VALUES (#{uid}, #{platform}, #{openid})")
    @Options(keyColumn = "oid", keyProperty = "oid", useGeneratedKeys = true)
    void insert(OAuth item);

    @Select("SELECT uid FROM oauth WHERE platform = #{platform} AND openid = #{openid}")
    String findUid(@Param("platform") String platform, @Param("openid") String openid);
}

package com.rabbit.backend.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface StaticDAO {
    @Update("UPDATE ${table} SET ${field} = ${field} + ${value} WHERE ${condition} = #{conditionValue}")
    void increment(@Param("table") String table, @Param("field") String field, @Param("condition") String condition,
                   @Param("conditionValue") String conditionValue, @Param("value") Integer value);

    @Update("UPDATE ${table} SET ${field} = ${field} - ${value} WHERE ${condition} = #{conditionValue}")
    void decrement(@Param("table") String table, @Param("field") String field, @Param("condition") String condition,
                   @Param("conditionValue") String conditionValue, @Param("value") Integer value);
}

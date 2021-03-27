package com.example.graduate.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.pojo.UserPointLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserPointLogMapper extends BaseMapper<UserPointLog> {
    @Select("<script> " +
            "select count(1) from user_point_log " +
            "<where> " +
            "<if test = 'name!=null'> " +
            " and name = #{name}" +
            "</if>" +
            "</where>" +
            "</script>")
    int qryTotal(Map<String,Object> params);
    @Select("<script> " +
            "select * from user_point_log " +
            "<where> " +
            "<if test = 'name!=null'> " +
            " and name = #{name}" +
            "</if>" +
            "</where>" +
            "</script>")
    List<UserPointLog> qryLogList(Page<UserPointLog> page, @Param("name")String name);
}

package com.example.graduate.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.graduate.dto.UserDTO;
import com.example.graduate.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("<script>" +
            "select name,password,salt,uname,phone,email " +
            "from user " +
            "where 1 = 1 " +
            "<if test = 'name!=null and name!=\"\"'> " +
            " and name = #{name} " +
            "</if>" +
            "</script>")
    List<UserDTO> qryUsersByPage(Page<UserDTO> page, @Param("name")String name);

    @Select("<script>" +
            "select count(1) " +
            "from user " +
            "<where> " +
            "<if test = 'name!=null and name!=\"\"'> " +
            " and name = #{name,jdbcType=VARCHAR} " +
            "</if>" +
            "</where>" +
            "</script>")
    int qryTotalRow(Map<String,Object> params);
}

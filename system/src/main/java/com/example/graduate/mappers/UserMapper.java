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
            "select count(1) " +
            "from user " +
            "<where> " +
            "<if test = 'name!=null and name!=\"\"'> " +
            " and name = #{name,jdbcType=VARCHAR} " +
            "</if>" +
            "</where>" +
            "</script>")
    int qryTotalRow(Map<String,Object> params);
    @Select("<script>" +
            "select count(distinct a.name) from user a " +
            "left join user_class b on a.id = b.uid " +
            "<where> " +
            "<if test = 'name!=null and name!=\"\"'> " +
            " and a.name like concat(#{name},'%') " +
            "</if> " +
            "<if test = 'cid!=null and cid != 0 and cid != -1'>" +
            " and b.cid = #{cid} " +
            "</if> " +
            "<if test = 'uname!=null and uname!=\"\"'> " +
            " and a.uname like concat(#{uname},'%') " +
            "</if> " +
            "</where>" +
            "</script>")
    int qryTotal(Map<String,Object> params);
    @Select("<script>" +
            "select a.* from user a " +
            "left join user_class b on a.id = b.uid " +
            "<where> " +
            "<if test = 'name!=null and name!=\"\"'> " +
            " and a.name like concat(#{name},'%') " +
            "</if> " +
            "<if test = 'cid!=null and cid != 0 and cid != -1'>" +
            " and b.cid = #{cid} " +
            "</if> " +
            "<if test = 'uname!=null and uname!=\"\"'> " +
            " and a.uname like concat(#{uname},'%') " +
            "</if> " +
            "</where> " +
            "</script>")
    List<User> qryUserPage(Page<User> page,@Param("name") String name,@Param("cid") int cid,@Param("uname")String uname);
}

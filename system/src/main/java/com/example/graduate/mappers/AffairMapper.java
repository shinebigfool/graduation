package com.example.graduate.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.pojo.Affair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AffairMapper extends BaseMapper<Affair> {
    @Select("<script> " +
            "select count(1) from affair " +
            "<where> " +
            "<if test = 'id!=null'> " +
            " and id = #{id}" +
            "</if>" +
            "</where>" +
            "</script>")
    int qryTotalRaw(Map<String,Object> params);
    @Select("<script> " +
            "select * from affair " +
            "<where> " +
            "<if test ='id!=null and id!=-1'> " +
            " and id = #{id}" +
            "</if>" +
            "</where>" +
            "</script>")
    List<Affair> qryAffairByPage(Page<Affair> page,@Param("id") Integer id);
}

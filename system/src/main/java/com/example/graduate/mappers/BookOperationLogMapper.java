package com.example.graduate.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.pojo.BookOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookOperationLogMapper extends BaseMapper<BookOperationLog> {

    @Select("<script> " +
            "select count(1) from book_operation_log a " +
            "<where> " +
            " <if test = 'opName!=null and opName!=\"\"'> " +
            " and a.operation_person like concat('%' , #{opName,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'state!=null and state!=0 and state !=-1'> " +
            " and a.status = #{state} " +
            "</if>" +
            " <if test = 'type!=null and type!=0 and type !=-1'> " +
            " and a.type =#{type} " +
            "</if>" +
            " <if test = 'bid!=null and bid != -1 and bid!=0'> " +
            " and a.bid = #{bid} " +
            "</if>" +
            " <if test = 'date!=null and date!=\"\"'> " +
            " and a.create_date >= #{date} " +
            "</if>" +
            "</where>" +
            "</script>")
    int qryTotalRow(Map<String,Object> params);
    @Select("<script> " +
            "select a.* from book_operation_log a " +
            "<where> " +
            " <if test = 'opName!=null and opName!=\"\"'> " +
            " and a.operation_person like concat('%' , #{opName,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'state!=null and state!=0 and state !=-1'> " +
            " and a.status = #{state} " +
            "</if>" +
            " <if test = 'type!=null and type!=0 and type !=-1'> " +
            " and a.type =#{type} " +
            "</if>" +
            " <if test = 'bid!=null and bid != -1 and bid!=0'> " +
            " and a.bid = #{bid} " +
            "</if>" +
            " <if test = 'date!=null and date!=\"\"'> " +
            " and a.create_date >= #{date} " +
            "</if>" +
            "</where>" +
            "</script>")
    List<BookOperationLog> qryLogPage(Page<BookOperationLog> page,
                                      @Param("opName") String opName,
                                      @Param("state") int state,
                                      @Param("type") int type,
                                      @Param("date") String date,
                                      @Param("bid") int bid);
}

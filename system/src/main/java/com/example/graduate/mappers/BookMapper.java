package com.example.graduate.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.pojo.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    @Select("<script>" +
            "select count(1) " +
            "from book " +
            "<where> " +
            " <if test = 'title!=null and title!=\"\"'> " +
            " or title like concat('%' , #{title,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'author!=null and author!=\"\"'> " +
            " or author like concat('%' , #{author,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'cid!=null and cid != -1 and cid !=\"\" and cid != 0'> " +
            " and cid = #{cid} " +
            "</if>" +
            " <if test = 'examineState!=null and examineState != -1'> " +
            " and examine_state = #{examineState} " +
            "</if>" +
            " <if test = 'availableState!=null and availableState != -1'> " +
            " and available_state = #{availableState} " +
            "</if>" +
            " <if test = 'examinePerson!=null and author!=\"\"'> " +
            " and examine_person like concat('%' , #{examinePerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'uploadPerson!=null and author!=\"\"'> " +
            " or upload_person like concat('%' , #{uploadPerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            "</where>" +
            "</script>")
    int qryTotalRow(Map<String,Object> params);

    @Select("<script>" +
            " select * from book " +
            "<where>" +
            " <if test = 'title!=null and title!=\"\"'> " +
            " or title like concat('%' , #{title,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'author!=null and author!=\"\"'> " +
            " or author like concat('%' , #{author,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'cid!=null and cid != -1 and cid != 0 and cid !=\"\"'> " +
            " and cid = #{cid} " +
            "</if>" +
            " <if test = 'examineState!=null and examineState != -1'> " +
            " and examine_state = #{examineState} " +
            "</if>" +
            " <if test = 'availableState!=null and availableState != -1'> " +
            " and available_state = #{availableState} " +
            "</if>" +
            " <if test = 'examinePerson!=null and examinePerson!=\"\"'> " +
            " and examine_person like concat('%' , #{examinePerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'uploadPerson!=null and uploadPerson!=\"\"'> " +
            " or upload_person like concat('%' , #{uploadPerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            "</where>" +
            "</script>")
    List<Book> qryBookByPage(Page<Book> page,@Param("title") String title,@Param("author") String author,
                             @Param("cid") int cid, @Param("examineState") int examineState,
                             @Param("availableState") int availableState,
                             @Param("examinePerson") String examinePerson,
                             @Param("uploadPerson") String uploadPerson);
}

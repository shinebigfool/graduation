package com.example.graduate.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BookFavorite;
import com.example.graduate.pojo.BookFavoriteCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookFavoriteMapper extends BaseMapper<BookFavorite> {
    @Select("<script>" +
            "select a.* from(" +
            "select * from book where id in (" +
            "  select b.book_id from book_favorite b where b.user_account = #{name} )) a " +
            "<where>"+
            " <if test = 'title!=null and title!=\"\"'> " +
            " or a.title like concat('%' , #{title,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'author!=null and author!=\"\"'> " +
            " or a.author like concat('%' , #{author,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'cid!=null and cid != -1 and cid != 0 and cid !=\"\"'> " +
            " and a.cid = #{cid} " +
            "</if>" +
            " <if test = 'uploadPerson!=null and uploadPerson!=\"\"'> " +
            " or a.upload_person like concat('%' , #{uploadPerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            "</where>" +
            "</script>")
    List<Book> qryFavoriteBook(Map<String,Object> params);
    @Select("select a.* ,b.favoriteCount from book a \n" +
            "right join \n" +
            "(\n" +
            "SELECT\n" +
            "\tbook_id,count( DISTINCT user_account ) favoriteCount\n" +
            "FROM\n" +
            "\tbook_favorite \n" +
            "GROUP BY\n" +
            "\tbook_id\n" +
            "ORDER BY\n" +
            "\tfavoriteCount desc\n" +
            ")\tb\n" +
            "on a.id = b.book_id " +
            "limit 10")
    List<BookFavoriteCount> favoriteCount();
    @Select("<script>" +
            "select a.* from(" +
            "select * from book where id in (" +
            "  select b.book_id from book_favorite b where b.user_account = #{name} )) a " +
            "<where>"+
            " <if test = 'title!=null and title!=\"\"'> " +
            " or a.title like concat('%' , #{title,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'author!=null and author!=\"\"'> " +
            " or a.author like concat('%' , #{author,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'cid!=null and cid != -1 and cid != 0 and cid !=\"\"'> " +
            " and a.cid = #{cid} " +
            "</if>" +
            " <if test = 'uploadPerson!=null and uploadPerson!=\"\"'> " +
            " or a.upload_person like concat('%' , #{uploadPerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            "</where>" +
            "</script>")
    List<Book> qryFavoriteByPage(Page<Book> page,
                                 @Param("title")String title,
                                 @Param("author")String author,
                                 @Param("cid")int cid,
                                 @Param("uploadPerson")String uploadPerson,
                                 @Param("name")String name);
    @Select("<script>" +
            "select count(1) from(" +
            "select * from book where id in (" +
            "  select b.book_id from book_favorite b where b.user_account = #{name} )) a " +
            "<where>"+
            " <if test = 'title!=null and title!=\"\"'> " +
            " or a.title like concat('%' , #{title,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'author!=null and author!=\"\"'> " +
            " or a.author like concat('%' , #{author,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'cid!=null and cid != -1 and cid != 0 and cid !=\"\"'> " +
            " and a.cid = #{cid} " +
            "</if>" +
            " <if test = 'uploadPerson!=null and uploadPerson!=\"\"'> " +
            " or a.upload_person like concat('%' , #{uploadPerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            "</where>" +
            "</script>")
    int countFavorite(Map<String,Object> params);
}

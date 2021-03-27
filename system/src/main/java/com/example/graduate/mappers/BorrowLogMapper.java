package com.example.graduate.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.pojo.BookLendCount;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.pojo.BorrowLogDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BorrowLogMapper extends BaseMapper<BorrowLog> {
    @Select("<script>" +
            "select a.id id, a.borrow_date,a.user_account,a.book_id,a.total_time," +
            "a.book_title,a.need_return_date,\n" +
            "a.note,a.state,a.return_date,b.author,b.cid,b.upload_person,datediff(now(),a.borrow_date) overDue " +
            "from borrow_log a\n" +
            "LEFT JOIN book b\n" +
            "on a.book_id = b.id " +
            "<where>" +
            " <if test = 'name!=null and name!=\"\"'> " +
            " and a.user_account = #{name,jdbcType=VARCHAR}" +
            "</if>" +
            " <if test = 'title!=null and title!=\"\"'> " +
            " and a.book_title like concat('%' , #{title,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'author!=null and author!=\"\"'> " +
            " and b.author like concat('%' , #{author,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'cid!=null and cid != -1'> " +
            " and b.cid = #{cid} " +
            "</if>" +
            " <if test = 'borrowState!=null and borrowState != -1'> " +
            " and a.state = #{borrowState} " +
            "</if>" +
            " <if test = 'uploadPerson!=null and uploadPerson!=\"\"'> " +
            " and b.upload_person like concat('%' , #{uploadPerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            "</where>" +
            "</script>")
    List<BorrowLogDetail> qryBorrowLogDetail(Map<String,Object> params);

    @Select("<script> " +
            "select count(1) from borrow_log a left join book b on a.book_id=b.id " +
            "<where> " +
            " <if test = 'name!=null and name!=\"\"'> " +
            " and a.user_account = #{name,jdbcType=VARCHAR} " +
            "</if>" +
            " <if test = 'title!=null and title!=\"\"'> " +
            " and a.book_title like concat('%' , #{title,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'author!=null and author!=\"\"'> " +
            " and b.author like concat('%' , #{author,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'cid!=null and cid != -1 and cid != \"\"'> " +
            " and b.cid = #{cid} " +
            "</if>" +
            " <if test = 'borrowState!=null and borrowState != -1 and borrowState != \"\"'> " +
            " and a.state = #{borrowState} " +
            "</if>" +
            " <if test = 'uploadPerson!=null and uploadPerson!=\"\"'> " +
            " and b.upload_person like concat('%' , #{uploadPerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            "<if test = 'overDue!=null'> " +
            " and datediff(now(),a.borrow_date)>20 and a.state = 2" +
            "</if>" +
            "</where>" +
            "</script>")
    int qryTotalRow(Map<String,Object> params);

    @Select("<script>" +
            "select a.id id, a.borrow_date,a.user_account,a.book_id,a.total_time," +
            "a.book_title,a.need_return_date,\n" +
            "a.note,a.state,a.return_date,b.author,b.cid,b.upload_person,datediff(now(),a.borrow_date)-20 overDue  " +
            "from borrow_log a\n" +
            "LEFT JOIN book b\n" +
            "on a.book_id = b.id " +
            "<where>" +
            " <if test = 'name!=null and name!=\"\"'> " +
            " and a.user_account = #{name,jdbcType=VARCHAR}" +
            "</if>" +
            " <if test = 'title!=null and title!=\"\"'> " +
            " and a.book_title like concat('%' , #{title,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'author!=null and author!=\"\"'> " +
            " and b.author like concat('%' , #{author,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            " <if test = 'cid!=null and cid != -1'> " +
            " and b.cid = #{cid} " +
            "</if>" +
            " <if test = 'borrowState!=null and borrowState != -1'> " +
            " and a.state = #{borrowState} " +
            "</if>" +
            " <if test = 'uploadPerson!=null and uploadPerson!=\"\"'> " +
            " and b.upload_person like concat('%' , #{uploadPerson,jdbcType=VARCHAR}, '%') " +
            "</if>" +
            "<if test = 'overDue!=null and overDue == 1'> " +
            " and datediff(now(),a.borrow_date)>20 and a.state = 2" +
            "</if>" +
            "</where>" +
            "</script>")
    List<BorrowLogDetail> qryLogPage(Page<BorrowLogDetail> page,
                               @Param("title") String title,
                               @Param("author") String author,
                               @Param("cid") int cid,
                               @Param("borrowState") int borrowState,
                               @Param("uploadPerson") String uploadPerson,
                               @Param("name") String name,
                               @Param("overDue")int overDue);
    @Select("select a.*,b.lendCount from book a\n" +
            "right join \n" +
            "(\n" +
            "select book_id,count(1) lendCount from borrow_log \n" +
            "group by book_id\n" +
            "order by lendCount desc\n" +
            "limit 10\n" +
            ") b\n" +
            "on a.id = b.book_id")
    List<BookLendCount> lendCount();
}

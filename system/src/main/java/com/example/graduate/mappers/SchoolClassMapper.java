package com.example.graduate.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.graduate.pojo.SchoolClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SchoolClassMapper extends BaseMapper<SchoolClass> {
    @Select("select count(1) readingAmount,b.class_id,c.class_name from borrow_log a \n" +
            "left join user b on a.user_account = b.name\n" +
            "left join school_class c on c.class_id = b.class_id\n" +
            "group by b.class_id")
    List<Map<String,Object>> qryReadingAmountByClass();

    @Select("select count(1) readingAmount,c.class_grade from borrow_log a \n" +
            "left join user b on a.user_account = b.name\n" +
            "left join school_class c on c.class_id = b.class_id\n" +
            "group by c.class_grade")
    List<Map<String,Object>> qryReadingAmountByGrade();

    @Select("select count(distinct a.user_account) readingAmount,b.class_id,c.class_name from borrow_log a \n" +
            "left join user b on a.user_account = b.name\n" +
            "left join school_class c on c.class_id = b.class_id\n" +
            "group by b.class_id")
    List<Map<String,Object>> qryReadingStuByClass();

    @Select("select count(distinct a.user_account) readingAmount,c.class_grade from borrow_log a \n" +
            "left join user b on a.user_account = b.name\n" +
            "left join school_class c on c.class_id = b.class_id\n" +
            "group by c.class_grade")
    List<Map<String,Object>> qryReadingStuByGrade();
}

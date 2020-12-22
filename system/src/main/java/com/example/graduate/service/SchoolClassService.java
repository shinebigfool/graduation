package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.pojo.SchoolClass;

import java.util.List;
import java.util.Map;

public interface SchoolClassService extends IService<SchoolClass> {
    List<Map<String,Object>> qryReadingAmountByClass();
    List<Map<String,Object>> qryReadingAmountByGrade();
    List<Map<String,Object>> qryReadingStuByClass();
    List<Map<String,Object>> qryReadingStuByGrade();
}

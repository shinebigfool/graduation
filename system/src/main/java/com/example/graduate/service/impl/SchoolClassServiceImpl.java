package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.SchoolClassMapper;
import com.example.graduate.pojo.SchoolClass;
import com.example.graduate.service.SchoolClassService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SchoolClassServiceImpl extends ServiceImpl<SchoolClassMapper, SchoolClass> implements SchoolClassService {

    @Override
    public List<Map<String, Object>> qryReadingAmountByClass() {
        return this.baseMapper.qryReadingAmountByClass();
    }

    @Override
    public List<Map<String, Object>> qryReadingAmountByGrade() {
        return this.baseMapper.qryReadingAmountByGrade();
    }

    @Override
    public List<Map<String, Object>> qryReadingStuByClass() {
        return this.baseMapper.qryReadingStuByClass();
    }

    @Override
    public List<Map<String, Object>> qryReadingStuByGrade() {
        return this.baseMapper.qryReadingStuByGrade();
    }
}

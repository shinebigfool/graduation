package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.AffairMapper;
import com.example.graduate.pojo.Affair;
import com.example.graduate.service.AffairService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AffairServiceImpl extends ServiceImpl<AffairMapper, Affair> implements AffairService{
    @Override
    public int qryTotalRaw(Map<String, Object> params) {
        return this.baseMapper.qryTotalRaw(params);
    }

    @Override
    public List<Affair> qryAffairByPage(Page<Affair> page, Integer id) {
        return this.baseMapper.qryAffairByPage(page,id);
    }
}

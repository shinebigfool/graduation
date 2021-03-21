package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.pojo.Affair;

import java.util.List;
import java.util.Map;

public interface AffairService extends IService<Affair> {
    int qryTotalRaw(Map<String,Object> params);
    List<Affair> qryAffairByPage(Page<Affair> page,Integer id);
}

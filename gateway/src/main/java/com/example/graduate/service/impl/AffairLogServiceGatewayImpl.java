package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.pojo.AffairLog;
import com.example.graduate.service.AffairLogService;
import com.example.graduate.service.AffairLogServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AffairLogServiceGatewayImpl implements AffairLogServiceGateway {
    @Autowired
    private AffairLogService affairLogService;

    @Override
    public DTO delByAffairId(Integer affairId) {
        LambdaQueryWrapper<AffairLog> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(AffairLog::getAffairId,affairId);
        List<Integer> ids = affairLogService.list(wrapper).stream().map(AffairLog::getId).collect(Collectors.toList());
        affairLogService.removeByIds(ids);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public DTO addAffairLog(AffairLog affairLog) {
        boolean save = affairLogService.save(affairLog);
        if(save){
            return new DTO(RetCodeEnum.SUCCEED);
        }
        return new DTO(RetCodeEnum.FAIL);
    }

    @Override
    public ListDTO<AffairLog> qryAffairLogByAffairId(Integer affairId) {
        LambdaQueryWrapper<AffairLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AffairLog::getAffairId,affairId);
        List<AffairLog> list = affairLogService.list(wrapper);
        ListDTO<AffairLog> listDTO= new ListDTO<>(RetCodeEnum.SUCCEED);
        listDTO.setRetList(list);
        return listDTO;
    }
}

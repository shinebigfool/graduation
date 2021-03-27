package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.UserPointLog;
import com.example.graduate.service.UserPointLogService;
import com.example.graduate.service.UserPointLogServiceGateway;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.PresentUserUtils;
import com.example.graduate.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserPointLogServiceGatewayImpl implements UserPointLogServiceGateway {
    @Autowired
    private UserPointLogService userPointLogService;
    @Override
    public ListDTO<UserPointLog> qryLog(String name){
        ListDTO<UserPointLog> dto = new ListDTO<>(RetCodeEnum.SUCCEED);
        LambdaQueryWrapper<UserPointLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointLog::getName,name);
        dto.setRetList(userPointLogService.list(wrapper));
        return dto;
    }
    @Override
    public DTO addLog(UserPointLog userPointLog){
        userPointLogService.save(userPointLog);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public PageDTO<UserPointLog> qryLogByPage(Map<String, Object> params) {
        String name = PresentUserUtils.qryPresentUserAccount();
        params.put("name",name);
        PageDTO<UserPointLog> dto = new PageDTO<>(RetCodeEnum.SUCCEED);
        int totalRow = userPointLogService.qryTotal(params);
        params.put("totalRow",totalRow);
        int size = PageUtil.transParam2Page(params,dto);
        int current = StringUtil.objectToInt(params.get("current"));
        Page<UserPointLog> page = new Page<>(current,size);
        List<UserPointLog> list = userPointLogService.qryLogList(page,name);
        dto.setRetList(list);
        return dto;
    }
}


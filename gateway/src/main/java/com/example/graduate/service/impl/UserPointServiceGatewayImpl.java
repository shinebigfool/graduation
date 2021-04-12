package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.annotation.PointLogAnnotation;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ObjectDTO;
import com.example.graduate.pojo.UserPoint;
import com.example.graduate.service.UserPointService;
import com.example.graduate.service.UserPointServiceGateway;
import com.example.graduate.utils.PresentUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPointServiceGatewayImpl implements UserPointServiceGateway {
    @Autowired
    private UserPointService userPointService;
    @Override
    @PointLogAnnotation(note = "新增关联记录")
    public DTO addUserPoint(UserPoint userPoint, String note) {
        if(userPointService.save(userPoint)){
            return new DTO(RetCodeEnum.SUCCEED);
        }
        return new DTO(RetCodeEnum.FAIL);
    }

    @Override
    @PointLogAnnotation(note = "修改积分")
    public DTO modUserPoint(UserPoint userPoint,String note) {
        userPointService.updateById(userPoint);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public DTO del(int id) {
        userPointService.removeById(id);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public UserPoint getByName(String name) {
        LambdaQueryWrapper<UserPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPoint::getName,name);
        List<UserPoint> list = userPointService.list(wrapper);
        if(list.size()!=1){
            return null;
        }

        return list.get(0);
    }

    @Override
    public ObjectDTO getPoint() {
        UserPoint userPoint = getByName(PresentUserUtils.qryPresentUserAccount());
        ObjectDTO dto = new ObjectDTO(RetCodeEnum.SUCCEED);
        dto.setData(userPoint.getPoint());
        return dto;
    }
}

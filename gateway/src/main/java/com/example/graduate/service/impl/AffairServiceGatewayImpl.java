package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.annotation.AffairLogAnnotation;
import com.example.graduate.codeEnum.LogOperationType;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.Affair;
import com.example.graduate.pojo.UserPoint;
import com.example.graduate.service.AffairService;
import com.example.graduate.service.AffairServiceGateway;
import com.example.graduate.service.UserPointServiceGateway;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.PresentUserUtils;
import com.example.graduate.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AffairServiceGatewayImpl implements AffairServiceGateway {
    @Autowired
    private AffairService affairService;
    @Autowired
    private UserPointServiceGateway userPointServiceGateway;
    @Override
    @AffairLogAnnotation(description = "新建流程",type = LogOperationType.ADD)
    public DTO addAffair(Affair affair) {
        String applyUser = PresentUserUtils.qryPresentUserAccount();
        affair.setApplyUser(applyUser);
        if(affairService.save(affair)){
            return new DTO(RetCodeEnum.SUCCEED);
        }
        return new DTO(RetCodeEnum.FAIL);
    }

    @Override
    public DTO modAffair(Affair affair) {
        Affair inDb = affairService.getById(affair.getId());
        if(inDb.getState()==1){
            return new DTO(RetCodeEnum.FAIL.getCode(),"该流程已结束");
        }
        inDb.setOperateUser(PresentUserUtils.qryPresentUserAccount());
        inDb.setNote(affair.getNote());
        inDb.setState(affair.getState());
        inDb.setAffairDetail(affair.getAffairDetail());
        if(affair.getState()==1){
            UserPoint userPoint = userPointServiceGateway.getByName(affair.getApplyUser());
            userPoint.setPoint(userPoint.getPoint()-affair.getAffairDetail());
            String type=null;
            if(affair.getAffairType()==1){
                type = "超期";
            }else if(affair.getAffairType()==2){
                type = "申请购书";
            }else if(affair.getAffairType()==3){
                type = "遗失";
            }
            userPointServiceGateway.modUserPoint(userPoint,"编号"+affair.getId()+type+"事务"+"-"+affair.getAffairDetail()+"分");
        }
        if(affairService.updateById(inDb)){
            return new DTO(RetCodeEnum.SUCCEED);
        }
        return new DTO(RetCodeEnum.FAIL);
    }

    @Override
    public DTO delAffair(int id) {
        if(affairService.removeById(id)){
            return new DTO(RetCodeEnum.SUCCEED);
        }
        return new DTO(RetCodeEnum.FAIL);
    }

    @Override
    public PageDTO<Affair> qryAffair(Map<String, Object> params) {
        PageDTO<Affair> pageDTO = new PageDTO<>(RetCodeEnum.SUCCEED);
        int totalRaw = affairService.qryTotalRaw(params);
        params.put("totalRow",totalRaw);
        int size = PageUtil.transParam2Page(params,pageDTO);
        int current = StringUtil.objectToInt(params.get("current"));
        Page<Affair> page = new Page<>(current,size);
        int id = StringUtil.objectToInt(params.get("id"));
        List<Affair> affairs = affairService.qryAffairByPage(page,id);
        pageDTO.setRetList(affairs);
        return pageDTO;
    }
}

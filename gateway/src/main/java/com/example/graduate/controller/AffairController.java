package com.example.graduate.controller;

import com.example.graduate.annotation.AffairLogAnnotation;
import com.example.graduate.codeEnum.LogOperationType;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.Affair;
import com.example.graduate.pojo.AffairLog;
import com.example.graduate.service.AffairLogServiceGateway;
import com.example.graduate.service.AffairServiceGateway;
import com.example.graduate.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@RequestMapping("/affair")
@Api(value = "事务管理",tags = "AffairController")
public class AffairController {
    @Autowired
    private AffairServiceGateway affairServiceGateway;
    @Autowired
    private AffairLogServiceGateway affairLogServiceGateway;
    @PostMapping("/affair")

    DTO addAffair(@RequestBody Affair affair){

        return affairServiceGateway.addAffair(affair);
    }
    @PutMapping("/affair")
    @AffairLogAnnotation(description = "更新流程",type = LogOperationType.UPDATE)
    DTO modAffair(@RequestBody Affair affair){
        return affairServiceGateway.modAffair(affair);
    }
    @DeleteMapping("/affair")
    @AffairLogAnnotation(description = "删除流程",type = LogOperationType.DELETE)
    DTO delAffair(@RequestBody @ApiIgnore Map<String,Object> params){
        int affairId = StringUtil.objectToInt(params.get("affairId"));
        return affairServiceGateway.delAffair(affairId);
    }
    @GetMapping("/affair")
    PageDTO<Affair> qryAffair(@ApiIgnore @RequestParam Map<String,Object> params){
        return affairServiceGateway.qryAffair(params);
    }

    @GetMapping("/log")
    ListDTO<AffairLog> qryLog(@RequestParam("affairId")Integer id){
        return affairLogServiceGateway.qryAffairLogByAffairId(id);
    }
}

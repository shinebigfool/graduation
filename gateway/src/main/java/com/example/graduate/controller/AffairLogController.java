package com.example.graduate.controller;

import com.example.graduate.dto.ListDTO;
import com.example.graduate.pojo.AffairLog;
import com.example.graduate.service.AffairLogServiceGateway;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("affairLog")
@Api(value = "事务流程详情",tags = "AffairLogController")
public class AffairLogController {
    @Autowired
    private AffairLogServiceGateway affairLogServiceGateway;
    @GetMapping("/affairLog")
    ListDTO<AffairLog> qryLog(@RequestParam("affairId") Integer id){
        return affairLogServiceGateway.qryAffairLogByAffairId(id);
    }
}

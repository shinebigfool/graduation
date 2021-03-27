package com.example.graduate.controller;

import com.example.graduate.dto.ObjectDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.UserPointLog;
import com.example.graduate.service.UserPointLogServiceGateway;
import com.example.graduate.service.UserPointServiceGateway;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/point")
@Api(value = "积分管理",tags = "UserPointController")
public class UserPointController {
    @Autowired
    private UserPointServiceGateway userPointServiceGateway;
    @Autowired
    private UserPointLogServiceGateway userPointLogServiceGateway;
    @GetMapping("/point")
    ObjectDTO qryPoint(){
        return userPointServiceGateway.getPoint();
    }
    @GetMapping("/log")
    PageDTO<UserPointLog> qryLog(@RequestParam Map<String,Object> params){
        return userPointLogServiceGateway.qryLogByPage(params);
    }
}

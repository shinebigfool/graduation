package com.example.graduate.controller;

import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.BookOperationLog;
import com.example.graduate.service.BookOperationServiceGateway;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@Api(value = "图书操作日志管理", tags = "BookOperationController")
@RequestMapping("log")
public class BookOperationController {

    @Autowired
    private BookOperationServiceGateway bookOperationServiceGateway;
    @GetMapping("log")
    @ApiOperation(value = "查询日志详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页面", required = true, paramType = "query"),
            @ApiImplicitParam(name = "size", value = "页面大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "bid", value = "图书ID", required = false, paramType = "query"),
            @ApiImplicitParam(name = "opName", value = "操作人账号", required = false, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "操作状态", required = false, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "操作种类", required = false, paramType = "query"),
            @ApiImplicitParam(name = "date", value = "操作时间", required = false, paramType = "query"),
    })
    PageDTO<BookOperationLog> qryLog(@ApiIgnore @RequestParam Map<String,Object> params) {
        return bookOperationServiceGateway.qryLog(params);
    }
}

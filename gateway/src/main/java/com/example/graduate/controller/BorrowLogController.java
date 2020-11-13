package com.example.graduate.controller;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.service.BorrowLogServiceGateway;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * @author 倪鑫彦
 * @since 16:12 2020/11/12
 */
@RestController
@RequestMapping("/borrow")
@Api(value = "借阅日志管理",tags = "BorrowLogController")
public class BorrowLogController {
    @Autowired
    private BorrowLogServiceGateway borrowLogServiceGateway;

    @PostMapping("/lend")
    @ApiOperation(value = "借书")
    @ApiImplicitParam(name = "bid",value = "图书id",paramType = "query",required = true)
    DTO lendBook(@RequestParam int bid){
        return borrowLogServiceGateway.lendBook(bid);
    }

    @PutMapping("/return")
    @ApiOperation(value = "还书")
    @ApiImplicitParam(name = "bid",value = "图书id",paramType = "query",required = true)
    DTO returnBook(@RequestParam int bid){
        return borrowLogServiceGateway.returnBook(bid);
    }

    @GetMapping("log")
    @ApiOperation(value = "查询当前用户的借书记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bookId",value = "图书id",paramType = "query",required = false),
            @ApiImplicitParam(name = "title",value = "图书标题(模糊)",paramType = "query",required = false),
            @ApiImplicitParam(name = "author",value = "作者",required = false,paramType = "query"),
            @ApiImplicitParam(name = "cid",value = "种类ID",required = false,paramType = "query"),
            @ApiImplicitParam(name = "borrowState",value = "是否已归还",required = false,paramType = "query"),
            @ApiImplicitParam(name = "uploadPerson",value = "上传者",required = false,paramType = "query")
    })
    ListDTO qryBorrowLog(@ApiIgnore @RequestParam Map<String,Object> params){
        return borrowLogServiceGateway.qryBorrowLog(params);
    }
}

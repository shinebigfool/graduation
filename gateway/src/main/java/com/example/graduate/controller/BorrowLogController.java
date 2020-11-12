package com.example.graduate.controller;

import com.example.graduate.dto.DTO;
import com.example.graduate.service.BorrowLogServiceGateway;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    // TODO 待实现
    @PostMapping("/lend")
    @ApiOperation(value = "借书")
    DTO lendBook(@RequestParam int bid){
        return borrowLogServiceGateway.lendBook(bid);
    }
}

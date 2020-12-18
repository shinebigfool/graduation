package com.example.graduate.controller;

import com.example.graduate.dto.*;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.pojo.BorrowLogDetail;
import com.example.graduate.service.BorrowLogServiceGateway;
import com.example.graduate.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
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
@Api(value = "借阅日志管理", tags = "BorrowLogController")
public class BorrowLogController {
    @Autowired
    private BorrowLogServiceGateway borrowLogServiceGateway;

    @PostMapping("/lend")
    @ApiOperation(value = "借书")
    @ApiImplicitParam(name = "bid", value = "图书id", paramType = "query", required = true)
    DTO lendBook(@RequestBody BookDTO book) {
        return borrowLogServiceGateway.lendBook(book.getId());
    }

    @PutMapping("/return")
    @ApiOperation(value = "还书")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bid", value = "图书id", paramType = "query", required = true),
            @ApiImplicitParam(name = "name", value = "用户账户", paramType = "query", required = false)
    })
    DTO returnBook(@RequestBody @ApiIgnore Map<String,Object> params) {
        int bid = StringUtil.objectToInt(params.get("bid"));
        String name = StringUtil.parseString(params.get("name"));
        return borrowLogServiceGateway.returnBook(bid,name);
    }

    @GetMapping("log")
    @ApiOperation(value = "查询当前用户的借书记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bookId", value = "图书id", paramType = "query", required = false),
            @ApiImplicitParam(name = "title", value = "图书标题(模糊)", paramType = "query", required = false),
            @ApiImplicitParam(name = "author", value = "作者", required = false, paramType = "query"),
            @ApiImplicitParam(name = "cid", value = "种类ID", required = false, paramType = "query"),
            @ApiImplicitParam(name = "borrowState", value = "是否已归还", required = false, paramType = "query"),
            @ApiImplicitParam(name = "uploadPerson", value = "上传者", required = false, paramType = "query")
    })
    ListDTO qryBorrowLog(@ApiIgnore @RequestParam Map<String, Object> params) {
        return borrowLogServiceGateway.qryBorrowLog(params);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询所有用户的借书日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页面", required = true, paramType = "query"),
            @ApiImplicitParam(name = "size", value = "页面大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "图书标题(模糊)", paramType = "query", required = false),
            @ApiImplicitParam(name = "author", value = "作者", required = false, paramType = "query"),
            @ApiImplicitParam(name = "cid", value = "种类ID", required = false, paramType = "query"),
            @ApiImplicitParam(name = "borrowState", value = "是否已归还", required = false, paramType = "query"),
            @ApiImplicitParam(name = "uploadPerson", value = "上传者", required = false, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "借阅人", required = false, paramType = "query")
    })
    PageDTO<BorrowLogDetail> qryWholeLog(@ApiIgnore @RequestParam Map<String, Object> params) {
        return borrowLogServiceGateway.qryWholeLog(params);
    }

    /**
     * 根据当前登录账号，查询总借书量，近一个月借书量，未还书量，收藏夹图书
     * 驾驶舱页面显示
     * @param
     * @return com.example.graduate.dto.PersonalBorrowInfoDTO
     */
    @GetMapping("/count")
    @ApiOperation("查询当前用户借书信息统计")
    PersonalBorrowInfoDTO qryPersonalBorrowInfo() {
        return borrowLogServiceGateway.qryPersonalBorrowInfo();
    }
    /**
     *
     *
     * @param id 借书日志id
     * @return com.example.graduate.dto.BookDTO
     */
    @GetMapping("/detail")
    @ApiOperation("查询借书日志详情")
    @ApiImplicitParam(name = "id",value = "日志id",required = true,paramType = "query")
    BookDTO qryBorrowLogDetail(@RequestParam("id") int id){
        return borrowLogServiceGateway.qryBorrowLogDetail(id);
    }
}

package com.example.graduate.controller;

import com.example.graduate.annotation.OperationLogAnnotation;
import com.example.graduate.codeEnum.LogOperationType;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.Book;
import com.example.graduate.service.BookServiceGateway;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @author 倪鑫彦
 * @since 17:18 2020/11/11
 */
@RestController
@RequestMapping("/book")
@Api(value = "图书管理",tags = "BookController")
public class BookController {
    @Autowired
    private BookServiceGateway bookServiceGateway;

    @GetMapping("/page")
    @ApiOperation(value = "分页查询图书(String模糊查，int精准查)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页面",required = true,paramType = "query"),
            @ApiImplicitParam(name = "size",value = "页面大小",required = true,paramType = "query"),
            @ApiImplicitParam(name = "title",value = "标题",required = false,paramType = "query"),
            @ApiImplicitParam(name = "author",value = "作者",required = false,paramType = "query"),
            @ApiImplicitParam(name = "cid",value = "种类ID",required = false,paramType = "query"),
            @ApiImplicitParam(name = "examineState",value = "审核状态",required = false,paramType = "query"),
            @ApiImplicitParam(name = "availableState",value = "可用状态",required = false,paramType = "query"),
            @ApiImplicitParam(name = "examinePerson",value = "审核人",required = false,paramType = "query"),
            @ApiImplicitParam(name = "uploadPerson",value = "上传者",required = false,paramType = "query")
    })
    PageDTO<Book> qryBook(@ApiIgnore @RequestParam Map<String,Object> params){
        return bookServiceGateway.qryBook(params);
    }

    // TODO 待实现
    @DeleteMapping("/book")
    @ApiOperation(value = "删除图书")
    @OperationLogAnnotation(description = "删除图书",type = LogOperationType.DELETE)
    DTO delBookById(@RequestParam List<Integer> bids){
        return bookServiceGateway.delBookById(bids);
    }

    @PostMapping("/book")
    @ApiOperation(value = "新增图书(需要送审)")
    @OperationLogAnnotation(description = "新增图书",type = LogOperationType.ADD)
    DTO saveBook(@RequestBody Book book){
        return bookServiceGateway.saveBook(book);
    }

    // TODO 待实现
    @PutMapping("/examine")
    @ApiOperation(value = "审核图书(修改examinePerson，examineState，availableState，examineNote，updateDate字段)")
    @OperationLogAnnotation(description = "审批图书",type = LogOperationType.EXAMINE)
    DTO examineBook(@RequestBody Book book){
        return bookServiceGateway.examineBook(book);
    }

}

package com.example.graduate.controller;

import com.example.graduate.annotation.OperationLogAnnotation;
import com.example.graduate.codeEnum.LogOperationType;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.exception.NxyException;
import com.example.graduate.pojo.Book;
import com.example.graduate.service.BookServiceGateway;
import com.example.graduate.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
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


    @DeleteMapping("/book")
    @ApiOperation(value = "删除图书")
    @OperationLogAnnotation(description = "删除图书",type = LogOperationType.DELETE)
    DTO delBookById(@RequestParam List<Integer> bids) throws NxyException {
        return bookServiceGateway.delBookById(bids);
    }

    @PostMapping("/book")
    @ApiOperation(value = "新增图书(需要送审)")
    @OperationLogAnnotation(description = "新增图书",type = LogOperationType.ADD)
    DTO saveBook(@RequestBody Book book) throws NxyException {
        return bookServiceGateway.saveBook(book);
    }

    // TODO 待实现
    @PutMapping("/examine")
    @ApiOperation(value = "审核图书(修改examinePerson，examineState，availableState，examineNote，updateDate字段)")
    @OperationLogAnnotation(description = "审批图书",type = LogOperationType.EXAMINE)
    DTO examineBook(@RequestBody Book book){
        return bookServiceGateway.examineBook(book);
    }

    @PostMapping("/cover")
    @ApiOperation(value = "上传图片")
    DTO coversUpload(MultipartFile file){
        String folder = "D:/pic";
        File imageFolder = new File(folder);
        File f = new File(imageFolder, StringUtil.getRandomString(6) + file.getOriginalFilename()
                .substring(file.getOriginalFilename().length() - 4));
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        try {
            file.transferTo(f);
            String imgURL = "http://localhost:2048/api/file/" + f.getName();
            System.out.println(imgURL);
            return new DTO(RetCodeEnum.SUCCEED.getCode(),imgURL);
        } catch (IOException e) {
            e.printStackTrace();
            return new DTO(RetCodeEnum.FAIL);
        }
    }
}

package com.example.graduate.controller;

import com.example.graduate.annotation.OperationLogAnnotation;
import com.example.graduate.bean.RuleEngineRequest;
import com.example.graduate.codeEnum.LogOperationType;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.BookDTO;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.exception.NxyException;
import com.example.graduate.pojo.Book;
import com.example.graduate.service.BookServiceGateway;
import com.example.graduate.service.GroovyParserEngine;
import com.example.graduate.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 倪鑫彦
 * @since 17:18 2020/11/11
 */
@RestController
@RequestMapping("/book")
@Api(value = "图书管理", tags = "BookController")
public class BookController {
    @Autowired
    private BookServiceGateway bookServiceGateway;

    @Autowired
    private GroovyParserEngine groovyParserEngine;
    @GetMapping("/page")
    @ApiOperation(value = "分页查询图书(String模糊查，int精准查)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页面", required = true, paramType = "query"),
            @ApiImplicitParam(name = "size", value = "页面大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "标题", required = false, paramType = "query"),
            @ApiImplicitParam(name = "author", value = "作者", required = false, paramType = "query"),
            @ApiImplicitParam(name = "cid", value = "种类ID", required = false, paramType = "query"),
            @ApiImplicitParam(name = "examineState", value = "审核状态", required = false, paramType = "query"),
            @ApiImplicitParam(name = "availableState", value = "可用状态", required = false, paramType = "query"),
            @ApiImplicitParam(name = "examinePerson", value = "审核人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "uploadPerson", value = "上传者", required = false, paramType = "query")
    })
    PageDTO<Book> qryBook(@ApiIgnore @RequestParam Map<String, Object> params) {
        return bookServiceGateway.qryBook(params);
    }


    @GetMapping("/book")
    @ApiOperation(value = "精确查单个图书")
    @ApiImplicitParam(name = "id",value = "图书id",required = true,paramType = "query")
    BookDTO qryBookDetail(@RequestParam("id") int id){
        return bookServiceGateway.qryBookDetail(id);
    }
    @DeleteMapping("/book")
    @ApiOperation(value = "下架图书")
    @OperationLogAnnotation(description = "下架图书", type = LogOperationType.DELETE)
    DTO delBookById(@RequestParam List<Integer> bids) throws NxyException {
        return bookServiceGateway.delBookById(bids);
    }

    @PostMapping("/book")
    @ApiOperation(value = "新增图书(需要送审)")
    @OperationLogAnnotation(description = "新增图书", type = LogOperationType.ADD)
    DTO saveBook(@RequestBody Book book) throws NxyException {
        return bookServiceGateway.saveBook(book);
    }

    /**
     * @param book
     * @return com.example.graduate.dto.DTO
     */
    @PutMapping("/examine")
    @ApiOperation(value = "审核图书(修改examinePerson，examineState，availableState，examineNote，updateDate字段)")
    @OperationLogAnnotation(description = "审批图书", type = LogOperationType.EXAMINE)
    DTO examineBook(@RequestBody Book book) throws NxyException {
        return bookServiceGateway.examineBook(book);
    }

    @PostMapping("/cover")
    @ApiOperation(value = "上传图片")
    @OperationLogAnnotation(description = "上传图书",type = LogOperationType.ADD)
    DTO coversUpload(MultipartFile file) {
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
            return new DTO(RetCodeEnum.SUCCEED.getCode(), imgURL);
        } catch (IOException e) {
            e.printStackTrace();
            return new DTO(RetCodeEnum.FAIL);
        }
    }

    @PostMapping("/modify")
    @ApiOperation(value = "修改图书")
    DTO modifyBook(@RequestBody Book book) throws NxyException {
        return bookServiceGateway.modifyBook(book);
    }

    /**
     * 驾驶舱显示
     *
     * @param params
     * @return com.example.graduate.dto.ListDTO<com.example.graduate.pojo.Book>
     */
    @GetMapping("/suggest")
    @ApiOperation(value = "随机推荐图书")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", value = "类型", required = false, paramType = "query"),
            @ApiImplicitParam(name = "size", value = "数量", required = false, paramType = "query")
    })
    ListDTO<Book> suggestBook(@ApiIgnore @RequestParam Map<String, Object> params) {
        return bookServiceGateway.suggestBook(params);
    }
    @GetMapping("/favorite")
    @ApiOperation(value = "查询当前用户的收藏夹")
    ListDTO<Book> qryFavoriteBook(){
        return bookServiceGateway.qryFavoriteBook();
    }
    @PostMapping("/favorite")
    @ApiOperation(value = "收藏图书")
    DTO addFavoriteBook(@RequestBody Book book){
        return bookServiceGateway.addFavoriteBook(book);
    }

    @DeleteMapping("/favorite")
    @ApiOperation(value = "取消收藏")
    DTO removeFavoriteBook(@RequestBody Book book){
        return bookServiceGateway.removeFavoriteBook(book);
    }

    @GetMapping("/testEngine")
    DTO test(){
        RuleEngineRequest request = new RuleEngineRequest();
        request.setInterfaceId("BookFair");
        request.setParams(new HashMap<>());
        return groovyParserEngine.parse2DTO(request);
    }
}

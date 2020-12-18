package com.example.graduate.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.annotation.OperationLogAnnotation;
import com.example.graduate.bean.GroovyInfo;
import com.example.graduate.bean.RuleEngineRequest;
import com.example.graduate.cache.BeanNameCache;
import com.example.graduate.cache.GroovyInnerCache;
import com.example.graduate.cache.GroovyScriptCache;
import com.example.graduate.cache.RuleCache;
import com.example.graduate.codeEnum.LogOperationType;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.config.GroovyDynamicLoader;
import com.example.graduate.dto.BookDTO;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.exception.NxyException;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.CalculateRule;
import com.example.graduate.service.BookServiceGateway;
import com.example.graduate.service.CalculateRuleService;
import com.example.graduate.service.GroovyParserEngine;
import com.example.graduate.service.RuleServiceGateway;
import com.example.graduate.utils.GroovyScriptUtil;
import com.example.graduate.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
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
    @Resource
    private GroovyDynamicLoader groovyDynamicLoader;
    @Autowired
    private GroovyParserEngine groovyParserEngine;
    @Autowired
    private CalculateRuleService calculateRuleService;
    @Autowired
    private RuleServiceGateway ruleServiceGateway;
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
    DTO delBookById(@RequestBody Map<String,Object> map) throws NxyException {
        int bid = StringUtil.objectToInt(map.get("bid"));
        if(bid==-1){
            return new DTO(RetCodeEnum.FAIL.getCode(),"图书id错误");
        }
        return bookServiceGateway.delBookById(bid);
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
    @ApiOperation(value = "上传图片封面")
    @OperationLogAnnotation(description = "修改图书封面",type = LogOperationType.UPDATE)
    DTO coversUpload(MultipartFile file) {
        String folder = "D:/pic";
        File imageFolder = new File(folder);
        File f = new File(imageFolder, StringUtil.getRandomString(6) + file.getOriginalFilename()
                .substring(file.getOriginalFilename().length() - 4));
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        try {
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
            return new DTO(RetCodeEnum.FAIL);
        }
        String imgURL = "http://localhost:2048/api/file/" + f.getName();
        System.out.println(imgURL);
        return new DTO(RetCodeEnum.SUCCEED.getCode(), imgURL);
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
    @ApiIgnore
    DTO testEngine(@RequestParam int id){
        String interfaceId = calculateRuleService.getById(id).getInterfaceId();
        RuleEngineRequest request = new RuleEngineRequest();
        request.setInterfaceId(interfaceId);
        Map<String,Object> params = new HashMap<>();
        List<Integer> nums = new ArrayList<>();
        nums.add(10);
        nums.add(11);
        params.put("str1",111);
        params.put("str2",222);
        params.put("num",nums);
        params.put("rule",calculateRuleService.getById(id));
        request.setParams(params);
        return groovyParserEngine.parse2DTO(request);
    }
    @GetMapping("/refreshCache")
    @ApiIgnore
    DTO refreshCache(){
        groovyDynamicLoader.refresh();
        GroovyScriptCache.clear();
        RuleCache.clear();
        return new DTO(RetCodeEnum.SUCCEED);
    }
    @GetMapping("/removeRule")
    @ApiIgnore
    DTO test3(@RequestParam int id){
        LambdaQueryWrapper<CalculateRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalculateRule::getBeanName,"CalculateBookFair");
        ruleServiceGateway.deleteRule(calculateRuleService.getById(id));
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @GetMapping("/invokeRule")
    @ApiIgnore
    DTO invokeRule(@RequestParam int id) throws Exception {
        CalculateRule calculateRule = calculateRuleService.getById(id);
        HashMap<String, Object> params = new HashMap<>();
        params.put("str1","1111");
        params.put("str2","2222");
        GroovyScriptUtil.invoke(calculateRule,params);
        GroovyScriptCache.listCache();
        return new DTO(RetCodeEnum.SUCCEED);
    }


    @GetMapping("/removeCache")
    @ApiIgnore
    DTO removeCache(@RequestParam int id){
        CalculateRule calculateRule = calculateRuleService.getById(id);
        ruleServiceGateway.deleteRuleFromCache(calculateRule);
        return new DTO(RetCodeEnum.SUCCEED);
    }
    @GetMapping("/showAllCache")
    @ApiIgnore
    DTO showAllCache(){
        System.out.println("BeanNameCache");
        BeanNameCache.listMap();
        System.out.println("GroovyInnerCache");
        GroovyInnerCache.listMap();
        System.out.println("GroovyScriptCache");
        GroovyScriptCache.listCache();
        System.out.println("GroovyClassLoaderCache");
        RuleCache.listCache();
        return new DTO(RetCodeEnum.SUCCEED);
    }
    @GetMapping("/groovyClassloader")
    @ApiIgnore
    DTO testGroovyClassloader(@RequestParam int id) throws InstantiationException, IllegalAccessException {
        CalculateRule calculateRule = calculateRuleService.getById(id);
        HashMap<String, Object> params = new HashMap<>();
        params.put("str1","1111");
        params.put("str2","2222");
        return GroovyScriptUtil.invokeByGroovyClassLoader(calculateRule,params);
    }
}

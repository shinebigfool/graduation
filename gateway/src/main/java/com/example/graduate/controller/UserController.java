package com.example.graduate.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.dto.UserDTO;
import com.example.graduate.mapstruct.UserConverter;
import com.example.graduate.pojo.User;
import com.example.graduate.service.UserService;
import com.example.graduate.utils.StringUtil;
import com.example.graduate.service.UserServiceGateway;
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
 * @description
 * @since 10:21 2020/11/6
 */
@RestController
@RequestMapping("user")
@Api(value = "用户管理",tags = "UserController")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserServiceGateway userServiceGateway;
    @ApiOperation(value = "测试Swagger")
    @GetMapping("/test")
    public Integer test(){
        return 1024;
    }

    @GetMapping("/findUser/{name}")
    @ApiOperation(value = "查询用户详细信息")
    @ApiImplicitParam(name = "name",value = "用户账号",required = true,paramType = "path")
    UserDTO qryUserByName(@PathVariable("name")String name){
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(User::getName,name);
        User one = userService.getOne(userQueryWrapper);
        if(one!=null){
            UserDTO userDTO = UserConverter.INSTANCE.domain2dto(one);
            userDTO.setResult(RetCodeEnum.SUCCEED);
            return userDTO;
        }
        return new UserDTO(RetCodeEnum.RESULT_EMPTY);
    }
    @GetMapping("/page")
    @ApiOperation(value = "用户信息分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "用户账号",required = false,paramType = "query"),
            @ApiImplicitParam(name = "current",value = "当前页面",required = true,paramType = "query"),
            @ApiImplicitParam(name = "size",value = "页面大小",required = true,paramType = "query")
    })
    PageDTO<UserDTO> qryUsersByPage(@ApiIgnore @RequestParam Map<String,Object> params){
        PageDTO<UserDTO> pageDTO = new PageDTO<>(RetCodeEnum.SUCCEED);
        int current = StringUtil.objectToInt(params.get("current"));
        int size = StringUtil.objectToInt(params.get("size"));
        pageDTO.setCurPage(current);
        pageDTO.setPageSize(size);
        int totalRow = userService.qryTotalRow(params);
        int totalPage = totalRow % size == 0 ? totalRow/size:totalRow/size+1;
        pageDTO.setTotalRow(totalRow);
        pageDTO.setTotalPage(totalPage);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        Page<User> page = new Page<>(current,size);
        if(params.get("name")!=null){
            userQueryWrapper.lambda().eq(User::getName,params.get("name"));
        }
        userService.page(page,userQueryWrapper);
        List<User> userList = page.getRecords();
        List<UserDTO> userDTOS = UserConverter.INSTANCE.domain2dto(userList);
        pageDTO.setRetList(userDTOS);
        return pageDTO;
    }
    @PutMapping("/userInfo")
    @ApiOperation(value = "修改用户信息")
    DTO modUser(@RequestBody UserDTO u){
        if(StringUtil.isBlank(u.getName())){
            return new DTO(RetCodeEnum.PARAM_ERROR);
        }
        return null;
    }

}

package com.example.graduate.controller;

import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.*;
import com.example.graduate.pojo.AdminRole;
import com.example.graduate.service.UserService;
import com.example.graduate.service.UserServiceGateway;
import com.example.graduate.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * @author 倪鑫彦
 * @description
 * @since 10:21 2020/11/6
 */
@RestController
@RequestMapping("user")
@Api(value = "用户管理", tags = "UserController")
public class UserController {
    @Autowired
    private UserServiceGateway userServiceGateway;

    @ApiOperation(value = "测试Swagger")
    @GetMapping("/test")
    @RequiresRoles(value = {"super", "sysAdmin", "operations"}, logical = Logical.OR)
    public Integer test() {
        return 1024;
    }

    @GetMapping("/findUser/{name}")
    @ApiOperation(value = "查询用户详细信息")
    @ApiImplicitParam(name = "name", value = "用户账号", required = true, paramType = "path")
    UserDTO qryUserByName(@PathVariable("name") String name) {
        if (StringUtil.isBlank(name)) {
            return new UserDTO(RetCodeEnum.PARAM_ERROR);
        }
        return userServiceGateway.qryUserByName(name);

    }

    @GetMapping("/page")
    @ApiOperation(value = "用户信息分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户账号", required = false, paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面", required = true, paramType = "query"),
            @ApiImplicitParam(name = "size", value = "页面大小", required = true, paramType = "query")
    })
    PageDTO<UserDTO> qryUsersByPage(@ApiIgnore @RequestParam Map<String, Object> params) {

        return userServiceGateway.qryUsersByPage(params);
    }

    @PutMapping("/userInfo")
    @ApiOperation(value = "修改用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户账号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "用户密码", required = false, paramType = "query"),
            @ApiImplicitParam(name = "uname", value = "用户名", required = false, paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "电话号码", required = false, paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱地址", required = false, paramType = "query"),
            @ApiImplicitParam(name = "roles", value = "拥有角色", required = false, paramType = "query")
    })
    DTO modUser(@ApiIgnore @RequestBody UserDTO u) {

        if (StringUtil.isBlank(u.getName())) {
            return new DTO(RetCodeEnum.PARAM_ERROR.getCode(), "非法账号");
        }
        return userServiceGateway.modUser(u);
    }

    @PostMapping("/login")
    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "account", dataType = "String", required = true, value = "用户账号"),
            @ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "用户密码"),
            @ApiImplicitParam(paramType = "query", name = "isRememberMe", dataType = "boolean", required = true, value = "是否记住我")
    })
    DTO login(@NotBlank(message = "用户名不能为空") @RequestParam("account") String account,
              @NotBlank(message = "密码不能为空") @RequestParam("password") String password,
              @NotBlank(message = "配置不能为空") @RequestParam("isRememberMe") boolean isRememberMe) {
        return userServiceGateway.login(account, password, isRememberMe);

    }

    @PostMapping("/regist")
    @ApiOperation(value = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", dataType = "String", required = true, value = "用户账号"),
            @ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "用户密码"),
            @ApiImplicitParam(paramType = "query",name="uname",dataType = "String",required = false,value = "昵称"),
            @ApiImplicitParam(paramType = "query",name = "phone",required = false,value = "电话号码"),
            @ApiImplicitParam(paramType = "query",name = "email",required = false,value = "邮箱"),
            @ApiImplicitParam(paramType = "query",name = "photoUrl",required = true,value = "头像地址"),
            @ApiImplicitParam(paramType = "query",name = "roles",required = false,value = "角色列表"),
            @ApiImplicitParam(paramType = "query",name = "sex",required = false,value = "性别")
    })
    DTO regist(@RequestBody @ApiIgnore UserDTO userDTO) {
        return userServiceGateway.regist(userDTO);
    }
    /**
     * 用户首次登录时触发
     *
     * @param
     * @return com.example.graduate.dto.RoleDTO
     */
    @GetMapping("/role")
    @ApiOperation(value = "查询当前登录用户的信息")
    RoleDTO qryPresentUserRoles() {
        return userServiceGateway.qryPresentUserRoles();
    }
    /**
     * 清除后台session
     *
     * @param
     * @return com.example.graduate.dto.DTO
     */
    @GetMapping("/logout")
    @ApiOperation(value = "登出")
    DTO logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new DTO(RetCodeEnum.SUCCEED);
    }
    /**
     *
     *
     * @param params json格式
     * @return com.example.graduate.dto.DTO
     */
    @DeleteMapping("/user")
    @ApiOperation(value = "删除用户")
    @ApiImplicitParam(paramType = "query",name = "ids",required = true)
    DTO deleteUserByIds(@RequestBody Map<String,Object> params){
        int id = StringUtil.objectToInt(params.get("id"));
        return userServiceGateway.deleteUserByIds(id);
    }
    /**
     *
     *
     * @param id 只支持操作单个用户
     * @return com.example.graduate.dto.DTO
     */
    @PutMapping("/blackList")
    @ApiOperation(value = "拉黑")
    @ApiImplicitParam(paramType = "query",name = "id",required = true)
    DTO blackList(@RequestParam int id){
        return userServiceGateway.blackList(id);
    }

    @GetMapping("/roleList")
    @ApiOperation(value = "获取角色列表")
    ListDTO<AdminRole> listRole(){
        return userServiceGateway.listRole();
    }
}

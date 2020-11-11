package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.dto.UserDTO;
import com.example.graduate.mapstruct.UserConverter;
import com.example.graduate.pojo.AdminRole;
import com.example.graduate.pojo.AdminUserRole;
import com.example.graduate.pojo.User;
import com.example.graduate.service.AdminUserRoleService;
import com.example.graduate.service.UserService;
import com.example.graduate.service.UserServiceGateway;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceGatewayImpl implements UserServiceGateway {
    @Autowired
    private UserService userService;

    @Autowired
    private AdminUserRoleService adminUserRoleService;
    public UserDTO qryUserByName(String name){
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

    public PageDTO<UserDTO> qryUsersByPage(Map<String,Object> params){
        PageDTO<UserDTO> pageDTO = new PageDTO<>(RetCodeEnum.SUCCEED);
        int totalRow = userService.qryTotalRow(params);
        params.put("totalRow",totalRow);
        int size = PageUtil.transParam2Page(params,pageDTO);
        int current = StringUtil.objectToInt(params.get("current"));
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        Page<User> page = new Page<>(current,size);
        if(params.get("name")!=null){
            userQueryWrapper.lambda().eq(User::getName,params.get("name"));
        }
        userService.page(page,userQueryWrapper);
        List<User> userList = page.getRecords();
        if(userList.size()<=0){
            pageDTO.setResult(RetCodeEnum.RESULT_EMPTY);
        }
        List<UserDTO> userDTOS = UserConverter.INSTANCE.domain2dto(userList);
        pageDTO.setRetList(userDTOS);
        return pageDTO;
    }

    @Override
    public DTO modUser(UserDTO u) {
        User user = UserConverter.INSTANCE.dto2domain(u);
        userService.save(user);
        List<AdminRole> roles = u.getRoles();
        List<Integer> rid = roles.stream().map(AdminRole::getId).collect(Collectors.toList());
        LambdaQueryWrapper<AdminUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserRole::getUid,u.getId());
        List<Integer> ids = adminUserRoleService.list(wrapper).stream().map(AdminUserRole::getId).collect(Collectors.toList());
        adminUserRoleService.removeByIds(ids);

        return null;
    }

    @Override
    public DTO login(String account, String password, boolean isRememberMe) {
        User userInDB = qryUserInDBByName(account);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(account,password);
        token.setRememberMe(isRememberMe);
        try {
            subject.login(token);
            System.out.println(SecurityUtils.getSubject().getPrincipal().toString());
            System.out.println("login in success");
            userInDB.setLoginTime(new Date());
            userService.updateById(userInDB);
        }catch (UnknownAccountException e1){
            return new DTO(RetCodeEnum.AUDIT_OBJECT_DELETED);
        }catch (AuthenticationException e1){
            return new DTO(RetCodeEnum.PSW_WRONG);
        }catch (Exception e){
            return new DTO(RetCodeEnum.EXCEPTION.getCode(),e.getMessage());
        }
        return new DTO(RetCodeEnum.SUCCEED.getCode(),"登录成功");
    }

    @Override
    public DTO regist(UserDTO userDTO) {
        String name = userDTO.getName();
        String password = userDTO.getPassword();
        UserDTO userInDB = qryUserByName(name);
        if(!userInDB.getRetCode().equals("900001")){
            return new DTO(RetCodeEnum.FAIL.getCode(),"该用户名已被注册");
        }
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String encodePassword = new SimpleHash("md5",password,salt,times).toString();
        User user = UserConverter.INSTANCE.dto2domain(userDTO);
        user.setPassword(encodePassword);
        user.setSalt(salt);
        user.setRegistTime(new Date());

        userService.save(user);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public User qryUserInDBByName(String name) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(User::getName,name);
        User one = userService.getOne(userQueryWrapper);
        return one;
    }
}

package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.*;
import com.example.graduate.mapstruct.UserConverter;
import com.example.graduate.pojo.AdminRole;
import com.example.graduate.pojo.AdminUserRole;
import com.example.graduate.pojo.User;
import com.example.graduate.service.AdminRoleService;
import com.example.graduate.service.AdminUserRoleService;
import com.example.graduate.service.UserService;
import com.example.graduate.service.UserServiceGateway;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.PresentUserUtils;
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
    @Autowired
    private AdminRoleService adminRoleService;

    public UserDTO qryUserByName(String name) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(User::getName, name);
        User one = userService.getOne(userQueryWrapper);
        if (one != null) {
            UserDTO userDTO = UserConverter.INSTANCE.domain2dto(one);
            userDTO.setResult(RetCodeEnum.SUCCEED);
            return userDTO;
        }
        return new UserDTO(RetCodeEnum.RESULT_EMPTY);
    }

    public PageDTO<UserDTO> qryUsersByPage(Map<String, Object> params) {
        PageDTO<UserDTO> pageDTO = new PageDTO<>(RetCodeEnum.SUCCEED);
        int totalRow = userService.qryTotalRow(params);
        params.put("totalRow", totalRow);
        int size = PageUtil.transParam2Page(params, pageDTO);
        int current = StringUtil.objectToInt(params.get("current"));
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        Page<User> page = new Page<>(current, size);
        if (params.get("name") != null) {
            userQueryWrapper.lambda().eq(User::getName, params.get("name"));
        }
        userService.page(page, userQueryWrapper);
        List<User> userList = page.getRecords();
        if (userList.size() <= 0) {
            pageDTO.setResult(RetCodeEnum.RESULT_EMPTY);
        }
        List<UserDTO> userDTOS = UserConverter.INSTANCE.domain2dto(userList);
        pageDTO.setRetList(userDTOS);
        return pageDTO;
    }

    // TODO 修改用户信息有问题
    @Override
    public DTO modUser(UserDTO u) {
        User user = UserConverter.INSTANCE.dto2domain(u);
        userService.save(user);

        LambdaQueryWrapper<AdminUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserRole::getUid, u.getId());
        List<Integer> ids = adminUserRoleService.list(wrapper).stream().map(AdminUserRole::getId).collect(Collectors.toList());
        adminUserRoleService.removeByIds(ids);

        return null;
    }

    @Override
    public DTO login(String account, String password, boolean isRememberMe) {
        User userInDB = qryUserInDBByName(account);
        if (!userInDB.isEnabled()) {
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "您的账号已被封禁，请联系管理员");
        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(account, password);
        token.setRememberMe(isRememberMe);
        try {
            subject.login(token);
            System.out.println(SecurityUtils.getSubject().getPrincipal().toString());
            System.out.println("login in success");
            userInDB.setLoginTime(new Date());
            userService.updateById(userInDB);
        } catch (UnknownAccountException e1) {
            return new DTO(RetCodeEnum.AUDIT_OBJECT_DELETED);
        } catch (AuthenticationException e1) {
            return new DTO(RetCodeEnum.PSW_WRONG);
        } catch (Exception e) {
            return new DTO(RetCodeEnum.EXCEPTION.getCode(), e.getMessage());
        }
        return new DTO(RetCodeEnum.SUCCEED.getCode(), "登录成功");
    }

    @Override
    public DTO regist(UserDTO userDTO) {
        String name = userDTO.getName();
        if (StringUtil.isBlank(name)) {
            return new DTO(RetCodeEnum.PARAM_ERROR.getCode(), "非法账号字段，请重新注册");
        }
        String password = userDTO.getPassword();
        UserDTO userInDB = qryUserByName(name);
        if (!userInDB.getRetCode().equals("900001")) {
            return new DTO(RetCodeEnum.FAIL.getCode(), "该用户名已被注册");
        }
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String encodePassword = new SimpleHash("md5", password, salt, times).toString();
        User user = UserConverter.INSTANCE.dto2domain(userDTO);
        user.setPassword(encodePassword);
        user.setSalt(salt);
        user.setEnabled(true);
        List<Integer> roles = userDTO.getRoles();

        if (roles.contains(1) || roles.contains(2)) {
            //check permission
            String presentUser = PresentUserUtils.qryPresentUserAccount();
            if (StringUtil.isBlank(presentUser)) {
                return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "无权限，请先登录");
            }
            if (!PresentUserUtils.checkAdminRole()) {
                return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "您无权进行此操作");
            }
        }
        if (roles.size() == 0) {
            roles.add(3);
        }
        userService.save(user);
        for (Integer role : roles) {
            AdminUserRole temp = new AdminUserRole();
            temp.setRid(role);
            temp.setUid(user.getId());
            adminUserRoleService.save(temp);
        }

        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public User qryUserInDBByName(String name) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(User::getName, name);
        User one = userService.getOne(userQueryWrapper);
        return one;
    }

    @Override
    public RoleDTO qryPresentUserRoles() {
        SecurityUtils.getSubject();
        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            return new RoleDTO(RetCodeEnum.FAIL.getCode(), "请先登录");
        }
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getName, name);
        User one = userService.getOne(userWrapper);
        LambdaQueryWrapper<AdminUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserRole::getUid, one.getId());
        List<AdminUserRole> relations = adminUserRoleService.list(wrapper);
        List<Integer> rids = relations.stream().map(AdminUserRole::getRid).collect(Collectors.toList());
        if (rids.size() == 0) {
            return new RoleDTO(RetCodeEnum.SUCCEED);
        }
        List<AdminRole> roles = adminRoleService.listByIds(rids);
        List<String> roleNames = roles.stream().map(AdminRole::getName).collect(Collectors.toList());
        RoleDTO roleDTO = new RoleDTO(RetCodeEnum.SUCCEED);
        //设置头像url
        roleDTO.setAvatar(one.getPhotoUrl());
        //设置用户名
        roleDTO.setName(one.getUname());
        //角色列表
        roleDTO.setRoles(roleNames);
        return roleDTO;
    }

    @Override
    public DTO deleteUserByIds(int id) {
        //先删角色用户映射表
        LambdaQueryWrapper<AdminUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserRole::getUid, id);
        adminUserRoleService.removeByIds(adminUserRoleService.list(wrapper).
                stream().map(AdminUserRole::getId).collect(Collectors.toList()));
        //后删用户表
        userService.removeById(id);

        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public DTO blackList(int id) {
        if (!PresentUserUtils.checkAdminRole()) {
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "你无权执行此操作");
        }
        User inDB = userService.getById(id);
        inDB.setEnabled(!inDB.isEnabled());
        userService.updateById(inDB);
        return new DTO(RetCodeEnum.SUCCEED);
    }
}

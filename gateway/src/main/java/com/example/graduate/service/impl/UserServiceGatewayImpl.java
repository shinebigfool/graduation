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
import com.example.graduate.service.*;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceGatewayImpl implements UserServiceGateway {
    @Autowired
    private UserService userService;

    @Autowired
    private AdminUserRoleService adminUserRoleService;
    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    private SchoolClassService schoolClassService;
    Calendar now = Calendar.getInstance();
    Calendar birthday = Calendar.getInstance();

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

        for (UserDTO userDTO : userDTOS) {
            //计算用户年龄
            now.setTime(new Date());
            birthday.setTime(userDTO.getBirthday());
            userDTO.setAge(now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR));
            //获取用户班级信息
            if(schoolClassService.getById(userDTO.getClassId())!=null){
                userDTO.setClassName(schoolClassService.getById(userDTO.getClassId()).getClassName());
            }
            // 获取用户角色信息
            LambdaQueryWrapper<AdminUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(AdminUserRole::getUid, userDTO.getId());
            List<Integer> rids = adminUserRoleService.list(userRoleWrapper).stream().
                    map(AdminUserRole::getRid).collect(Collectors.toList());
            //设置用户主身份
            userDTO.setMainRole(calMainRole(rids));
            if (rids.size() == 0) {
                continue;
            }
            List<AdminRole> roles = adminRoleService.listByIds(rids);

            userDTO.setAdminRoles(roles);
            userDTO.setRoles(rids);

        }
        pageDTO.setRetList(userDTOS);
        return pageDTO;
    }


    @Override
    public DTO modUser(UserDTO u) {
        //check permission
        if (!PresentUserUtils.hasAdminRole() &&
                !PresentUserUtils.qryPresentUserAccount().equals(u.getName())) {
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "您无权执行此操作");
        }
        LambdaQueryWrapper<AdminUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserRole::getUid, u.getId());
        List<Integer> roles = u.getRoles();
        if (roles.contains(1) || roles.contains(2)) {
            //check permission
            String presentUser = PresentUserUtils.qryPresentUserAccount();
            if (StringUtil.isBlank(presentUser)) {
                return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "无权限，请先登录");
            }
            if (!PresentUserUtils.hasAdminRole()) {
                return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "您无权进行此操作");
            }
        }
        if (roles.size() == 0) {
            roles.add(3);
        }


        List<Integer> ids = adminUserRoleService.list(wrapper).stream().map(AdminUserRole::getId).collect(Collectors.toList());
        adminUserRoleService.removeByIds(ids);
        List<AdminUserRole> userRoles = new ArrayList<>();
        for (Integer role : u.getRoles()) {
            AdminUserRole adminUserRole = new AdminUserRole();
            adminUserRole.setUid(u.getId());
            adminUserRole.setRid(role);
            userRoles.add(adminUserRole);
        }
        adminUserRoleService.saveBatch(userRoles);
        User user = UserConverter.INSTANCE.dto2domain(u);
        userService.updateById(user);

        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public DTO login(String account, String password, boolean isRememberMe) {
        User userInDB = qryUserInDBByName(account);
        if(userInDB==null){
            return new DTO(RetCodeEnum.FAIL.getCode(),"无此账户！");
        }
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
            if (!PresentUserUtils.hasAdminRole()) {
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
        roleDTO.setMainRole(calMainRole(rids));
        return roleDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, timeout = 36000, rollbackFor = Exception.class)
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
        if (!PresentUserUtils.hasAdminRole()) {
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "你无权执行此操作");
        }
        User inDB = userService.getById(id);
        inDB.setEnabled(!inDB.isEnabled());
        userService.updateById(inDB);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public ListDTO<AdminRole> listRole() {
        List<AdminRole> roles = adminRoleService.list();
        ListDTO<AdminRole> dto = new ListDTO<>(RetCodeEnum.SUCCEED);
        dto.setRetList(roles);
        return dto;
    }

    @Override
    public int calMainRole(List<Integer> rids) {
        if (rids.size() == 0) {
            return 4;
        }
        if (rids.contains(1) || rids.contains(2) || rids.contains(10)) {
            return 2;
        } else if (rids.contains(9)) {
            return 1;
        } else if (rids.contains(11)) {
            return 3;
        } else {
            return 4;
        }
    }

    @Override
    public PageDTO<UserDTO> qryUsersPage(Map<String, Object> params) {
        params.put("cid",StringUtil.objectToInt(params.get("cid")));
        int totalRow = userService.qryTotal(params);
        PageDTO<UserDTO> pageDTO= new PageDTO<>(RetCodeEnum.SUCCEED);
        params.put("totalRow", totalRow);
        int size = PageUtil.transParam2Page(params, pageDTO);
        int current = StringUtil.objectToInt(params.get("current"));
        Page<User> page = new Page<>(current,size);
        String name = StringUtil.parseString(params.get("name"));
        String uname = StringUtil.parseString(params.get("uname"));
        int cid = StringUtil.objectToInt(params.get("cid"));
        List<User> users = userService.qryUserPage(page, name, cid, uname);
        List<UserDTO> userDTOS = UserConverter.INSTANCE.domain2dto(users);
        for (UserDTO userDTO : userDTOS) {
            //计算用户年龄
            now.setTime(new Date());
            birthday.setTime(userDTO.getBirthday());
            userDTO.setAge(now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR));
            //获取用户班级信息
            if(schoolClassService.getById(userDTO.getClassId())!=null){
                userDTO.setClassName(schoolClassService.getById(userDTO.getClassId()).getClassName());
            }

            // 获取用户角色信息
            LambdaQueryWrapper<AdminUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(AdminUserRole::getUid, userDTO.getId());
            List<Integer> rids = adminUserRoleService.list(userRoleWrapper).stream().
                    map(AdminUserRole::getRid).collect(Collectors.toList());
            //设置用户主身份
            userDTO.setMainRole(calMainRole(rids));
            if (rids.size() == 0) {
                continue;
            }
            List<AdminRole> roles = adminRoleService.listByIds(rids);

            userDTO.setAdminRoles(roles);
            userDTO.setRoles(rids);
        }
        pageDTO.setRetList(userDTOS);
        return pageDTO;
    }

    @Override
    public boolean isStudent(int uid) {
        int mainRole = calMainRole(qryUserRoles(uid));
        return mainRole==1;
    }

    @Override
    public List<Integer> qryUserRoles(int uid) {
        LambdaQueryWrapper<AdminUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(AdminUserRole::getUid, uid);
        List<Integer> rids = adminUserRoleService.list(userRoleWrapper).stream().
                map(AdminUserRole::getRid).collect(Collectors.toList());

        return rids;
    }

    @Override
    public void changeStuClass(int uid, int cid) {
        User inDB = userService.getById(uid);
        inDB.setClassId(cid);
        userService.updateById(inDB);
    }
}

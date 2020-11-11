package com.example.graduate.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.pojo.AdminRole;
import com.example.graduate.pojo.AdminUserRole;
import com.example.graduate.pojo.User;
import com.example.graduate.service.AdminRoleService;
import com.example.graduate.service.AdminUserRoleService;
import com.example.graduate.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminUserRoleService adminUserRoleService;

    @Autowired
    private AdminRoleService adminRoleService;


    /**
     *
     * 授权
     * @param principals
     * @return org.apache.shiro.authz.AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("shiro 授权管理");

        //查询权限注入shiro
        // 授权管理对象
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 查询出用户
        String name = principals.getPrimaryPrincipal().toString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName,name);
        User user = userService.getOne(wrapper);
        int uid = user.getId();
        if (user.getName().equals("bigfool")) {
            // 留后门，超管账号
            info.addRole("super");
            info.addStringPermission("super");
        } else {
            // 查询用户-->角色
            // info.addRoles((Collection) roles);
            LambdaQueryWrapper<AdminUserRole> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.eq(AdminUserRole::getUid,uid);
            List<AdminUserRole> userRoleList = adminUserRoleService.list(roleWrapper);
            List<Integer> roleIdList = userRoleList.stream().map(AdminUserRole::getRid).collect(Collectors.toList());
            List<String> roles = adminRoleService.listByIds(roleIdList).stream().map(AdminRole::getName).collect(Collectors.toList());
            info.addRoles(roles);
            System.out.println("shiroRealm"+name+"拥有角色："+roles);
        }

        return info;
    }

    /**
     *
     * 认证
     * @param token 与登录时对应
     * @return org.apache.shiro.authc.AuthenticationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("shiro 认证管理");
        // 获取用户登录信息
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String name = token.getPrincipal().toString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName,name);
        User user = userService.getOne(wrapper);

        if (user == null) {
            // 用户名不存在 抛出指定异常,跳转到登录页面
            return null;
        } else {
            String passwordInDB = user.getPassword();
            String salt = user.getSalt();
            return new SimpleAuthenticationInfo(name,passwordInDB,ByteSource.Util.bytes(salt),this.getName());
        }
    }

}

package com.example.graduate.service;

import com.example.graduate.dto.*;
import com.example.graduate.pojo.AdminRole;
import com.example.graduate.pojo.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface UserServiceGateway {
    UserDTO qryUserByName(String name);
    PageDTO<UserDTO> qryUsersByPage(Map<String,Object> params);
    DTO modUser(UserDTO u);
    DTO login(String account, String password,boolean isRememberMe);
    DTO regist(UserDTO userDTO);
    User qryUserInDBByName(String name);
    RoleDTO qryPresentUserRoles();
    DTO deleteUserByIds(int id);
    DTO blackList(int id);
    ListDTO<AdminRole> listRole();
}

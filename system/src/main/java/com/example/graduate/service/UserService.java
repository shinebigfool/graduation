package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.dto.UserDTO;
import com.example.graduate.pojo.User;


import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
    List<UserDTO> qryUsersByPage(Page<UserDTO> page, String name);
    int qryTotalRow(Map<String,Object> params);
}

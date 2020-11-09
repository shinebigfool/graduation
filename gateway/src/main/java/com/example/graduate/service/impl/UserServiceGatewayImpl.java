package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.UserDTO;
import com.example.graduate.mapstruct.UserConverter;
import com.example.graduate.pojo.User;
import com.example.graduate.service.UserService;
import com.example.graduate.service.UserServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceGatewayImpl implements UserServiceGateway {
    @Autowired
    private UserService userService;
    UserDTO qryUserByName(String name){
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

}

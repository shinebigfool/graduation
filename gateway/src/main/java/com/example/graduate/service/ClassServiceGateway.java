package com.example.graduate.service;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.SchoolClassDTO;
import com.example.graduate.dto.UserDTO;
import com.example.graduate.pojo.SchoolClass;
import com.example.graduate.pojo.User;

import java.util.Map;

public interface ClassServiceGateway {
    ListDTO<SchoolClassDTO> qryClass(Map<String,Object> params);
    DTO addClass(SchoolClass schoolClass);
    DTO modClass(SchoolClass schoolClass);
    DTO removeClass(Map<String,Object> params);
    DTO addUser(Map<String,Object> params);
    DTO removeUser(Map<String,Object> params);
    ListDTO<User> qryStudents(int cid);
    DTO addStu(Map<String,Object> params);
    DTO removeStu(Map<String,Object> params);
    int countUser(int cid);
    void removeUserFromAllClass(int uid);
}

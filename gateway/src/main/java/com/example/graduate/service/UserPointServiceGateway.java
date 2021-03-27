package com.example.graduate.service;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ObjectDTO;
import com.example.graduate.pojo.UserPoint;

public interface UserPointServiceGateway {
    DTO addUserPoint(UserPoint userPoint);
    DTO modUserPoint(UserPoint userPoint,String note);
    DTO del(int id);
    UserPoint getByName(String name);
    ObjectDTO getPoint();
}

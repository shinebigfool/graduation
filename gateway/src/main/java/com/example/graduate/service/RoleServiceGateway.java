package com.example.graduate.service;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.pojo.AdminMenu;
import com.example.graduate.pojo.AdminRole;

import java.util.List;
import java.util.Map;

public interface RoleServiceGateway {
    ListDTO listRoles();
    List<AdminMenu> getMenusByRoleId(int id);
    DTO updateRoleMenu(int rid, Map<String,List<Integer>> menusIds) throws Exception;
}

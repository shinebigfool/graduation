package com.example.graduate.controller;

import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.pojo.AdminRole;
import com.example.graduate.service.RoleServiceGateway;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 倪鑫彦
 * @since 16:12 2020/11/11
 */
@RestController
@RequestMapping("role")
@Api(value = "角色管理",tags = "RoleController")
public class RoleController {
    @Autowired
    RoleServiceGateway roleServiceGateway;
    @GetMapping("/list")
    @ApiOperation(value = "角色列表")
    ListDTO listRoles(){
        return roleServiceGateway.listRoles();
    }

    @PutMapping("rolemenu")
    @ApiOperation(value = "更新角色——菜单关系")
    DTO updateRoleMenu(@RequestParam int rid,@RequestBody Map<String, List<Integer>> menusIds){
        try {
            return roleServiceGateway.updateRoleMenu(rid,menusIds);
        } catch (Exception e) {
            return new DTO(RetCodeEnum.EXCEPTION.getCode(),e.getMessage());
        }
    }
}

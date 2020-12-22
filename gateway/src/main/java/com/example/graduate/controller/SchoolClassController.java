package com.example.graduate.controller;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.SchoolClassDTO;
import com.example.graduate.pojo.SchoolClass;
import com.example.graduate.pojo.User;
import com.example.graduate.service.ClassServiceGateway;
import com.example.graduate.service.SchoolClassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/class")
@Api(value = "班级管理",tags = "SchoolClassController")
public class SchoolClassController {
    @Autowired
    private ClassServiceGateway classServiceGateway;
    @Autowired
    private SchoolClassService schoolClassService;
    @GetMapping("/class")
    @ApiOperation(value = "班级查询")
    ListDTO<SchoolClassDTO> qryClass(@ApiIgnore @RequestParam Map<String,Object> params){
        return classServiceGateway.qryClass(params);
    }
    @PostMapping("/class")
    @ApiOperation(value = "新增班级")
    DTO addClass(@RequestBody SchoolClass schoolClass){
        return classServiceGateway.addClass(schoolClass);
    }
    @PutMapping("/class")
    @ApiOperation(value = "修改班级信息")
    DTO modClass(@RequestBody SchoolClass schoolClass){
        return classServiceGateway.modClass(schoolClass);
    }
    @DeleteMapping("/class")
    @ApiOperation(value = "删除班级")
    DTO removeClass(@RequestBody Map<String,Object> params){
        return classServiceGateway.removeClass(params);
    }
    @PostMapping("/user")
    @ApiOperation(value = "将用户录入班级(映射表)")
    DTO addUser(@ApiIgnore @RequestBody Map<String,Object> params){
        return classServiceGateway.addUser(params);
    }
    @DeleteMapping("/user")
    @ApiOperation(value = "将用户踢出班级(映射表)")
    DTO removeUser(@ApiIgnore @RequestBody Map<String,Object> params){
        return classServiceGateway.removeUser(params);
    }
    @PutMapping("/student")
    @ApiOperation(value = "将用户录入班级(修改用户信息)")
    DTO addStu(@ApiIgnore @RequestBody Map<String,Object> params){
        return classServiceGateway.addStu(params);
    }
    @DeleteMapping("/student")
    @ApiOperation(value = "将用户踢出班级(修改用户信息)")
    DTO removeStu(@ApiIgnore @RequestBody Map<String,Object> params){
        return classServiceGateway.removeStu(params);
    }

    //以班级筛选用户(学生)
    @GetMapping("/student")
    @ApiOperation(value = "查找某一班级的学生")
    ListDTO<User> qryStudents(@RequestParam int cid){
        return classServiceGateway.qryStudents(cid);
    }
    //以班级为单位，以年级为单位计算总借书量，活跃人数
    @GetMapping("/readingAmountByClass")
    @ApiOperation(value = "按班级统计阅读量")
    List<Map<String,Object>> qryReadingAmountByClass(){
        return schoolClassService.qryReadingAmountByClass();
    }
    @GetMapping("/readingAmountByGrade")
    @ApiOperation(value = "按年级统计阅读量")
    List<Map<String,Object>> qryReadingAmountByGrade(){
        return schoolClassService.qryReadingAmountByGrade();
    }
    @GetMapping("/readingStuByClass")
    @ApiOperation(value = "按班级统计活跃人数")
    List<Map<String,Object>> qryReadingStuByClass(){
        return schoolClassService.qryReadingStuByClass();
    }
    @GetMapping("/readingStuByGrade")
    List<Map<String,Object>> qryReadingStuByGrade(){
        return schoolClassService.qryReadingStuByGrade();
    }
}

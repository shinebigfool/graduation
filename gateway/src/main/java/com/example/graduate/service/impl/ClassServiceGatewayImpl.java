package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.SchoolClassDTO;
import com.example.graduate.mapstruct.ClassConverter;
import com.example.graduate.pojo.AdminUserRole;
import com.example.graduate.pojo.SchoolClass;
import com.example.graduate.pojo.User;
import com.example.graduate.pojo.UserClass;
import com.example.graduate.service.*;
import com.example.graduate.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClassServiceGatewayImpl implements ClassServiceGateway {
    @Autowired
    private SchoolClassService schoolClassService;
    @Autowired
    private UserClassService userClassService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminUserRoleService adminUserRoleService;
    @Autowired
    private UserServiceGateway userServiceGateway;
    @Override
    public ListDTO<SchoolClassDTO> qryClass(Map<String, Object> params) {
        List<SchoolClass> classes = schoolClassService.listByMap(params);
        if(classes.size()==0){
            return new ListDTO<>(RetCodeEnum.SUCCEED.getCode(),"结果为空");
        }
        List<SchoolClassDTO> classDTOS = ClassConverter.INSTANCE.domain2dto(classes);
        for (SchoolClassDTO classDTO : classDTOS) {
            classDTO.setUserAmount(countUser(classDTO.getId()));
            classDTO.setName(classDTO.getClassGrade()+classDTO.getClassId()+"班"+classDTO.getClassName());
        }
        ListDTO<SchoolClassDTO> dto = new ListDTO<>(RetCodeEnum.SUCCEED);
        dto.setRetList(classDTOS);
        return dto;
    }

    @Override
    public DTO addClass(SchoolClass schoolClass) {
        schoolClassService.save(schoolClass);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public DTO modClass(SchoolClass schoolClass) {
        schoolClassService.updateById(schoolClass);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, timeout = 36000, rollbackFor = Exception.class)
    public DTO removeClass(Map<String, Object> params) {
        //先删映射，后删班级
        int cid = StringUtil.objectToInt(params.get("id"));
        LambdaQueryWrapper<UserClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserClass::getCid,cid);
        List<Integer> ids = userClassService.list(wrapper).stream().
                map(UserClass::getId).collect(Collectors.toList());
        userClassService.removeByIds(ids);
        schoolClassService.removeById(cid);
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getClassId,cid);
        List<User> users = userService.list(userWrapper);
        for (User user : users) {
            user.setClassId(-1);
        }
        userService.updateBatchById(users);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public DTO addUser(Map<String, Object> params) {
        int cid = StringUtil.objectToInt(params.get("cid"));
        List<Integer> uids = (List<Integer>) params.get("uids");

        for (Integer uid : uids) {
            if(userServiceGateway.isStudent(uid)){
                userServiceGateway.changeStuClass(uid,cid);
                removeUserFromAllClass(uid);
            }
            UserClass userClass = new UserClass();
            userClass.setCid(cid);
            userClass.setUid(uid);
            try {
                userClassService.save(userClass);
            }catch (Exception e){
                log.error(uid+"已在"+cid+"班级里了");
            }
        }

        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public DTO removeUser(Map<String, Object> params) {
        int cid = StringUtil.objectToInt(params.get("cid"));
        List<Integer> uids = (List<Integer>) params.get("uids");
        LambdaQueryWrapper<UserClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserClass::getCid,cid);
        wrapper.in(UserClass::getUid,uids);
        List<Integer> list = userClassService.list(wrapper).stream().
                map(UserClass::getId).collect(Collectors.toList());
        userClassService.removeByIds(list);
        removeStu(params);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public ListDTO<User> qryStudents(int cid) {
        LambdaQueryWrapper<UserClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserClass::getCid,cid);
        List<Integer> uids = userClassService.list(wrapper).stream().
                map(UserClass::getUid).collect(Collectors.toList());
        LambdaQueryWrapper<AdminUserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(AdminUserRole::getUid,uids).eq(AdminUserRole::getRid,9);
        List<Integer> stuIds = adminUserRoleService.list(roleWrapper).stream().
                map(AdminUserRole::getUid).collect(Collectors.toList());
        List<User> stus = userService.listByIds(stuIds);
        ListDTO<User> dto = new ListDTO<>(RetCodeEnum.SUCCEED);
        dto.setRetList(stus);
        return dto;
    }

    @Override
    public DTO addStu(Map<String, Object> params) {
        int cid = StringUtil.objectToInt(params.get("cid"));
        List<Integer> uids = (List<Integer>) params.get("uids");
        List<User> users = userService.listByIds(uids);
        for (User user : users) {
            user.setClassId(cid);
        }
        userService.updateBatchById(users);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public DTO removeStu(Map<String, Object> params) {
        int cid = StringUtil.objectToInt(params.get("cid"));
        List<Integer> uids = (List<Integer>) params.get("uids");
        List<User> users = userService.listByIds(uids);
        for (User user : users) {
            if(user.getClassId()==cid){
                user.setClassId(0);
            }
        }
        userService.updateBatchById(users);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public int countUser(int cid) {
        LambdaQueryWrapper<UserClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserClass::getCid,cid);
        return userClassService.count(wrapper);
    }

    @Override
    public void removeUserFromAllClass(int uid) {
        LambdaQueryWrapper<UserClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserClass::getUid,uid);
        userClassService.removeByIds(
        userClassService.list(wrapper).stream().
                map(UserClass::getId).collect(Collectors.toList()));
    }
}

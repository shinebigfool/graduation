package com.example.graduate.mapstruct;


import com.example.graduate.dto.UserDTO;
import com.example.graduate.pojo.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author 倪鑫彦
 * @description 用户信息DTO,DO转换工具类
 * @since 10:20 2020/11/6
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);
    @Mappings({})
    List<UserDTO> domain2dto(List<User> list);
    UserDTO domain2dto(User user);
}

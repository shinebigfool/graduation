package com.example.graduate.mapstruct;

import com.example.graduate.dto.SchoolClassDTO;
import com.example.graduate.pojo.SchoolClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassConverter {
    ClassConverter INSTANCE = Mappers.getMapper(ClassConverter.class);
    @Mappings({})
    SchoolClassDTO domain2dto(SchoolClass schoolClass);
    List<SchoolClassDTO> domain2dto(List<SchoolClass> list);
}

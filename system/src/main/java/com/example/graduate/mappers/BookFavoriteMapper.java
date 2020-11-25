package com.example.graduate.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.graduate.pojo.BookFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookFavoriteMapper extends BaseMapper<BookFavorite> {
}
package com.example.graduate.service.impl

import com.example.graduate.codeEnum.RetCodeEnum
import com.example.graduate.dto.DTO
import com.example.graduate.service.CalculateParser

class CalculateBookFair implements CalculateParser{
    @Override
    DTO parse2DTO(Map<String, Object> params) {
        return new DTO(RetCodeEnum.SUCCEED)
    }
}

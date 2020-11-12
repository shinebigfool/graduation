package com.example.graduate.service.impl;

import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.service.BookService;
import com.example.graduate.service.BorrowLogService;
import com.example.graduate.service.BorrowLogServiceGateway;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class BorrowLogServiceGatewayImpl implements BorrowLogServiceGateway {
    @Autowired
    private BookService bookService;
    @Autowired
    private BorrowLogService borrowLogService;
    @Override
    public DTO lendBook(int bid) {
        // TODO 获取当前登录用户信息
        SecurityUtils.getSubject();
        Book book = bookService.getById(bid);
        if (book.getAvailableState()==0){
            return new DTO(RetCodeEnum.FAIL.getCode(),"该书暂无法借阅");
        }
        BorrowLog borrowLog = new BorrowLog();
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE,20);
        Date feature = calendar.getTime();
        borrowLog.setLog(now,"bigfool",bid,book.getTitle(),feature);
        book.setAvailableState(0);

        bookService.updateById(book);
        borrowLogService.save(borrowLog);

        return null;
    }
}

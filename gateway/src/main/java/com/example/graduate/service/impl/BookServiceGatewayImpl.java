package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.exception.NxyException;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.service.BookService;
import com.example.graduate.service.BookServiceGateway;
import com.example.graduate.service.BorrowLogService;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceGatewayImpl implements BookServiceGateway {

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowLogService borrowLogService;
    @Override
    public PageDTO<Book> qryBook(Map<String, Object> params) {
        PageDTO<Book> pageDTO = new PageDTO<>(RetCodeEnum.SUCCEED);
        // 查询符合条件的书的总数
        int totalRow = bookService.qryTotalRow(params);
        params.put("totalRow",totalRow);
        int size = PageUtil.transParam2Page(params,pageDTO);
        int current = StringUtil.objectToInt(params.get("current"));
        // 分页
        Page<Book> bookPage = new Page<>(current,size);
        String title = StringUtil.parseString(params.get("title"));
        String author = StringUtil.parseString(params.get("author"));
        int cid = StringUtil.objectToInt(params.get("cid"));
        int examineState = StringUtil.objectToInt(params.get("examineState"));
        int availableState = StringUtil.objectToInt(params.get("availableState"));
        String examinePerson = StringUtil.parseString(params.get("examinePerson"));
        String uploadPerson = StringUtil.parseString(params.get("uploadPerson"));
        List<Book> books = bookService.qryBookByPage(bookPage,title,author,cid,examineState,
                availableState,examinePerson,uploadPerson);
        if(books.size()==0){
            return new PageDTO<>(RetCodeEnum.RESULT_EMPTY);
        }
        pageDTO.setRetList(books);
        return pageDTO;
    }

    @Override
    public DTO saveBook(Book book) throws NxyException {
        book.setExamineState(0);
        String name = SecurityUtils.getSubject().getPrincipal().toString();
        if(name==null||name.equals("")){
            throw new NxyException("请先登录");
        }
        book.setUploadPerson(name);
        book.setAvailableState(0);
        book.setExaminePerson("");
        book.setExamineNote("");
        try {
            bookService.save(book);
            return new DTO(RetCodeEnum.SUCCEED.getCode(),StringUtil.parseString(book.getId()));
        }catch (Exception e){
            throw new NxyException("新增图书失败");
        }


    }

    @Override
    public DTO delBookById(List<Integer> bids) throws NxyException {
        try {
            bookService.removeByIds(bids);
            return new DTO(RetCodeEnum.SUCCEED);
        }catch (Exception e){
            throw new NxyException("删除图书失败");
        }


    }

    // TODO 消息推送
    @Override
    public DTO examineBook(Book book) {
        bookService.updateById(book);
        return new DTO(RetCodeEnum.SUCCEED);
    }


}

package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.exception.NxyException;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.service.BookService;
import com.example.graduate.service.BookServiceGateway;
import com.example.graduate.service.BorrowLogService;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.PresentUserUtils;
import com.example.graduate.utils.StringUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
        params.put("totalRow", totalRow);
        int size = PageUtil.transParam2Page(params, pageDTO);
        int current = StringUtil.objectToInt(params.get("current"));
        // 分页
        Page<Book> bookPage = new Page<>(current, size);
        String title = StringUtil.parseString(params.get("title"));
        String author = StringUtil.parseString(params.get("author"));
        int cid = StringUtil.objectToInt(params.get("cid"));
        int examineState = StringUtil.objectToInt(params.get("examineState"));
        int availableState = StringUtil.objectToInt(params.get("availableState"));
        String examinePerson = StringUtil.parseString(params.get("examinePerson"));
        String uploadPerson = StringUtil.parseString(params.get("uploadPerson"));
        List<Book> books = bookService.qryBookByPage(bookPage, title, author, cid, examineState,
                availableState, examinePerson, uploadPerson);
        if (books.size() == 0) {
            return new PageDTO<>(RetCodeEnum.RESULT_EMPTY);
        }
        pageDTO.setRetList(books);
        return pageDTO;
    }

    @Override
    public DTO saveBook(Book book) throws NxyException {
        book.setExamineState(0);
        String name = SecurityUtils.getSubject().getPrincipal().toString();
        if (name == null || name.equals("")) {
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(),"请先登录");
        }
        book.setUploadPerson(name);
        book.setAvailableState(0);
        book.setExaminePerson("");
        book.setExamineNote("");
        try {
            bookService.save(book);
            return new DTO(RetCodeEnum.SUCCEED.getCode(), StringUtil.parseString(book.getId()));
        } catch (Exception e) {
            throw new NxyException("新增图书失败");
        }


    }

    @Override
    public DTO delBookById(List<Integer> bids) throws NxyException {
        StringBuilder ret = new StringBuilder();
        List<Book> books = bookService.listByIds(bids);
        for (Book book : books) {
            if (book.getAvailableState() == 0 && book.getExamineState() == 1) {
                ret.append(book.getTitle()).append("\t");
                continue;
            }
            book.setAvailableState(3);
        }
        try {
            bookService.updateBatchById(books);
        } catch (Exception e) {
            return new DTO(RetCodeEnum.EXCEPTION.getCode(), e.getMessage());
        }
        if (ret.toString().equals("")) {
            return new DTO(RetCodeEnum.SUCCEED);
        }
        return new DTO(RetCodeEnum.FAIL.getCode(), ret.toString());


    }

    // TODO 消息推送
    @Override
    public DTO examineBook(Book book) throws NxyException {
        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(),"请先登录");
        }
        Book inDB = bookService.getById(book.getId());
        //该书审核通过且借出中
        if (inDB.getAvailableState() == 0 && inDB.getExamineState() == 1) {
            return new DTO(RetCodeEnum.FAIL.getCode(), "当前图书外借中，暂无法执行此操作");
        }
        //已下架
        if (inDB.getAvailableState() == 3) {
            return new DTO(RetCodeEnum.FAIL.getCode(), "该书已下架，无法执行此操作");
        }
        inDB.setExamineNote(book.getExamineNote());
        inDB.setExamineState(book.getExamineState());
        inDB.setExaminePerson(name);
        //0 待审，1 审核通过，2 未过审；0 不可用，1 可用
        //1=》1   0，2=》0 根据审核结果设置图书是否可用
        inDB.setAvailableState(book.getExamineState() % 2);
        try {
            bookService.updateById(inDB);
        } catch (Exception e) {
            throw new NxyException("审核图书失败");
        }
        return new DTO(RetCodeEnum.SUCCEED);
    }

    // TODO 消息推送
    @Override
    public DTO modifyBook(Book book) throws NxyException {
        Book inDB = bookService.getById(book.getId());
        //该书审核通过且借出中
        if (inDB.getAvailableState() == 0 && inDB.getExamineState() == 1) {
            return new DTO(RetCodeEnum.FAIL.getCode(), "当前图书外借中，暂无法执行此操作");
        }
        //已下架
        if (inDB.getAvailableState() == 3) {
            return new DTO(RetCodeEnum.FAIL.getCode(), "该书已下架，暂无法执行此操作");
        }
        inDB.setAbs(book.getAbs());
        inDB.setAuthor(book.getAuthor());
        inDB.setCid(book.getCid());
        inDB.setCover(book.getCover());
        inDB.setPress(book.getPress());
        inDB.setTitle(book.getTitle());
        inDB.setAvailableState(0);
        inDB.setInsertDate(book.getInsertDate());
        inDB.setExamineState(0);
        try {
            bookService.updateById(inDB);
        } catch (Exception e) {
            throw new NxyException("图书更新异常");
        }

        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public ListDTO<Book> suggestBook(Map<String, Object> params) {
        ListDTO<Book> dto = new ListDTO<>(RetCodeEnum.SUCCEED);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getAvailableState, 1);
        if (StringUtil.objectToInt(params.get("cid")) != -1) {
            wrapper.eq(Book::getCid, StringUtil.objectToInt(params.get("cid")));
        }
        List<Book> books = bookService.list(wrapper);
        int size = StringUtil.objectToInt(params.get("size")) != -1
                ? Math.min(StringUtil.objectToInt(params.get("size")), books.size())
                : Math.min(3, books.size());
        if (size == books.size()) {
            dto.setRetList(books);
            return dto;
        }
        Set<Integer> set = new HashSet<>();
        List<Book> ret = new ArrayList<>();
        int count = 0;
        while (count < size) {
            int temp = ThreadLocalRandom.current().nextInt(0, books.size());
            if (set.add(temp)) {
                ret.add(books.get(temp));
                count++;
            }
        }
        dto.setRetList(ret);
        return dto;
    }


}

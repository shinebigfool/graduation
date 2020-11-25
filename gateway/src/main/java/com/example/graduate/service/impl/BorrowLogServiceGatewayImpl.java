package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.dto.PersonalBorrowInfoDTO;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BookFavorite;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.pojo.BorrowLogDetail;
import com.example.graduate.service.BookFavoriteService;
import com.example.graduate.service.BookService;
import com.example.graduate.service.BorrowLogService;
import com.example.graduate.service.BorrowLogServiceGateway;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.PresentUserUtils;
import com.example.graduate.utils.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BorrowLogServiceGatewayImpl implements BorrowLogServiceGateway {
    @Autowired
    private BookService bookService;
    @Autowired
    private BorrowLogService borrowLogService;
    @Autowired
    private BookFavoriteService bookFavoriteService;
    @Override
    public DTO lendBook(int bid) {
        // TODO 获取当前登录用户信息
        SecurityUtils.getSubject().getPrincipal();
        String name = "test";
        if(StringUtil.isBlank(name)){
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(),"请先登录");
        }
        Book book = bookService.getById(bid);
        if(book==null){
            return new DTO(RetCodeEnum.FAIL.getCode(),"查无此书");
        }
        if (book.getAvailableState()==0){
            return new DTO(RetCodeEnum.FAIL.getCode(),"该书暂无法借阅");
        }
        BorrowLog borrowLog = new BorrowLog();
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE,20);
        Date feature = calendar.getTime();
        borrowLog.setLog(now,name,bid,book.getTitle(),feature);
        book.setAvailableState(0);

        bookService.updateById(book);
        borrowLogService.save(borrowLog);

        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public DTO returnBook(int bid) {
        SecurityUtils.getSubject().getPrincipal();
        String name = "bigfool";
        LambdaQueryWrapper<BorrowLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowLog::getState,2).eq(BorrowLog::getUserAccount,name).eq(BorrowLog::getBookId,bid);
        List<BorrowLog> borrowLogs = borrowLogService.list(wrapper);
        if (borrowLogs.size()!=1){

            return new DTO(RetCodeEnum.FAIL.getCode(),"您当前有"+borrowLogs.size()+"条对应借书记录" +
                    "请联系管理员进行处理");
        }
        BorrowLog log = borrowLogs.get(0);
        int totalTime = (int) ((new Date().getTime()-log.getBorrowDate().getTime())/86400000);
        log.setTotalTime(totalTime);
        log.setReturnDate(new Date());
        log.setState(1);
        borrowLogService.updateById(log);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public ListDTO qryBorrowLog(Map<String, Object> params) {
        String name=PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)){

        }
        name = "bigfool";
        params.put("name",name);
        List<BorrowLogDetail> borrowLogDetails = borrowLogService.qryBorrowLogDetail(params);
        ListDTO listDTO = new ListDTO(RetCodeEnum.SUCCEED);
        listDTO.setRetList(borrowLogDetails);
        return listDTO;
    }

    @Override
    public PageDTO<BorrowLogDetail> qryWholeLog(Map<String, Object> params) {
        PageDTO<BorrowLogDetail> pageDTO = new PageDTO<>(RetCodeEnum.SUCCEED);
        int totalRow = borrowLogService.qryTotalRow(params);
        params.put("totalRow",totalRow);
        int size = PageUtil.transParam2Page(params,pageDTO);
        int current = StringUtil.objectToInt(params.get("current"));
        Page<BorrowLogDetail> borrowLogPage = new Page<>(current,size);
        String title = StringUtil.parseString(params.get("title"));
        String author = StringUtil.parseString(params.get("author"));
        int cid = StringUtil.objectToInt(params.get("cid"));
        int borrowState = StringUtil.objectToInt(params.get("borrowState"));
        String uploadPerson = StringUtil.parseString(params.get("uploadPerson"));
        String name = StringUtil.parseString(params.get("name"));
        List<BorrowLogDetail> list = borrowLogService.qryLogPage(borrowLogPage, title,
                author, cid, borrowState, uploadPerson, name);
        if(list.size()==0){
            return new PageDTO<>(RetCodeEnum.RESULT_EMPTY);
        }
        pageDTO.setRetList(list);
        return pageDTO;
    }

    @Override
    public PersonalBorrowInfoDTO qryPersonalBorrowInfo() {
        PersonalBorrowInfoDTO dto = new PersonalBorrowInfoDTO(RetCodeEnum.SUCCEED);
        String name = PresentUserUtils.qryPresentUserAccount();
        if(StringUtil.isBlank(name)){
            return new PersonalBorrowInfoDTO(RetCodeEnum.FORBIDDEN.getCode(),"请先登录");
        }
        //借书日志里所有相关记录
        LambdaQueryWrapper<BorrowLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowLog::getUserAccount,name);
        int bookTotal = borrowLogService.count(wrapper);
        //借书日志里未还相关记录
        wrapper.eq(BorrowLog::getState,2);
        int bookInHand = borrowLogService.count(wrapper);
        //借书日志近一个月记录
        LambdaQueryWrapper<BorrowLog> newWrapper = new LambdaQueryWrapper<>();
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE,-30);
        newWrapper.eq(BorrowLog::getUserAccount,name).gt(BorrowLog::getBorrowDate,calendar.getTime());
        int bookMonth = borrowLogService.count(newWrapper);
        //收藏夹所有相关记录
        LambdaQueryWrapper<BookFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(BookFavorite::getUserAccount,name);
        int bookFavorite = bookFavoriteService.count(favoriteWrapper);
        //收藏夹排序，取最近几条图书信息
        favoriteWrapper.orderByDesc(BookFavorite::getCreateTime);
        BookFavorite favoriteRecent = bookFavoriteService.getOne(favoriteWrapper);
        Book bookRecent = bookService.getById(favoriteRecent.getBookId());
        dto.setResults(bookTotal,bookInHand,bookMonth,bookFavorite,bookRecent);
        return dto;
    }
}

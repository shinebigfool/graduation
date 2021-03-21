package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.*;
import com.example.graduate.mapstruct.BookConverter;
import com.example.graduate.pojo.*;
import com.example.graduate.service.*;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.PresentUserUtils;
import com.example.graduate.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BorrowLogServiceGatewayImpl implements BorrowLogServiceGateway {
    @Autowired
    private AffairServiceGateway affairServiceGateway;
    @Autowired
    private BookService bookService;
    @Autowired
    private BorrowLogService borrowLogService;
    @Autowired
    private BookFavoriteService bookFavoriteService;
    @Autowired
    private BookServiceGateway bookServiceGateway;
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, timeout = 36000, rollbackFor = Exception.class)
    public DTO lendBook(int bid) {
        String name = PresentUserUtils.qryPresentUserAccount();
        if(StringUtil.isBlank(name)){
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(),"请先登录");
        }
        Book book = bookService.getById(bid);
        if(book==null){
            return new DTO(RetCodeEnum.FAIL.getCode(),"查无此书");
        }
        if (book.getAvailableState()==0||book.getAvailableState()==3){
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, timeout = 36000, rollbackFor = Exception.class)
    public DTO returnBook(int bid,String name) {
        if(PresentUserUtils.qryPresentUserAccount().equals("")){
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(),"请先登录");
        }
        if(StringUtil.isBlank(name)){
            name = PresentUserUtils.qryPresentUserAccount();
        }
        //查找借书记录并检查是否有错
        LambdaQueryWrapper<BorrowLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowLog::getState,2).eq(BorrowLog::getUserAccount,name).eq(BorrowLog::getBookId,bid);
        List<BorrowLog> borrowLogs = borrowLogService.list(wrapper);
        if (borrowLogs.size()!=1){

            return new DTO(RetCodeEnum.FAIL.getCode(),"您当前有"+borrowLogs.size()+"条对应借书记录" +
                    "请联系管理员进行处理");
        }
        //更新借书状态
        BorrowLog log = borrowLogs.get(0);
        int totalTime = (int) ((new Date().getTime()-log.getBorrowDate().getTime())/86400000);
        if(totalTime>20){
            Affair affair = new Affair();
            affair.setAffairDetail(5+(totalTime-20)/8);
            affair.setAffairType(1);
            affair.setBookInfo(log.getBookTitle());
            affairServiceGateway.addAffair(affair);
        }
        log.setTotalTime(totalTime);
        log.setReturnDate(new Date());
        log.setState(1);
        borrowLogService.updateById(log);
        //更新图书状态
        Book bookInDB = bookService.getById(bid);
        bookInDB.setAvailableState(1);
        bookService.updateById(bookInDB);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    public PageDTO<BorrowLogDetail> qryBorrowLog(Map<String, Object> params) {
        String name=PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)){
            return new PageDTO<>(RetCodeEnum.FAIL.getCode(),"请先登录");
        }
        params.put("name",name);
        PageDTO<BorrowLogDetail> dto = this.qryWholeLog(params);
        if(params.get("overDue")!=null) {
            dto.getRetList().forEach(e -> e.setPoint(5 + e.getOverDue() / 8));
        }
        return dto;
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
        int overDue = StringUtil.objectToInt(params.get("overDue"));
        List<BorrowLogDetail> list = borrowLogService.qryLogPage(borrowLogPage, title,
                author, cid, borrowState, uploadPerson, name, overDue);
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

        dto.setResults(bookTotal,bookInHand,bookMonth,bookFavorite,null);
        return dto;
    }

    @Override
    public BookDTO qryBorrowLogDetail(int id) {
        BorrowLog log = borrowLogService.getById(id);
        Book bookInDB = bookService.getById(log.getBookId());
        BookDTO dto = BookConverter.INSTANCE.domain2dto(bookInDB);
        dto.setFavorite(bookServiceGateway.isFavorite(bookInDB)?1:0);

        dto.setResult(RetCodeEnum.SUCCEED);
        dto.setBorrowPerson(log.getUserAccount());

        return dto;
    }

    @Override
    public Boolean isInHand(int bid,String userAccount) {
        LambdaQueryWrapper<BorrowLog> borrowWrapper = new LambdaQueryWrapper<>();
        borrowWrapper.eq(BorrowLog::getBookId,bid).
                eq(BorrowLog::getUserAccount,userAccount).
                eq(BorrowLog::getState,2);
        List<BorrowLog> list = borrowLogService.list(borrowWrapper);
        if(list.size()==1){
            return true;
        }
        if(list.size()==0){
            return false;
        }
        return null;
    }

    @Override
    public ListDTO<BookLendCount> lendCount() {
        ListDTO<BookLendCount> dto = new ListDTO<>(RetCodeEnum.SUCCEED);
        dto.setRetList(borrowLogService.lendCount());
        return dto;
    }
}

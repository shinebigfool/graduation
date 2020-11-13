package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.pojo.BorrowLogDetail;
import com.example.graduate.service.BookService;
import com.example.graduate.service.BorrowLogService;
import com.example.graduate.service.BorrowLogServiceGateway;
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
    @Override
    public DTO lendBook(int bid) {
        // TODO 获取当前登录用户信息
        SecurityUtils.getSubject().getPrincipal();
        String name = "bigfool";
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
}

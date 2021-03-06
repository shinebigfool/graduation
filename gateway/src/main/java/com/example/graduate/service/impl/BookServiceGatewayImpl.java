package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.domain.BookDO;
import com.example.graduate.dto.*;
import com.example.graduate.exception.NxyException;
import com.example.graduate.mapstruct.BookConverter;
import com.example.graduate.pojo.*;
import com.example.graduate.service.*;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.PresentUserUtils;
import com.example.graduate.utils.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class BookServiceGatewayImpl implements BookServiceGateway {

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowLogService borrowLogService;

    @Autowired
    private BookFavoriteService bookFavoriteService;

    @Autowired
    private BorrowLogServiceGateway borrowLogServiceGateway;

    @Autowired
    private FavoriteHistoryGateway favoriteHistoryGateway;
    @Autowired
    private UserPointServiceGateway userPointServiceGateway;
    @Autowired
    private FavoriteHistoryService favoriteHistoryService;
    @Autowired
    private UserServiceGateway userServiceGateway;
    @Autowired
    @Lazy
    private AffairServiceGateway affairServiceGateway;
    @Override
    public PageDTO<Book> qryBook(Map<String, Object> params) {
        int examineState = StringUtil.objectToInt(params.get("examineState"));
        int availableState = StringUtil.objectToInt(params.get("availableState"));
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
    public PageDTO<BookDO> qryMyBook(Map<String, Object> params) {
        String name = PresentUserUtils.qryPresentUserAccount();
        params.put("uploadPerson",name);
        int examineState = StringUtil.objectToInt(params.get("examineState"));
        int availableState = StringUtil.objectToInt(params.get("availableState"));
        PageDTO<BookDO> pageDTO = new PageDTO<>(RetCodeEnum.SUCCEED);
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

        String examinePerson = StringUtil.parseString(params.get("examinePerson"));
        String uploadPerson = StringUtil.parseString(params.get("uploadPerson"));
        List<Book> books = bookService.qryBookByPage(bookPage, title, author, cid, examineState,
                availableState, examinePerson, uploadPerson);
        List<BookDO> list = BookConverter.INSTANCE.po2do(books);
        for (BookDO bookDO : list) {
            processBookDO(bookDO);
        }
        pageDTO.setRetList(list);
        return pageDTO;
    }

    @Override
    public DTO saveBook(Book book) throws NxyException {
        book.setExamineState(0);
        String name = SecurityUtils.getSubject().getPrincipal().toString();
        if (name == null || name.equals("")) {
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "请先登录");
        }
        book.setUploadPerson(name);
        book.setAvailableState(0);
        book.setExaminePerson("");
        book.setExamineNote("");
        try {
            bookService.save(book);
            UserPoint userPoint = userPointServiceGateway.getByName(name);
            userPoint.setPoint(userPoint.getPoint()+10);
            userPointServiceGateway.modUserPoint(userPoint,"共享图书"+book.getTitle()+"+10分");
            return new DTO(RetCodeEnum.SUCCEED.getCode(), "新增图书成功，请等待管理员审核");
        } catch (Exception e) {
            System.out.println(e);
            throw new NxyException("新增图书失败");
        }


    }

    @Override
    public DTO delBookById(int bid) throws NxyException {

        Book book = bookService.getById(bid);
        //借出状态
        if (book.getAvailableState() == 0 && book.getExamineState() == 1) {
            return new DTO(RetCodeEnum.FAIL.getCode(), "此书外接中，下架失败");
        }
        book.setAvailableState(3);
        try {
            bookService.updateById(book);
        } catch (Exception e) {
            e.printStackTrace();
            return new DTO(RetCodeEnum.EXCEPTION.getCode(), e.getMessage());
        }

        return new DTO(RetCodeEnum.SUCCEED);


    }

    // TODO 消息推送
    @Override
    public DTO examineBook(Book book) throws NxyException {
        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            return new DTO(RetCodeEnum.FORBIDDEN.getCode(), "请先登录");
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
        inDB.setOnlineUrl(book.getOnlineUrl());
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

    @Override
    public BookDTO qryBookDetail(int id) {
        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            return new BookDTO(RetCodeEnum.FAIL.getCode(), "请先登录");
        }
        Book book = bookService.getById(id);
        Boolean isInHand = borrowLogServiceGateway.isInHand(id, name);
        if (isInHand == null) {
            return new BookDTO(RetCodeEnum.EXCEPTION.getCode(), "当前用户借书记录存在异常！");
        }
        BookDTO bookDTO = BookConverter.INSTANCE.domain2dto(book);
        if (isFavorite(book)) {
            bookDTO.setFavorite(1);
        } else {
            bookDTO.setFavorite(0);
        }

        bookDTO.setIsInHand(isInHand ? 2 : 1);
        bookDTO.setResult(RetCodeEnum.SUCCEED);
        return bookDTO;
    }

    @Override
    public ListDTO<Book> qryFavoriteBook() {
        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            return new ListDTO<>(RetCodeEnum.FAIL.getCode(), "请先登录");
        }
        LambdaQueryWrapper<BookFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(BookFavorite::getUserAccount, name);
        List<BookFavorite> favorites = bookFavoriteService.list(favoriteWrapper);
        List<Integer> bids = favorites.stream().map(BookFavorite::getBookId).collect(Collectors.toList());
        if (bids.size() == 0) {
            return new ListDTO<>(RetCodeEnum.SUCCEED.getCode(), "结果为空");
        }
        List<Book> books = bookService.listByIds(bids);

        ListDTO<Book> dto = new ListDTO<>(RetCodeEnum.SUCCEED);
        dto.setRetList(books);
        return dto;
    }

    @Override
    public DTO addFavoriteBook(Book book) {
        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            return new DTO(RetCodeEnum.FAIL.getCode(), "请先登录");
        }
        if (isFavorite(book)) {
            return new DTO(RetCodeEnum.SUCCEED.getCode(), "已在收藏夹");
        }
        if(!favoriteHistoryGateway.isExists(name,book.getId())){
            FavoriteHistory favoriteHistory = new FavoriteHistory();
            favoriteHistory.setBid(book.getId());
            favoriteHistory.setName(name);
            favoriteHistoryGateway.add(favoriteHistory);
            String uploader = book.getUploadPerson();
            UserPoint userPoint = userPointServiceGateway.getByName(uploader);
            userPoint.setPoint(userPoint.getPoint()+1);
            userPointServiceGateway.modUserPoint(userPoint,book.getTitle()+"被收藏+1分");
        }
        BookFavorite favorite = new BookFavorite();
        favorite.setBookId(book.getId());
        favorite.setBookTitle(book.getTitle());
        favorite.setUserAccount(name);
        bookFavoriteService.save(favorite);
        return new DTO(RetCodeEnum.SUCCEED.getCode(), "收藏成功");
    }

    @Override
    public DTO removeFavoriteBook(Book book) {
        String name = PresentUserUtils.qryPresentUserAccount();
        LambdaQueryWrapper<BookFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(BookFavorite::getBookId, book.getId()).eq(BookFavorite::getUserAccount, name);
        BookFavorite one = bookFavoriteService.getOne(favoriteWrapper);
        if (one != null) {
            bookFavoriteService.removeById(one.getId());
            return new DTO(RetCodeEnum.SUCCEED.getCode(), "取消收藏成功");
        }
        return new DTO(RetCodeEnum.SUCCEED.getCode(), "您的收藏夹中无此书");
    }

    @Override
    public Boolean isFavorite(Book book) {
        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            return false;
        }
        LambdaQueryWrapper<BookFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(BookFavorite::getBookId, book.getId()).eq(BookFavorite::getUserAccount, name);
        int count = bookFavoriteService.count(favoriteWrapper);

        return count != 0;
    }

    @Override
    public PageDTO<Book> qryFavoriteBook(Map<String, Object> params) {
        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            return new PageDTO<>(RetCodeEnum.FORBIDDEN.getCode(), "请先登录");
        }
        PageDTO<Book> pageDTO = new PageDTO<>(RetCodeEnum.SUCCEED);
        params.put("name",name);
        int totalRow = bookFavoriteService.countFavorite(params);
        params.put("totalRow", totalRow);
        int size = PageUtil.transParam2Page(params, pageDTO);
        int current = StringUtil.objectToInt(params.get("current"));
        Page<Book> page = new Page<>(current, size);
        String title = StringUtil.parseString(params.get("title"));
        String author = StringUtil.parseString(params.get("author"));
        String uploadPerson = StringUtil.parseString(params.get("uploadPerson"));
        int cid = StringUtil.objectToInt(params.get("cid"));
        List<Book> books = bookFavoriteService.qryFavoriteByPage(page, title, author, cid, uploadPerson, name);
        pageDTO.setRetList(books);
        return pageDTO;
    }

    @Override
    public ListDTO<BookFavoriteCount> favoriteCount() {
        ListDTO<BookFavoriteCount> dto = new ListDTO<>(RetCodeEnum.SUCCEED);
        List<BookFavoriteCount> bookFavoriteCounts = bookFavoriteService.favoriteCount();
        dto.setRetList(bookFavoriteCounts);
        return dto;
    }

    @Override
    public int qryFavoriteNum(int bid) {
        LambdaQueryWrapper<FavoriteHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteHistory::getBid,bid);
        return favoriteHistoryService.count(wrapper);
    }

    @Override
    public int borrowNum(int bid) {
        LambdaQueryWrapper<BorrowLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowLog::getBookId,bid);
        return borrowLogService.count(wrapper);
    }

    @Override
    public DTO gift(Book book) {
        Book inDb = bookService.getById(book.getId());
        UserDTO userDTO = userServiceGateway.qryUserByName(book.getUploadPerson());
        if (!userDTO.getRetCode().equals("000000")){
            return new DTO(RetCodeEnum.FAIL.getCode(),"查无此人");
        }
        inDb.setUploadPerson(book.getUploadPerson());
        bookService.updateById(book);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, timeout = 36000, rollbackFor = Exception.class)
    public DTO lossBook(int bid) {
        String name = PresentUserUtils.qryPresentUserAccount();
        Book byId = bookService.getById(bid);
        // 图书下架
        byId.setAvailableState(3);
        // 新建遗失事务
        Affair affair = new Affair();
        affair.setAffairDetail(20);
        affair.setNote("遗失"+byId.getTitle());
        affair.setAffairType(3);
        affair.setBookInfo(byId.getTitle());
        affair.setBid(bid);
        // 更新借书日志
        BorrowLog borrowLog = borrowLogServiceGateway.qryCurrLog(bid, name);
        borrowLog.setState(1);
        borrowLog.setReturnDate(new Date());
        borrowLogService.updateById(borrowLog);
        affairServiceGateway.addAffair(affair);
        bookService.updateById(byId);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, timeout = 36000, rollbackFor = Exception.class)
    public DTO brokenBook(Affair affair) {
        // 新建损坏事务
        Affair inDB = new Affair();
        inDB.setAffairType(4);
        inDB.setBid(affair.getBid());
        inDB.setAffairDetail(10);
        inDB.setNote("损坏"+affair.getBookInfo());
        inDB.setBookInfo(affair.getBookInfo());
        inDB.setBrokenPic(affair.getBrokenPic());
        affairServiceGateway.addAffair(inDB);
        // 正常还书
        borrowLogServiceGateway.returnBook(affair.getBid(),PresentUserUtils.qryPresentUserAccount());
        return new DTO(RetCodeEnum.SUCCEED);
    }

    private void processBookDO(BookDO bookDO){
        bookDO.setFavoriteNum(qryFavoriteNum(bookDO.getId()));
        bookDO.setReadNum(borrowNum(bookDO.getId()));
        bookDO.setAviPoint(bookDO.getFavoriteNum()+bookDO.getReadNum()*5);
    }

}

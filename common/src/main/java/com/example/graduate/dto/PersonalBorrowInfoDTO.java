package com.example.graduate.dto;

import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.pojo.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class PersonalBorrowInfoDTO extends DTO{
    //总借书量（包括已还）
    private int bookTotal;
    //未还书
    private int bookInHand;
    //当月借书总数
    private int bookMonth;
    //收藏夹
    private int bookFavorite;
    //近期足迹
    private Book bookRecent;

    public PersonalBorrowInfoDTO(){
        super();
    }
    public PersonalBorrowInfoDTO(RetCodeEnum e){
        super(e);
    }
    public PersonalBorrowInfoDTO(String code,String msg){
        super(code,msg);
    }
    public void setResults(int bookTotal,int bookInHand,int bookMonth,int bookFavorite,Book bookRecent){
        this.bookFavorite=bookFavorite;
        this.bookInHand=bookInHand;
        this.bookMonth=bookMonth;
        this.bookRecent=bookRecent;
        this.bookTotal = bookTotal;
    }
}

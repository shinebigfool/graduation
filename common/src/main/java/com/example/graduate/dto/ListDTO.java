package com.example.graduate.dto;

import com.example.graduate.codeEnum.RetCodeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 倪鑫彦
 * @since 15:03 2020/11/11
 */
@ToString
@Getter
@Setter
@ApiModel(value = "ListDTO工具类",description = "ListDTO工具类")
public class ListDTO<E> extends DTO{
    private static final long serialVersionUID = 1L;
    private List<E> retList = new ArrayList<>();
    public ListDTO() {
        super();
    }

    public ListDTO(String retCode, String retMsg) {
        super(retCode, retMsg);
    }

    public ListDTO(RetCodeEnum re) {
        super(re);
    }

    public List<E> getRetList() {
        return retList;
    }

    public void setRetList(List<E> retList) {
        this.retList = retList;
    }

    public void addAll(List<E> list) {
        for(E e : list) {
            addItem(e);
        }
    }

    public void addItem(E e) {
        this.retList.add(e);
    }
}

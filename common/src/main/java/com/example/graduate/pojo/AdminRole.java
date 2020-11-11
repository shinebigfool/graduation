package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Data
public class AdminRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String nameZh;

    private boolean enabled;

    @TableField(exist = false)
    private List<AdminMenu> menus;

    public AdminRole(){
        this.id = 3;
        this.name = "visitor";
        this.nameZh = "访客";
    }

}

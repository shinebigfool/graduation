package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@TableName("admin_menu")
public class AdminMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String path;

    private String name;

    private String namezh;

    private String iconcls;

    private String component;

    private int parentid;

    @Transient
    List<AdminMenu> children;
}

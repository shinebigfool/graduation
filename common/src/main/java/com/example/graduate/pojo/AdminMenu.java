package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
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

    private String nameZh;

    private String iconcls;

    private String component;

    private int parentId;

    @TableField(exist = false)
    List<AdminMenu> children;
}

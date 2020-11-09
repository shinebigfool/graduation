package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@TableName("admin_role")
public class AdminRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String NameZh;

    private boolean enabled;

    @Transient
    private List<AdminPermission> perms;

    @Transient
    private List<AdminMenu> menus;

}

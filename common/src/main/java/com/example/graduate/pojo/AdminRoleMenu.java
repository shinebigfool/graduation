package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@TableName("admin_role_menu")
public class AdminRoleMenu {
    @TableId(type = IdType.AUTO)
    private int id;
    private int rid;
    private int mid;
}

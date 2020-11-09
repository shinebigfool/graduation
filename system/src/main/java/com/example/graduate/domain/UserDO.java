package com.example.graduate.domain;



import com.example.graduate.pojo.AdminRole;
import com.example.graduate.pojo.User;

import java.util.List;

public class UserDO extends User {
    private List<AdminRole> roles;
}

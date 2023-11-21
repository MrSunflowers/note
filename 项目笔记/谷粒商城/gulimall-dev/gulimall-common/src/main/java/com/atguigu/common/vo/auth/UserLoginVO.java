package com.atguigu.common.vo.auth;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginVO {
    private String loginacct;
    private String password;
}

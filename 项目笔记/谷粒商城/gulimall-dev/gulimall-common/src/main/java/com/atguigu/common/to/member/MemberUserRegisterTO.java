package com.atguigu.common.to.member;

import lombok.Data;

/**
 * 会员注册VO
 */
@Data
public class MemberUserRegisterTO {
    private String userName;
    private String password;
    private String phone;
}

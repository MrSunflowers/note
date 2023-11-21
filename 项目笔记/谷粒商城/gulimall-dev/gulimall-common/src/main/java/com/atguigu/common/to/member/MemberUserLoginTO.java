package com.atguigu.common.to.member;

import lombok.Data;

/**
 * 登录VO
 */
@Data
public class MemberUserLoginTO {
    private String loginacct;
    private String password;
}

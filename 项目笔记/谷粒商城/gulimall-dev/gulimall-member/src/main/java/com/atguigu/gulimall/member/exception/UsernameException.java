package com.atguigu.gulimall.member.exception;

/**
 * 用户名异常
 */
public class UsernameException extends RuntimeException {

    public UsernameException() {
        super("存在相同的用户名");
    }
}

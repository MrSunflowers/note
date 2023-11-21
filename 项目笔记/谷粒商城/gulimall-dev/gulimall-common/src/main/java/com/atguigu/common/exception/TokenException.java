package com.atguigu.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 令牌处理异常
 * @Created: with IntelliJ IDEA.
 * @author: wan
 */
public class TokenException extends RuntimeException {

    public TokenException() {
        super("处理token，返回错误信息时异常");
    }
}

package com.atguigu.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.Set;

/**
 * 订单验价异常
 * @Created: with IntelliJ IDEA.
 * @author: wan
 */
public class VerifyPriceException extends RuntimeException {

    public VerifyPriceException() {
        super("订单商品价格发生变化，请确认后再次提交");
    }

}

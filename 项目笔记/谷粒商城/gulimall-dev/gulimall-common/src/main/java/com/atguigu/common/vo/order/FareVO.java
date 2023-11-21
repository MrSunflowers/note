package com.atguigu.common.vo.order;

import com.atguigu.common.vo.ware.MemberAddressVO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: wan
 */
@Data
public class FareVO {
    private MemberAddressVO address;
    private BigDecimal fare;
}

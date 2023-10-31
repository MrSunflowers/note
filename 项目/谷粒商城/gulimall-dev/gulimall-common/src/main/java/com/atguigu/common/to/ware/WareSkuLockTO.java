package com.atguigu.common.to.ware;

import com.atguigu.common.vo.ware.OrderItemVO;
import lombok.Data;

import java.util.List;

/**
 * @Description: 锁定库存传输对象
 * @Created: with IntelliJ IDEA.
 * @author: wanzenghui
 **/

@Data
public class WareSkuLockTO {

    private String orderSn;

    /** 需要锁住的所有库存信息 **/
    private List<OrderItemVO> locks;



}

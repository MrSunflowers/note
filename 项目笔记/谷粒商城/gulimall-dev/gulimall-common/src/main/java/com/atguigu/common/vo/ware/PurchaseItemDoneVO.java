package com.atguigu.common.vo.ware;

import lombok.Data;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: wanzenghui
 **/

@Data
public class PurchaseItemDoneVO {

    private Long itemId;// 采购需求ID

    private Integer status;// 采购状态

    private String reason;// 原因

}

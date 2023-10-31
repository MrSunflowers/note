package com.atguigu.common.to.seckill;

import com.atguigu.common.vo.product.SeckillSkuVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 秒杀场次信息
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: wanzenghui
 * @createTime: 2020-07-09 21:12
 **/

@Data
public class SeckillSessionWithSkusTO {

    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;

    private List<SeckillSkuVO> relationSkus;

}

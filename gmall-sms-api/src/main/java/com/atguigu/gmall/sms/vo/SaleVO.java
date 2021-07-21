package com.atguigu.gmall.sms.vo;

/**
 * @author wyl
 * @create 2020-06-28 19:40
 */

import lombok.Data;

/**
 * 所有的优惠信息
 */
@Data
public class SaleVO {
    public static final String PREFERENTIAL_LEFT = "购买送成长优惠积分：%s";
    public static final String PREFERENTIAL_RIGHT = "购买送购物积分：%s";
    public static final String FULL_REDUCE_LEFT = "满：%s 件";
    public static final String FULL_REDUCE_RIGHT = "减 %s 元";
    public static final String LADDER_PRICE_LEFT = "满：%s 件";
    public static final String LADDER_PRICE_RIGHT = "打 %s 折";
    public static final int PREFERENTIAL = 0;
    public static final int FULL_REDUCE = 1;
    public static final int LADDER_PRICE = 2;
    // 0-优惠券    1-满减    2-阶梯
    private Integer type;

    private String name;//促销信息/优惠券的名字
}
package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wyl
 * @create 2020-06-14 19:33
 */
@Data
public class SkuInfoVO extends SkuInfoEntity {
    // 图片
    private List<String> images;
    // sku销售属性
    private List<SkuSaleAttrValueEntity> saleAttrs;


    // skuBoundsEntity 积分相关
    // 购物积分
    private BigDecimal buyBounds;
    // 成长积分
    private BigDecimal growBounds;
    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;
     * 0 - 无优惠，成长积分是否赠送;
     * 1 - 无优惠，购物积分是否赠送;
     * 2 - 有优惠，成长积分是否赠送;
     * 3 - 有优惠，购物积分是否赠送
     * 【状态位0：不赠送，1：赠送】]
     */
    private List<Integer> work;

    // 阶梯
    // 满几件
    private Integer fullCount;
    // 打几折
    private BigDecimal discount;
    // 是否叠加其他优惠[0-不可叠加，1-可叠加]
    private Integer ladderAddOther;

    // 满减
    // 满多少
    private BigDecimal fullPrice;
    // 减多少
    private BigDecimal reducePrice;
    // 是否参与其他优惠
    private Integer fullAddOther;
}

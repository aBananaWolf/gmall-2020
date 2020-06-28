package com.atguigu.gmall.search.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wyl
 * @create 2020-06-22 16:25
 */
@Data
public class Goods {
    //skuId
    private Long id;
    //skuName
//    private String skuName;

    //所属分类id
    private Long productCategoryId;
    private String productCategoryName;
    //品牌id
    private Long brandId;
    private String brandName;
    //默认图片
    private String pic;
    //标题
    private String name;
    //价格
    private BigDecimal price;

    // 新品
    private Long createTime;
    // 销量
    private Long sale;
    // 库存
    private Long stock;
    // 热度
    private Long sort;

    private List<SearchAttr> attrValueList;

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", productCategoryId=" + productCategoryId +
                ", productCategoryName='" + productCategoryName + '\'' +
                ", brandId=" + brandId +
                ", brandName='" + brandName + '\'' +
                ", pic='" + pic + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", createTime=" + createTime +
                ", sale=" + sale +
                ", stock=" + stock +
                ", sort=" + sort +
                ", attrValueList=" + attrValueList +
                '}';
    }
}

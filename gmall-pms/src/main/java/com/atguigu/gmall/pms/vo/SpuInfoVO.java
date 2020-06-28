package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-14 19:11
 */
@Data
public class SpuInfoVO extends SpuInfoEntity {

    private List<String> spuImages;

    // spu基本属性
    private List<ProductAttrVO> baseAttrs;
//
//    // sku
    private List<SkuInfoVO> skus;
}

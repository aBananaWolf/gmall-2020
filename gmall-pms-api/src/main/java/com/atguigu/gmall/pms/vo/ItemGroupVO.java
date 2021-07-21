package com.atguigu.gmall.pms.vo;

/**
 * @author wyl
 * @create 2020-06-28 19:58
 */

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;

import java.util.List;

/**
 * 基本属性分组及组下的规格参数
 */
@Data
public class ItemGroupVO {
    private String name;
    private List<ProductAttrValueEntity> baseAttrs;
}
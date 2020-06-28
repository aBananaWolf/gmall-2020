package com.atguigu.gmall.search.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wyl
 * @create 2020-06-22 16:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAttr {
    private Long id;  //商品和属性关联的数据表的主键id
    private Long productAttributeId; //当前sku对应的属性的attr_id
    private String name;//属性名  电池
    private String value;//3G   3000mah
    private Long spuId;//这个属性关系对应的spu的id

    @Override
    public String toString() {
        return "SearchAttr{" +
                "id=" + id +
                ", productAttributeId=" + productAttributeId +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", spuId=" + spuId +
                '}';
    }
}

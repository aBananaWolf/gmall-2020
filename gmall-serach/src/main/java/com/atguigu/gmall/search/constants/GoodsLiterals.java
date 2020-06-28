package com.atguigu.gmall.search.constants;

import com.atguigu.gmall.search.entity.SearchAttr;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wyl
 * @create 2020-06-22 17:24
 */
public interface GoodsLiterals {
    String INDEX = "gmall_goods";
    //skuId
    String SKU_ID = "id";
    //spuId
//    String SPU_ID = "spuId";
    /**
     * 所属分类id
     */
    String CATEGORY_ID = "productCategoryId";
    String CATEGORY_NAME = "productCategoryName";
    /**
     * 品牌id
     */
    String BRAND_ID = "brandId";
    String BRAND_NAME = "brandName";
    /**
     * 默认图片
     */
    String SKU_DEFAULT_IMG = "pic";
    /**
     * 标题
     */
    String SKU_TITLE = "name";
    /**
     * 价格
     */
    String PRICE = "price";

    /**
     * 新品
     */
    String CREATE_TIME = "createTime";
    /**
     * 销量
     */
    String SALE_COUNT = "sale";
    /**
     * 库存
     */
    String STOCK = "stock";

    /**
     * 热度分
     */
    String SORT = "sort";

    /**
     * 平台属性映射
     */
    String SEARCH_ATTRS = "attrValueList";

    // 具体的平台属性
    String PRODUCT_ATTR_ID = "id";
    String ATTR_ID = "productAttributeId";
    String ATTR_NAME = "name";
    String ATTR_VALUE = "value";
    String SPU_ID = "spuId";

}

package com.atguigu.gmall.search.constants;

public interface SearchParamDisplayLiterals {
    int defaultPageSize = 40;
    //品牌 和 分类聚合 此时vo对象中的id字段保留（不用写） name就是“品牌” value: [{id:100,name:华为,logo:xxx},{id:101,name:小米,log:yyy}]
    String AGGS_DISPLAY_OBJ_ID = "id";
    String AGGS_DISPLAY_OBJ_Name = "name";
    String AGGS_DISPLAY_OBJ_LOGO = "logo";
}

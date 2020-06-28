package com.atguigu.gmall.search.entity;

import lombok.Data;

import java.util.List;


/**
 * @author wyl
 * @create 2020-06-24 17:26
 */
@Data
public class SearchParam {
    private String[] catelog3;
    private String[] brand;
    private String keyword;
    private String order;
    private Integer pageNum;
    private List<String> props;
    private Integer pageSize;
    private Integer priceFrom;
    private Integer priceTo;
}

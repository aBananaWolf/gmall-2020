package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-24 12:39
 */
@Data
public class ProductAttrVO {
   private Long attrId;
   private String attrName;
   private List<String> valueSelected;
}

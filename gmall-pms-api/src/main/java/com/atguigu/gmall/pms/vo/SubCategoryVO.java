package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-27 14:51
 */
@Data
public class SubCategoryVO extends CategoryEntity {
    public List<CategoryEntity> subs;
}

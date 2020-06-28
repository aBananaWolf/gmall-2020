package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.api.PMSIndexApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.SubCategoryVO;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-28 11:48
 */
public interface IndexService {
    List<CategoryEntity> listOneLevelCategory();

    List<SubCategoryVO> listSubCategory(Long categoryId);
}

package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.SubCategoryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/index")
public interface PMSIndexApi {
    @GetMapping("/category/oneLevel")
    List<CategoryEntity> listOneLevelCategory();

    @GetMapping("/category/{categoryId}")
    List<SubCategoryVO> listSubCategory(@PathVariable("categoryId") Long categoryId);

}

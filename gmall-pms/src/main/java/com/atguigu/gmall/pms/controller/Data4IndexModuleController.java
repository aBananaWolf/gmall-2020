package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.pms.api.PMSIndexApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;
import com.atguigu.gmall.pms.vo.SubCategoryVO;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-27 14:59
 */
@RequestMapping("/index")
@RestController
public class Data4IndexModuleController implements PMSIndexApi {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/oneLevel")
    public List<CategoryEntity> listOneLevelCategory() {
        return categoryService.listOneLevelCategory();
    }

    @GetMapping("/category/{categoryId}")
    public List<SubCategoryVO> listSubCategory(@PathVariable("categoryId") Long categoryId){
        return categoryService.listSubCategory(categoryId);
    }
}

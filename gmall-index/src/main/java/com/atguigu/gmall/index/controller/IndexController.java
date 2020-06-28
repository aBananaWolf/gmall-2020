package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.api.IndexApiService;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.api.PMSIndexApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.SubCategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-27 13:03
 */
@RequestMapping("/index")
@RestController
@Api(tags = "商品门户")
public class IndexController{
    @Autowired
    private IndexService indexService;

    @GetMapping("/cates")
    public Resp<List<CategoryEntity>> listOneLevelCategory() {
        List<CategoryEntity> categoryEntities = indexService.listOneLevelCategory();
        return Resp.ok(categoryEntities);
    }
    @GetMapping("/cates/{categoryId}")
    public Resp<List<SubCategoryVO>> listSubCategory(@PathVariable("categoryId") Long categoryId){
        List<SubCategoryVO> subCategoryVOS = indexService.listSubCategory(categoryId);
        return Resp.ok(subCategoryVOS);
    }

}

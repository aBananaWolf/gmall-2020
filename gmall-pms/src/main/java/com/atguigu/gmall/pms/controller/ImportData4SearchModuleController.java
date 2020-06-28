package com.atguigu.gmall.pms.controller;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.api.PMSDataImportApi;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-23 15:37
 */
@RestController
@RequestMapping("/search")
public class ImportData4SearchModuleController implements PMSDataImportApi {

    @Autowired
    private ProductAttrValueService attrService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private SkuInfoService skuInfoService;

    @ApiOperation("被检索的商品参数")
    @GetMapping("/attr/api/searchattrs/cat/{spuId}")
    public List<ProductAttrValueEntity> listSearchAttr(@PathVariable("spuId") Long spuId) {
        return attrService.listSearchAttr(spuId);
    }

    @ApiOperation("品牌详细")
    @GetMapping("/brand/api/info/{brandId}")
    public BrandEntity brandInfo(@PathVariable("brandId") Long brandId){
        return brandService.getById(brandId);
    }

    @ApiOperation("分类详细")
    @GetMapping("/category/api/info/{catId}")
    public CategoryEntity categoryInfo(@PathVariable("catId") Long catId) {
        return categoryService.getById(catId);
    }

    /**
     * 列表
     */
    @ApiOperation("搜索分页查询(排序)")
    @PostMapping("/skuinfo/api/listBySearchModule/{spuId}")
    public List<SkuInfoEntity> listSkuBySearchModule(@RequestBody QueryCondition queryCondition, @PathVariable("spuId") Long spuId) {
        return skuInfoService.queryPage(queryCondition,spuId);
    }

    /**
     * 列表
     */
    @ApiOperation("搜索模块分页查询(排序)")
    @PostMapping("/spuinfo/api/listByPublished")
    public List<SpuInfoEntity> listSpuByPublished(@RequestBody QueryCondition queryCondition) {
        return spuInfoService.listByPublished(queryCondition);
    }

    @ApiOperation("详情查询")
    @GetMapping("/spuinfo/{id}")
    public SpuInfoEntity info(@PathVariable("id") Long id){
        return spuInfoService.getById(id);
    }
}

package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.entity.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/search")
public interface PMSDataImportApi {
    @ApiOperation("被检索的商品参数")
    @GetMapping("/attr/api/searchattrs/cat/{spuId}")
    List<ProductAttrValueEntity> listSearchAttr(@PathVariable("spuId") Long spuId);

    @ApiOperation("品牌详细")
    @GetMapping("/brand/api/info/{brandId}")
    BrandEntity brandInfo(@PathVariable("brandId") Long brandId);

    @ApiOperation("分类详情查询")
    @GetMapping("/category/api/info/{catId}")
    CategoryEntity categoryInfo(@PathVariable("catId") Long catId);

    @ApiOperation("sku分页查询(排序)")
    @PostMapping("/skuinfo/api/listBySearchModule/{spuId}")
    List<SkuInfoEntity> listSkuBySearchModule(@RequestBody QueryCondition queryCondition, @PathVariable("spuId") Long spuId);

    @ApiOperation("发布的spu分页查询(排序)")
    @PostMapping("/spuinfo/api/listByPublished")
    List<SpuInfoEntity> listSpuByPublished(@RequestBody QueryCondition queryCondition);

    @ApiOperation("详情查询")
    @GetMapping("/spuinfo/{id}")
    SpuInfoEntity info(@PathVariable("id") Long id);

}

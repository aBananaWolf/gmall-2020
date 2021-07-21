package com.atguigu.gmall.pms.controller;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.api.PMSItemApi;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wyl
 * @create 2020-06-28 19:26
 */
@RestController
@RequestMapping("/item")
public class Data4ItemModuleController implements PMSItemApi {
    /**
     //3、sku的所有促销信息
     private List<SaleVO> sales;

     */
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    /**
     * 信息
     */
    @ApiOperation("当前sku的基本信息")
    @GetMapping("/sku/info/{skuId}")
    public SkuInfoEntity skuInfo(@PathVariable("skuId") Long skuId){
        return skuInfoService.getById(skuId);
    }


    /**
     * 信息
     */
    @ApiOperation("sku的所有图片")
    @GetMapping("/img/info/{skuId}")
    public List<SkuImagesEntity> imgInfo(@PathVariable("skuId") Long skuId){
        QueryWrapper<SkuImagesEntity> images = new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId);
        return skuImagesService.list(images);
    }

    @ApiOperation("spu的所有基本属性")
    @GetMapping("/{spuId}/{categoryId}")
    public List<ItemGroupVO> listGroupAndAttrByCategoryId(@NotNull @PathVariable("spuId") Long spuId, @NotNull @PathVariable("categoryId") Long categoryId) {
        return productAttrValueService.listGroupAndProductAttrBySpuId(spuId, categoryId);
    }

    /**
     * 信息
     */
    @ApiOperation("详情介绍")
    @GetMapping("/spuDesc/info/{spuId}")
    public SpuInfoDescEntity spuDescInfo(@PathVariable("spuId") Long spuId){
        return spuInfoDescService.getById(spuId);
    }

    @ApiOperation("sku销售属性")
    @GetMapping("/saleAttr/{spuId}")
    public List<SkuSaleAttrValueEntity> listSaleAttrsBySpuId(@PathVariable("spuId") Long spuId) {
        return skuSaleAttrValueService.listSaleAttrsBySpuId(spuId);
    }
}

package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/item")
public interface PMSItemApi {

    /**
     * 信息
     */
    @ApiOperation("当前sku的基本信息")
    @GetMapping("/sku/info/{skuId}")
    public SkuInfoEntity skuInfo(@PathVariable("skuId") Long skuId);



    /**
     * 信息
     */
    @ApiOperation("sku的所有图片")
    @GetMapping("/img/info/{id}")
    public List<SkuImagesEntity> imgInfo(@PathVariable("id") Long id);

    @ApiOperation("spu的所有基本属性")
    @GetMapping("/{spuId}/{categoryId}")
    public List<ItemGroupVO> listGroupAndAttrByCategoryId(@NotNull @PathVariable("spuId") Long spuId, @NotNull @PathVariable("categoryId") Long categoryId) ;

    /**
     * 信息
     */
    @ApiOperation("详情介绍")
    @GetMapping("/spuDesc/info/{spuId}")
    public SpuInfoDescEntity spuDescInfo(@PathVariable("spuId") Long spuId);

    @ApiOperation("sku销售属性")
    @GetMapping("/saleAttr/{spuId}")
    public List<SkuSaleAttrValueEntity> listSaleAttrsBySpuId(@PathVariable("spuId") Long spuId) ;
}
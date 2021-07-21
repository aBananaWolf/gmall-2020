package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.annotation.GmallCache;
import com.atguigu.gmall.item.api.ItemApiService;
import com.atguigu.gmall.item.api.PreferentialApiService;
import com.atguigu.gmall.item.api.SearchApiService;
import com.atguigu.gmall.item.exception.ItemCompletionFutureException;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.*;
import com.atguigu.gmall.sms.vo.SaleVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * @author wyl
 * @create 2020-06-28 19:32
 */
@Service
@Slf4j
@GmallCache(lockName = "item:lock:info")
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemApiService itemApiService;
    @Autowired
    private SearchApiService apiService;
    @Autowired
    private PreferentialApiService preferentialApiService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    private final String CACHE_PREFIX = "item:info:";

    @Override
//    @GmallCache(prefix = CACHE_PREFIX)
    public ItemVO getAndRenderItem(Long skuId) {
        ItemVO itemVO = new ItemVO();
        // sku详细
        CompletableFuture<SkuInfoEntity> skuInfoEntityCompletableFuture = CompletableFuture
                .supplyAsync(() -> {
                            SkuInfoEntity skuInfoEntity = itemApiService.skuInfo(skuId);
                            verificationData(skuInfoEntity);
                            itemVO.setSkuId(skuInfoEntity.getSkuId());
                            itemVO.setSpuId(skuInfoEntity.getSpuId());
                            itemVO.setSkuTitle(skuInfoEntity.getSkuTitle());
                            itemVO.setSubTitle(skuInfoEntity.getSkuSubtitle());
                            itemVO.setPrice(skuInfoEntity.getPrice());
                            itemVO.setWeight(skuInfoEntity.getWeight());
                            return skuInfoEntity;
                        } ,threadPoolExecutor);
        // sku图片
        CompletableFuture<Void> skuImagesEntityCompletableFuture = CompletableFuture
                .runAsync(() -> {
                            List<SkuImagesEntity> skuImagesEntities = itemApiService.imgInfo(skuId);
                            List<String> images = skuImagesEntities.stream().map(SkuImagesEntity::getImgUrl).collect(toList());
                            itemVO.setPics(images);
                        } ,threadPoolExecutor
                );
        // 优惠信息
        CompletableFuture<Void> saleVOCompletableFuture = CompletableFuture
                .runAsync(() -> {
                            List<SaleVO> saleVOS = preferentialApiService.allPreferential(skuId);
                            itemVO.setSales(saleVOS);
                        } , threadPoolExecutor
                );
        // 组及关联的商品基本属性
        CompletableFuture<Void> itemGroupVOCompletableFuture = skuInfoEntityCompletableFuture
                .thenAcceptAsync(skuInfoEntity -> {
                            List<ItemGroupVO> itemGroupVOS = itemApiService.listGroupAndAttrByCategoryId(skuInfoEntity.getSpuId(), skuInfoEntity.getCatalogId());
                            itemVO.setGroups(itemGroupVOS);
                        } , threadPoolExecutor
                );
        // sku的销售属性
        CompletableFuture<Void> skuSaleAttrValueEntityCompletableFuture = skuInfoEntityCompletableFuture
                .thenAcceptAsync(skuInfoEntity -> {
                            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = itemApiService.listSaleAttrsBySpuId(skuInfoEntity.getSpuId());
                            itemVO.setSaleAttrs(skuSaleAttrValueEntities);
                        }, threadPoolExecutor
                );
        // 分类
        CompletableFuture<Void> categoryEntityCompletableFuture = skuInfoEntityCompletableFuture
                .thenAcceptAsync(skuInfoEntity -> {
                            CategoryEntity categoryEntity = apiService.categoryInfo(skuInfoEntity.getCatalogId());
                            itemVO.setCategoryEntity(categoryEntity);
                        }, threadPoolExecutor
                );
        // 品牌
        CompletableFuture<Void> brandEntityCompletableFuture = skuInfoEntityCompletableFuture.
                thenAcceptAsync(skuInfoEntity -> {
                            BrandEntity brandEntity = apiService.brandInfo(skuInfoEntity.getBrandId());
                            itemVO.setBrandEntity(brandEntity);
                        }, threadPoolExecutor
                );

        try {
            CompletableFuture.allOf(skuInfoEntityCompletableFuture, skuImagesEntityCompletableFuture,
                    saleVOCompletableFuture, itemGroupVOCompletableFuture, skuSaleAttrValueEntityCompletableFuture,
                    categoryEntityCompletableFuture, brandEntityCompletableFuture)
                    .join();
        } catch (Exception e) {
            log.warn("this could be a malicious attack , ItemModule completionFutureException：", e);
        }

        return itemVO;
        /**
        // sku详细
        SkuInfoEntity skuInfoEntity = itemApiService.skuInfo(skuId);
        // sku图片
        List<SkuImagesEntity> skuImagesEntities = itemApiService.imgInfo(skuId);


        SpuInfoDescEntity spuInfoDescEntity = itemApiService.spuDescInfo(skuInfoEntity.getSpuId());
        // 组及关联的商品基本属性
        List<ItemGroupVO> groupVOS = itemApiService.listGroupAndAttrByCategoryId(skuInfoEntity.getSpuId(),skuInfoEntity.getCatalogId());
        // sku的销售属性
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = itemApiService.listSaleAttrsBySpuId(skuInfoEntity.getSpuId());
        // 分类
        CategoryEntity categoryEntity = apiService.categoryInfo(skuInfoEntity.getCatalogId());
        // 品牌
        BrandEntity brandEntity = apiService.brandInfo(skuInfoEntity.getBrandId());
        // 优惠信息
        List<SaleVO> saleVOS = preferentialApiService.allPreferential(skuInfoEntity.getSkuId());


        ItemVO itemVO = new ItemVO();
        itemVO.setSkuId(skuInfoEntity.getSkuId());
        itemVO.setSpuId(skuInfoEntity.getSpuId());
        itemVO.setCategoryEntity(categoryEntity);
        itemVO.setBrandEntity(brandEntity);
        itemVO.setSkuTitle(skuInfoEntity.getSkuTitle());
        itemVO.setSubTitle(skuInfoEntity.getSkuSubtitle());
        itemVO.setPrice(skuInfoEntity.getPrice());
        itemVO.setWeight(skuInfoEntity.getWeight());
        List<String> images = skuImagesEntities.stream().map(SkuImagesEntity::getImgUrl).collect(toList());
        itemVO.setPics(images);
        itemVO.setSaleAttrs(skuSaleAttrValueEntities);
        /**
         *     {@link BaseGroupVO}
         *     private Long id;
         *     private String name;//分组的名字
         *     private List{@link BaseAttrVO} attrs;
         *
         *     {@link BaseAttrVO}
         *     private Long attrId;
         *     private String attrName;
         *     private String[]  attrValues;
         /
        itemVO.setGroups(groupVOS);
        itemVO.setSales(saleVOS);
        return itemVO;
        */

    }

    private void verificationData(Object obj) {
        if (obj == null)
            throw new ItemCompletionFutureException("com.atguigu.gmall.item.service.impl.ItemServiceImpl.getAndRenderItem");
    }
}

package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.api.SMSPreferentialInfoService;
import com.atguigu.gmall.pms.config.BasicMessageSender;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.vo.SkuPreferentialVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.WrongMethodTypeException;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import static java.util.stream.Collectors.toList;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SMSPreferentialInfoService preferentialInfoService;

    @Autowired
    private BasicMessageSender basicMessageSender;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo findSpuList(QueryCondition queryCondition, Long catId) {

        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<>();
        if (catId != 0) {
            spuInfoEntityQueryWrapper.eq("catalog_id", catId);
        }
        if (StringUtils.isNotBlank(queryCondition.getKey())) {
            spuInfoEntityQueryWrapper.and(key -> key.like("spu_name", key).or().eq("id", key));
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(queryCondition),
                spuInfoEntityQueryWrapper
        );

        return new PageVo(page);
    }

    @Override
    @GlobalTransactional
    public void saveSpuSkuAndPreferentialInfo(SpuInfoVO spuInfoVO) {
        // 1.保存SPU
        // 1.1 保存info
        Date date = new Date();
        spuInfoVO.setCreateTime(date);
        spuInfoVO.setUodateTime(date);
        this.save(spuInfoVO);
        // 1.2 保存image
        this.saveSpuImage(spuInfoVO);
        // 1.3 保存productAttrValue
        this.saveProductAttrValue(spuInfoVO);
        // 1.4 保存desc
        this.saveSpuDescription(spuInfoVO);
        // 2.保存sku
        if (!CollectionUtils.isEmpty(spuInfoVO.getSkus())) {
            spuInfoVO.getSkus().forEach(skuVO -> {
                // 2.1.保存skuInfo
                this.saveSkuInfo(skuVO,spuInfoVO);

                // 2.2.保存image
                this.saveSkuImage(skuVO);

                // 2.3.保存skuSaleAttrValue
                this.saveSkuSaleAttrValue(skuVO);

                // 3.优惠信息 sms 表
                // 调用api接口
                preferentialInfoService.savePreferentialInfo(getSkuPreferentialVO(skuVO));
            });
        }
    }

    @Override
    public List<SpuInfoEntity> listByPublished(QueryCondition params) {
        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<>();
        spuInfoEntityQueryWrapper.eq("publish_status",1);
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                spuInfoEntityQueryWrapper
        );

        return page.getRecords();
    }

    @Override
    public void updateSearchData(Long id) {
        basicMessageSender.sendMessage(String.valueOf(id),"search.save.goods");
    }

    /**
     * 根据sku信息生成优惠实体
     * @param skuVO
     * @return
     */
    private SkuPreferentialVO getSkuPreferentialVO(SkuInfoVO skuVO) {
        SkuPreferentialVO skuPreferentialVO = new SkuPreferentialVO();
        skuPreferentialVO.setSkuId(skuVO.getSkuId());
        // 3.1.sku_bounds 积分
        skuPreferentialVO.setBuyBounds(skuVO.getBuyBounds());
        skuPreferentialVO.setGrowBounds(skuVO.getGrowBounds());
        skuPreferentialVO.setWork(calculationWork(skuVO.getWork()));
        // 3.2.sku_ladder 打折(阶梯价格)
        skuPreferentialVO.setFullCount(skuVO.getFullCount());
        skuPreferentialVO.setDiscount(skuVO.getDiscount());
        skuPreferentialVO.setLadderAddOther(skuVO.getLadderAddOther());
        // 3.3.sku_full_reduction
        skuPreferentialVO.setFullPrice(skuVO.getFullPrice());
        skuPreferentialVO.setReducePrice(skuVO.getReducePrice());
        skuPreferentialVO.setFullAddOther(skuVO.getFullAddOther());
        return skuPreferentialVO;
    }

    /**
     * 保存sku销售属性
     * @param skuVO
     * @param id
     */
    private void saveSkuSaleAttrValue(SkuInfoVO skuVO) {
        skuVO.setSaleAttrs(skuSaleAttrValueService.listSearchAttr(skuVO.getSkuId()));
        if (!CollectionUtils.isEmpty(skuVO.getSaleAttrs())) {
            skuVO.getSaleAttrs()
                    .forEach(saleAttr -> {
                        SkuSaleAttrValueEntity skuSaleEntity = new SkuSaleAttrValueEntity();
                        skuSaleEntity.setAttrId(saleAttr.getAttrId());
                        skuSaleEntity.setAttrName(saleAttr.getAttrName());
                        skuSaleEntity.setAttrSort(saleAttr.getAttrSort());
                        skuSaleEntity.setAttrValue(saleAttr.getAttrValue());
                        skuSaleEntity.setSkuId(skuVO.getSkuId());
                        skuSaleAttrValueService.save(skuSaleEntity);
                    });
        }
    }

    /**
     * 保存sku图片
     * @param skuVO
     *
     */
    private void saveSkuImage(SkuInfoVO skuVO) {
        if (!CollectionUtils.isEmpty(skuVO.getImages())) {
            skuVO.getImages().forEach(imageUrl -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuVO.getSkuId());
                skuImagesEntity.setImgUrl(imageUrl);
                skuImagesEntity.setDefaultImg(
                        StringUtils.isNotBlank(skuVO.getSkuDefaultImg())
                                && skuVO.getSkuDefaultImg().equals(imageUrl)
                                ? 1 : 0
                );
                skuImagesService.save(skuImagesEntity);
            });
        }
    }

    /**
     * 保存SkuInfo
     * @param skuVO
     * @param spuInfoVO
     */
    private void saveSkuInfo(SkuInfoVO skuVO, SpuInfoVO spuInfoVO) {
        Random random = new Random();
        int code = random.nextInt(300);
        skuVO.setSkuCode(String.valueOf(code));
        skuVO.setBrandId(spuInfoVO.getBrandId());
        skuVO.setCatalogId(spuInfoVO.getCatalogId());
        if (!CollectionUtils.isEmpty(skuVO.getImages())) {
            skuVO.setSkuDefaultImg(StringUtils.isNotBlank(skuVO.getSkuDefaultImg())
                    ? skuVO.getSkuDefaultImg() :
                    (CollectionUtils.isEmpty(skuVO.getImages()) ? null : skuVO.getImages().get(0)));
        }
        skuVO.setSpuId(spuInfoVO.getId());
        skuInfoService.save(skuVO);
    }

    /**
     * 保存spu描述
     * @param spuInfoVO
     */
    private void saveSpuDescription(SpuInfoVO spuInfoVO) {
        if (StringUtils.isNotBlank(spuInfoVO.getSpuDescription())){
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setDecript(spuInfoVO.getSpuDescription());
            spuInfoDescEntity.setSpuId(spuInfoVO.getId());
            spuInfoDescService.save(spuInfoDescEntity);
        }
    }

    /**
     * 保存spu基本属性
     * @param spuInfoVO
     */
    private void saveProductAttrValue(SpuInfoVO spuInfoVO) {
        if (!CollectionUtils.isEmpty(spuInfoVO.getBaseAttrs()))
            spuInfoVO.getBaseAttrs().stream()
                    .map(productAttrVO -> {
                        ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                        productAttrValueEntity.setSpuId(spuInfoVO.getId());
                        productAttrValueEntity.setAttrId(productAttrVO.getAttrId());
                        productAttrValueEntity.setAttrName(productAttrVO.getAttrName());
                        productAttrValueEntity.setAttrValue(StringUtils.join(productAttrVO.getValueSelected(),","));
                        return productAttrValueEntity;
                    })
                    .collect(toList())
                    .forEach(spuAttrEntity -> productAttrValueService.save(spuAttrEntity));
    }

    /**
     * 保存图片信息
     * @param spuInfoVO
     */
    private void saveSpuImage(SpuInfoVO spuInfoVO) {
        if (!CollectionUtils.isEmpty(spuInfoVO.getSpuImages()))
            spuInfoVO.getSpuImages().stream()
                    .map(imageStr -> {
                        SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                        spuImagesEntity.setSpuId(spuInfoVO.getId());
                        spuImagesEntity.setImgName(UUID.randomUUID().toString().replaceAll("\\-","") + System.currentTimeMillis());
                        spuImagesEntity.setImgUrl(imageStr);
                        return spuImagesEntity;
                    })
                    .collect(toList())
                    .forEach(imageEntity -> spuImagesService.save(imageEntity));
    }

    public int calculationWork(List<Integer> work) {
        return work.get(3) | work.get(2) << 1 | work.get(1) << 2 | work.get(0) << 3;
    }
}
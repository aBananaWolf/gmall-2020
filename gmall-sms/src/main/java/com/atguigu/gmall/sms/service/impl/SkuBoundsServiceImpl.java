package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import com.atguigu.gmall.sms.service.SkuLadderService;
import com.atguigu.gmall.sms.vo.SkuPreferentialVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuBoundsService skuBoundsService;

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private SkuFullReductionService skuFullReductionService;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void savePreferentialInfo(SkuPreferentialVO skuPreferentialVO) {
        // sms_sku_bounds
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        skuBoundsEntity.setSkuId(skuPreferentialVO.getSkuId());
        skuBoundsEntity.setGrowBounds(skuPreferentialVO.getGrowBounds());
        skuBoundsEntity.setBuyBounds(skuPreferentialVO.getBuyBounds());
        skuBoundsEntity.setWork(skuPreferentialVO.getWork());
        skuBoundsService.save(skuBoundsEntity);
        // sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setSkuId(skuPreferentialVO.getSkuId());
        skuFullReductionEntity.setFullPrice(skuPreferentialVO.getFullPrice());
        skuFullReductionEntity.setReducePrice(skuPreferentialVO.getReducePrice());
        skuFullReductionEntity.setAddOther(skuPreferentialVO.getFullAddOther());
        skuFullReductionService.save(skuFullReductionEntity);
        // sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setDiscount(skuPreferentialVO.getDiscount());
        skuLadderEntity.setSkuId(skuPreferentialVO.getSkuId());
        skuLadderEntity.setFullCount(skuPreferentialVO.getFullCount());
        skuLadderEntity.setAddOther(skuPreferentialVO.getLadderAddOther());
        skuLadderService.save(skuLadderEntity);
    }

}
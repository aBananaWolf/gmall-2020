package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import com.atguigu.gmall.sms.service.SkuLadderService;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.sms.vo.SkuPreferentialVO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

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

    private Predicate<String> judge = str -> str != null && !"".equals(str) && Integer.parseInt(str) > 0;
    private BiPredicate<String,String> BiParamJudge = (str1,str2) -> judge.test(str1) && judge.test(str2);


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

    @Override
    public List<SaleVO> getAllPreferential(Long skuId) {
        ArrayList<SaleVO> saleList = new ArrayList<>(3);

        // 优惠
        QueryWrapper<SkuBoundsEntity> skuBoundsEntityQueryWrapper = new QueryWrapper<>();
        skuBoundsEntityQueryWrapper.eq("sku_id", skuId);
        SkuBoundsEntity skuBoundsEntity = skuBoundsService.getOne(skuBoundsEntityQueryWrapper);
        SaleVO preferential = this.extractInfo(skuBoundsEntity.getGrowBounds().toString(), skuBoundsEntity.getBuyBounds().toString(), SaleVO.PREFERENTIAL);
        if (preferential != null )
            saleList.add(preferential);

        // 阶梯
        QueryWrapper<SkuLadderEntity> skuLadderEntityQueryWrapper = new QueryWrapper<>();
        skuLadderEntityQueryWrapper.eq("sku_id", skuId);
        SkuLadderEntity skuLadderEntity = skuLadderService.getOne(skuLadderEntityQueryWrapper);
        SaleVO ladder = this.extractInfo(skuLadderEntity.getFullCount().toString(), skuLadderEntity.getDiscount().divide(new BigDecimal("10")).toString(), SaleVO.LADDER_PRICE);
        if (ladder != null )
            saleList.add(ladder);

        // 折扣
        QueryWrapper<SkuFullReductionEntity> skuFullReductionEntityQueryWrapper = new QueryWrapper<>();
        skuFullReductionEntityQueryWrapper.eq("sku_id", skuId);
        SkuFullReductionEntity fullReductionEntity = skuFullReductionService.getOne(skuFullReductionEntityQueryWrapper);
        SaleVO reduce = this.extractInfo(fullReductionEntity.getFullPrice().toString(), fullReductionEntity.getReducePrice().toString(), SaleVO.FULL_REDUCE);
        if (reduce != null )
            saleList.add(ladder);

        if (saleList.size() == 0) {
            return null;
        }

        return saleList;
    }

    private SaleVO extractInfo (String arg1, String arg2, int preferentialType) {

        String preferentialPatternLeft = null;
        String preferentialPatternRight = null;
        switch (preferentialType) {
            case SaleVO.PREFERENTIAL:
                preferentialPatternLeft = SaleVO.PREFERENTIAL_LEFT;
                preferentialPatternRight = SaleVO.PREFERENTIAL_RIGHT;
                if (!judge.test(arg1) && !judge.test(arg2))
                    return null;

                break;
            case SaleVO.FULL_REDUCE:
                preferentialPatternLeft = SaleVO.FULL_REDUCE_LEFT;
                preferentialPatternRight = SaleVO.FULL_REDUCE_RIGHT;
                if (!BiParamJudge.test(arg1,arg2))
                    return null;
                break;
            case SaleVO.LADDER_PRICE:
                preferentialPatternLeft = SaleVO.LADDER_PRICE_LEFT;
                preferentialPatternRight = SaleVO.LADDER_PRICE_RIGHT;
                if (!BiParamJudge.test(arg1,arg2))
                    return null;
                break;
        }

        SaleVO preferential = new SaleVO();
        preferential.setType(preferentialType);
        StringBuilder builder = new StringBuilder();
        boolean BuyBoundIsEmp = StringUtils.isNotEmpty(arg1);
        if (BuyBoundIsEmp){
            builder.append(String.format(preferentialPatternLeft,arg1));
        }
        if (StringUtils.isNotEmpty(arg2)) {
            if (!BuyBoundIsEmp)
                builder.append("，");
            builder.append(String.format(preferentialPatternRight,arg2));
        }
        preferential.setName(builder.toString());
        return preferential;
    }

}
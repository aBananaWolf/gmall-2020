package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.vo.ItemGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private ProductAttrValueDao productAttrValueDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<ProductAttrValueEntity> listSearchAttr(Long spuId) {
        return productAttrValueDao.listSearchAttr(spuId);
//        return this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
    }

    @Override
    public List<ItemGroupVO> listGroupAndProductAttrBySpuId(Long spuId, Long categoryId) {
        return productAttrValueDao.listGroupAndProductAttrBySpuId(spuId, categoryId);
    }

}
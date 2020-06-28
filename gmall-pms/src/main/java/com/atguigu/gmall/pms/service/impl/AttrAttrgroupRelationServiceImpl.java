package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.Resp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.service.AttrAttrgroupRelationService;

import static java.util.stream.Collectors.toList;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void deleteRelation(List<AttrAttrgroupRelationEntity> relationEntity) {
        QueryWrapper<AttrAttrgroupRelationEntity> relationEntityQueryWrapper = new QueryWrapper<>();
        List<Long> attrIds = relationEntity.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(toList());
        List<Long> groupIds = relationEntity.stream().map(AttrAttrgroupRelationEntity::getAttrGroupId).collect(toList());
        relationEntityQueryWrapper.in("attr_id",attrIds);
        relationEntityQueryWrapper.in("attr_group_id",groupIds);
        this.remove(relationEntityQueryWrapper);
    }

}
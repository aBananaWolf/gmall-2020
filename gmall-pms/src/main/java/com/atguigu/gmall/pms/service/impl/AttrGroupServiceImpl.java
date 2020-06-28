package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.dao.GroupAndAttr;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.GroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;

import java.util.List;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private GroupAndAttr groupAndAttr;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public GroupVO selectByGroup(Long gid) {
        return groupAndAttr.selectByGroup(gid);
    }

    @Override
    public PageVo listGroupByCategoryId(QueryCondition queryCondition, Long categoryId) {

        IPage<AttrGroupEntity> catelogId = this.page(
                new Query<AttrGroupEntity>().getPage(queryCondition),
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", categoryId)
        );

        return new PageVo(catelogId);
    }

    @Override
    public List<GroupVO> listGroupAndAttrByCategoryId(Long categoryId) {
        return groupAndAttr.listGroupAndAttrByCategoryId(categoryId);
    }

    @Override
    public Object listGroupByCondition(Long categoryId, QueryCondition queryCondition) {
        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<>();
        attrGroupEntityQueryWrapper.eq("catelog_id", categoryId);
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(queryCondition),
                attrGroupEntityQueryWrapper);
        return new PageVo(page);
    }


}
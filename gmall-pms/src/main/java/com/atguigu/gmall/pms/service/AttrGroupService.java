package com.atguigu.gmall.pms.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-06-07 14:49:29
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    GroupVO selectByGroup(Long gid);

    PageVo listGroupByCategoryId(QueryCondition queryCondition, Long categoryId);

    List<GroupVO> listGroupAndAttrByCategoryId(Long categoryId);


    Object listGroupByCondition(Long categoryId, QueryCondition queryCondition);
}


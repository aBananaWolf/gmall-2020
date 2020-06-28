package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.GroupVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupAndAttr {
    GroupVO selectByGroup(Long id);

    List<GroupVO> listGroupAndAttrByCategoryId(Long categoryId);

}

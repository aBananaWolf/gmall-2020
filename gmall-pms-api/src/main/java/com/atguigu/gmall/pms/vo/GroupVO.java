package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-13 18:15
 */
@Data
public class GroupVO extends AttrGroupEntity {
    private List<AttrEntity>  attrEntities;
    private List<AttrAttrgroupRelationEntity> relations;
}

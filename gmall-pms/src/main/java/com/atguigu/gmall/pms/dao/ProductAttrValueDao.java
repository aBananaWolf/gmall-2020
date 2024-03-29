package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 * 
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-06-07 14:49:29
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    List<ProductAttrValueEntity> listSearchAttr(Long spuId);

    List<ItemGroupVO> listGroupAndProductAttrBySpuId(@Param("spuId") Long spuId, @Param("categoryId")Long categoryId);
}

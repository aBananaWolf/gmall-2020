package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.ItemGroupVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * spu属性值
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-06-07 14:49:29
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageVo queryPage(QueryCondition params);

    List<ProductAttrValueEntity> listSearchAttr(Long spuId);

    List<ItemGroupVO> listGroupAndProductAttrBySpuId(@NotNull Long spuId, @NotNull Long categoryId);
}


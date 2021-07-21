package com.atguigu.gmall.item.service;

import com.atguigu.gmall.pms.vo.ItemVO;

public interface ItemService {
    ItemVO getAndRenderItem(Long skuId);
}

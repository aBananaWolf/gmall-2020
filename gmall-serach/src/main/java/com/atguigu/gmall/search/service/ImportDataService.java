package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.entity.Goods;

/**
 * @author wyl
 * @create 2020-06-23 15:28
 */

public interface ImportDataService {
    void importData(Goods goods);
    void importDataBySpuId(Long spuId);
}

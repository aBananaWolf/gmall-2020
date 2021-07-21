package com.atguigu.gmall.search.service;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttr;

import java.util.ArrayList;

/**
 * @author wyl
 * @create 2020-06-23 15:28
 */

public interface ImportDataService {
    void importData(Goods goods);
    void importDataBySpuId(Long spuId);
    public void queryDataAndImport(SpuInfoEntity spu, Long skuInitialPage, Long pageSize, boolean skuDataImportFinish, QueryCondition skuQueryCondition, ArrayList<SearchAttr> goodsSearchAttrs, Goods goods) ;

    }

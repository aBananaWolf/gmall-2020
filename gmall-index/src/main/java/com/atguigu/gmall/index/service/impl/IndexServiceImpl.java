package com.atguigu.gmall.index.service.impl;

import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.api.IndexApiService;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.api.PMSIndexApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.SubCategoryVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-28 11:11
 */
@Service
@GmallCache(lockName = "index:lock:category")
public class IndexServiceImpl implements IndexService {
    @Autowired
    private IndexApiService indexApiService;

    private final String CACHE_PREFIX = "index:category:";

    @Override
    @GmallCache(prefix = CACHE_PREFIX, cacheKey = "oneLevelCategory")
    public List<CategoryEntity> listOneLevelCategory() {
        return indexApiService.listOneLevelCategory();
    }

    @Override
    @GmallCache(prefix = CACHE_PREFIX)
    public List<SubCategoryVO> listSubCategory(Long categoryId) {
        return indexApiService.listSubCategory(categoryId);
    }
}

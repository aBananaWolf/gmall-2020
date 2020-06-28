package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.entity.SearchParam;
import com.atguigu.gmall.search.entity.SearchResponse;

public interface SearchService {
    SearchResponse search(SearchParam searchParam);
}

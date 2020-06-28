package com.atguigu.gmall.search.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.search.entity.SearchParam;
import com.atguigu.gmall.search.entity.SearchResponse;
import com.atguigu.gmall.search.service.SearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wyl
 * @create 2020-06-24 17:33
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    @Autowired
    private SearchService searchService;
    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping
    public SearchResponse searchData(SearchParam searchParam) {
        SearchResponse search = null;
        try {
            search = searchService.search(searchParam);
        } catch (Exception e) {
            log.error("search module exception ",e);
        }
        if (search == null)
            log.error("search module data validation exception error");
        return search;
    }
}

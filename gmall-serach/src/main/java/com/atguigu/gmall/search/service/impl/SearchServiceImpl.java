package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.search.constants.AggsLiterals;
import com.atguigu.gmall.search.constants.GoodsLiterals;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchParam;
import com.atguigu.gmall.search.constants.SearchParamDisplayLiterals;
import com.atguigu.gmall.search.entity.SearchResponseAttrVO;
import com.atguigu.gmall.search.exception.SearchModuleDataVerificationException;
import com.atguigu.gmall.search.exception.SearchModuleInternalException;
import com.atguigu.gmall.search.regular.Verification;
import com.atguigu.gmall.search.service.SearchService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.internal.util.LinkedArrayList;

import java.io.IOException;
import java.util.*;

/**
 * @author wyl
 * @create 2020-06-24 17:15
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public com.atguigu.gmall.search.entity.SearchResponse search(SearchParam searchParam) {
        SearchRequest searchRequest = null;
        SearchSourceBuilder searchSourceBuilder = null;
        ArrayList<Goods> resultSetParseArray;
        boolean isPage = isPage(searchParam);
        try {
            // 搜索请求
            searchRequest = new SearchRequest(GoodsLiterals.INDEX);
            // 构建dsl
            searchSourceBuilder = new SearchSourceBuilder();
            // 分页
            if(isPage) {
                int from = (searchParam.getPageNum() - 1) * searchParam.getPageSize();
                int size = from + searchParam.getPageSize();
                searchSourceBuilder.from(from);
                searchSourceBuilder.size(size);
            }
            // 搜索 & 高亮
            searchAndHighlighter(searchParam, searchSourceBuilder);
            // 过滤
            searchParamFilter(searchParam, searchSourceBuilder);
            // 价格分区
            searchByRangePrice(searchParam, searchSourceBuilder);
            // 排序
            searchBySort(searchParam, searchSourceBuilder);
            // 聚合
            searchContentByAggs(searchSourceBuilder);

        } catch (SearchModuleDataVerificationException e) {
            log.warn("searchParam data format exception");
            throw new RuntimeException(e);
        }
        try {
            // 放入查询语句
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // 展示数据
            com.atguigu.gmall.search.entity.SearchResponse pageDisplaySearchResponse = new com.atguigu.gmall.search.entity.SearchResponse();
            // 处理页数
            resultSetParseArray = ProcessingPage(searchParam, isPage, pageDisplaySearchResponse);
            // 商品
            processingGoods(resultSetParseArray, searchResponse, pageDisplaySearchResponse);
            // 聚合
            Map<String, Aggregation> aggsMap = searchResponse.getAggregations().getAsMap();
            if (aggsMap.size() > 0){
                // 解析品牌聚合
                parseBrandAggs(pageDisplaySearchResponse, aggsMap);
                // 解析分类聚合
                parseCategoryAggs(pageDisplaySearchResponse, aggsMap);
                // 解析嵌套聚合
                parseAttrNestedAggs(pageDisplaySearchResponse, aggsMap);
            }
            return pageDisplaySearchResponse;
        } catch (IOException | SearchModuleInternalException e) {
            log.error("dsl statement execution failed, search module may is question",e);
        }
        return null;
    }

    /**
     * 解析规格属性聚合
     * @param pageDisplaySearchResponse
     * @param aggsMap
     * @throws SearchModuleInternalException
     */
    private void parseAttrNestedAggs(com.atguigu.gmall.search.entity.SearchResponse pageDisplaySearchResponse, Map<String, Aggregation> aggsMap) throws SearchModuleInternalException {

        ParsedNested attrIdsAggs = (ParsedNested)aggsMap.get(AggsLiterals.ATTR_IDS_AGGS);
        if (attrIdsAggs != null) {
            ArrayList<SearchResponseAttrVO> attrRespVOList = new ArrayList<>();
            ParsedLongTerms attrIdAggs = (ParsedLongTerms) attrIdsAggs.getAggregations().getAsMap().get(AggsLiterals.ATTR_ID_AGGS);
            for (Terms.Bucket attrIdBucket : attrIdAggs.getBuckets()) {
                SearchResponseAttrVO searchResponseAttrVO = new SearchResponseAttrVO();
                ArrayList<String> attrValueList = new ArrayList<>();
                // attrId
                Long key = (Long) attrIdBucket.getKey();

                Map<String, Aggregation> AttrNameAndValues = attrIdBucket.getAggregations().getAsMap();
                // attrName
                ParsedStringTerms attrName = (ParsedStringTerms) AttrNameAndValues.get(AggsLiterals.ATTR_NAME_AGGS);
                if (attrName.getBuckets().size() > 1) {
                    throwSearchModuleException("同一个attrId下不应该有多个属性名称");
                }
                // attrValue
                ParsedStringTerms attrValues = (ParsedStringTerms) AttrNameAndValues.get(AggsLiterals.ATTR_VALUES_AGGS);
                for (Terms.Bucket bucket : attrValues.getBuckets()) {
                    attrValueList.add((String) bucket.getKey());
                }
                searchResponseAttrVO.setProductAttributeId(key);
                searchResponseAttrVO.setName((String) attrName.getBuckets().get(0).getKey());
                searchResponseAttrVO.setValue(attrValueList);
                attrRespVOList.add(searchResponseAttrVO);
            }
            pageDisplaySearchResponse.setAttrs(attrRespVOList);
        }
    }

    /**
     * 解析分类聚合
     * @param pageDisplaySearchResponse
     * @param aggsMap
     * @throws SearchModuleInternalException
     * @throws JsonProcessingException
     */
    private void parseCategoryAggs(com.atguigu.gmall.search.entity.SearchResponse pageDisplaySearchResponse, Map<String, Aggregation> aggsMap) throws SearchModuleInternalException, JsonProcessingException {

        ParsedLongTerms categoryAggs = (ParsedLongTerms)aggsMap.get(AggsLiterals.CATEGORY_AGGS);
        if (categoryAggs != null) {

            List<? extends Terms.Bucket> categoryBuckets = categoryAggs.getBuckets();

            SearchResponseAttrVO categoryVO = new SearchResponseAttrVO();
            categoryVO.setName("分类");
            LinkedList<String> categoryBucketLinkedArrayList = new LinkedList<String>();
            for (Terms.Bucket bucket : categoryBuckets) {
                HashMap<String, Object> categoryMap = new HashMap<>(4);
                // name parse
                Aggregations categoryNameAggs = bucket.getAggregations();
                Map<String, Aggregation> brandNameMap = categoryNameAggs.getAsMap();
                ParsedStringTerms categoryNameBucket = (ParsedStringTerms) brandNameMap.get(AggsLiterals.CATEGORY_NAME_AGGS);
                if (categoryNameBucket.getBuckets().size() > 1) {
                    throwSearchModuleException("同一个分类id下不应该有多个分类名称");
                }
                // name
                String name = (String) categoryNameBucket.getBuckets().get(0).getKey();
                // id
                categoryMap.put(SearchParamDisplayLiterals.AGGS_DISPLAY_OBJ_ID,bucket.getKey());
                categoryMap.put(SearchParamDisplayLiterals.AGGS_DISPLAY_OBJ_Name,name);
                // toString
//                categoryBucketLinkedArrayList.add(objectMapper.writeValueAsString(categoryMap));
                categoryBucketLinkedArrayList.add(name);
            }
            categoryVO.setValue(categoryBucketLinkedArrayList);
            pageDisplaySearchResponse.setCatelog(categoryVO);
        }
    }

    /**
     * 解析品牌聚合
     * @param pageDisplaySearchResponse
     * @param aggsMap
     * @throws SearchModuleInternalException
     * @throws JsonProcessingException
     */
    private void parseBrandAggs(com.atguigu.gmall.search.entity.SearchResponse pageDisplaySearchResponse, Map<String, Aggregation> aggsMap) throws SearchModuleInternalException, JsonProcessingException {
        ParsedLongTerms brandAggs = (ParsedLongTerms)aggsMap.get(AggsLiterals.BRAND_AGGS);
        if (brandAggs != null){
            List<? extends Terms.Bucket> brandBuckets = brandAggs.getBuckets();

            SearchResponseAttrVO brandVO = new SearchResponseAttrVO();
            // name就是“品牌”
            brandVO.setName("品牌");
            // value: [{id:100,name:华为,logo:xxx},{id:101,name:小米,log:yyy}]
            LinkedList<String> brandBucketLinkedArrayList = new LinkedList<String>();
            for (Terms.Bucket bucket : brandBuckets) {
                HashMap<String, Object> brandMap = new HashMap<>(4);
                // name parse
                Aggregations brandNameAggs = bucket.getAggregations();
                Map<String, Aggregation> brandNameMap = brandNameAggs.getAsMap();
                ParsedStringTerms brandNameBucket = (ParsedStringTerms) brandNameMap.get(AggsLiterals.BRAND_NAME_AGGS);
                if (brandNameBucket.getBuckets().size() > 1) {
                    throwSearchModuleException("同一个品牌id下不应该有多个品牌名称");
                }
                // name
                String name = (String) brandNameBucket.getBuckets().get(0).getKey();
                // id
                brandMap.put(SearchParamDisplayLiterals.AGGS_DISPLAY_OBJ_ID,bucket.getKey());
                brandMap.put(SearchParamDisplayLiterals.AGGS_DISPLAY_OBJ_Name,name);
                // toString
//                brandBucketLinkedArrayList.add(objectMapper.writeValueAsString(brandMap));
                brandBucketLinkedArrayList.add(name);
            }
            brandVO.setValue(brandBucketLinkedArrayList);
            pageDisplaySearchResponse.setBrand(brandVO);
        }
    }

    /**
     * 处理商品
     * @param resultSetParseArray
     * @param searchResponse
     * @param pageDisplaySearchResponse
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */
    private void processingGoods(ArrayList<Goods> resultSetParseArray, SearchResponse searchResponse, com.atguigu.gmall.search.entity.SearchResponse pageDisplaySearchResponse) throws com.fasterxml.jackson.core.JsonProcessingException {
        if (searchResponse.getHits().getHits().length > 0){
            for (int i = 0; i < searchResponse.getHits().getHits().length; i++) {
                SearchHit hit = searchResponse.getHits().getHits()[i];
                String sourceAsString = hit.getSourceAsString();
                Goods goods = objectMapper.readValue(sourceAsString, Goods.class);
                if (hit.getHighlightFields().get(GoodsLiterals.SKU_TITLE) != null)
                 goods.setName(hit.getHighlightFields().get(GoodsLiterals.SKU_TITLE).getFragments()[0].toString());
                resultSetParseArray.add(goods);
            }
        }
        // goods
        pageDisplaySearchResponse.setProducts(resultSetParseArray);
        // 命中数
        pageDisplaySearchResponse.setTotal(searchResponse.getHits().getTotalHits());
    }

    /**
     * 处理分页，并生成符合结果集大小的集合
     * @param searchParam
     * @param isPage
     * @param pageDisplaySearchResponse
     * @return
     */
    private ArrayList<Goods> ProcessingPage(SearchParam searchParam, boolean isPage, com.atguigu.gmall.search.entity.SearchResponse pageDisplaySearchResponse) {
        ArrayList<Goods> resultSetParseArray;
        if (isPage) {
            resultSetParseArray = new ArrayList<Goods>(searchParam.getPageSize());
            pageDisplaySearchResponse.setPageNum(searchParam.getPageNum());
            pageDisplaySearchResponse.setPageSize(searchParam.getPageSize());
        } else {
            resultSetParseArray = new ArrayList<Goods>(SearchParamDisplayLiterals.defaultPageSize);
            pageDisplaySearchResponse.setPageSize(SearchParamDisplayLiterals.defaultPageSize);
            pageDisplaySearchResponse.setPageNum(1);
        }
        return resultSetParseArray;
    }

    /**
     * 判断是否需要分页
     * @param searchParam
     * @return
     */
    private boolean isPage(SearchParam searchParam) {
        return searchParam.getPageNum() != null && searchParam.getPageNum() > 0 && searchParam.getPageSize() != null && searchParam.getPageSize() >= 0;
    }

    private void searchContentByAggs(SearchSourceBuilder searchSourceBuilder) {
        // 品牌聚合
        TermsAggregationBuilder brandsAggs =
                new TermsAggregationBuilder(AggsLiterals.BRAND_AGGS, ValueType.LONG).field(GoodsLiterals.BRAND_ID)
                        .subAggregation(new TermsAggregationBuilder(AggsLiterals.BRAND_NAME_AGGS,ValueType.STRING).field(GoodsLiterals.BRAND_NAME));
        searchSourceBuilder.aggregation(brandsAggs);
        // 分类聚合
        TermsAggregationBuilder categoryAggs =
                new TermsAggregationBuilder(AggsLiterals.CATEGORY_AGGS, ValueType.LONG).field(GoodsLiterals.CATEGORY_ID)
                        .subAggregation(new TermsAggregationBuilder(AggsLiterals.CATEGORY_NAME_AGGS,ValueType.STRING).field(GoodsLiterals.CATEGORY_NAME));
        searchSourceBuilder.aggregation(categoryAggs);
        // 属性聚合
        // attrIdAggs
        TermsAggregationBuilder attrIdAggs = new TermsAggregationBuilder(AggsLiterals.ATTR_ID_AGGS, ValueType.LONG).field(GoodsLiterals.SEARCH_ATTRS + "." + GoodsLiterals.ATTR_ID);
        // attrIdsAggs
        NestedAggregationBuilder attrIdsAggs =
                new NestedAggregationBuilder(AggsLiterals.ATTR_IDS_AGGS,GoodsLiterals.SEARCH_ATTRS)
                        .subAggregation(attrIdAggs);
        attrIdAggs.subAggregation(new TermsAggregationBuilder(AggsLiterals.ATTR_NAME_AGGS,ValueType.STRING).field(GoodsLiterals.SEARCH_ATTRS+"."+GoodsLiterals.ATTR_NAME));
        attrIdAggs.subAggregation(new TermsAggregationBuilder(AggsLiterals.ATTR_VALUES_AGGS,ValueType.STRING).field(GoodsLiterals.SEARCH_ATTRS+"."+GoodsLiterals.ATTR_VALUE));

        searchSourceBuilder.aggregation(attrIdsAggs);
    }

    /**
     * 排序
     * @param searchParam
     * @param searchSourceBuilder
     * @throws SearchModuleDataVerificationException
     */
    private void searchBySort(SearchParam searchParam, SearchSourceBuilder searchSourceBuilder) throws SearchModuleDataVerificationException {
        if (StringUtils.isNotEmpty(searchParam.getOrder())) {
            String[] orderStrByArray = searchParam.getOrder().split("\\:");
            if (searchStrVerification(orderStrByArray)){
                SortOrder sortOrder = VerificationAndChangeOrderStrArray(orderStrByArray);
                searchSourceBuilder.sort(orderStrByArray[0],sortOrder);
            }
        }
    }

    /**
     * 价格区间
     * @param searchParam
     * @param searchSourceBuilder
     */
    private void searchByRangePrice(SearchParam searchParam, SearchSourceBuilder searchSourceBuilder) {
        if (searchParam.getPriceFrom() != null && searchParam.getPriceTo() != null && searchParam.getPriceFrom() <= searchParam.getPriceTo()) {
            searchSourceBuilder.postFilter(new RangeQueryBuilder(GoodsLiterals.PRICE)
                    .gte(searchParam.getPriceFrom())
                    .lte(searchParam.getPriceTo()));
        }
    }

    /**
     * 高亮结果集合和关键字搜索
     * @param searchParam
     * @param searchSourceBuilder
     */
    private void searchAndHighlighter(SearchParam searchParam, SearchSourceBuilder searchSourceBuilder) {
        if (StringUtils.isNotEmpty(searchParam.getKeyword())) {
            BoolQueryBuilder searchKeyWord = new BoolQueryBuilder().must(
                    new MatchQueryBuilder(GoodsLiterals.SKU_TITLE, searchParam.getKeyword()).operator(Operator.AND)
            );
            searchSourceBuilder.query(searchKeyWord);
            searchSourceBuilder.highlighter(new HighlightBuilder().field(GoodsLiterals.SKU_TITLE).preTags("<em>").postTags("</em>"));
        }
    }

    /**
     * 搜索过滤
     * @param searchParam
     * @param searchSourceBuilder
     * @throws SearchModuleDataVerificationException
     */
    private void searchParamFilter(SearchParam searchParam, SearchSourceBuilder searchSourceBuilder) throws SearchModuleDataVerificationException {
        // 品牌过滤
        if (ArrayUtils.isNotEmpty(searchParam.getBrand())) {
            searchSourceBuilder.postFilter(new TermsQueryBuilder(GoodsLiterals.BRAND_NAME,searchParam.getBrand()));
        }
        // 分类过滤
        if (ArrayUtils.isNotEmpty(searchParam.getCatelog3())) {
            searchSourceBuilder.postFilter(new TermsQueryBuilder(GoodsLiterals.CATEGORY_NAME,searchParam.getCatelog3()));
        }
        // 商品属性过滤
        if (CollectionUtils.isNotEmpty(searchParam.getProps())) {
            // 校验格式
            for (String prop : searchParam.getProps()) {
                if (verificationSearchProp(prop)) {
                    // 消费并生成语句
                    attrIdAndProductAttrValuesConsumer(searchSourceBuilder,prop);
                }
            }
        }
    }

    /**
     * 校验格式并生成搜索参数，最后数据格式： ["price","desc"]
     * @param verificationAndChangeStrArray
     * @return asc desc
     * @throws SearchModuleDataVerificationException
     */
    private SortOrder VerificationAndChangeOrderStrArray(String[] verificationAndChangeStrArray) throws SearchModuleDataVerificationException {
        SortOrder sortOrder = SortOrder.DESC;
        switch (verificationAndChangeStrArray[1]){
            case "desc":
                // 暂无此项(需要综合搜索的算法，权重.我就做价格排序号了)
                sortOrder = SortOrder.DESC;
                break;
            case "asc":
                sortOrder = SortOrder.ASC;
                break;
            default:
                this.throwVerificationException("com.atguigu.gmall.search.service.impl.SearchServiceImpl.VerificationAndChangeOrderStrArray" + verificationAndChangeStrArray[1]);
                break;
        }
        switch (verificationAndChangeStrArray[0]){
            case "0":
                // 暂无此项(需要综合搜索的算法，权重.我就做价格排序号了)
                verificationAndChangeStrArray[0] = GoodsLiterals.SALE_COUNT;
                break;
            case "1":
                verificationAndChangeStrArray[0] = GoodsLiterals.SALE_COUNT;
                break;
            case "2":
                verificationAndChangeStrArray[0] = GoodsLiterals.PRICE;
                break;
            default:
                this.throwVerificationException("com.atguigu.gmall.search.service.impl.SearchServiceImpl.VerificationAndChangeOrderStrArray" + verificationAndChangeStrArray[0]);
                break;
        }
        return sortOrder;
    }

    /**
     * 添加嵌套的过滤，每一个商品的属性都为一个嵌套(保证不与其它属性混淆匹配)
     * @param searchSourceBuilder
     * @param attrIdAndProductAttrValues
     */
    private void attrIdAndProductAttrValuesConsumer(SearchSourceBuilder searchSourceBuilder, String attrIdAndProductAttrValues) {
        String[] attrIdAndProductAttrValueList = attrIdAndProductAttrValues.split("\\:");
        String[] AttrValueList = attrIdAndProductAttrValueList[1].split("\\-");
        // 嵌套过滤
        for (int i = 0; i < attrIdAndProductAttrValueList.length; i++) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            // attrValueList.productAttributeId
            boolQueryBuilder.must(
                    new TermQueryBuilder(GoodsLiterals.SEARCH_ATTRS+"."+GoodsLiterals.ATTR_ID,attrIdAndProductAttrValueList[0])
            );
            // attrValueList.value
            boolQueryBuilder.must(
                    new TermsQueryBuilder(GoodsLiterals.SEARCH_ATTRS+"."+GoodsLiterals.ATTR_VALUE,AttrValueList)
            );
            searchSourceBuilder.postFilter(
                    new NestedQueryBuilder(GoodsLiterals.SEARCH_ATTRS,
                            boolQueryBuilder, ScoreMode.Avg));
        }
    }

    /**
     *  校验格式 1:a-b-c
     * @param attrIdAndProductAttrValues
     * @return
     * @throws SearchModuleDataVerificationException
     */
    private boolean verificationSearchProp(String attrIdAndProductAttrValues) throws SearchModuleDataVerificationException {
        if (StringUtils.isNotEmpty(attrIdAndProductAttrValues)){

            String[] searchProp = attrIdAndProductAttrValues.split("\\:");
            if (searchStrVerification(searchProp)) {
                String[] attrValues = searchProp[1].split("\\-");
                if (ArrayUtils.isNotEmpty(attrValues)){
                    return true;
                }
            }
        }
        throw new SearchModuleDataVerificationException("com.atguigu.gmall.search.service.impl.SearchServiceImpl.verificationSearchProp " + attrIdAndProductAttrValues);
    }
    private void throwVerificationException(String str) throws SearchModuleDataVerificationException {
        throw new SearchModuleDataVerificationException(str);
    }
    private void throwSearchModuleException(String str) throws SearchModuleInternalException {
        throw new SearchModuleInternalException(str);
    }
    /**
     * 初步判断数组中是否为两个元素的合法字符串，并且arg[0]为非零自然数
     * @return
     */
    private boolean searchStrVerification(String[] verificationStr) {
        return ArrayUtils.isNotEmpty(verificationStr)
                && StringUtils.isNotEmpty(verificationStr[0])
                && StringUtils.isNotEmpty(verificationStr[1])
                && verificationStr.length == 2
                && Verification.isNoneZeroNumber(verificationStr[0]);
    }

}

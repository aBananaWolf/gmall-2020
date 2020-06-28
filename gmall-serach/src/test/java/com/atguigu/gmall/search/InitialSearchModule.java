package com.atguigu.gmall.search;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.api.PMSDataImportApi;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.api.PMSDataImportService;
import com.atguigu.gmall.search.constants.AnalyzerLiterals;
import com.atguigu.gmall.search.constants.GoodsLiterals;
import com.atguigu.gmall.search.constants.IndicesLiterals;
import com.atguigu.gmall.search.constants.MappingLiterals;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.atguigu.gmall.search.service.ImportDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wyl
 * @create 2020-06-22 16:20
 */
@SpringBootTest
public class InitialSearchModule {
    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private PMSDataImportService pmsDataImportService;

    @Autowired
    private ImportDataService importDataService;

    @Test
    void initialSearchModule(){
        DeleteRequest deleteRequest = new DeleteRequest(GoodsLiterals.INDEX);
        try {
            client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createMappings();
        try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {
            e.printStackTrace();
        }
        createGoods();
    }

    @Test
    void createSearchMappings() {
        createMappings();
    }

    @Test
    void importSearchData() {
        createGoods();
    }

    private void createGoods() {
        // 由于spu和sku是一对多关系，先从spu查询
        Long spuInitialPage = 1L;
        Long skuInitialPage = 1L;
        Long pageSize = 20L; // 所有数据都是 20 条一次查询
        boolean spuDataImportFinish = false;
        boolean skuDataImportFinish = false;
        // spu 分页
        QueryCondition spuQueryCondition = new QueryCondition();
        spuQueryCondition.setPage(spuInitialPage);
        spuQueryCondition.setLimit(pageSize);
        // sku 分页
        QueryCondition skuQueryCondition = new QueryCondition();
        skuQueryCondition.setPage(skuInitialPage);
        skuQueryCondition.setLimit(pageSize);

        // spu 查询响应数据
        Resp<PageVo> spuPageVoResp;
        PageVo spuPage;
        List<SpuInfoEntity> spuList;

        // 分类、品牌和搜索属性
        CategoryEntity categoryEntity;
        BrandEntity brandEntity;
        List<ProductAttrValueEntity> attrEntities;
        ArrayList<SearchAttr> goodsSearchAttrs = new ArrayList<>();


        // 搜索数据
        Goods goods = new Goods();

        // sku 查询响应数据
        Resp<PageVo> skuPageVoResp;
        PageVo skuPage;
        List<SkuInfoEntity> skuList;


        do {
            // 查询spu
            spuQueryCondition.setPage(spuInitialPage);
            spuQueryCondition.setLimit(pageSize);
            spuList = pmsDataImportService.listSpuByPublished(spuQueryCondition);

            if (!CollectionUtils.isEmpty(spuList)){
                if (spuList.size() < pageSize){
                    spuDataImportFinish = true;
                }
                // 遍历spu
                for (SpuInfoEntity spu : spuList) {
                    // 查询品牌和分类
                    if ((categoryEntity = pmsDataImportService.categoryInfo(spu.getCatalogId())) != null
                            && (brandEntity = pmsDataImportService.brandInfo(spu.getBrandId())) != null
                            && (attrEntities = pmsDataImportService.listSearchAttr(spu.getId())) != null){
                        do {
                            skuQueryCondition.setPage(skuInitialPage);
                            skuQueryCondition.setLimit(pageSize);
                            skuList = pmsDataImportService.listSkuBySearchModule(skuQueryCondition,spu.getId());
                            // 查询sku
                            if (!CollectionUtils.isEmpty(skuList)) {
                                if (skuList.size() < pageSize){
                                    skuDataImportFinish = true;
                                }
                                for (SkuInfoEntity sku : skuList) {
                                    goods.setId(sku.getSkuId());
                                    //spuId
                                    //                                    goods.setSpuId(spu.getId()); 不需要的field
                                    //skuName
                                    //                                    goods.setSkuName(sku.getSkuName());

                                    //所属分类id
                                    goods.setProductCategoryId(categoryEntity.getCatId());
                                    goods.setProductCategoryName(categoryEntity.getName());
                                    //品牌id
                                    goods.setBrandId(brandEntity.getBrandId());
                                    goods.setBrandName(brandEntity.getName());
                                    //默认图片
                                    goods.setPic(sku.getSkuDefaultImg());
                                    //标题
                                    goods.setName(sku.getSkuTitle());
                                    //价格
                                    goods.setPrice(sku.getPrice());

                                    // 新品
                                    goods.setCreateTime(spu.getCreateTime().getTime());
                                    // 销量
                                    goods.setSale(0L);
                                    goods.setSort(0L);
                                    // 库存
                                    // goods.setStock();
                                    goodsSearchAttrs.addAll(attrEntities.stream()
                                            .map(attrEntity ->
//                                    private Long id;  //商品和属性关联的数据表的主键id
//                                    private Long productAttributeId; //当前sku对应的属性的attr_id
//                                    private String name;//属性名  电池
//                                    private String value;//3G   3000mah
//                                    private Long spuId;//这个属性关系对应的spu的id
                                                    new SearchAttr(attrEntity.getId(),
                                                            attrEntity.getAttrId(),
                                                            attrEntity.getAttrName(),
                                                            attrEntity.getAttrValue(),
                                                            spu.getId())
                                            )
                                            .collect(Collectors.toList()));
                                    goods.setAttrValueList(goodsSearchAttrs);

                                    // 导入数据
                                    importDataService.importData(goods);

                                    goodsSearchAttrs.clear();

                                }
                                // 页码 + 1
                                skuInitialPage++;
                            } else {
                                skuDataImportFinish = true;
                            }
                        } while (!skuDataImportFinish) ;
                        skuDataImportFinish = false;
                        skuInitialPage = 1L;
                    }
                }
                // 页码 + 1
                spuInitialPage++;
            } else {
                spuDataImportFinish = true;
            }
        } while (!spuDataImportFinish);
    }

    private void createMappings() {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(GoodsLiterals.INDEX);
        XContentBuilder mappings;
        try {
            mappings = XContentFactory.jsonBuilder();

            mappings.startObject();
            {
                mappings.startObject(MappingLiterals.PROPERTIES);
                {
                    // id
                    mappings.startObject(GoodsLiterals.SKU_ID).field("type","long").endObject();
//                    mappings.startObject(GoodsLiterals.SPU_ID).field("type","long").field("index",false).endObject(); 不需要的field

                    // skuName 暂不使用
//                    mappings.startObject(GoodsLiterals.).field("type", AnalyzerLiterals.TEXT).field(AnalyzerLiterals.KEY,AnalyzerLiterals.MAX_WORD).endObject();

                    // 分类
                    mappings.startObject(GoodsLiterals.CATEGORY_ID).field("type","long").endObject();
                    mappings.startObject(GoodsLiterals.CATEGORY_NAME).field("type",AnalyzerLiterals.KEY_WORD).endObject();
                    // 品牌
                    mappings.startObject(GoodsLiterals.BRAND_ID).field("type","long").endObject();
                    mappings.startObject(GoodsLiterals.BRAND_NAME).field("type",AnalyzerLiterals.KEY_WORD).endObject();
                    // 图片
                    mappings.startObject(GoodsLiterals.SKU_DEFAULT_IMG).field("type",AnalyzerLiterals.KEY_WORD).endObject();
                    // 标题
                    mappings.startObject(GoodsLiterals.SKU_TITLE).field("type",AnalyzerLiterals.TEXT).field(AnalyzerLiterals.KEY,AnalyzerLiterals.MAX_WORD).endObject();
                    // 价格
                    mappings.startObject(GoodsLiterals.PRICE).field("type","double").endObject();
                    // 新品
                    mappings.startObject(GoodsLiterals.CREATE_TIME).field("type","long").endObject();
                    // 销量
                    mappings.startObject(GoodsLiterals.SALE_COUNT).field("type","long").endObject();
                    // 库存
                    mappings.startObject(GoodsLiterals.STOCK).field("type","long").endObject();
                    // 热度
                    mappings.startObject(GoodsLiterals.SORT).field("type","long").endObject();
                    // 查询属性
                    mappings.startObject(GoodsLiterals.SEARCH_ATTRS).field("type","nested");
                    {
                        mappings.startObject(MappingLiterals.PROPERTIES);
                        {
                            mappings.startObject(GoodsLiterals.PRODUCT_ATTR_ID).field("type","long").endObject();
                            mappings.startObject(GoodsLiterals.ATTR_ID).field("type","long").endObject();
                            mappings.startObject(GoodsLiterals.ATTR_NAME).field("type",AnalyzerLiterals.KEY_WORD).endObject();
                            mappings.startObject(GoodsLiterals.ATTR_VALUE).field("type",AnalyzerLiterals.KEY_WORD).endObject();
                            mappings.startObject(GoodsLiterals.SPU_ID).field("type","long").endObject();
                        }
                        mappings.endObject();
                    }
                    mappings.endObject();
                }
                mappings.endObject();
            }
            mappings.endObject();

            createIndexRequest.mapping(mappings);
            createIndexRequest.settings(Settings.builder()
                    .put(IndicesLiterals.NUMBER_OF_SHARE_KEY,IndicesLiterals.NUMBER_OF_SHARE)
                    .put(IndicesLiterals.NUMBER_OF_SHARE_KEY,IndicesLiterals.NUMBER_OF_SHARE)
                    .build());
            client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

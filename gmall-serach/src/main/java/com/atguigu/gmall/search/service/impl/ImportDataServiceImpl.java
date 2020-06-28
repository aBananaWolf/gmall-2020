package com.atguigu.gmall.search.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.api.PMSDataImportService;
import com.atguigu.gmall.search.constants.GoodsLiterals;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.atguigu.gmall.search.service.ImportDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wyl
 * @create 2020-06-23 15:51
 */
@Service
public class ImportDataServiceImpl implements ImportDataService {
    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PMSDataImportService pmsDataImportService;

    @Override
    public void importData(Goods goods) {
        try {
            String goodsStr = objectMapper.writeValueAsString(goods);
            IndexRequest indexRequest = new IndexRequest
                    (GoodsLiterals.INDEX,"_doc",String.valueOf(goods.getId()))
                    .source(goodsStr, XContentType.JSON);
            client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importDataBySpuId(Long spuId) {
        SpuInfoEntity spu = pmsDataImportService.info(spuId);
        // 由于spu和sku是一对多关系，先从spu查询
        Long skuInitialPage = 1L;
        Long pageSize = 20L; // 所有数据都是 20 条一次查询
        boolean skuDataImportFinish = false;
        // sku 分页
        QueryCondition skuQueryCondition = new QueryCondition();
        skuQueryCondition.setPage(skuInitialPage);
        skuQueryCondition.setLimit(pageSize);

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
                        this.importData(goods);

                        goodsSearchAttrs.clear();

                    }
                    // 页码 + 1
                    skuInitialPage++;
                } else {
                    skuDataImportFinish = true;
                }
            } while (!skuDataImportFinish) ;
        }
    }
}

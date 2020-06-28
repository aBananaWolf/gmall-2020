package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-23 18:59
 */
@SpringBootTest
public class MyTest {
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Test
    public void test(){
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuSaleAttrValueService.listSearchAttr(7L);
        System.out.println(skuSaleAttrValueEntities);
    }
}

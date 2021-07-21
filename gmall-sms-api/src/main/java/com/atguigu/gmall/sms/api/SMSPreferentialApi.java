package com.atguigu.gmall.sms.api;

import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.sms.vo.SkuPreferentialVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wyl
 * @create 2020-06-15 11:12
 */
@RequestMapping("sms/skubounds")
public interface SMSPreferentialApi {
    @PostMapping("/sku/preferential/save")
    public void savePreferentialInfo(@RequestBody SkuPreferentialVO skuPreferentialVO);

    @GetMapping("/all/preferential/{skuId}")
    public List<SaleVO> allPreferential(@PathVariable("skuId") Long skuId);
}
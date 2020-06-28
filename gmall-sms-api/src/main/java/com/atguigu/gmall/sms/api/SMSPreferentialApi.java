package com.atguigu.gmall.sms.api;

import com.atguigu.gmall.sms.vo.SkuPreferentialVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author wyl
 * @create 2020-06-15 11:12
 */
public interface SMSPreferentialApi {
    @PostMapping("sms/skubounds/sku/preferential/save")
    public void savePreferentialInfo(@RequestBody SkuPreferentialVO skuPreferentialVO);
}
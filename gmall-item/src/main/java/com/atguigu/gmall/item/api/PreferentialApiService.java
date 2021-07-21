package com.atguigu.gmall.item.api;

import com.atguigu.gmall.sms.api.SMSPreferentialApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "gmall-sms",contextId = "preferentialApiService")
public interface PreferentialApiService extends SMSPreferentialApi {
}

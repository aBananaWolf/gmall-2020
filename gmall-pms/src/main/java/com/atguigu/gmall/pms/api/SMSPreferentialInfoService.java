package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.sms.api.SMSPreferentialApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient("gmall-sms")
public interface SMSPreferentialInfoService extends SMSPreferentialApi {
}

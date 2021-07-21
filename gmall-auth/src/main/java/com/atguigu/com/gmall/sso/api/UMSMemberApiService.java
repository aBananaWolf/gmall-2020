package com.atguigu.com.gmall.sso.api;

import com.atguigu.gmall.ums.api.UMSMemberApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("gmall-ums")
public interface UMSMemberApiService extends UMSMemberApi {
}

package com.atguigu.gmall.index.api;

import com.atguigu.gmall.pms.api.PMSIndexApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient("gmall-pms")
@Component
public interface IndexApiService extends PMSIndexApi {
}

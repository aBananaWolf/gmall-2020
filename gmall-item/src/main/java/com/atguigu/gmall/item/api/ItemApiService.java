package com.atguigu.gmall.item.api;

import com.atguigu.gmall.pms.api.PMSItemApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

@FeignClient(value = "gmall-pms",contextId = "ItemApiService")
//@Service("itemApiService")
public interface ItemApiService extends PMSItemApi {
}

package com.atguigu.gmall.item.api;

import com.atguigu.gmall.pms.api.PMSDataImportApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

@FeignClient(value = "gmall-pms",contextId = "searchApiService")
//@Service("searchApiService")
public interface SearchApiService extends PMSDataImportApi {
}

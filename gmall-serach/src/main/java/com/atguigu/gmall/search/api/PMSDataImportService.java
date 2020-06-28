package com.atguigu.gmall.search.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.api.PMSDataImportApi;
import com.atguigu.gmall.pms.entity.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(value = "gmall-pms",contextId = "FragmentCompositionService")
@Component
public interface PMSDataImportService extends PMSDataImportApi {

}
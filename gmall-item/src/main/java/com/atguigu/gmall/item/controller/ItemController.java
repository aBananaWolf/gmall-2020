package com.atguigu.gmall.item.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.pms.vo.ItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wyl
 * @create 2020-06-28 19:07
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/{skuId}.html")
    public Object item(@PathVariable("skuId") Long skuId) {
        ItemVO andRenderItem = itemService.getAndRenderItem(skuId);
        return Resp.ok(andRenderItem);
    }
}

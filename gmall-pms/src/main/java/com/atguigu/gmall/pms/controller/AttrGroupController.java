package com.atguigu.gmall.pms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.dao.GroupAndAttr;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;

import javax.ws.rs.GET;


/**
 * 属性分组
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-06-07 14:49:29
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @ApiOperation("查询分类下的组及规格参数")
    @GetMapping("/withattrs/cat/{catId}")
    public Resp<Object> listGroupAndAttrByCategoryId(@PathVariable("catId") Long categoryId, QueryCondition queryCondition) {
        if (queryCondition.getLimit() != null && queryCondition.getPage() != null)
            // 这个是单纯的组参数
            return Resp.ok(attrGroupService.listGroupByCondition(categoryId,queryCondition));
        // GroupVO 是自己封装的，这是为了获取商品的 -基本属性-
        List<GroupVO> groupVoList = attrGroupService.listGroupAndAttrByCategoryId(categoryId);
        return Resp.ok(groupVoList);
    }


    @ApiOperation("三级分类下的分组")
    @GetMapping("{categoryId}")
    public Resp<PageVo> listGroupByCategoryId(QueryCondition queryCondition, @PathVariable("categoryId") Long categoryId) {
        PageVo pageVo = attrGroupService.listGroupByCategoryId(queryCondition,categoryId);
        return Resp.ok(pageVo);
    }

    @ApiOperation("查询组以及组的规格参数")
    @GetMapping("withattr/{gid}")
    public Resp<GroupVO> findGroup(@PathVariable("gid") Long gid) {
        GroupVO groupVOs = attrGroupService.selectByGroup(gid);
        return Resp.ok(groupVOs);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:attrgroup:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = attrGroupService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{attrGroupId}")
    @PreAuthorize("hasAuthority('pms:attrgroup:info')")
    public Resp<AttrGroupEntity> info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return Resp.ok(attrGroup);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:attrgroup:save')")
    public Resp<Object> save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:attrgroup:update')")
    public Resp<Object> update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:attrgroup:delete')")
    public Resp<Object> delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return Resp.ok(null);
    }

}

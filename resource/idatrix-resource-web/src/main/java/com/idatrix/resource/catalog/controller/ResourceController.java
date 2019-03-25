package com.idatrix.resource.catalog.controller;

import com.idatrix.resource.catalog.service.IResourceConfigService;
import com.idatrix.resource.catalog.service.IResourceStatiscsService;
import com.idatrix.resource.catalog.vo.ResourceConfigVO;
import com.idatrix.resource.catalog.vo.ResourceHistoryVO;
import com.idatrix.resource.catalog.vo.ResourceOverviewVO;
import com.idatrix.resource.catalog.vo.ResourcePubVO;
import com.idatrix.resource.catalog.vo.request.BatchImportRequestVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.ResourceTools;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.common.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 政务信息资源 增删改查
 * @Author: Wangbin
 * @Date: 2018/5/23
 */

@Controller
@RequestMapping("/resource")
@Api(value = "/resource" , tags="资源管理-资源信息处理接口")
public class ResourceController extends BaseController {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IResourceConfigService resourceConfigService;

    @Autowired
    private IResourceStatiscsService resourceStatiscsService;

    @Autowired
    private UserUtils userUtils;


    /**
     *  Title: 通过Excel 批量导入 资源目录
     */
    @ApiOperation(value = "通过Excel批量导入资源目录", notes="通过Excel批量导入资源目录", httpMethod="POST")
    @RequestMapping(value="/batchImport", method=RequestMethod.POST)
    @ResponseBody
    public Result<Map<String, Object>> batchImport(@RequestParam("file") CommonsMultipartFile file) {
        String user = getUserName(); //"admin";
        String fileName = null;
        try{
            fileName = resourceConfigService.saveBatchImport(user, file);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        Map<String, Object> attach = new HashMap<String, Object>();
        attach.put("fileName", fileName);
        return Result.ok(attach);
    }

    /**
     *  用于点击确认按键，导入功能进行处理。
     */
    @ApiOperation(value = "用于点击确认按键，导入功能进行处理。", notes="用于点击确认按键，导入功能进行处理。", httpMethod = "POST")
    @RequestMapping(value="/processExcel", method=RequestMethod.POST)
    @ResponseBody
    public Result processExcel(@RequestBody BatchImportRequestVO batchImportRequestVO) {
        String user = getUserName(); //"admin";
        String fileName = batchImportRequestVO.getFileName();
        Long rentId = userUtils.getCurrentUserRentId();
        LOG.info("fileName:{}", fileName);
        try{
            resourceConfigService.processExcel(rentId, user, fileName);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }


    /*增加政务信息资源*/
    @ApiOperation(value = "增加资源信息", notes="资源编辑中，增加资源信息", httpMethod = "POST")
    @RequestMapping(value="/add", method= RequestMethod.POST)
    @ResponseBody
    public Result addResource(@RequestBody ResourceConfigVO resourceConfigVO) {
        String user = getUserName(); //"admin";
        Long rentId = userUtils.getCurrentUserRentId();
        LOG.info("ResourceConfigVO 资源保存信息：{}", resourceConfigVO.toString());
        Long id = 0L;
        try{
            id=resourceConfigService.addResourceInfo(rentId, user, resourceConfigVO);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        Map<String, Object> attach = new HashMap<String, Object>();
        attach.put("id", id);
        return Result.ok(attach);
    }

    /*删除政务信息资源*/
    @ApiOperation(value = "删除资源信息详情", notes="删除资源信息详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "信息资源ID", required = true, dataType="String"),
    })
    @RequestMapping("/delete")
    @ResponseBody
    public Result deleteResource(@RequestParam(value="id", required = true) String id) {

        //TODO: 需要考虑删除树形节点时候，资源信息为删除或者草稿状态 才能够删除。
        //TODO: 考虑列表删除，当前用户只能删除自己创建的资源信息
        String user = getUserName(); //"admin";
        String[] idArray = id.split(",");

        try{
            for(String idEvery: idArray) {
                resourceConfigService.deleteResourceInfo(user, Long.valueOf(idEvery));
            }
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /*查找所有政务信息资源*/
    @ApiOperation(value = "获取资源信息详情", notes="获取资源信息详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "信息资源ID", required = true, dataType="Long"),
            @ApiImplicitParam(name = "type", value = "主要用于统计用户点详情查看次数，如果需要统计改点击次数，参数设置成 count", required = false, dataType="String")
    })
    @RequestMapping("/getResource")
    @ResponseBody
    public Result<ResourceConfigVO> getResourceById(@RequestParam(value = "id", required = true) Long id,
                                  @RequestParam(value = "type", required = false) String type) {
        ResourceConfigVO resourceConfigVO = null;
        try {
            resourceConfigVO =  resourceConfigService.getResourceInfoById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        if(StringUtils.equalsIgnoreCase(type, "count")){
            resourceStatiscsService.increaseViewDataCount(id);
        }
        return Result.ok(resourceConfigVO);
    }

    /*资源查询：可以按照资源名称、资源代码、提供方名称、提供方代码等方式进行查询
    * 其中libName: 表示按照什么库名称进行查询, own 表示按照当前用户用于的库名称
    *              base 按照基本库，topic 主题库， department 部门库
    *
    */
    @ApiOperation(value = "资源查询", notes="根据一些传递参数进行资源查询资源查询", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "资源状态，用数字表示：0-不确定，1-草稿，2-已删除，3-退回修改，4-待注册审批，5-已注册，6-待发布审批，7-已发布，8-下架", required = false, dataType="String"),
            @ApiImplicitParam(name = "name", value = "资源名称", required = false, dataType="String"),
            @ApiImplicitParam(name = "code", value = "资源编码", required = false, dataType="String"),
            @ApiImplicitParam(name = "deptName", value = "部门名称", required = false, dataType="String"),
            @ApiImplicitParam(name = "deptCode", value = "部门编码", required = false, dataType="String"),
            @ApiImplicitParam(name = "page", value = "页面起始页,默认为0", required = false, dataType="Long"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小,默认为10", required = false, dataType="Long")

    })
    @RequestMapping("/getAll")
    @ResponseBody
    public Result<ResultPager<ResourceOverviewVO>> queryResourceByCondition(
            @RequestParam(value = "status", required = false) String status,
               @RequestParam(value = "name", required = false) String  resourceName,
                    @RequestParam(value = "code", required = false) String  resourceCode,
                      @RequestParam(value = "deptName", required = false) String  deptName,
                        @RequestParam(value = "deptCode", required = false) String  deptCode,
                            @RequestParam(value = "page", required = false) Integer pageNum,
                                @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        String user = getUserName(); //"admin";

        Map<String, String> queryCondition = new HashMap<String, String>();
        queryCondition.put("rentId", userUtils.getCurrentUserRentId().toString());
        queryCondition.put("creator", user);
        if(StringUtils.isNotEmpty(resourceName)){
            queryCondition.put("name", resourceName);
        }
        if(StringUtils.isNotEmpty(resourceCode)){
            queryCondition.put("code", resourceCode);
        }
        if(StringUtils.isNotEmpty(deptName)){
            queryCondition.put("dept_name", deptName);
        }
        if(StringUtils.isNotEmpty(deptCode)){
            queryCondition.put("dept_code", deptCode);
        }
        if(StringUtils.isNotEmpty(deptCode)){
            queryCondition.put("dept_code", deptCode);
        }
        if(StringUtils.isNotEmpty(status)){
            int statusValue = Integer.valueOf(status);
            queryCondition.put("status", ResourceTools.ResourceStatus.getStatusCode(statusValue));
        }

        ResultPager tasks = null;
        try {
            tasks = resourceConfigService.queryByCondition(queryCondition, pageNum, pageSize);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(tasks);
    }

    /*获取资源变更历史*/
    @ApiOperation(value = "通过资源ID获取资源变更历史", notes="通过资源ID获取资源变更历史", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "信息资源ID", required = true, dataType="Long"),
    })
    @RequestMapping("/getHistory")
    @ResponseBody
    public Result<List<ResourceHistoryVO>> getHistory(@RequestParam(value = "id", required = true) Long id) {
        List<ResourceHistoryVO> rhVOList = resourceConfigService.getHistory(id);
        return Result.ok(rhVOList);
    }

    /*获取当前用户已经发布的资源*/
    @ApiOperation(value = "资源上报时候调用", notes="根据资源名称或者资源代码查询已经发布成功资源信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "资源名称", required = false, dataType="String"),
            @ApiImplicitParam(name = "code", value = "资源代码", required = false, dataType="String")
    })
    @RequestMapping("/getPub")
    @ResponseBody
    public Result<List<ResourcePubVO>> getPub(@RequestParam(value = "name", required = false) String resourceName,
                         @RequestParam(value = "code", required = false) String resourceCode) {

        String user = getUserName(); //"admin";
        Map<String, String> queryCondition = new HashMap<String, String>();
        queryCondition.put("creator", user);
        queryCondition.put("rentId", userUtils.getCurrentUserRentId().toString());
        if(StringUtils.isNotEmpty(resourceName)){
            queryCondition.put("name", resourceName);
        }
        if(StringUtils.isNotEmpty(resourceCode)){
            queryCondition.put("code", resourceCode);
        }

        List<ResourcePubVO> rhVOList = resourceConfigService.getPubResourceByCondition(queryCondition);
        return Result.ok(rhVOList);
    }


}

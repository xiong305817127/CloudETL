package com.idatrix.resource.catalog.controller;

import com.idatrix.resource.catalog.service.ICatalogClassifyService;
import com.idatrix.resource.catalog.service.IOverviewService;
import com.idatrix.resource.catalog.service.IResourceConfigService;
import com.idatrix.resource.catalog.service.IResourceStatiscsService;
import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import com.idatrix.resource.catalog.vo.MonthStatisticsVO;
import com.idatrix.resource.catalog.vo.ResourceConfigVO;
import com.idatrix.resource.catalog.vo.ResourceStatisticsVO;
import com.idatrix.resource.catalog.vo.request.ResourceCatalogSearchVO;
import com.idatrix.resource.common.controller.BaseController;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.idatrix.resource.common.utils.ResourceTools.ResourceStatus.PUB_SUCCESS;

/**
 * Created by Robin Wing on 2018-5-28.
 */

@Controller
@RequestMapping("/overview")
@Api(value = "/overview" , tags="资源管理-资源概览查询接口")
public class OverviewController extends BaseController {

    @Autowired
    private IOverviewService overviewService;

    @Autowired
    private IResourceConfigService resourceConfigService;

    @Autowired
    private IResourceStatiscsService resourceStatiscsService;

    @Autowired
    private ICatalogClassifyService catalogClassifyService;

    @Autowired
    private UserUtils userUtils;

    private static final Logger LOG = LoggerFactory.getLogger(OverviewController.class);


    /**
     * 获取订阅详情
     * @param id
     * @return
     */
    @ApiOperation(value = "查看资源详情", notes="在资源概览里面查询资源详情，会统计资源查看次数", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资源ID", required = true, dataType="Long"),
    })
    @RequestMapping("/getResource")
    @ResponseBody
    public Result<ResourceConfigVO> getResourceById(@RequestParam(value = "id", required = true) Long id) {
        ResourceConfigVO resourceConfigVO = null;
        try {
            resourceConfigVO = resourceConfigService.getResourceInfoById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        resourceStatiscsService.increaseViewDataCount(id);
        return Result.ok(resourceConfigVO);
    }

   /**
     * 总起情况: 包含总体 注册量、发布量、订阅量
     * @return
     */
    @ApiOperation(value = "获取所有统计数据信息", notes="查询当前租户下的注册、发布、订阅总量", httpMethod = "GET")
    @RequestMapping("/getOverall")
    @ResponseBody
    public Result<MonthStatisticsVO> getOverall() {

        Long rentId = userUtils.getCurrentUserRentId();
        MonthStatisticsVO rsVO = overviewService.getOverall(rentId);
        return Result.ok(rsVO);
    }

    /*获取三大类基本信息库概览: 基础库、部门库、主题库,分别获取 注册量、发布量、订阅量数据
     * libName: 分为 基础库、部门库、主题库： base/department/topic
     * type： 分为 注册量、发布量、订阅量： reg/pub/sub
     *
     * */
    @ApiOperation(value = "获取三大类基本库统计信息", notes="获取最近几个月注册量、发布量、订阅量等统计数据", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", value = "需要展现月份数，默认设置为6，非必须数据", required = false, dataType="Long", paramType = "query"),
    })
    @RequestMapping("/getOverview")
    @ResponseBody
    public Result<List<MonthStatisticsVO>> getOverview(@RequestParam(value = "num", required = false, defaultValue = "6") Integer num) {
        /*默认读取最近半年的数据*/
        Long rentId = userUtils.getCurrentUserRentId();
        List<MonthStatisticsVO> rsVOList = overviewService.getMonthlyTotalAmount(rentId, num);
        return Result.ok(rsVOList);
    }

    /*获取最新的N个资源信息*/
    @ApiOperation(value = "获取最新发布的资源信息", notes="获取若干个最新发布的资源信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", value = "最近资源个数，默认设置为4，非必须数据", required = false, dataType="Long", paramType = "query"),
    })
    @RequestMapping("/getLatest")
    @ResponseBody
    public Result getLatest(@RequestParam(value = "num", required = false, defaultValue = "4") Long num) {

        Long rentId = userUtils.getCurrentUserRentId();
        List<ResourceStatisticsVO> resourceStatisticsVOList = overviewService.getLatestResourceInfo(rentId, num);
        return Result.ok(resourceStatisticsVOList);
    }

    /**
     * 资源查询：可以按照资源名称、资源代码、提供方名称、提供方代码等方式查询已上架的资源,按照发布日期倒序进行排序 查询对象: 所有库、三大库各个库里面信息。
     */
    @ApiOperation(value = "通过最新资源入口进行资源查询", notes="可以按照资源名称、资源代码、提供方名称、" +
            "提供方代码等方式查询已上架的资源,按照发布日期倒序进行排序", httpMethod = "GET")
    @RequestMapping("/getPublishedAll")
    @ResponseBody
    public Result queryPublishedResourceByCondition(
            ResourceCatalogSearchVO catalogSearchVO) {

        catalogSearchVO.setRentId(userUtils.getCurrentUserRentId());
        catalogSearchVO.setCreator(getUserName());
        catalogSearchVO.setStatus((PUB_SUCCESS.getStatusCode()));

        Long catalogId = catalogSearchVO.getCatalogId();
        if (catalogId != null) {
            CatalogNodeVO catalogNodeVO = catalogClassifyService.getCatalogNode(catalogId);
            catalogSearchVO.setCatalogCode(
                    catalogNodeVO.getParentFullCode() + catalogNodeVO.getResourceEncode());
        }

        String catalogCode = catalogSearchVO.getCatalogCode();
        if (StringUtils.isNotBlank(catalogCode)) {
            if (catalogCode.startsWith("0")) {
                catalogSearchVO.setCatalogCode(catalogCode.substring(catalogCode.indexOf("0") + 1));
            }
        }

        ResultPager tasks;
        try {
            //原型图暂时按照 发布成功的去查询
            tasks = overviewService.getPublishedResourcesByCondition(catalogSearchVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(tasks);
    }

    /*
     *  库内容基本查询： 包含基础库、部门库、主题库查询
     *  查询对象: 所有库、三大库各个库里面信息。
     */
    @ApiOperation(value = "通过三大基本库入口进行资源查询", notes="可以按照资源名称、资源代码、提供方名称、" +
            "提供方代码等方式查询已上架的资源,按照发布日期倒序进行排序", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "libName", value = "基本类名称", required = false, dataType="String"),
            @ApiImplicitParam(name = "name", value = "资源名称", required = false, dataType="String"),
            @ApiImplicitParam(name = "code", value = "资源编码", required = false, dataType="String"),
            @ApiImplicitParam(name = "deptName", value = "部门提供方名称", required = false, dataType="String"),
            @ApiImplicitParam(name = "deptCode", value = "部门提供方编码", required = false, dataType="String"),
            @ApiImplicitParam(name = "page", value = "分页起始页", required = false, dataType="Long"),
            @ApiImplicitParam(name = "pageSize", value = "分页页面大小", required = false, dataType="Long"),
    })
    @RequestMapping("/getLib")
    @ResponseBody
    public Result queryLibResourceByCondition(
            @RequestParam(value = "libName", required = false) String libName,
            @RequestParam(value = "name", required = false) String resourceName,
            @RequestParam(value = "code", required = false) String resourceCode,
            @RequestParam(value = "deptName", required = false) String deptName,
            @RequestParam(value = "deptCode", required = false) String deptCode,
            @RequestParam(value = "page", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        String user = getUserName(); //"admin";
        String rentName = userUtils.getCurrentUserRentName();

        Map<String, String> queryCondition = new HashMap<String, String>();
        queryCondition.put("lib_name", libName);
        queryCondition.put("rentId", userUtils.getCurrentUserRentId().toString());
        if (StringUtils.isNotEmpty(resourceName)) {
            queryCondition.put("name", resourceName);
        }
        if (StringUtils.isNotEmpty(resourceCode)) {
            queryCondition.put("code", resourceCode);
        }
        if (StringUtils.isNotEmpty(deptName)) {
            queryCondition.put("dept_name", deptName);
        }
        if (StringUtils.isNotEmpty(deptCode)) {
            queryCondition.put("dept_code", deptCode);
        }

        ResultPager tasks = null;
        try {
            //原型图暂时按照 发布成功的去查询
            tasks = overviewService
                    .getLibResourcesByCondition(PUB_SUCCESS.getStatusCode(), queryCondition,
                            pageNum, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
    }
        return Result.ok(tasks);
    }


}

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.idatrix.resource.common.utils.ResourceTools.ResourceStatus.PUB_SUCCESS;

/**
 * Created by Robin Wing on 2018-5-28.
 */

@Controller
@RequestMapping("/overview")
public class OverviewController extends BaseController {

    @Autowired
    private IOverviewService overviewService;

    @Autowired
    private IResourceConfigService resourceConfigService;

    @Autowired
    private IResourceStatiscsService resourceStatiscsService;

    @Autowired
    private ICatalogClassifyService catalogClassifyService;

    private static final Logger LOG = LoggerFactory.getLogger(OverviewController.class);

    /*获取订阅详情*/
    @RequestMapping("/getResource")
    @ResponseBody
    public Result getResourceById(@RequestParam(value = "id", required = true) Long id) {
        ResourceConfigVO resourceConfigVO = null;
        try {
            resourceConfigVO = resourceConfigService.getResourceInfoById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        resourceStatiscsService.increaseViewDataCount(id);
        return Result.ok(resourceConfigVO);
    }

    /*总起情况: 包含总体 注册量、发布量、订阅量*/
    @RequestMapping("/getOverall")
    @ResponseBody
    public Result getOverall() {

        Boolean flag = true;
        MonthStatisticsVO rsVO = new MonthStatisticsVO();
        rsVO = overviewService.getOverall();
        //总注册量
        return Result.ok(rsVO);
    }

    /*获取三大类基本信息库概览: 基础库、部门库、主题库,分别获取 注册量、发布量、订阅量数据
     * libName: 分为 基础库、部门库、主题库： base/department/topic
     * type： 分为 注册量、发布量、订阅量： reg/pub/sub
     *
     * */
    @RequestMapping("/getOverview")
    @ResponseBody
    public Result getOverview(@RequestParam(value = "num", required = false) Integer num) {
        /*默认读取最近半年的数据*/
        if (num == null) {
            num = 6;
        }
        List<MonthStatisticsVO> rsVOList = new ArrayList<MonthStatisticsVO>();
        rsVOList = overviewService.getMonthlyTotalAmount(num);
        return Result.ok(rsVOList);
    }

    /*获取最新的N个资源信息*/
    @RequestMapping("/getLatest")
    @ResponseBody
    public Result getLatest(@RequestParam(value = "num", required = false) Long num) {

        //TODO: 最近资源需要为发布状态
        List<ResourceStatisticsVO> resourceStatisticsVOList = new ArrayList<ResourceStatisticsVO>();
        if (num == null) {
            num = 3L;
        }
        resourceStatisticsVOList = overviewService.getLatestResourceInfo(num);
        return Result.ok(resourceStatisticsVOList);
    }

    /*获取最新资源点击更多时候
     *  在所有库里面，按照状态为已经上架，并且按照 update_time 倒序展示出来
     *  结果按照分页展示：pageNum 表示页数，
     *
     */
    @RequestMapping("/getMoreLatest")
    @ResponseBody
    public Result getMoreLatest(@RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return null;
    }

    /**
     * 资源查询：可以按照资源名称、资源代码、提供方名称、提供方代码等方式查询已上架的资源,按照发布日期倒序进行排序 查询对象: 所有库、三大库各个库里面信息。
     */
    @RequestMapping("/getPublishedAll")
    @ResponseBody
    public Result queryPublishedResourceByCondition(
            ResourceCatalogSearchVO catalogSearchVO) {

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
            tasks = overviewService
                    .getPublishedResourcesByCondition(catalogSearchVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(tasks);
    }

    /*
     *  库内容基本查询： 包含基础库、部门库、主题库查询
     *  查询对象: 所有库、三大库各个库里面信息。
     */
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

        Map<String, String> queryCondition = new HashMap<String, String>();
        queryCondition.put("lib_name", libName);
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
            return Result.error(6001000, e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(tasks);
    }
}

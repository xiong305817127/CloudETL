package com.idatrix.resource.portal.controller;

import com.idatrix.resource.catalog.service.ICatalogClassifyService;
import com.idatrix.resource.catalog.service.IResourceConfigService;
import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.portal.service.IHomepageService;
import com.idatrix.resource.portal.service.IPortalSystemConfigService;
import com.idatrix.resource.portal.service.IStatisticsService;
import com.idatrix.resource.portal.vo.CatalogResourceInfo;
import com.idatrix.resource.portal.vo.DeptResourceStatisticsVO;
import com.idatrix.resource.portal.vo.PubCount;
import com.idatrix.resource.portal.vo.ResourcePubInfo;
import com.idatrix.resource.servicelog.po.ServiceLogPO;
import com.idatrix.resource.servicelog.service.IServiceLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 *  门户系统主页请求
 */

@Slf4j
@Controller
@RequestMapping("/portal/homepage")
@Api(value = "portal-homepage" , tags="资源门户系统-主页请求信息")
public class HomepageController extends BaseController{

    @Autowired
    private ICatalogClassifyService catalogClassifyService;

    @Autowired
    private IHomepageService homepageService;

    @Autowired
    private IStatisticsService statisticsService;

    @Autowired
    private IResourceConfigService resourceConfigService;

    @Autowired
    private IPortalSystemConfigService portalSystemConfigService;

    @Autowired
    private IServiceLogService serviceLogService;


    /**
     * 门户： 获取数据发布详情，内容格式为 [部门]于时间 发布1个 XXX(资源内容) 资源
     * @param count 表示需要显示个数
     * @return
     */
    @ApiOperation(value = "获取数据发布详情", notes="内容格式为 [部门]于时间 发布1个 XXX(资源内容) 资源", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "count", value = "表示需要显示个数", required = false,paramType="query",dataType="Long")
    })
    @RequestMapping("/getPubInfo")
    @ResponseBody
    public Result<List<ResourcePubInfo>> getPubInfo(@RequestParam(value = "count", required = false) Long count){

        Long rentId = portalSystemConfigService.getPortalRentId();
        List<ResourcePubInfo> pubInfo = homepageService.getResourcePubInfo(rentId, count);
        log.info("数据发布情况 {}", pubInfo.toString());
        return Result.ok(pubInfo);
    }


    /**
     * 门户： 获取数据发布详情，内容格式为 [部门]于时间 发布1个 XXX(资源内容) 资源
     * @param count 表示需要显示个数
     * @return
     */
    @ApiOperation(value = "获取接口调用详情", notes="内容格式为 [部门]于时间 调用1个 XXX(资源内容) 资源", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "count", value = "表示需要显示个数", required = false,paramType="query",dataType="Long")
    })
    @RequestMapping("/getInterfaceInfo")
    @ResponseBody
    public Result<List<ResourcePubInfo>> getInterfaceInfo(@RequestParam(value = "count", required = false) Long count){

        List<ResourcePubInfo> pubInfo = new ArrayList<>();
        count=count==null?10:count;
        List<ServiceLogPO> slPOList = serviceLogService.getLastestCalledService(count);
        if(slPOList!=null&&slPOList.size()>0){
            for(ServiceLogPO slPO:slPOList){
                pubInfo.add(new ResourcePubInfo(slPO.getCallerDeptName(),
                        slPO.getCreateTime(), "接口" ));
            }
        }
        log.info("接口调用情况 {}", pubInfo.toString());
        return Result.ok(pubInfo);
    }

    /**
     * 获取 不同类型资源发布数量
     * @return
     */
    @ApiOperation(value = "获取不同类型资源发布数量", notes="获取不同类型资源发布数量", httpMethod = "GET")
    @RequestMapping("/getPubTotal")
    @ResponseBody
    public Result<PubCount> getPubTotalCount(){
        Long rentId = portalSystemConfigService.getPortalRentId();
        PubCount pubCount = homepageService.getPubTotalCount(rentId);
        log.info("不同类型资源发布数量 {}", pubCount.toString());
        return  Result.ok(pubCount);
    }


    /**
     * 获取资源目录分类的子目录
     * @param id
     * @return
     */
    @ApiOperation(value = "获取资源目录分类的子目录", notes="获取资源目录分类节点所有子树", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="表示资源目录分类节点的ID,id为0时候表示获取整个分类树，其它表示具体ID子树", required = true,paramType="query",dataType = "Long"),
    })
    @RequestMapping("/getSubtree")
    @ResponseBody
    public Result<List<CatalogNodeVO>> getCatalogSubtree(@RequestParam(value="id", required = true)Long id){
        Long rentId = portalSystemConfigService.getPortalRentId();
        String user = portalSystemConfigService.getPortalUserName();
        List<CatalogNodeVO> catalogNodeVOList = catalogClassifyService.getCatalogNodeSubtree(rentId, user, id);
        return Result.ok(catalogNodeVOList);
    }


    /**
     * 获取子节点包含资源个数统计
     * @param id
     * @return
     */
    @ApiOperation(value = "获取子节点包含资源个数统计", notes="获取子节点包含资源个数统计", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="表示资源目录分类节点的ID,id为0时候表示获取整个分类树，其它表示具体ID子树", required=true,paramType="query",dataType="Long"),
    })
    @RequestMapping("/getCatalogNodeResourceCount")
    @ResponseBody
    public Result<List<CatalogResourceInfo>> getResourceCount(@RequestParam(value="id", required = true)Long id){
        Long rentId = portalSystemConfigService.getPortalRentId();
        List<CatalogResourceInfo> crList = homepageService.getCatalogClassifyInfo(rentId, id);
        return Result.ok(crList);
    }


    /**
     * 获取政务信息资源分布情况
     * @param type
     * @return
     */
    @ApiOperation(value = "获取政务信息资源分布情况", notes="从资源所属类型（基础类、主题类、部门类）获取信息资源目录分布、数据分布、接口分布等情况", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="type",value="资源类型，可以取值base/topic/dept分布表示（基础类/主题类/部门类）获取信息资源目录分布、数据分布、接口分布等情况", required=true,paramType="query",dataType="String"),
    })
    @RequestMapping("/getDistributeInfo")
    @ResponseBody
    public Result<List<DeptResourceStatisticsVO>> getDistributeInfo(@RequestParam(value="type", required = true)String type){
        Long rentId = portalSystemConfigService.getPortalRentId();
        if(StringUtils.equalsAnyIgnoreCase(type, "dept")){
            type = "department";  //前端用dept作为部门类，实际数据库存储使用的是department
        }
        List<DeptResourceStatisticsVO> crList = homepageService.getDistributeInfo(rentId, type);
        log.info("政务信息资源分布情况 {},内容：{}",type, crList.toString());
        return Result.ok(crList);
    }
}

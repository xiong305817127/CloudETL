package com.idatrix.resource.portal.controller;

import com.idatrix.resource.catalog.po.CatalogNodePO;
import com.idatrix.resource.catalog.service.ICatalogClassifyService;
import com.idatrix.resource.catalog.service.IResourceConfigService;
import com.idatrix.resource.catalog.service.IResourceStatiscsService;
import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import com.idatrix.resource.catalog.vo.ResourceConfigVO;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.resource.datareport.service.IResourceFileService;
import com.idatrix.resource.datareport.vo.ResourceFileVO;
import com.idatrix.resource.portal.service.IPortalSystemConfigService;
import com.idatrix.resource.portal.service.IQueryService;
import com.idatrix.resource.portal.vo.ResourceQueryRequestVO;
import com.idatrix.resource.portal.vo.ResourceQueryVO;
import com.idatrix.resource.portal.vo.ResourceSearchRequestVO;
import com.idatrix.resource.portal.vo.ResourceSearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源门户查询
 */

@Controller
@RequestMapping("/portal/query")
@Api(value = "/portal/query" , tags="资源门户系统-资源目录查询接口")
public class QueryController {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private IQueryService queryService;

    @Autowired
    private IPortalSystemConfigService portalSystemConfigService;

    @Autowired
    private IResourceConfigService resourceConfigService;

    @Autowired
    private IResourceStatiscsService resourceStatiscsService;

    @Autowired
    private IResourceFileService resourceFileService;

    @Autowired
    private ICatalogClassifyService catalogClassifyService;


    /**
     * 门户： 根据条件查询资源
     *
     * @param queryRequestVO 查询条件
     * @return
     */
    @ApiOperation(value = "门户资源查询接口", notes = "根据资源类型、资源名称等条件查询资源", httpMethod = "POST")
    @RequestMapping(value = "/queryResource", method = RequestMethod.POST)
    @ResponseBody
    public Result<ResultPager<ResourceQueryVO>> queryResource(@RequestBody ResourceQueryRequestVO queryRequestVO) {

        Long rentId = portalSystemConfigService.getPortalRentId();

        ResultPager<ResourceQueryVO> result = null;
        queryRequestVO.setRentId(rentId);

        Long catalogId = queryRequestVO.getCatalogId();
        if (catalogId != null) {
            CatalogNodeVO catalogNodeVO = catalogClassifyService.getCatalogNode(catalogId);
            queryRequestVO.setCatalogCode(
                    catalogNodeVO.getParentFullCode() + catalogNodeVO.getResourceEncode());
        }

        String catalogCode = queryRequestVO.getCatalogCode();
        if (StringUtils.isNotBlank(catalogCode)) {
            if (catalogCode.startsWith("0")) {
                queryRequestVO.setCatalogCode(catalogCode.substring(catalogCode.indexOf("0") + 1));
            }
        }

        String catalogName = queryRequestVO.getCatalogName();
        if(StringUtils.isNotEmpty(catalogName)){
            List<CatalogNodePO> cnList = catalogClassifyService.getCatalogNodeByCatalogName(rentId, catalogName, 2L);
            if(cnList!=null&&cnList.size()>0){
                CatalogNodePO nodePO = cnList.get(0);
                queryRequestVO.setCatalogCode(nodePO.getParentFullCode() + nodePO.getResourceEncode());
            }else{
                queryRequestVO.setCatalogCode("4");
            }
        }

        try {
            result = queryService.queryResourceByCondition(queryRequestVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(result);
    }


    /**
     * 门户： 根据关键字搜索已发布资源
     *
     * @param searchRequestVO 查询条件
     * @return
     */
    @ApiOperation(value = "门户资源搜索接口", notes = "根据资源名称、资源摘要、信息项条件查询资源", httpMethod = "POST")
    @ApiImplicitParam(name = "searchRequestVO", value = "检索资源目录对象,json格式", required = true, dataType = "SearchRequestVO")
    @RequestMapping(value = "/searchResource", method = RequestMethod.POST)
    @ResponseBody
    public Result<ResultPager<ResourceSearchVO>> queryResourceByKeyword(@RequestBody ResourceSearchRequestVO searchRequestVO) {


        ResultPager<ResourceSearchVO> result;
        try {
            result = queryService.queryResourceByKeyword(searchRequestVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(result);
    }

    /*查找所有政务信息资源*/
    @ApiOperation(value = "获取资源信息详情", notes="获取资源信息详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "信息资源ID", required = true, dataType="Long"),
            @ApiImplicitParam(name = "type", value = "主要用于统计用户点详情查看次数，如果需要统计改点击次数，参数设置成 count", required = false, dataType="String")
    })
    @RequestMapping("/getResource")
    @ResponseBody
    public Result<ResourceConfigVO> getResourceById(@RequestParam(value = "userName", required = false) String userName,
                                @RequestParam(value = "id", required = true) Long id,
                                      @RequestParam(value = "type", required = false) String type) {

        Long rentId = portalSystemConfigService.getPortalRentId();

        ResourceConfigVO resourceConfigVO = null;
        try {
            resourceConfigVO =  resourceConfigService.getResourceInfoById(userName, id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        if(StringUtils.equalsIgnoreCase(type, "count")){
            resourceStatiscsService.increaseViewDataCount(id);
        }
        return Result.ok(resourceConfigVO);
    }

    /*上报文件查看*/
    @ApiOperation(value = "资源文件查询", notes="资源文件查询", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资源ID", required = true, dataType="Long",paramType = "query"),
    })
    @RequestMapping(value="/getResourceFile", method= RequestMethod.GET)
    @ResponseBody
    public Result getResourceFile(
            @RequestParam(value = "id") Long resourceId){

        Map<String, String> con = new HashMap<String, String>();
        con.put("id",resourceId.toString());
        con.put("descOrder", "down");
        List<ResourceFileVO> resourceFileVOList ;
        try{
            resourceFileVOList = resourceFileService.getResourceFileByResourceId(con);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(resourceFileVOList);
    }




}

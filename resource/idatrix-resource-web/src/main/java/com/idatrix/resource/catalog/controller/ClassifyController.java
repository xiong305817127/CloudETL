package com.idatrix.resource.catalog.controller;

import com.idatrix.resource.catalog.service.ICatalogClassifyService;
import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import com.idatrix.resource.catalog.vo.request.BatchImportRequestVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 政务信息分类树结构  增删改查
 * @Author: Wangbin
 * @Date: 2018/5/23
 */

@Controller
@RequestMapping("/classify")
@Api(value = "/classify" , tags="资源管理-资源分类处理接口")
public class ClassifyController extends BaseController {

    @Autowired
    private ICatalogClassifyService catalogClassifyService;

    @Autowired
    private UserUtils userUtils;

    private static final Logger LOG = LoggerFactory.getLogger(ClassifyController.class);


    /**
     *  Title: 通过Excel 批量导入 资源分类
     */
    @ApiOperation(value = "Excel批量导入资源分类中文件上传", notes="通过Excel批量导入资源分类", httpMethod = "POST")
    @RequestMapping(value="/batchImport", method=RequestMethod.POST)
    @ResponseBody
    public Result batchImport(@RequestParam("file") CommonsMultipartFile file) {
        String user = getUserName(); //"admin";
        LOG.info("fileName:{}", file.getOriginalFilename());
        String fileName = null;
        try{
            fileName = catalogClassifyService.saveBatchImport(user, file);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        Map<String, Object> attach = new HashMap<String, Object>();
        attach.put("fileName", fileName);
        return Result.ok(attach);
    }

    /**
     *  Title: 通过Excel 批量导入 资源分类
     */
    @ApiOperation(value = "Excel批量导入资源分类中点击确认", notes="Excel批量导入资源分类中点击确认", httpMethod = "POST")
    @RequestMapping(value="/processExcel", method=RequestMethod.POST)
    @ResponseBody
   public Result processExcel(@RequestBody BatchImportRequestVO batchImportRequestVO) {
        String fileName = batchImportRequestVO.getFileName();
        LOG.info("fileName:{}", fileName);
        String user = getUserName(); //"admin";
        Long rentId = userUtils.getCurrentUserRentId();
        LOG.info("Excel批量导入资源分类开始时间 {}", DateTools.formatDate(new Date()));
        try{
            catalogClassifyService.processExcel(rentId, user, fileName);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        LOG.info("Excel批量导入资源分类结束时间 {}", DateTools.formatDate(new Date()));
        return Result.ok(true);
    }


    /**
     *  增加信息济源分类节点/修改节点信息在此处统一处理
     *  @return
     */
    @ApiOperation(value = "增加资源分类", notes="增加信息济源分类节点/修改节点信息在此处统一处理", httpMethod = "POST")
    @RequestMapping(value="/save", method=RequestMethod.POST)
    @ResponseBody
    public Result saveCatalogNode(@RequestBody CatalogNodeVO node) {

        String user = getUserName(); //"admin";
        Long rentId = userUtils.getCurrentUserRentId();
        LOG.info("CatalogNodeVO 资源分类保存信息：{}", node.toString());
        Long id = null;
        try {
            id = catalogClassifyService.saveCatalogNode(rentId, user, node);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        Map<String, Object> attach = new HashMap<String, Object>();
        attach.put("id", id);
        return Result.ok(attach);
    }


    /**
     * 删除信息资源分类节点,删除节点时候需要考虑本身节点以及子节点。
     *
     * @return
     */
    @ApiOperation(value = "删除资源分类节点", notes="本身节点以及子节点没有被添加资源都会删除", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资源分类节点ID", required = true, dataType="Long"),
    })
    @RequestMapping("/delete")
    @ResponseBody
    public Result deleteCatalogNode(@RequestParam(value = "id", required = true) Long id) {
        Boolean flag = true;
        String user = getUserName(); //"admin";
        Long rentId = userUtils.getCurrentUserRentId();
        try {
            catalogClassifyService.deleteCatalogNode(rentId, user, id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(flag);
    }

    /**
     * 查找信息资源分类所有信息
     *
     * @return
     */
    @ApiOperation(value = "查找信息资源分类所有信息", notes="分类节点将按照树型结构返回", httpMethod = "GET")
    @RequestMapping("/getAll")
    @ResponseBody
    public Result<List<CatalogNodeVO>> getAllCatalogNode() {
        Boolean flag = false;
        String user = getUserName();
        Long rentId = userUtils.getCurrentUserRentId();
        List<CatalogNodeVO> catalogNodeVOList = catalogClassifyService.getAllCatalogNode(rentId, user);
        return Result.ok(catalogNodeVOList);
    }

    /**
     * 查找某个节点下面一级目录子树
     *
     * @return
     */
    @ApiOperation(value = "查找某个节点下面一级目录子树", notes="查找某个节点下面一级目录子树", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资源分类节点ID", required = true, dataType="Long"),
    })
    @RequestMapping("/getSubtree")
    @ResponseBody
    public Result getCatalogNodeSubtree(@RequestParam(value = "id", required = true) Long id) {
        Long rentId = userUtils.getCurrentUserRentId();
        String user = userUtils.getCurrentUserName();
        List<CatalogNodeVO> catalogNodeVOList = catalogClassifyService.getCatalogNodeSubtree(rentId, user, id);
        return Result.ok(catalogNodeVOList);
    }

    /**
     * 查找某个节点下面一级目录子树
     *
     * @return
     */
    @ApiOperation(value = "根据分类节点和深度查找子节点", notes="根据分类节点和深度查找子节点", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资源分类节点ID", required = false, dataType="Long"),
    })
    @RequestMapping("/getSubtreeAndDepth")
    @ResponseBody
    public Result<List<CatalogNodeVO>> getSubtreeAndDepth(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                                     @RequestParam(value = "depth", required = false, defaultValue = "2")Long depth) {
        Long rentId = userUtils.getCurrentUserRentId();
        String user = userUtils.getCurrentUserName();
        Long startTime = System.currentTimeMillis();
        List<CatalogNodeVO> catalogNodeVOList = catalogClassifyService.getCatalogNodeSubtree(rentId, user, id, depth);
        LOG.info("根据分类节点和深度查找子节点耗时 {}",System.currentTimeMillis()-startTime);
        return Result.ok(catalogNodeVOList);
    }

    /**
     * 查找某个节点详细信息
     *
     * @return
     */
    @ApiOperation(value = "查找某个节点详细信息", notes="查找某个节点详细信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资源分类节点ID", required = true, dataType="Long"),
    })
    @RequestMapping("/getNode")
    @ResponseBody
    public Result getCatalogNode(@RequestParam(value = "id", required = true) Long id) {

        CatalogNodeVO catalogNodeVO = catalogClassifyService.getCatalogNode(id);
        return Result.ok(catalogNodeVO);
    }
}

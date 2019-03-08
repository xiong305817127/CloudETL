package com.idatrix.resource.catalog.controller;

import com.idatrix.resource.catalog.service.ICatalogClassifyService;
import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import com.idatrix.resource.catalog.vo.request.BatchImportRequestVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.ArrayList;
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
public class ClassifyController extends BaseController {

    @Autowired
    private ICatalogClassifyService catalogClassifyService;

    private static final Logger LOG = LoggerFactory.getLogger(ClassifyController.class);


    /**
     *  Title: 通过Excel 批量导入 资源分类
     */
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
            return Result.error(6001000, e.getMessage());
        }
        Map<String, Object> attach = new HashMap<String, Object>();
        attach.put("fileName", fileName);
        return Result.ok(attach);
    }

    /**
     *  Title: 通过Excel 批量导入 资源分类
     */
    @RequestMapping(value="/processExcel", method=RequestMethod.POST)
    @ResponseBody
   public Result processExcel(@RequestBody BatchImportRequestVO batchImportRequestVO) {
        String fileName = batchImportRequestVO.getFileName();
        LOG.info("fileName:{}", fileName);
        String user = getUserName(); //"admin";
        try{
            catalogClassifyService.processExcel(user, fileName);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(true);
    }


    /**
     *  增加信息济源分类节点/修改节点信息在此处统一处理
     *  @return
     */
    @RequestMapping(value="/save", method=RequestMethod.POST)
    @ResponseBody
    public Result saveCatalogNode(@RequestBody CatalogNodeVO node) {

        String user = getUserName(); //"admin";
        LOG.info("CatalogNodeVO 资源分类保存信息：{}", node.toString());
        Long id = null;
        try {
            id = catalogClassifyService.saveCatalogNode(user, node);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
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
    @RequestMapping("/delete")
    @ResponseBody
    public Result deleteCatalogNode(@RequestParam(value = "id", required = true) Long id) {
        Boolean flag = true;
        String user = getUserName(); //"admin";
        try {
            catalogClassifyService.deleteCatalogNode(user, id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(flag);
    }

    /**
     * 查找信息资源分类所有信息
     *
     * @return
     */
    @RequestMapping("/getAll")
    @ResponseBody
    public Result getAllCatalogNode() {
        Boolean flag = false;
        List<CatalogNodeVO> catalogNodeVOList = new ArrayList<CatalogNodeVO>();
        catalogNodeVOList = catalogClassifyService.getAllCatalogNode();
        return Result.ok(catalogNodeVOList);
    }

    /**
     * 查找某个节点下面一级目录子树
     *
     * @return
     */
    @RequestMapping("/getSubtree")
    @ResponseBody
    public Result getCatalogNodeSubtree(@RequestParam(value = "id", required = true) Long id) {
        Boolean flag = true;
//        CatalogNodeVO catalogNodeVO = catalogClassifyService.getCatalogNodeSubtree(id);
        List<CatalogNodeVO> catalogNodeVOList = new ArrayList<CatalogNodeVO>();
        catalogNodeVOList = catalogClassifyService.getCatalogNodeSubtree(id);
        return Result.ok(catalogNodeVOList);
    }

    /**
     * 查找某个节点详细信息
     *
     * @return
     */
    @RequestMapping("/getNode")
    @ResponseBody
    public Result getCatalogNode(@RequestParam(value = "id", required = true) Long id) {
        Boolean flag = true;
        CatalogNodeVO catalogNodeVO = null;
        if(id!=null) {
            catalogNodeVO = catalogClassifyService.getCatalogNode(id);
        }
        return Result.ok(catalogNodeVO);
    }
}

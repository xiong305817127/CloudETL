package com.idatrix.resource.catalog.service;

import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.List;

/**
 * @ClassName: ICatalogClassifyService
 * @Description:  信息资源分类服务
 * @Author: Robin
 * @Date: 2018/05/17
 */
public interface ICatalogClassifyService {


    /**
     * 增加信息济源分类节点
     */
    Long saveCatalogNode(String user, CatalogNodeVO node) throws Exception;


    /**
     * 删除信息资源分类节点,删除节点时候需要考虑本身节点以及子节点。
     */
    int deleteCatalogNode(String user, Long id) throws Exception;


    /**
     * 查找信息资源分类所有信息
     */
    List<CatalogNodeVO> getAllCatalogNode();


    /**
     * 查找某个节点下一级字节点
     */
    List<CatalogNodeVO> getCatalogNodeSubtree(Long id);

    /**
     * 查找某个节点详细信息
     */
    CatalogNodeVO getCatalogNode(Long id);


    /*
    *  处理批量上传 节点信息
    */
    String saveBatchImport(String user, CommonsMultipartFile multiPartFile) throws Exception;

    /*
    *  处理批量上传 节点信息
    */
    void processExcel(String user,String fileName) throws Exception;


    /*测试使用*/
    public void saveExcelCatalogNode(String user, List<CatalogNodeVO> cnVOList) throws Exception;

}
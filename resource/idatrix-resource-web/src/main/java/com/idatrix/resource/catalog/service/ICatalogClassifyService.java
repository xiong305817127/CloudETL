package com.idatrix.resource.catalog.service;

import com.idatrix.resource.catalog.po.CatalogNodePO;
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
    Long saveCatalogNode(Long rentId, String user, CatalogNodeVO node) throws Exception;


    /**
     * 删除信息资源分类节点,删除节点时候需要考虑本身节点以及子节点。
     */
    int deleteCatalogNode(Long rentId, String user, Long id) throws Exception;


    /**
     * 查找信息资源分类所有信息
     */
    List<CatalogNodeVO> getAllCatalogNode(Long rentId,String user);


    /**
     * 查找某个节点下一级字节点
     */
    List<CatalogNodeVO> getCatalogNodeSubtree(Long rentId, String user, Long id);

    /**
     * 查找某个节点下一级字节点
     */
    List<CatalogNodeVO> getCatalogNodeSubtree(Long rentId, String user, Long id, Long depth);

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
    void processExcel(Long rentId, String user,String fileName) throws Exception;


    /*测试使用*/
    public void saveExcelCatalogNode(Long rentId, String user, List<CatalogNodeVO> cnVOList) throws Exception;

    /**
     * 根据租户和分类名称获取分类列表
     * @param rentId
     * @param catalogName
     * @return
     */
    List<CatalogNodePO> getCatalogNodeByCatalogName(Long rentId, String catalogName, Long depth);

}
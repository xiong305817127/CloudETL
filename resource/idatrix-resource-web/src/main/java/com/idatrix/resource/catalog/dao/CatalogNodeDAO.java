package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.CatalogNodePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CatalogNodeDAO {

    void insert(CatalogNodePO catalogNodePO);

    void insertList(List<CatalogNodePO> catalogNodeVOList);

    void deleteByNodeId(Long id);

    int updateById(CatalogNodePO catalogNodePO);

    CatalogNodePO getCatalogNodeById(Long id);

    List<CatalogNodePO> getByParentFullCode(String parentFullCode);


    /*获取父节点为parentId 的节点列表*/
    List<CatalogNodePO> getCatalogByParentId(@Param("rentId") Long rentId,
                                             @Param("parentId") Long parentId);

    /*获取所有是父节点的目录 ID列表*/
    List<Long> getCatalogParentList();

    /**************************多租户隔离增加的接口**************************/
    /*获取所有 目录树节点*/
    List<CatalogNodePO> getAllCatalogNodesByRentId(Long rentId);

    CatalogNodePO getByCondition(@Param("rentId")Long rentId, @Param("fullCode")String parentFullCode,
                                 @Param("code")String code,  @Param("name")String name);

    /*获取所有是父节点的目录 ID列表*/
    List<Long> getCatalogParentListByRentId(Long rentId);

    List<CatalogNodePO> getByParentFullCodeByRentId(@Param("rentId") Long rentId,
                                                    @Param("parentFullCode") String parentFullCode);

    List<CatalogNodePO> getObscureByParentFullCodeAndRentId(@Param("rentId") Long rentId,
                                                    @Param("parentFullCode") String parentFullCode,
                                                           @Param("depth")Long depth);


    /**
     * 根据租户和分类名称获取分类列表
     * @param rentId
     * @param catalogName
     * @return
     */
    List<CatalogNodePO> getCatalogNodeByCatalogName(@Param("rentId")Long rentId,
                                                    @Param("catalogName")String catalogName,
                                                    @Param("depth")Long depth);
}

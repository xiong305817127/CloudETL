package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.CatalogNodePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CatalogNodeDAO {

    public void insert(CatalogNodePO catalogNodePO);

    public void insertList(List<CatalogNodePO> catalogNodeVOList);

    public void deleteByNodeId(Long id);

    public int updateById(CatalogNodePO catalogNodePO);

    public CatalogNodePO getCatalogNodeById(Long id);

    public List<CatalogNodePO> getByParentFullCode(String parentFullCode);

    public CatalogNodePO getByCondition(@Param("fullCode")String parentFullCode,
              @Param("code")String code,@Param("name")String name);

    /*获取所有 目录树节点*/
    public List<CatalogNodePO> getAllCatalogNodes();

    /*获取父节点为parentId 的节点列表*/
    public List<CatalogNodePO> getCatalogByParentId(Long parentId);

    /*获取所有是父节点的目录 ID列表*/
    public List<Long> getCatalogParentList();
}

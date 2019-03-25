package com.idatrix.resource.catalog.dao;


import com.idatrix.resource.catalog.po.CatalogResourcePO;

import java.util.List;

/**
 * Created by Robin Wing on 2018-5-28.
 */
public interface CatalogResourceDAO {

    void insert(CatalogResourcePO catalogResourcePO);

    void deleteByResourceId(Long resourceId);

    /*可以根据resourceId或者catalogId去查找*/
    List<CatalogResourcePO> getByResourceId(Long resourceId);

    List<CatalogResourcePO> getByCatalogId(Long catalog);

    List<CatalogResourcePO> getAll();

    Long[] getCatalogListByResourceId(Long resourceId);

    /*此处不需要使用update,逻辑上需要修改信息时，改变了父节点关系，是要先删除的！*/

    Long getCatalogCount(Long catalogId);
}

package com.idatrix.resource.catalog.dao;


import com.idatrix.resource.catalog.po.CatalogResourcePO;

import java.util.List;

/**
 * Created by Robin Wing on 2018-5-28.
 */
public interface CatalogResourceDAO {

    public void insert(CatalogResourcePO catalogResourcePO);

    public void deleteByResourceId(Long resourceId);

    /*可以根据resourceId或者catalogId去查找*/
    public List<CatalogResourcePO> getByResourceId(Long resourceId);

    public List<CatalogResourcePO> getByCatalogId(Long catalog);

    public List<CatalogResourcePO> getAll();

    public Long[] getCatalogListByResourceId(Long resourceId);

    /*此处不需要使用update,逻辑上需要修改信息时，改变了父节点关系，是要先删除的！*/


}

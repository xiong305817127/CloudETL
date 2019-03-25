package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.ResourceHistoryPO;

import java.util.List;

/**
 * Created by Robin Wing on 2018-6-6.
 */
public interface ResourceHistoryDAO {

    public void insert(ResourceHistoryPO resourceHistoryPO);

    public void deleteById(Long id);

    public void deleteByResource(Long resourceId);

    public ResourceHistoryPO getHistoryById(Long id);

    public List<ResourceHistoryPO> getHistoryByResourceId(Long resourceId);

}

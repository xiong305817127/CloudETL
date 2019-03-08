package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.ResourceColumnPO;
import java.util.List;

/**
 * Created by Robin Wing on 2018-5-23.
 */
public interface ResourceColumnDAO {

    void insert(ResourceColumnPO resourceColumnPO);

    void deleteById(Long id);

    void deleteByResourceId(Long resourceId);

    int updateById(ResourceColumnPO resourceColumnPO);

    ResourceColumnPO getColumnById(Long id);

    List<ResourceColumnPO> getAllResourceColumn();

    List<ResourceColumnPO> getColumnByResourceId(Long resourceId);
}

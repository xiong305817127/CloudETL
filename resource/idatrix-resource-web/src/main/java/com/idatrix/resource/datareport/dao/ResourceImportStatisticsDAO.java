package com.idatrix.resource.datareport.dao;

import com.idatrix.resource.datareport.po.ResourceImportStatisticsPO;

import java.util.List;

public interface ResourceImportStatisticsDAO {
    int deleteByPrimaryKey(Long id);

    int insert(ResourceImportStatisticsPO record);

    int insertSelective(ResourceImportStatisticsPO record);

    ResourceImportStatisticsPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ResourceImportStatisticsPO record);

    int updateByPrimaryKey(ResourceImportStatisticsPO record);

    List<ResourceImportStatisticsPO> getAllImportStatitics();
}
package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SystemSettings;
import org.apache.ibatis.annotations.Param;

public interface SystemSettingsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SystemSettings record);

    int insertSelective(SystemSettings record);

    SystemSettings selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SystemSettings record);

    int updateByPrimaryKey(SystemSettings record);

    SystemSettings findSystemSetByRenterId(@Param("renterId") Long renterId);
}
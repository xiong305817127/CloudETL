package com.idatrix.resource.basedata.dao;

import com.idatrix.resource.basedata.po.SystemConfigPO;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Robin Wing on 2018-6-14.
 */
public interface SystemConfigDAO {

    void insert(SystemConfigPO systemConfigPO);

    void deleteById(Long id);

    void deleteByCreator(String creator);

    SystemConfigPO getById(Long id);

    SystemConfigPO getByCreatorName(String creator);

    int updateById(SystemConfigPO systemConfigPO);

    SystemConfigPO getLastestSysConfig();

    /*根据租户信息查询： rentInfo实际上为 "rentId+" 如 ”123+" */
    SystemConfigPO getAdaptByRentId(@Param(value="rentInfo") String rentInfo);

}

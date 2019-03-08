package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.api.domain.LoginDateInfo;
import com.idatrix.unisecurity.api.domain.OrganizationUserLoginInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface LoginCountProviderMapper {

    // 查询今日一共登录次数
    int findNowLoginCountByRenterId(@Param("renterId") Long renterId);

    // 查询今日登录的用户数
    int findNowLoginUserCountByRenterId(@Param("renterId") Long renterId);

    // 今日登录的组织数
    int findNowLoginDeptCountByRenterId(@Param("renterId") Long renterId);

    // 查询所有的登录次数
    int findAllLoginCountByRenterId(@Param("renterId") Long renterId);

    // 根据租户ID获取所属部门下的登录情况
    List<OrganizationUserLoginInfo> findDeptUserLoginInfoByRentId(@Param("renterId") Long renterId);

    // 根据租户ID和一个确定的时间段获取时间段中每一天的登录次数登录单位
    List<LoginDateInfo> findUserLoginInfoByRenterIdAndTimeSlot(@Param("renterId") Long renterId,
                                                               @Param("startLoginDate") Date startLoginDate, @Param("lastLoginDate") Date lastLoginDate);

}

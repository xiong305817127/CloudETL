package com.idatrix.resource.basedata.service;

import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.vo.SystemConfigVO;
import com.idatrix.unisecurity.api.domain.User;

/**
 * Created by Robin Wing on 2018-6-14.
 */
public interface ISystemConfigService {

    /*存储系统配置*/
    Long save(Long rentId, String user, SystemConfigVO systemConfigVO) throws Exception;

    /*获取当前用户系统配置*/
    SystemConfigVO getSystemConfig(String user);

    /*获取当前用户系统配置*/
    SystemConfigVO getSystemConfig(Long rentId);

    /*根据用户系统参数*/
    SystemConfigPO getSystemConfigByUser(String user);

    /*获取当前用户系统配置*/
    SystemConfigPO getSystemConfig();

    /*获取资料填报人员: deptId 表示部门所在ID*/
    User getDeptStaff(int deptId) throws Exception;

    /*获取中心管理员用户名*/
    String getCenterUserName(Long rentId) throws Exception;

    /*获取订阅审核人员: deptId 表示部门所在ID, 存在跨租户问题，所以需要根据创建用户所在租户查找*/
    User getSubscribeApprover(String user, Long deptId) throws Exception;

    /*获取部门管理员*/
    User getCurrentUserDeptAdmin() throws Exception;

    /*获取当前用户所在租户 中心管理员*/
    User getCurrentUserCenterAdmin() throws Exception;

}

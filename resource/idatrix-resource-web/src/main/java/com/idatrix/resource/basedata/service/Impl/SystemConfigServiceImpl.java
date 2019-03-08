package com.idatrix.resource.basedata.service.Impl;

import com.idatrix.resource.basedata.dao.SystemConfigDAO;
import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.basedata.vo.SystemConfigVO;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.sso.client.UserHolder;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Robin Wing on 2018-6-14.
 */
@Transactional
@Service("systemConfigService")
public class SystemConfigServiceImpl implements ISystemConfigService {


    @Autowired
    private SystemConfigDAO systemConfigDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    @Override
    public Long save(String user, SystemConfigVO systemConfigVO) throws Exception{

        if(StringUtils.equals(systemConfigVO.getOriginFileRoot(), systemConfigVO.getFileRoot())){
            throw new Exception("上报文件目录和文件类型资源目录不能配置一样，请修改配置。");
        }
        Long configId = systemConfigVO.getId();
        SystemConfigPO scPO = tranforSystemConfigVoToPo(systemConfigVO);
        scPO.setModifier(user);
        scPO.setModifyTime(new Date());
        if(configId==null || configId==0){
            scPO.setCreator(user);
            scPO.setCreateTime(new Date());
            systemConfigDAO.insert(scPO);
            configId = scPO.getId();
        }else{
            SystemConfigPO scOldPO = systemConfigDAO.getById(configId);
            scPO.setCreator(scOldPO.getCreator());
            scPO.setCreateTime(scOldPO.getCreateTime());
            scPO.setId(systemConfigVO.getId());
            systemConfigDAO.updateById(scPO);
        }
        return configId;
    }

    @Override
    public SystemConfigVO getSystemConfig(String user) {

//        SystemConfigPO scPO = systemConfigDAO.getByCreatorName(user)
        SystemConfigPO scPO = getByUserName(user);
        //SystemConfigPO scPO = systemConfigDAO.getLastestSysConfig();
        return tranforSystemConfigPoToVo(scPO);
    }

    private SystemConfigPO getByUserName(String user){

        SystemConfigPO scPO = systemConfigDAO.getByCreatorName(user);
        if(scPO==null){

            //没有根据用户存储过，需要查找租户来查询信息
            User userInfo = userService.findByUserName(user);
            Long rentId = userInfo.getRenterId();
            scPO = systemConfigDAO.getAdaptByRentId(rentId.toString()+"+");
            if(scPO==null){
                User rentUserInfo = userService.findRenterByRenterId(rentId);
                if(rentUserInfo!=null){
                    scPO = systemConfigDAO.getByCreatorName(rentUserInfo.getUsername());
                }
            }
        }
        return scPO;
    }

    @Override
    public SystemConfigPO getSystemConfigByUser(String user) {
        return getByUserName(user);
    }

    @Override
    public SystemConfigPO getSystemConfig() {

        String userName = userUtils.getCurrentUserName();
        return getByUserName(userName);
   }



    @Override
    public String getCenterUserName(Long rentId) throws Exception {

        String centerAdminName = null;

        SystemConfigPO scPO = systemConfigDAO.getAdaptByRentId(rentId.toString()+"+");
        Long centerAdminRole = scPO.getCenterAdminRole();
        //Long centerAdminRole = getCenterAdminRoleId();

        if (centerAdminRole != null && centerAdminRole > 0) {
            List<User> userList = userService.findUserByRoleAndRenter(centerAdminRole.intValue(), rentId);
            if (userList != null && userList.size() > 0) {
                User approveUser = userList.get(0);
                centerAdminName = approveUser.getUsername();
            } else {
                throw new RuntimeException("还未设置数据中心管理员，请先配置数据中心管理员再提交注册");
            }
        }
        return centerAdminName;
    }

    private SystemConfigPO tranforSystemConfigVoToPo(SystemConfigVO scVO){
        SystemConfigPO scPO = new SystemConfigPO();
        scPO.setFileRoot(scVO.getFileRoot());
        scPO.setOriginFileRoot(scVO.getOriginFileRoot());
        scPO.setDbUploadSize(scVO.getDbUploadSize());
        scPO.setFileUploadSize(scVO.getFileUploadSize());
        scPO.setImportInterval(scVO.getImportInterval());
        scPO.setDeptStaffRole(scVO.getDeptStaffRole());
        scPO.setDeptAdminRole(scVO.getDeptAdminRole());
        scPO.setCenterAdminRole(scVO.getCenterAdminRole());
        scPO.setSubApproverRole(scVO.getSubApproverRole());
        scPO.setOriginFileRootIds(scVO.getOriginFileRootIds());
        scPO.setFileRootIds(scVO.getFileRootIds());
        return scPO;
    }

    private SystemConfigVO tranforSystemConfigPoToVo(SystemConfigPO scPO){
        if(scPO==null){
            return null;
        }
        SystemConfigVO scVO = new SystemConfigVO();
        scVO.setId(scPO.getId());
        scVO.setFileRoot(scPO.getFileRoot());
        scVO.setOriginFileRoot(scPO.getOriginFileRoot());
        scVO.setDbUploadSize(scPO.getDbUploadSize());
        scVO.setFileUploadSize(scPO.getFileUploadSize());
        scVO.setImportInterval(scPO.getImportInterval());
        scVO.setDeptStaffRole(scPO.getDeptStaffRole());
        scVO.setDeptAdminRole(scPO.getDeptAdminRole());
        scVO.setCenterAdminRole(scPO.getCenterAdminRole());
        scVO.setSubApproverRole(scPO.getSubApproverRole());
        scVO.setUpdateTime(DateTools.formatDate(scPO.getModifyTime()));
        scVO.setOriginFileRootIds(scPO.getOriginFileRootIds());
        scVO.setFileRootIds(scPO.getFileRootIds());
        return scVO;
    }

    @Override
    public User getSubscribeApprover(String user, Long deptId) throws Exception{

        User approveUser = new User();

        //获取系统配置的部门管理员 设置成流程下一处理人:存在跨租户问题，所以需要根据创建用户所在租户查找*/
        SystemConfigPO sysConfigPO = getByUserName(user); //systemConfigDAO.getLastestSysConfig();  //解决多租户问题

        if(sysConfigPO!=null){
            Long deptAdminRol = sysConfigPO.getSubApproverRole();
            if(deptAdminRol!=null && deptAdminRol>0) {
                //int deptId = (Integer) userSSOInfo.getProperty("deptId");
                //int deptId = creatorInfo.getDeptId().intValue();
                int deptIdValue =  deptId.intValue();
                List<User> userList = userService.findUsersByDeptAndRole(deptIdValue, deptAdminRol.intValue());
                if (userList != null && userList.size() > 0) {
                    approveUser = userList.get(0);
//                    approve = approveUser.getUsername();
//                    approveName = approveUser.getRealName();
                } else {
                    throw new RuntimeException("还未设置订阅管理员，请先配置订阅管理员再提交订阅");
                }
            }
        }
        return approveUser;
    }

    @Override
    public User getDeptStaff(int deptId) throws Exception {

        User approveUser = new User();
        SystemConfigPO sysConfigPO = getSystemConfig(); // systemConfigDAO.getLastestSysConfig();
        if (sysConfigPO != null) {
            Long deptStaffRole = sysConfigPO.getDeptStaffRole();
            if (deptStaffRole != null && deptStaffRole > 0) {
                List<User> userList = new ArrayList<User>();
                userList = userService.findUsersByDeptAndRole(deptId, deptStaffRole.intValue());
                if (userList != null && userList.size() > 0) {
                    approveUser = userList.get(0);
                    //deptStaffName = approveUser.getUsername();
                } else {
                    throw new RuntimeException("还未设置部门填报人员信息，请先配置填报人员信息再提交注册");
                }
            }
        } else {
            throw new Exception("系统参数没有配置，请先配置再上传");
        }
        return approveUser;
    }

    @Override
    public User getCurrentUserDeptAdmin() throws Exception {

        User approveUser = new User();
        SystemConfigPO sysConfigPO = getSystemConfig();
        if(sysConfigPO!=null){
            Long deptAdminRol = sysConfigPO.getDeptAdminRole();
            if(deptAdminRol!=null && deptAdminRol>0) {
//                int deptId = (Integer) userSSOInfo.getProperty("deptId");
                int deptId = userUtils.getCurrentUserDeptId();
                List<User> userList = new ArrayList<User>();
                userList = userService.findUsersByDeptAndRole(deptId, deptAdminRol.intValue());
                if (userList != null && userList.size() > 0) {
                    approveUser = userList.get(0);
//                    approve = approveUser.getUsername();
//                    approveName = approveUser.getRealName();
                } else {
                    throw new RuntimeException("还未设置部门管理员，请先配置部门管理员再提交注册");
                }
            }
        }else{
            throw new Exception("系统参数没有配置，请先配置再上传");
        }
        return approveUser;
    }

    @Override
    public User getCurrentUserCenterAdmin() throws Exception {

        User approveUser = new User();
        Long rentId = userUtils.getCurrentUserRentId();
        SystemConfigPO sysConfigPO = getSystemConfig(); //systemConfigDAO.getLastestSysConfig();
        if(sysConfigPO!=null){
            Long deptAdminRol = sysConfigPO.getCenterAdminRole();
            if(deptAdminRol!=null && deptAdminRol>0) {
//                int deptId = (Integer) userSSOInfo.getProperty("deptId");
                int deptId = userUtils.getCurrentUserDeptId();
                List<User> userList = new ArrayList<User>();
                userList = userService.findUserByRoleAndRenter(deptAdminRol.intValue(), rentId);
//                        userList = userService.findUsersByDeptAndRole(deptId, deptAdminRol.intValue());
                if (userList != null && userList.size() > 0) {
                    approveUser = userList.get(0);
//                    nextApprove = approveUser.getUsername();
//                    nextApproveName = approveUser.getRealName();
                } else {
                    throw new RuntimeException("还未设置数据中心目录管理员，请先配置再提交发布审核");
                }
            }
        }else{
            throw new Exception("系统参数没有配置，请先配置再上传");
        }
        return approveUser;
    }
}

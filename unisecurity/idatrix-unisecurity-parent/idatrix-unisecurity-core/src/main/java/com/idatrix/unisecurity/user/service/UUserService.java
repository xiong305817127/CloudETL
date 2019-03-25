package com.idatrix.unisecurity.user.service;

import com.idatrix.unisecurity.common.domain.ImportMsg;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.domain.UserData;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.permission.bo.URoleBo;
import com.idatrix.unisecurity.permission.bo.UserRoleAllocationBo;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

public interface UUserService {

    // 根据用户账号查询用户信息
    UUser getUserByUsername(String username);

    // 查询所有用户信息
    List<UUser> findAll();

    // 根据id删除用户信息（逻辑删除）
    int deleteByPrimaryKey(Long id);

    UUser insert(UUser record);

    UUser insertSelective(UUser record);

    UUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UUser record);

    int updateByPrimaryKey(UUser record);

    UUser login(String username, String pswd);

    Pagination<UUser> findByPage(Map<String, Object> resultMap, Integer pageNo,
                                 Integer pageSize);

    Integer deleteUserById(String ids) throws Exception;

    Pagination<UserRoleAllocationBo> findUserAndRole(ModelMap modelMap,
                                                     Integer pageNo, Integer pageSize);

    List<URoleBo> selectRoleByUserId(Long id);

    int addRole2User(Long userId, String ids);

    int addUsersToRole(Long roleId, String uids);

    int deleteRoleByUserIds(String userIds);

    int findUserByUsername(String username);

    UUser getUser(String username);

    List<UserData> export(String ids);

    // 组织页面操作，组织主动去关联多个用户
    Integer addUserToOrg(Long orgId, String uIds) throws Exception;

	int findUserByEmail(String email);

	List<UUser> findUsersByOrganizationId(Long deptId);

	List<UUser> findUsersByRoleId(Long roleId);

	int findUserByPhone(String phone);

	List<UUser> getUsersByuserNames(List<String> userNames); 

	Integer addOrgToUser(Long orgId, String uId);

	List<UUser> findUsersByOrganizationIds(List<Long> deptIds);

	void insertErrLog(String batchId, String fileName, String valueOf, String string);

	List<ImportMsg> exportImportMsg(String batchId);

	int isRenterByUserId(Long userId, String cid);

	void insertErrLog(ImportMsg importMsg);

    int userIsRenter(Long userId);

    void updateVisitTimesByUsername(String username);

    void updateLoginUserInfo(UUser user);

    // 根据租户ids禁用用户
    int updateUserStatusByRenterIds(String[] renterIds, Long status);
}

package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.permission.bo.URoleBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UUserMapper {

    List<UUser> selectAll();

    int deleteByPrimaryKey(Long id);

    int insert(UUser record);

    int insertSelective(UUser record);

    UUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UUser record);

    int updateByPrimaryKey(UUser record);

    UUser login(Map<String, Object> map);

    // 根据用户name查询用户信息
    UUser getUserByUsername(@Param("username") String username);

    // 根据用户name判断当前用户是否存在
    int findUserByUsername(String username);

    List<URoleBo> selectRoleByUserId(Long id);

    void updateStatusByUsername(String username);

    UUser getUser(String username);

    String getLTUser(String lt);

    void updateLoginToken(Map paramMap);

    void recordUserLogin(UUser uUser);

    void updateByUsername(UUser uUser);

    int checkQuestAndAnswer(Map map);

    void updateVisitTimesByUsername(String username);
    
    int findUserByEmail(String email);

    List<UUser> findUsersByOrganizationId(Long deptId);

	List<UUser> findUsersByRoleId(Long roleId);

	void clearOrganizationId(Map params);

	int findUserByPhone(String phone);

	// 根据组织ID查询用户ID
	List<Long> findUserIdsByOrganizationId(Long orgId);

	List<UUser> findUsersByOrganizationIds(List<Long> deptIds);

	int isRenterByUserId(@Param("userId")Long userId, @Param("cid")String cid);

    Integer userIsRenter(@Param("userId") Long userId);

    // 根据组织id来置空某些用户组织id
    int userClearOrganizationId(@Param("deptId") Long deptId);

    // 组织关联用户
    int organizationToUser(@Param("deptId") Long deptId, @Param("userIdArray") String[] userIdArray);

    // 根据租户id禁用用户
    int updateUserStatusByRenterIds(@Param("array") String[] renterIds, @Param("status") Long status);
}
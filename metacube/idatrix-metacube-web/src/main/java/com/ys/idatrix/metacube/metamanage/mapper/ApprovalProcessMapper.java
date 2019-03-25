package com.ys.idatrix.metacube.metamanage.mapper;


import com.ys.idatrix.metacube.metamanage.domain.ApprovalProcess;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ResourceAuthVO;
import com.ys.idatrix.metacube.metamanage.vo.response.AuthMetadataVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ApprovalProcessMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ApprovalProcess record);

    int insertSelective(ApprovalProcess record);

    ApprovalProcess selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ApprovalProcess record);

    int updateByPrimaryKey(ApprovalProcess record);

    // 不定参数查询
    int find(ApprovalProcess record);

    // 搜索资源申请列表
    List<ApprovalProcessVO> searchResourceApplyList(ApprovalProcessSearchVo search);

    // 搜索权限审批列表
    List<ApprovalProcessVO> searchAuthorityApproveList(ApprovalProcessSearchVo search);

    // 解析权限
    List<ResourceAuthVO> getPermissionInfo(@Param("approvalProcessId") Long approvalProcessId);

    // 批量通过审批资源
    int batchToPass(@Param("list") List<Long> idList, @Param("approver") String approver, @Param("now") Date now);

    // 批量不通过审批资源
    int batchToNoPass(@Param("list") List<Long> idList, @Param("approver") String approver,
                      @Param("opinion") String opinion, @Param("now") Date now);

    // 批量回收授权
    int batchToRecycled(@Param("list") List<Long> idList, @Param("approver") String approver, @Param("now") Date now);


    /**
     * 不定参数获取所属组织指定权限和数据源类型的元数据
     *
     * @param resourceId
     * @param status
     * @param deptCode
     * @param authName
     * @param authValues
     * @param resourceTypes
     * @return
     */
    List<AuthMetadataVO> getAuthMetadata(@Param("resourceId")Long resourceId, @Param("status") Integer status,
                                         @Param("deptCode") String deptCode, @Param("authName") String authName,
                                         @Param("authValues") List<Integer> authValues,
                                         @Param("resourceTypes") List<Integer> resourceTypes);


    // 获取用户授权通过的资源
    List<ApprovalProcessVO> findAuthResource(@Param("deptCode") String deptCode,
                                             @Param("databaseTypes") List<Integer> databaseTypes,
                                             @Param("resourceTypes") List<Integer> resourceTypes,
                                             @Param("authValues") List<Integer> authValues);

    /**
     * 查询固定资源给某个部门授权通过的列表
     * @param resourceId
     * @param deptCode
     * @param status 默认等于2
     * @return
     */
    List<ApprovalProcess> getAuthByResourceIdAndValue(@Param("resourceId")Long resourceId,
                                                      @Param("deptCode") String deptCode,
                                                      @Param("status") Integer status);

}
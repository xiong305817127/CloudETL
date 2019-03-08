package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.ApprovalProcess;
import com.ys.idatrix.metacube.metamanage.domain.ResourceAuth;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessVO;

import java.util.List;

/**
 * @ClassName AuthorityService
 * @Description
 * @Author ouyang
 * @Date
 */
public interface AuthorityService {

    // ==========
    // ==========
    // 用户授权申请处理
    // ==========
    // ==========

    // 查询当前用户所属组织的授权申请列表
    PageResultBean<ApprovalProcessVO> searchResourceApplyList(ApprovalProcessSearchVo search);

    // 根据元数据ID获取可授权列表
    List<ResourceAuth> getAuthorityListByMetadataId(Long metadataId, Integer resourceType);

    // 判断当前用户是否拥有某个元数据的所有权限
    Boolean userIfAuthority(Long metadataId, Integer resourceType);

    // 用户对某条元数据申请权限
    void userApplyAuthority(ApprovalProcess approvalProcess);


    // ==========
    // ==========
    // 授权审批处理
    // ==========
    // ==========

    // 搜索
    PageResultBean<ApprovalProcessVO> searchAuthorityApproveList(ApprovalProcessSearchVo search);

    // 批量通过审批资源
    int batchToPass(List<Long> idList);

    // 批量不通过审批资源
    int batchToNoPass(String ids, String opinion);

    // 批量回收授权
    int batchToRecycled(List<Long> idList);

    // ==== 获取授权成功的数据

    /**
     * @param username 用户名
     * @param module 操作模块
     * @param actionType 操作类型
     * @param databaseTypes 数据库类型，可以传递多个
     * @param resourceTypes 资源类型，如果不确定资源类型可以传null
     * @return
     */
    List<ApprovalProcessVO> getAuthorizedResource(String username, ModuleTypeEnum module, ActionTypeEnum actionType,
                                                  List<Integer> databaseTypes, List<Integer>  resourceTypes);

}
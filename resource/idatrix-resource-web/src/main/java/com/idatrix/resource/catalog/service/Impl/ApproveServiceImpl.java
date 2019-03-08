package com.idatrix.resource.catalog.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.basedata.dao.SystemConfigDAO;
import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.catalog.dao.*;
import com.idatrix.resource.catalog.po.*;
import com.idatrix.resource.catalog.service.IApproveService;
import com.idatrix.resource.catalog.vo.ResourceApproveVO;
import com.idatrix.resource.catalog.vo.ResourceOverviewVO;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.ResourceTools;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.sso.client.UserHolder;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import com.ys.idatrix.metacube.api.bean.base.BaseResult;
import com.ys.idatrix.metacube.api.bean.dataswap.MetadataField;
import com.ys.idatrix.metacube.api.bean.dataswap.QueryMetadataFieldsResult;
import com.ys.idatrix.metacube.api.service.dataswap.DataSwapService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.idatrix.resource.common.utils.ResourceTools.FormatType.DB;
import static com.idatrix.resource.common.utils.ResourceTools.ResourceAction.*;
import static com.idatrix.resource.common.utils.ResourceTools.ResourceStatus;
import static com.idatrix.resource.common.utils.ResourceTools.ResourceStatus.*;
import static com.ys.idatrix.metacube.api.bean.dataswap.AuthorizedFlowType.SUBSCRIBED;

/**
 * Created by Robin Wing on 2018-6-9.
 */

@Transactional
@Service("approveService")
public class ApproveServiceImpl implements IApproveService{

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SystemConfigDAO systemConfigDAO;

    @Autowired
    private ResourceApproveDAO resourceApproveDAO;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private ResourceColumnDAO resourceColumnDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceStatisticsDAO resourceStatisticsDAO;

    @Autowired
    private ResourceHistoryDAO resourceHistoryDAO;

    @Autowired
    private DataSwapService metacubeCatalogService;

    @Autowired
    private ISystemConfigService systemConfigService;

    private static final String ACTION_AGREE = "agree";
    private static final String ACTION_REJECT = "reject";

    private static final String ACTION_AGREE_ZH = "同意。";
    private static final String ACTION_REJECT_ZH = "拒绝。";


    @Override
    public Long submitApprove(String user, Long id) throws Exception{

        //首先判断该资源当前状态只有在操作后者 发布后 已经下架的资源才能提交注册审批
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(id);
        if(!StringUtils.equals(rcPO.getStatus(), DRAFT.getStatusCode())
                && !StringUtils.equals(rcPO.getStatus(), WAIT_UPDATE.getStatusCode()))
        {
            throw new RuntimeException("该资源已经审核");
        }
        ResourceApprovePO raP = resourceApproveDAO.getWaitApproveByResourceId(id);
        if(raP!=null){
            throw new RuntimeException("该资源已提交审核");
        }
        //校验数据库类型是否配置了 column以及 column里面是否有主键
        int formatType = rcPO.getFormatType();
        if(formatType==DB.getTypeValue()){
            List<ResourceColumnPO> rcList =resourceColumnDAO.getColumnByResourceId(id);
            if(rcList==null || rcList.size()==0){
                throw new RuntimeException(rcPO.getName() + " 为数据库类型资源没有配置资源细项，请重新编辑");
            }else{
                boolean configUniqueFlag = false;
                for(ResourceColumnPO rc : rcList){
                    if(rc.getUniqueFlag()==null || rc.getRequiredFlag()==null){
                        throw new RuntimeException(rcPO.getName() + " 为数据库类型资源，资源细项中唯一标识和订阅必选项为空，请编辑保存");
                    }
                    configUniqueFlag = rc.getUniqueFlag();
                    if(configUniqueFlag){
                        break;
                    }
                }
                if(!configUniqueFlag){
                    throw new RuntimeException(rcPO.getName() + " 为数据库类型资源，资源细项没有配置唯一标识，请重新编辑");
                }
            }

            //判断数据库类型绑定的数据表是否包含
            //数据批次ds_batch,数据同步时间ds_sync_time，数据同步操作ds_sync_flag
            //获取元数据字段
            Long bindTableId = rcPO.getBindTableId();
            if(bindTableId==null || bindTableId.equals(0L)){
                throw new RuntimeException(rcPO.getName() + " 为数据库类型资源，物理表名中没有配置成功，请重新编辑");

            }

            List<MetadataField> metadataOriginFields = new ArrayList<MetadataField>();
            QueryMetadataFieldsResult metaResult = metacubeCatalogService.getMetadataFieldsByMetaId(bindTableId.intValue());
            if(metaResult.isSuccess()){
                metadataOriginFields = metaResult.getMetadataField();
            }else{
                throw new Exception(metaResult.getMessage());
            }
            List<String> metaName = new ArrayList<String>();
            for(MetadataField field:metadataOriginFields){
                metaName.add(field.getColName().toLowerCase());
            }
            List<String> requireColumns = Arrays.asList("ds_batch","ds_sync_time","ds_sync_flag");
            if(!metaName.containsAll(requireColumns)){
                throw new RuntimeException("请重新配置绑定物理表名里面字段，确保包含ds_batch，ds_sync_time，ds_sync_flag字段");
            }
        }
        rcPO.setStatus(WAIT_REG_APPROVE.getStatusCode());
        resourceConfigDAO.updateById(rcPO);

        //修改资源状态
        SSOUser userSSOInfo = UserHolder.getUser();
        String approve =(String) userSSOInfo.getProperty("username"); //审批人账号，
        String approveName = (String) userSSOInfo.getProperty("realName");;    //审批人姓名

        //获取系统配置的部门管理员 设置成流程下一处理人

//        SystemConfigPO sysConfigPO = systemConfigDAO.getLastestSysConfig(); //解决多租户问题
//        SystemConfigPO sysConfigPO = systemConfigService.getSystemConfig();
//        if(sysConfigPO!=null){
//            Long deptAdminRol = sysConfigPO.getDeptAdminRole();
//            if(deptAdminRol!=null && deptAdminRol>0) {
//                int deptId = (Integer) userSSOInfo.getProperty("deptId");
//                List<User> userList = new ArrayList<User>();
//                userList = userService.findUsersByDeptAndRole(deptId, deptAdminRol.intValue());
//                if (userList != null && userList.size() > 0) {
//                    User approveUser = userList.get(0);
//                    approve = approveUser.getUsername();
//                    approveName = approveUser.getRealName();
//                } else {
//                    throw new RuntimeException("还未设置部门管理员，请先配置部门管理员再提交注册");
//                }
//            }
//        }else{
//            throw new Exception("系统参数没有配置，请先配置再上传");
//        }

        User approveUser = systemConfigService.getCurrentUserDeptAdmin();
        if(approveUser!=null){
            approve = approveUser.getUsername();
            approveName = approveUser.getRealName();
        }

        ResourceApprovePO raPO = new ResourceApprovePO();
        raPO.setResourceId(id);
        raPO.setApprover(approve);
        raPO.setApproverName(approveName);
        raPO.setCurrentStatus(WAIT_REG_APPROVE.getStatusCode());
        raPO.setNextStatus(WAIT_PUB_APPROVE.getStatusCode());
        raPO.setActiveFlag(true);
        raPO.setCreator(user);
        raPO.setCreateTime(new Date());
        raPO.setModifier(user);
        raPO.setModifyTime(new Date());
        resourceApproveDAO.insert(raPO);
        return 0L;
    }


    @Override
    public void pubResource(String user, Long[] ids) throws Exception {
        if(ids==null || ids.length==0){
            return ;
        }
        for(Long id:ids){
            ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(id);
            if(!StringUtils.equals(rcPO.getStatus(), RECALL.getStatusCode())){
                throw new RuntimeException("存在不能直接发布的资源");
            }
        }
        for(Long id:ids){
            resourceMaintain(user, id, MAINTAIN_PUB.getActionCode());
        }
    }

    @Override
    public void recallResource(String user, Long[] ids) throws Exception {
        if(ids==null || ids.length==0){
            return ;
        }
        for(Long id:ids){
            ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(id);
            if(!StringUtils.equals(rcPO.getStatus(), PUB_SUCCESS.getStatusCode())){
                throw new RuntimeException("存在不能直接下架的资源");
            }
        }
        for(Long id:ids){
            resourceMaintain(user, id, MAINTAIN_RECALL.getActionCode());
        }
    }

    @Override
    public void backResource(String user, Long[] ids) throws Exception {
        if(ids==null || ids.length==0){
            return ;
        }
        for(Long id:ids){
            ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(id);
            if(!StringUtils.equals(rcPO.getStatus(), RECALL.getStatusCode())){
                throw new RuntimeException("存在不能直接退回修改的资源");
            }
        }
        for(Long id:ids){
            resourceMaintain(user, id, MAINTAIN_BACK.getActionCode());
        }
    }

    private void saveMaintainAction(String user, Long resourceId, String action){
        ResourceHistoryPO rh = new ResourceHistoryPO();
        rh.setCreateTime(new Date());
        rh.setCreator(user);
        rh.setResourceId(resourceId);
        rh.setAction(action);
        rh.setActionName(getAction(action));
        resourceHistoryDAO.insert(rh);

    }

    private Boolean ableToMaintain(String user, Long id) throws Exception{

        Map<String, String> condition = new HashMap<String, String>();

        User userCurrent = userService.findByUserName(user);
        String centerUser = systemConfigService.getCenterUserName(userCurrent.getRenterId());
        if(StringUtils.equals(user, centerUser)){
            //中心管理员可以上下架全部用户
        }else{
            condition.put("approver", user);
        }

        condition.put("currentStatus", WAIT_PUB_APPROVE.getStatusCode());
        condition.put("nextStatus", PUB_SUCCESS.getStatusCode());
        //condition.put("approver", user);
        condition.put("resourceId", Long.toString(id));
        ResourceApprovePO raPO = resourceApproveDAO.getMaintainResource(condition);

        if(raPO==null) {
            return false;
        }
        return true;
    }

    /*资源维护*/
    private Long resourceMaintain(String user, Long resourceId, String method) throws Exception{

        if(!ableToMaintain(user, resourceId)){
            throw new RuntimeException("该资源不能进行维护");
        }
        String resourceStatus = null;
        if(StringUtils.equals(method, MAINTAIN_PUB.getActionCode())){
            resourceStatus = PUB_SUCCESS.getStatusCode();
        }else if(StringUtils.equals(method, MAINTAIN_RECALL.getActionCode())){
            resourceStatus = RECALL.getStatusCode();
        }else if(StringUtils.equals(method, MAINTAIN_BACK.getActionCode())){
            resourceStatus = WAIT_UPDATE.getStatusCode();
        }

        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        rcPO.setUpdater(user);
        rcPO.setUpdateTime(new Date());
        //TODO：资源维护时间怎么分析定义
        rcPO.setStatus(resourceStatus);
        resourceConfigDAO.updateById(rcPO);

        saveMaintainAction(user, resourceId, method);
        return rcPO.getId();
    }

    /*获取资源审批历史*/
    @Override
    public List<ResourceApproveVO> getHistory(Long id) {

        List<ResourceApprovePO> raPOList = resourceApproveDAO.getApproveHistoryByResourceId(id);
        return transferResourceApprovePOToVO(raPOList);
    }

    @Override
    public ResultPager<ResourceOverviewVO> queryWaitRegApprove(Map<String, String> con, Integer pageNum, Integer pageSize) {
        con.put("activeFlag", "1"); //表示灭有审批的
        return queryApproveByCondition(con, WAIT_REG_APPROVE.getStatusCode(), pageNum, pageSize);
    }

    @Override
    public ResultPager<ResourceOverviewVO> queryWaitPubApprove(Map<String, String> con, Integer pageNum, Integer pageSize) {
        con.put("activeFlag", "1");  //表示没有审批的
        return queryApproveByCondition(con, WAIT_PUB_APPROVE.getStatusCode(), pageNum, pageSize);
    }

    @Override
    public ResultPager<ResourceOverviewVO> queryProcessedPubApprove(Map<String, String> con, Integer pageNum, Integer pageSize) {
        con.put("activeFlag", "0");  //表示已经审批的
        return queryApproveByCondition(con, WAIT_PUB_APPROVE.getStatusCode(), pageNum, pageSize);
    }

    @Override
    public ResultPager<ResourceOverviewVO> queryProcessedRegApprove(Map<String, String> con, Integer pageNum, Integer pageSize) {
        con.put("activeFlag", "0"); //表示已经审批的
        return queryApproveByCondition(con, WAIT_REG_APPROVE.getStatusCode(), pageNum, pageSize);
    }


    private ResultPager<ResourceOverviewVO> queryApproveByCondition(Map<String, String> con, String status,
                                                                    Integer pageNum, Integer pageSize){

        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);

        ResourceStatus approveStatus = ResourceStatus.getResourceStatus(status);
        if(StringUtils.equals(approveStatus.getStatusCode(), NOT_SURE.getStatusCode())){
            return null;
        }
        con.put("currentStatus", status);
        //TODO:对于正常审批的资源怎么处理，对于整个资源只有一条记录中 is_active 为1,是否需要过滤activeFlag条件
        List<ResourceApprovePO> raPOList = resourceApproveDAO.getApproveByCondition(con);
        if(raPOList==null || raPOList.size()==0){
            return null;
        }
        List<ResourceOverviewVO> roVOList = transferResourceApprovePOToROVO(raPOList);
        PageInfo<ResourceApprovePO> pi = new PageInfo<ResourceApprovePO>(raPOList);
        Long totalNum = pi.getTotal();
        ResultPager<ResourceOverviewVO> rp = new ResultPager<ResourceOverviewVO>(pi.getPageNum(), totalNum, roVOList);
        return rp;
    }

    @Override
    public ResultPager<ResourceOverviewVO> queryMaintainResource(Map<String, String> con,
                                                                 Integer pageNum, Integer pageSize){
        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);

        con.put("activeFlag", "0");  //在审核表格中查询 已经处理
        con.put("currentStatus", WAIT_PUB_APPROVE.getStatusCode());  //被当前用户发布审核过的资源
//        raPOList = resourceApproveDAO.getApproveByCondition(con);

        String status = con.get("status");
        if(StringUtils.equals(status, "pub")){
            con.put("status", "\"pub_success\"");
        }else if(StringUtils.equals(status, "recall")){
            con.put("status", "\"recall\"");
        }else{
            con.put("status", "\"pub_success\",\"recall\"");
        }

        List<ResourceApprovePO> raPOList = resourceApproveDAO.getMaintainResourceByCondition(con);
        List<ResourceOverviewVO> roVOList = transferResourceApprovePOToROVO(PUB_SUCCESS.getStatusCode(), raPOList);

        //用PageInfo对结果进行包装
        PageInfo<ResourceApprovePO> pi = new PageInfo<ResourceApprovePO>(raPOList);
        Long totalNum = pi.getTotal();
        ResultPager<ResourceOverviewVO> rp = new ResultPager<ResourceOverviewVO>(pi.getPageNum(), totalNum, roVOList);
        return rp;
    }

    /*用户处理审批流程*/
    @Override
    public void processApprove(String user, Long resourceId, String action, String suggestion) throws Exception{

        if(resourceId==null || StringUtils.isEmpty(action)){
            throw new RuntimeException("传入参数存在异常");
        }
        ResourceApprovePO raPO = resourceApproveDAO.getWaitApproveByResourceId(resourceId);
        if(raPO==null){
            throw new RuntimeException("审批记录中不存在该资源");
        }
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        if(rcPO==null){
            throw new RuntimeException("资源记录中不存在该资源");
        }
        if(StringUtils.isEmpty(suggestion)){
            suggestion = ACTION_AGREE_ZH;
            if(StringUtils.equals(action, ACTION_REJECT)) {  //同意
                suggestion = ACTION_REJECT_ZH;
            }
        }

        //获取流程当前状态
        String status = raPO.getCurrentStatus();
        String nextStatus = null;

        //待注册审批
        if(StringUtils.equals(status, WAIT_REG_APPROVE.getStatusCode())){
            if(StringUtils.equals(action, ACTION_AGREE)){  //同意
                nextStatus = REG_SUCCESS.getStatusCode();

                //获取系统配置 approve userSSOInfo approve_name 修改资源状态
                SSOUser userSSOInfo = UserHolder.getUser();
                String nextApprove = (String) userSSOInfo.getProperty("username");  //审批人账号，
                String nextApproveName = (String) userSSOInfo.getProperty("realName");    //审批人姓名

//                Object obj = userSSOInfo.getProperty("renterId");
//                Long rentId = Long.valueOf((String) obj);
//
//                SystemConfigPO sysConfigPO = systemConfigDAO.getLastestSysConfig();
//                if(sysConfigPO!=null){
//                    Long deptAdminRol = sysConfigPO.getCenterAdminRole();
//                    if(deptAdminRol!=null && deptAdminRol>0) {
//                        int deptId = (Integer) userSSOInfo.getProperty("deptId");
//                        List<User> userList = new ArrayList<User>();
//                        userList = userService.findUserByRoleAndRenter(deptAdminRol.intValue(), rentId);
////                        userList = userService.findUsersByDeptAndRole(deptId, deptAdminRol.intValue());
//                        if (userList != null && userList.size() > 0) {
//                            User approveUser = userList.get(0);
//                            nextApprove = approveUser.getUsername();
//                            nextApproveName = approveUser.getRealName();
//                        } else {
//                            throw new RuntimeException("还未设置数据中心目录管理员，请先配置再提交发布审核");
//                        }
//                    }
//                }else{
//                    throw new Exception("系统参数没有配置，请先配置再上传");
//                }

                User approveUser = systemConfigService.getCurrentUserCenterAdmin();
                if(approveUser!=null) {
                    nextApprove = approveUser.getUsername();
                    nextApproveName = approveUser.getRealName();
                }

                ResourceApprovePO raNextPO = new ResourceApprovePO();
                raNextPO.setResourceId(resourceId);
                raNextPO.setApprover(nextApprove);
                raNextPO.setApproverName(nextApproveName);
                raNextPO.setCurrentStatus(WAIT_PUB_APPROVE.getStatusCode());
                raNextPO.setCreator(user);
                raNextPO.setCreateTime(new Date());
                raNextPO.setModifier(user);
                raNextPO.setModifyTime(new Date());
                resourceApproveDAO.insert(raNextPO);

            }else if(StringUtils.equals(action, ACTION_REJECT)){ //拒绝
                nextStatus = WAIT_UPDATE.getStatusCode();
            }
        }else if(StringUtils.equals(status, WAIT_PUB_APPROVE.getStatusCode())){
            if(StringUtils.equals(action, ACTION_AGREE)){  //同意
                nextStatus = PUB_SUCCESS.getStatusCode();
                rcPO.setPubDate(new Date()); //更新发布时间

                //TODO:同意发布要在 rc_resource_statistics,统计时候怎么维护？
                ResourceStatisticsPO rsPO = resourceStatisticsDAO.getLatestByResourceId(resourceId);
                if(rsPO==null){
                    ResourceStatisticsPO rsSavePO = new ResourceStatisticsPO();
                    rsSavePO.setCreator(user);
                    rsSavePO.setCreateTime(new Date());
                    rsSavePO.setModifier(user);
                    rsSavePO.setModifyTime(new Date());
                    rsSavePO.setId(resourceId);
                    //rsSavePO.setDataUpdateTime(new Date());
                    resourceStatisticsDAO.insert(rsSavePO);
                }

                //发布审批同意时候，对填报用户开放权限，创建完表格时候，对中心库进行授权
                if(rcPO.getFormatType()==DB.getTypeValue()) {
                    String resourceCreateUser = rcPO.getCreator();
                    User resourceCreateUserInfo = userService.findByUserName(resourceCreateUser);
                    Long bindTableId = rcPO.getBindTableId();
                    BaseResult baseResult = metacubeCatalogService.authorizedTableForUser(resourceCreateUser, bindTableId.intValue(),
                            resourceCreateUserInfo.getDeptId().intValue(), SUBSCRIBED);
                    if (!baseResult.isSuccess()) {
                        throw new Exception(baseResult.getMessage());
                    }
                }
            }else if(StringUtils.equals(action, ACTION_REJECT)){ //拒绝
                nextStatus = WAIT_UPDATE.getStatusCode();
            }
        }
        //为了区分资源是注册申请时候被打回还是 发布申请时候被打回，所有不改变cuurentStatus,
        //变更状态时候 在NextStatus里面添加一条记录。
        raPO.setApproveAction(action);
        raPO.setSuggestion(suggestion);
        raPO.setApproveTime(new Date());
        raPO.setActiveFlag(false);
        raPO.setNextStatus(nextStatus);
        raPO.setModifier(user);
        raPO.setModifyTime(new Date());
        resourceApproveDAO.updateById(raPO);

        status = nextStatus;
        if(StringUtils.equals(nextStatus, REG_SUCCESS.getStatusCode())){
            status = WAIT_PUB_APPROVE.getStatusCode();
        }
        rcPO.setStatus(status);
        rcPO.setUpdateTime(new Date());
        resourceConfigDAO.updateById(rcPO);
    }

    /*批量处理 注册/发布审批*/
    @Override
    public void batchProcessApprove(String user, Long[] resourceIds) throws Exception{

        //TODO: 批处理失败时候回滚操作
        if(resourceIds.length==0){
            throw new RuntimeException("未提交需要审批的资源");
        }
        for(Long resourId : resourceIds){
            try{
                processApprove(user, resourId, ACTION_AGREE, ACTION_AGREE_ZH);
            }catch (Exception ex){
                throw ex;
            }
        }
    }

    private List<ResourceOverviewVO> transferResourceApprovePOToROVO(String status, List<ResourceApprovePO> raPOList){

        if(raPOList==null || raPOList.size()==0){
            return null;
        }
        List<ResourceOverviewVO> raVOList = new ArrayList<ResourceOverviewVO>();
        List<Long> idList = new ArrayList<Long>();
        for(ResourceApprovePO raPO : raPOList){

            ResourceOverviewVO rcOverviewVO = new ResourceOverviewVO();
            Long resourceId = raPO.getResourceId();
            if(idList!=null && idList.size()>0 && idList.contains(resourceId)){
                continue;
            }else{
                idList.add(resourceId);
            }

            ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
            Date update = rcPO.getUpdateTime();
            if(update!=null){
                rcOverviewVO.setUpdateTime(DateTools.formatDate(update));
            }
            if(rcPO==null){
                return null;
            }
            rcOverviewVO.setStatus(ResourceTools.ResourceStatus.getStatusByCode(rcPO.getStatus()));
            rcOverviewVO.setId(rcPO.getId());
            rcOverviewVO.setCatalogName(rcPO.getCatalogFullName());
            rcOverviewVO.setDeptCode(rcPO.getDeptCode());
            rcOverviewVO.setDeptName(rcPO.getDeptName());
            rcOverviewVO.setResourceCode(rcPO.getCode());
            rcOverviewVO.setResourceName(rcPO.getName());
            rcOverviewVO.setCatalogCode(rcPO.getCode());
            rcOverviewVO.setCreator(rcPO.getCreator());
            raVOList.add(rcOverviewVO);
        }
        return raVOList;
    }


    private List<ResourceOverviewVO> transferResourceApprovePOToROVO(List<ResourceApprovePO> raPOList){

        if(raPOList==null || raPOList.size()==0){
            return null;
        }
        List<ResourceOverviewVO> raVOList = new ArrayList<ResourceOverviewVO>();
        for(ResourceApprovePO raPO : raPOList){

            ResourceOverviewVO rcOverviewVO = new ResourceOverviewVO();
            if(StringUtils.isNotEmpty(raPO.getSuggestion())){
                rcOverviewVO.setApproveSuggestion(raPO.getSuggestion());
            }
            if(StringUtils.isNotEmpty(raPO.getApproveAction())){
                rcOverviewVO.setApproveAction(raPO.getApproveAction());
            }
            Date approveTime = raPO.getApproveTime();
            if(approveTime!=null){
                rcOverviewVO.setApproveTime(DateTools.formatDate(approveTime));
            }

            Long resourceId = raPO.getResourceId();
            ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
            if(rcPO==null){
                return null;
            }
            rcOverviewVO.setId(rcPO.getId());
            rcOverviewVO.setCatalogName(rcPO.getCatalogFullName());
            rcOverviewVO.setDeptCode(rcPO.getDeptCode());
            rcOverviewVO.setDeptName(rcPO.getDeptName());
            rcOverviewVO.setResourceCode(rcPO.getCode());
            rcOverviewVO.setResourceName(rcPO.getName());
            rcOverviewVO.setCatalogCode(rcPO.getCode());
            rcOverviewVO.setCreator(rcPO.getCreator());
            rcOverviewVO.setStatus(ResourceTools.ResourceStatus.getStatusByCode(rcPO.getStatus()));
            raVOList.add(rcOverviewVO);
        }
        return raVOList;
    }

    private List<ResourceApproveVO> transferResourceApprovePOToVO(List<ResourceApprovePO> raPOList) {
        if (raPOList == null || raPOList.size() == 0) {
            return null;
        }

        List<ResourceApproveVO> raVOList = new ArrayList<ResourceApproveVO>();
        for (ResourceApprovePO raPO : raPOList) {
            ResourceApproveVO raVO = new ResourceApproveVO();
            raVO.setId(raPO.getId());
            raVO.setResourceId(raPO.getResourceId());
            raVO.setApproverName(raPO.getApproverName());
            raVO.setApprover(raPO.getApprover());
            raVO.setApproveAction(raPO.getApproveAction());
            raVO.setSuggestion(raPO.getSuggestion());

            raVO.setCurrentStatus(ResourceStatus.getStatusByCode(raPO.getCurrentStatus()));
            raVO.setNextStatus(ResourceStatus.getStatusByCode(raPO.getNextStatus()));
            if(raPO.getActiveFlag()){
               raVO.setApproveTime("无");

            }else{
               raVO.setApproveTime(DateTools.formatDate(raPO.getApproveTime()));
            }
            raVO.setActiveFlag(raPO.getActiveFlag());
            raVOList.add(raVO);
        }
        return raVOList;
    }
}

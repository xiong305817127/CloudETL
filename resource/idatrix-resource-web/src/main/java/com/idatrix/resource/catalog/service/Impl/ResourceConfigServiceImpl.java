package com.idatrix.resource.catalog.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.basedata.dao.SystemConfigDAO;
import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.catalog.dao.*;
import com.idatrix.resource.catalog.po.*;
import com.idatrix.resource.catalog.service.IResourceConfigService;
import com.idatrix.resource.catalog.vo.*;
import com.idatrix.resource.common.utils.*;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.sso.client.UserHolder;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.idatrix.resource.common.utils.ResourceTools.ResourceAction;
import static com.idatrix.resource.common.utils.ResourceTools.ResourceAction.*;
import static com.idatrix.resource.common.utils.ResourceTools.ResourceAction.DELETE;
import static com.idatrix.resource.common.utils.ResourceTools.ResourceStatus.*;

/**
 * 政府信息资源编辑服务
 */

@Transactional
@Service("resourceConfigService")
public class ResourceConfigServiceImpl implements IResourceConfigService {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;
    @Autowired
    private ResourceColumnDAO resourceColumnDAO;
    @Autowired
    private CatalogResourceDAO catalogResourceDAO;
    @Autowired
    private CatalogNodeDAO catalogNodeDAO;
    @Autowired
    private DeptLimitedDAO deptLimitedDAO;
    @Autowired
    private ResourceHistoryDAO resourceHistoryDAO;
    @Autowired
    private ResourceApproveDAO resourceApproveDAO;
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    @Autowired
    private BatchTools batchTools;
    @Autowired
    private UserService userService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private ISystemConfigService systemConfigService;

    @Override
    public Long addResourceInfo(String user, ResourceConfigVO resourceConfigVO) throws Exception{
        //TODO: 考虑数据查重
        if(ownSameDate(resourceConfigVO)){
            throw new RuntimeException("配置中存在相同资源名称或者相同资源代码");
        }
        int formatType = resourceConfigVO.getFormatType();
        Long id = resourceConfigVO.getId();
        Long sourceServerId = null;
        ResourceConfigPO rcPO = transferResourceConfigVoTOPo(user, resourceConfigVO);

        /*1.资源编码生成：资源编码，由catalog_code +”/”+ seq_num，自动生成*/
        String codeTmp = resourceConfigVO.getCatalogCode() + "/" +resourceConfigVO.getSeqNum();
        rcPO.setCode(codeTmp);
        rcPO.setCatalogFullName(getResourceCatalogFullName(resourceConfigVO));

        ResourceAction action = null;
        if(id!=null && id!=0){  //update一下

            ResourceConfigPO oldrcPO = resourceConfigDAO.getConfigById(id);
            String status = oldrcPO.getStatus();
            if(!StringUtils.equals(status, DRAFT.getStatusCode()) &&
                !StringUtils.equals(status, WAIT_UPDATE.getStatusCode()) &&
                  !StringUtils.equals(status, WAIT_REG_APPROVE.getStatusCode()) &&
                   !StringUtils.equals(status, WAIT_PUB_APPROVE.getStatusCode())){

                throw new RuntimeException("当前资源不可编辑");
            }

            rcPO.setStatus(oldrcPO.getStatus());
            rcPO.setCreateTime(oldrcPO.getCreateTime());
            rcPO.setCreator(oldrcPO.getCreator());
            resourceConfigDAO.updateById(rcPO);
            action = UPDATE;
        }else{  //add 直接增加信息
            rcPO.setStatus(DRAFT.getStatusCode());
            resourceConfigDAO.insert(rcPO);
            id = rcPO.getId();
            action = CREATE;
        }
        //资源修改记录入库
        saveResourceHistory(user, id, action);

        //数据库需要存储表格
        //if(ResourceTools.FormatType.getRefreshCycle(formatType)== DB){
            List<ResourceColumnVO> rsColumnList = new ArrayList<ResourceColumnVO>();
            rsColumnList = resourceConfigVO.getResourceColumnVOList();
            if(rsColumnList!=null && rsColumnList.size()>0) {
                saveResourceColumn(user, id, rsColumnList);
            }
        //}

        /*存储分类和资源id 映射关系*/
        saveCatelogResourceMap(resourceConfigVO, id);

        //将信息资源的 共享部门入库
        if(resourceConfigVO.getShareType()!=3) {  //不是不予共享都需要处理
            saveSharedDept(user, resourceConfigVO, id);
        }
        return id;
    }

    /*将Excel数据批量入库*/
    @Transactional(rollbackFor = Exception.class)
    private void  saveExcelResource(String user, List<ResourceConfigVO> rcList)throws Exception{

        //处理资源的部门列表ID问题
        Boolean userManageFlag = false;
        SSOUser ssoUser = UserHolder.getUser();
        Object obj = ssoUser.getProperty("renterId");
        Long rentId = Long.valueOf((String) obj);

        String userName =(String)ssoUser.getProperty("username");
        User rentUser = userService.findRenterByRenterId(rentId);
        ////当前用户是租户,存储时候需要对用户进行干预
        if(StringUtils.equals(userName, rentUser.getUsername())){
            userManageFlag  = true;
        }else {

            //数据中心管理员上传也需要特殊处理
//            String centerAdminName = null;
//            SystemConfigPO sysConfigPO = systemConfigDAO.getLastestSysConfig();
//            if (sysConfigPO != null) {
//                Long centerAdminRole = sysConfigPO.getCenterAdminRole();
//                if (centerAdminRole != null && centerAdminRole > 0) {
//                    List<User> userList = new ArrayList<User>();
//                    userList = userService.findUserByRoleAndRenter(centerAdminRole.intValue(), rentId);
////                    userList = userService.findUsersByDeptAndRole(deptId, deptAdminRol.intValue());
//                    if (userList != null && userList.size() > 0) {
//                        User approveUser = userList.get(0);
//                        centerAdminName = approveUser.getUsername();
//                    } else {
//                        throw new RuntimeException("还未设置数据中心管理员，请先配置数据中心管理员再提交注册");
//                    }
//                }
//            } else {
//                throw new Exception("系统参数没有配置，请先配置再上传");
//            }
//
            String centerAdminName = null;
            User centerUser = systemConfigService.getCurrentUserCenterAdmin();
            if(centerUser!=null){
                centerAdminName = centerUser.getUsername();
            }

            if (StringUtils.equals(userName, centerAdminName)) {
                userManageFlag = true;
            }
        }

        Map<String, String> deptCodeUserNameMap = new ConcurrentHashMap<String, String>();

        for(ResourceConfigVO rcVO :rcList){
            String deptCode = rcVO.getDeptCode();
            if(userManageFlag) {
                String deptStaffName = null;

                if(deptCodeUserNameMap.get(deptCode)!=null){
                    user = deptCodeUserNameMap.get(deptCode);
                }else {

                    //租户用户对存储用户信息进行干预，按照部门资源信息来存储
                    List<Integer> parentIds = userService.findParentIdsByUnifiedCreditCode(rcVO.getDeptCode(), rentId);
                    if (parentIds != null && parentIds.size() > 0) {
                        //获取最后一个信息
                        Integer deptId = parentIds.get(parentIds.size() - 1);

                        //数据中心管理员上传也需要特殊处理
//                        SystemConfigPO sysConfigPO = systemConfigDAO.getLastestSysConfig();
//                        if (sysConfigPO != null) {
//                            Long deptStaffRole = sysConfigPO.getDeptStaffRole();
//                            if (deptStaffRole != null && deptStaffRole > 0) {
//                                List<User> userList = new ArrayList<User>();
//                                userList = userService.findUsersByDeptAndRole(deptId, deptStaffRole.intValue());
//                                if (userList != null && userList.size() > 0) {
//                                    User approveUser = userList.get(0);
//                                    deptStaffName = approveUser.getUsername();
//                                } else {
//                                    throw new RuntimeException("还未设置部门填报人员信息，请先配置填报人员信息再提交注册");
//                                }
//                            }
//                        } else {
//                            throw new Exception("系统参数没有配置，请先配置再上传");
//                        }

                        User approveUser = systemConfigService.getDeptStaff(deptId);
                        if(approveUser!=null){
                            deptStaffName = approveUser.getUsername();
                        }
                    }else{
                        throw new RuntimeException("导入表格中存在非当前租户下部门机构资源目录，部门统一社会信用编码为："+rcVO.getDeptCode());
                    }
                    user = deptStaffName;
                    deptCodeUserNameMap.put(deptCode, user);
                }
            }else{

                Organization org = userService.getUserOrganizationByUserId(Long.valueOf(userUtils.getCurrentUserId()));
                if(org!=null){
                    if(!StringUtils.equals(org.getUnifiedCreditCode(), rcVO.getDeptCode())){
                        throw new RuntimeException("当前用户不是租户和数据中心管理员角色，只能添加本部门数据，请修改表格数据");
                    }
                }else {
                    throw new RuntimeException("当前用户没有配置所属部门，请先配置再使用");
                }
//                userService.getUserOrganizationByUserId();
//                userService
//                Long importDeptID = rcVO.getDeptNameIdArray().split();
//                if(deptId)
            }

            ResourceConfigPO rcPO = transferResourceConfigVoTOPo(user, rcVO);
            resourceConfigDAO.insert(rcPO);
            Long id = rcPO.getId();

            //资源修改记录入库
            saveResourceHistory(user, id, CREATE);

            //数据库需要存储表格
            int formatType = rcVO.getFormatType();
//            if(ResourceTools.FormatType.getRefreshCycle(formatType)== DB){
                List<ResourceColumnVO> rsColumnList = new ArrayList<ResourceColumnVO>();
                rsColumnList = rcVO.getResourceColumnVOList();
                if(rsColumnList!=null && rsColumnList.size()>0) {
                    saveResourceColumn(user, id, rsColumnList);
                }
//            }

            /*存储分类和资源id 映射关系*/
            saveCatelogResourceMap(rcVO, id);

            //将信息资源的 共享部门入库
            if(rcVO.getShareType()!=3) {  //不是不予共享都需要处理
                saveSharedDept(user, rcVO, id);
            }
            //System.out.print(".");
        }
    }

    public int deleteResourceInfo(String user, Long id) throws Exception{

        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(id);
        if(rcPO==null){
            throw new RuntimeException("不存在该资源信息");
        }
        if(!StringUtils.equals(rcPO.getCreator(), user)){
            throw new RuntimeException("该用户没有删除本资源权限");
        }
        if(StringUtils.equals(rcPO.getStatus(), DRAFT.getStatusCode())
                || StringUtils.equals(rcPO.getStatus(), DELETE.getActionCode())){
            resourceConfigDAO.deleteById(id);

            //同时删除rc_catalog_resource map表中对应信息
            catalogResourceDAO.deleteByResourceId(id);
            deptLimitedDAO.deleteByResourceId(id);
            resourceColumnDAO.deleteByResourceId(id);

            ResourceHistoryPO rsHistoryPO = new ResourceHistoryPO();
            rsHistoryPO.setCreator(user);
            rsHistoryPO.setCreateTime(new Date());
            rsHistoryPO.setResourceId(id);
            rsHistoryPO.setActionName(DELETE.getAction());
            rsHistoryPO.setAction(DELETE.getActionCode());
            resourceHistoryDAO.insert(rsHistoryPO);
            return 0;
        }else{
            throw new RuntimeException("该资源信息已被占用，不用删除");
        }

    }

    public List<ResourceOverviewVO> getResourceInfoByUser(String user) {
        List<ResourceConfigPO> rcPOList = resourceConfigDAO.getConfigByUser(user);
        return transferResourceConfigPoTOVo(rcPOList);
    }

    public ResourceConfigVO getResourceInfoById(Long id) throws Exception {
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(id);
        if(rcPO==null){
            throw new RuntimeException("不存在该信息资源记录");
        }

        ResourceConfigVO rcVO = transferResourceConfigPoTOVo(rcPO);
        //TODO:获取详情时候，对于数据库需要将对应column详细和dept部门信息传递过来
//        int formatType = rcVO.getFormatType();
//        if(ResourceTools.FormatType.getRefreshCycle(formatType)== DB){

            List<ResourceColumnPO> rColumnPoList = resourceColumnDAO.getColumnByResourceId(id);
            if(rColumnPoList!=null && rColumnPoList.size()>0) {
                rcVO.setResourceColumnVOList(transferResourceColumnPoTOVo(rColumnPoList));
            }
//        }

        Long[] deptIdArray = deptLimitedDAO.getDeptArrayByResource(id);
        Arrays.sort(deptIdArray);
        rcVO.setShareDeptArray(deptIdArray);

        List<CatalogResourcePO> rcPOList = catalogResourceDAO.getByResourceId(id);
        Collections.sort(rcPOList);

        Long[] catalogArray = new Long[rcPOList.size()];
        for(int index=0; index<rcPOList.size(); index++){
            catalogArray[index] = rcPOList.get(index).getCatalogId();
        }
        rcVO.setCatalogIdArray(catalogArray);

        return rcVO;
    }


    public ResultPager<ResourceOverviewVO> queryByCondition(Map<String, String> conditionMap, Integer pageNum, Integer pageSize) {

        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
        List<ResourceConfigPO> rsPOList = resourceConfigDAO.queryByCondition(conditionMap);
        List<ResourceOverviewVO> rcVOList = transferResourceConfigPoTOVo(rsPOList);

        //用PageInfo对结果进行包装
        PageInfo<ResourceConfigPO> pi = new PageInfo<ResourceConfigPO>(rsPOList);
        Long totalNums = pi.getTotal();
        ResultPager<ResourceOverviewVO> rp = new ResultPager<ResourceOverviewVO>(pi.getPageNum(), totalNums, rcVOList);
        return rp;
    }

    @Override
    public List<ResourceHistoryVO> getHistory(Long id) {

        List<ResourceHistoryPO> rhPOList = resourceHistoryDAO.getHistoryByResourceId(id);
        if(rhPOList==null||rhPOList.size()==0){
            throw new RuntimeException("该资源记录为空");
        }

        List<ResourceHistoryVO> rhVOList = new ArrayList<ResourceHistoryVO>();
        for(ResourceHistoryPO rhPO : rhPOList){
            ResourceHistoryVO rhVO = new ResourceHistoryVO();
            rhVO.setId(rhPO.getId());
            rhVO.setActionName(rhPO.getActionName());
            rhVO.setOperator(rhPO.getCreator());
            rhVO.setOperatorTime(DateTools.formatDate(rhPO.getCreateTime()));
            rhVOList.add(rhVO);
        }
        return rhVOList;
    }

    @Override
    public List<ResourcePubVO> getPubResourceByCondition(Map<String, String> con) {

        con.put("status", PUB_SUCCESS.getStatusCode());
        List<ResourceConfigPO> rcList = resourceConfigDAO.queryByCondition(con);
        return transferRCToResourcePub(rcList);
    }

    @Override
    public String saveBatchImport(String user, CommonsMultipartFile multiPartFile) throws Exception {

        if(multiPartFile==null){
            throw new Exception("上传信息没有包含文件");
        }

        String fileOriginName = multiPartFile.getOriginalFilename();
        if(!batchTools.verifyExcel(fileOriginName)){
            throw new Exception("上传文件格式不符合要求");
        }

        //文件大小过滤
        SystemConfigPO systemConfigPO = systemConfigService.getSystemConfig(); //systemConfigDAO.getLastestSysConfig();
        if(systemConfigPO==null || systemConfigPO.getFileUploadSize()==0){
            throw new Exception("系统参数没有配置，请先配置再上传");
        }
        int fileLimitSizeMB = systemConfigPO.getFileUploadSize();
        if(!FileUtils.validFileSize(fileLimitSizeMB, multiPartFile)){
            throw new Exception("文件超过系统配置大小，文件限制大小为 "+fileLimitSizeMB+" MB");
        }

        String fileName = FileUtils.createFile("excel", multiPartFile);
        return fileName;
    }

    @Override
    public void processExcel(String user, String fileName) throws Exception {
        if(StringUtils.isEmpty(fileName)){
            throw new Exception("文件名为空");
        }
        String filePath = FileUtils.getFileDirByType("excel")+File.separator+fileName;
        LOG.info("======1=====CurrentTime"+ DateTools.formatDate(new Date())  );
        List<ResourceConfigVO> rcList = batchTools.processResourceExcel(batchTools.readResourceExcelValue(new File(filePath)));
        LOG.info("======2=====CurrentTime"+ DateTools.formatDate(new Date())  );
        //batchTools.preProcesBeforeSave(rcList);
        saveExcelResource(user, batchTools.preProcesBeforeSave(rcList));
        LOG.info("======3=====CurrentTime"+ DateTools.formatDate(new Date())  );
        FileUtils.deletefile(filePath);
    }


    /*将资源增删改查记录入库*/
    private void saveResourceHistory(String user, Long id, ResourceAction action){
        ResourceHistoryPO rsHistoryPO = new ResourceHistoryPO();
        rsHistoryPO.setCreator(user);
        rsHistoryPO.setCreateTime(new Date());
        rsHistoryPO.setActionName(action.getAction());
        rsHistoryPO.setAction(action.getActionCode());
        rsHistoryPO.setResourceId(id);
        resourceHistoryDAO.insert(rsHistoryPO);
    }

    /*存储分类和资源id 映射关系*/
    private void saveCatelogResourceMap(ResourceConfigVO resourceConfigVO, Long id){

        //资源编码的生成，涉及资源名称的存储，资源名称使用全路径名称存储，和id列表
        Long[] catalogListInLib = catalogResourceDAO.getCatalogListByResourceId(id);
        Long[] catalogIdList = resourceConfigVO.getCatalogIdArray();
        Arrays.sort(catalogListInLib);
        Arrays.sort(catalogIdList);
        if(!Arrays.equals(catalogListInLib, catalogIdList)){
            if(catalogListInLib!=null && catalogListInLib.length>0){
                catalogResourceDAO.deleteByResourceId(id);
            }
            for(Long catalogId :catalogIdList){
                CatalogResourcePO cr = new CatalogResourcePO();
                cr.setResourceId(id);
                cr.setCatalogId(catalogId);
                CatalogNodePO cnPO = catalogNodeDAO.getCatalogNodeById(catalogId);
                cr.setDepth(cnPO.getDept());
                catalogResourceDAO.insert(cr);
            }
        }
    }


    /*将共享部门信息入库*/
    private void saveSharedDept(String user, ResourceConfigVO resourceConfigVO, Long id){

        if(resourceConfigVO.getShareType()!=3) {  //不是不予共享都需要处理
            Long[] deptIdInLib = deptLimitedDAO.getDeptArrayByResource(id);
            Long[] deptIdList = resourceConfigVO.getShareDeptArray();
            if(deptIdList==null || deptIdList.length==0){
                return;
            }
            Arrays.sort(deptIdInLib);
            Arrays.sort(deptIdList);
            if(!Arrays.equals(deptIdInLib, deptIdList)){
                if(deptIdInLib!=null && deptIdInLib.length>0){
                    deptLimitedDAO.deleteByResourceId(id);
                }
                for (Long deptId : deptIdList) {
                    DeptLimitedPO deptLimitedPO = new DeptLimitedPO();
                    deptLimitedPO.setDeptId(deptId);
                    deptLimitedPO.setResourceId(id);
                    deptLimitedPO.setCreator(user);
                    deptLimitedPO.setCreateTime(new Date());
                    deptLimitedPO.setUpdater(user);
                    deptLimitedPO.setUpdateTime(new Date());
                    deptLimitedPO.setStatus("Y");
                    deptLimitedDAO.insert(deptLimitedPO);
                }
            }
        }
    }

    private List<ResourcePubVO> transferRCToResourcePub(List<ResourceConfigPO> rcList){

        List<ResourcePubVO> rpList = new ArrayList<ResourcePubVO>();
        if(rcList==null || rcList.size()==0) {
            return rpList;
        }
        for(ResourceConfigPO rc: rcList){
            ResourcePubVO rp = new ResourcePubVO();
            rp.setId(rc.getId());
            rp.setFormatInfo(rc.getFormatInfo());
            rp.setFormatType(rc.getFormatType());
            rp.setResourceName(rc.getName());
            rp.setCatalogName(rc.getCatalogFullName());
            rp.setResourceCode(rc.getSeqNum());
            rp.setResourceFullCode(rc.getCode());
            rp.setFormatInfoExtend(rc.getFormatInfoExtend());
            rpList.add(rp);
        }
        return rpList;

    }

    private Boolean ownSameDate(ResourceConfigVO rc){

        Long resourceId = rc.getId();
        if(resourceId!=null && resourceId!=0){
            return false;
        }
        List<ResourceConfigPO> rcPOList = resourceConfigDAO.getByNameOrCode(rc.getName(),
                rc.getCatalogCode(), rc.getSeqNum());
        if(rcPOList!=null && rcPOList.size()>0){
            return true;
        }
        return false;
    }

    /*
      * 存储 Resource Column信息
      */
    private void saveResourceColumn(String user, Long resourcdId, List<ResourceColumnVO> rsColumnList){

/*        List<Long> columnIdList = new ArrayList<Long>();
        for(ResourceColumnVO rcVO: rsColumnList){
            Long rcVOId = rcVO.getId();
            if(rcVOId!=null && rcVOId>0){
                columnIdList.add(rcVOId);
            }
        }
        List<ResourceColumnPO> rColumnPOList = new ArrayList<ResourceColumnPO>();
        rColumnPOList = resourceColumnDAO.getColumnByResourceId(resourcdId);
        for(ResourceColumnPO rColumnPO : rColumnPOList){
            Long rcColmnId = rColumnPO.getId();
            if(!columnIdList.contains(rcColmnId)){
                resourceColumnDAO.deleteById(rcColmnId);
            }
        }

        for(ResourceColumnVO rsColumn : rsColumnList){
            Long columnId = rsColumn.getId();
            ResourceColumnPO resourceColumnPO = transferResourceColumnVoTOPo(user, rsColumn);
            resourceColumnPO.setResourceId(resourcdId);
            if(columnId==null ){
                resourceColumnPO.setCreator(user);
                resourceColumnPO.setCreateTime(new Date());
                resourceColumnDAO.insert(resourceColumnPO);
            }else {
                ResourceColumnPO oldRcPO = resourceColumnDAO.getColumnById(columnId);
                resourceColumnPO.setCreator(oldRcPO.getCreator());
                resourceColumnPO.setCreateTime(oldRcPO.getCreateTime());
                resourceColumnDAO.updateById(resourceColumnPO);
            }
        }*/



        //由于以前的方法不能实现有序存储，所以每次添加的时候，都要全部删除然后添加 changed By robin 2018/09/18
        List<ResourceColumnPO> rColumnPOList = resourceColumnDAO.getColumnByResourceId(resourcdId);
        for(ResourceColumnPO rColumnPO : rColumnPOList){
           resourceColumnDAO.deleteById(rColumnPO.getId());
        }

        for(ResourceColumnVO rsColumn : rsColumnList) {
            Long columnId = rsColumn.getId();
            ResourceColumnPO resourceColumnPO = transferResourceColumnVoTOPo(user, rsColumn);
            resourceColumnPO.setResourceId(resourcdId);

            resourceColumnPO.setCreator(user);
            resourceColumnPO.setCreateTime(new Date());
            resourceColumnDAO.insert(resourceColumnPO);
        }

    }

    private String getResourceCatalogFullName(ResourceConfigVO rcVO){

        //需要根据depth层级深度 ！！！前端根据深度传入
        Long []catalogIdList = rcVO.getCatalogIdArray();
        int len = catalogIdList.length;
        if(len<=0){
            return null;
        }
        StringBuilder catalogFullName = new StringBuilder();
//        Arrays.sort(catalogIdList);
        for(Long catalogId :catalogIdList){
            CatalogNodePO cnPO = catalogNodeDAO.getCatalogNodeById(catalogId);
            catalogFullName.append(cnPO.getResourceName()+"/");
        }
        //不需要最后面的"/" 所以减 1
        return catalogFullName.substring(0, catalogFullName.length()-1);
    }


    private ResourceConfigPO transferResourceConfigVoTOPo(String user, ResourceConfigVO rcVO){
        ResourceConfigPO rcPO = new ResourceConfigPO();
        Long id = rcVO.getId();
        if(id!=null && id!=0){
            rcPO.setId(id);
        }

        if(StringUtils.isNotEmpty(rcVO.getStatus())){
            rcPO.setStatus(rcVO.getStatus());
        }
        if(StringUtils.isNotEmpty(rcVO.getCode())){
            rcPO.setCode(rcVO.getCode());
        }
        if(StringUtils.isNotEmpty(rcVO.getCatalogName())){
            rcPO.setCatalogFullName(rcVO.getCatalogName());
        }
        rcPO.setCatalogCode(rcVO.getCatalogCode());
        rcPO.setSeqNum(rcVO.getSeqNum());

        rcPO.setDeptNameIds(rcVO.getDeptNameIdArray());
        rcPO.setName(rcVO.getName());
        rcPO.setRemark(rcVO.getRemark());
        rcPO.setDeptName(rcVO.getDeptName());
        rcPO.setDeptCode(rcVO.getDeptCode());
        rcPO.setResourceAbstract(rcVO.getResourceAbstract());
        rcPO.setKeyword(rcVO.getKeyword());
        rcPO.setFormatType(rcVO.getFormatType());
        rcPO.setFormatInfo(rcVO.getFormatInfo());
        rcPO.setFormatInfoExtend(rcVO.getFormatInfoExtend());
        rcPO.setShareType(rcVO.getShareType());
        rcPO.setShareCondition(rcVO.getShareCondition());
        rcPO.setShareMethod(rcVO.getShareMethod());

        rcPO.setOpenType(rcVO.getOpenType());
        rcPO.setOpenCondition(rcVO.getOpenCondition());
        rcPO.setRefreshCycle(rcVO.getRefreshCycle());
        rcPO.setPubDate(rcVO.getPubDate());
        rcPO.setRelationCode(rcVO.getRelationCode());
        rcPO.setBindTableId(rcVO.getBindTableId());
        rcPO.setLibTableId(rcVO.getLibTableId());
        rcPO.setBindServiceId(rcVO.getBindServiceId());
        rcPO.setCreator(user);
        rcPO.setUpdater(user);
        rcPO.setCreateTime(new Date());
        rcPO.setUpdateTime(new Date());

        return rcPO;
    }

    private ResourceConfigVO transferResourceConfigPoTOVo(ResourceConfigPO rcPO){
        ResourceConfigVO rcVO = new ResourceConfigVO();
        rcVO.setId(rcPO.getId());
        rcVO.setCode(rcPO.getCode());
        rcVO.setCatalogName(rcPO.getCatalogFullName());
        rcVO.setCatalogCode(rcPO.getCatalogCode());
        rcVO.setSeqNum(rcPO.getSeqNum());
        rcVO.setName(rcPO.getName());

        rcVO.setRemark(rcPO.getRemark());
        rcVO.setDeptName(rcPO.getDeptName());
        rcVO.setDeptCode(rcPO.getDeptCode());
        rcVO.setDeptNameIdArray(rcPO.getDeptNameIds());
        rcVO.setResourceAbstract(rcPO.getResourceAbstract());
        rcVO.setKeyword(rcPO.getKeyword());
        rcVO.setFormatType(rcPO.getFormatType());
        rcVO.setFormatInfo(rcPO.getFormatInfo());
        rcVO.setFormatInfoExtend(rcPO.getFormatInfoExtend());
        rcVO.setShareType(rcPO.getShareType());
        rcVO.setShareCondition(rcPO.getShareCondition());
        rcVO.setShareMethod(rcPO.getShareMethod());

        rcVO.setOpenType(rcPO.getOpenType());
        rcVO.setOpenCondition(rcPO.getOpenCondition());
        rcVO.setRefreshCycle(rcPO.getRefreshCycle());
        rcVO.setPubDate(rcPO.getPubDate());
        rcVO.setRelationCode(rcPO.getRelationCode());
        rcVO.setBindTableId(rcPO.getBindTableId());
        rcVO.setBindServiceId(rcPO.getBindServiceId());
        rcVO.setLibTableId(rcPO.getLibTableId());
        rcVO.setStatus(ResourceTools.ResourceStatus.getStatusByCode(rcPO.getStatus()));
        rcVO.setCreator(rcPO.getCreator());
        return rcVO;
    }

    private List<ResourceOverviewVO> transferResourceConfigPoTOVo(List<ResourceConfigPO> rcPoList) {
        List<ResourceOverviewVO> rcOvervieweList = new ArrayList<ResourceOverviewVO>();
        for (ResourceConfigPO rcPO : rcPoList) {
            ResourceOverviewVO rcOverviewVO = new ResourceOverviewVO();
            rcOverviewVO.setId(rcPO.getId());
            rcOverviewVO.setCatalogName(rcPO.getCatalogFullName());
            rcOverviewVO.setDeptCode(rcPO.getDeptCode());
            rcOverviewVO.setDeptName(rcPO.getDeptName());
            rcOverviewVO.setResourceCode(rcPO.getCode());
            rcOverviewVO.setResourceName(rcPO.getName());
            rcOverviewVO.setCatalogCode(rcPO.getCode());

            StringBuilder status = new StringBuilder(ResourceTools.ResourceStatus.getStatusByCode(rcPO.getStatus()));
            //界面显示将 待审批资源展现成 注册待审批(当前审批人：XXX)
            if(StringUtils.equals(rcPO.getStatus(), WAIT_REG_APPROVE.getStatusCode()) ||
                    StringUtils.equals(rcPO.getStatus(), WAIT_PUB_APPROVE.getStatusCode())){
                ResourceApprovePO raPO = resourceApproveDAO.getWaitApproveByResourceId(rcPO.getId());
                if(raPO!=null && StringUtils.isNotEmpty(raPO.getApproverName())){

                    rcOverviewVO.setApproverName(raPO.getApproverName());
//                    status.append("（当前审批人：" + raPO.getApproverName()+"）");
                }
            }
            rcOverviewVO.setStatus(status.toString());
            rcOvervieweList.add(rcOverviewVO);
        }
        return rcOvervieweList;
    }

    private ResourceColumnPO transferResourceColumnVoTOPo(String user, ResourceColumnVO rsVO){
        ResourceColumnPO rsColumnPO=new ResourceColumnPO();
        rsColumnPO.setId(rsVO.getId());
//        rsColumnPO.setResourceId(rsVO.getResourceId());
        rsColumnPO.setColName(rsVO.getColName());
        rsColumnPO.setColType(rsVO.getColType());
        rsColumnPO.setColSeqNum(rsVO.getColSeqNum());
        rsColumnPO.setDateFormat(rsVO.getDateFormat());
        rsColumnPO.setTableColCode(rsVO.getTableColCode());
        rsColumnPO.setTableColType(rsVO.getTableColType());
        rsColumnPO.setColType(rsVO.getColType());
        rsColumnPO.setUniqueFlag(rsVO.getUniqueFlag());
        rsColumnPO.setRequiredFlag(rsVO.getRequiredFlag());
        rsColumnPO.setResourceId(rsVO.getResourceId());

        rsColumnPO.setModifier(user);
        rsColumnPO.setModifyTime(new Date());
        return rsColumnPO;
    }

    private List<ResourceColumnVO> transferResourceColumnPoTOVo(List<ResourceColumnPO> rcPOList){

        List<ResourceColumnVO> rcColumnList = new ArrayList<ResourceColumnVO>();
        for(ResourceColumnPO rcPO : rcPOList){
            ResourceColumnVO rcClomn = new ResourceColumnVO();
            rcClomn.setId(rcPO.getId());
            rcClomn.setResourceId(rcPO.getResourceId());
            rcClomn.setColName(rcPO.getColName());
            rcClomn.setColType(rcPO.getColType());
            rcClomn.setColSeqNum(rcPO.getColSeqNum());
            rcClomn.setDateFormat(rcPO.getDateFormat());
            rcClomn.setTableColCode(rcPO.getTableColCode());
            rcClomn.setTableColType(rcPO.getTableColType());
            rcClomn.setUniqueFlag(rcPO.getUniqueFlag());
            rcClomn.setRequiredFlag(rcPO.getRequiredFlag());
            rcClomn.setResourceId(rcPO.getResourceId());
            rcColumnList.add(rcClomn);
        }
        return rcColumnList;
    }
}

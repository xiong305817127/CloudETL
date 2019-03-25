package com.idatrix.resource.subscribe.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.basedata.dao.ServiceDAO;
import com.idatrix.resource.basedata.dao.SystemConfigDAO;
import com.idatrix.resource.basedata.po.ServicePO;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.catalog.dao.ResourceColumnDAO;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceColumnPO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.service.IResourceStatiscsService;
import com.idatrix.resource.catalog.vo.ResourceColumnVO;
import com.idatrix.resource.common.cache.SequenceNumberManager;
import com.idatrix.resource.common.task.ExchangeTask;
import com.idatrix.resource.common.utils.*;
import com.idatrix.resource.portal.common.ResourceFormatTypeEnum;
import com.idatrix.resource.subscribe.dao.SubscribeDAO;
import com.idatrix.resource.subscribe.dao.SubscribeDbioDAO;
import com.idatrix.resource.subscribe.po.SubscribeDbioPO;
import com.idatrix.resource.subscribe.po.SubscribePO;
import com.idatrix.resource.subscribe.service.ISubscribeService;
import com.idatrix.resource.subscribe.utils.DataMaskingTypeEnum;
import com.idatrix.resource.subscribe.utils.SubscribeStatusEnum;
import com.idatrix.resource.subscribe.vo.SubscribeOverviewVO;
import com.idatrix.resource.subscribe.vo.SubscribeVO;
import com.idatrix.resource.subscribe.vo.SubscribeWebServiceVO;
import com.idatrix.resource.subscribe.vo.request.SubscribeApproveRequestVO;
import com.idatrix.resource.taskmanage.dao.SubTaskDAO;
import com.idatrix.resource.taskmanage.po.SubTaskPO;
import com.idatrix.resource.terminalmanage.dao.TerminalManageDAO;
import com.idatrix.resource.terminalmanage.po.TerminalManagePO;
import com.idatrix.resource.terminalmanage.service.ITerminalManageService;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.dataswap.MetadataField;
import com.ys.idatrix.metacube.api.beans.dataswap.MetadataTable;
import com.ys.idatrix.metacube.api.beans.dataswap.QueryMetadataFieldsResult;
import com.ys.idatrix.metacube.api.beans.dataswap.SubscribeCrtTbResult;
import com.ys.idatrix.metacube.api.service.MetadataToDataSwapService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import static com.idatrix.resource.common.utils.ResourceTools.FormatType.DB;
import static com.idatrix.resource.common.utils.ResourceTools.FormatType.SERVICE_INTERFACE;
import static com.idatrix.resource.subscribe.utils.SubscribeStatusEnum.SUCCESS;
import static com.ys.idatrix.metacube.api.beans.dataswap.AuthorizedFlowType.SUBSCRIBED;


@Transactional
@Service("resourceSubscribeService")
@PropertySource("classpath:init.properties")
public class SubscribeServiceImpl implements ISubscribeService {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubscribeDAO subscribeDAO;

    @Autowired
    private SubscribeDbioDAO subscribeDbioDAO;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private SystemConfigDAO systemConfigDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceColumnDAO resourceColumnDAO;

    @Autowired
    private ServiceDAO serviceDAO;

    @Autowired
    private TerminalManageDAO terminalManageDAO;

    @Autowired
    private SequenceNumberManager sequenceNumberManager;

    @Autowired(required=false)
    private MetadataToDataSwapService metacubeCatalogService;

    @Autowired
    private ITerminalManageService terminalManageService;

    @Autowired
    private SubTaskDAO subTaskDAO;


    @Autowired
    private ExchangeTask exchangeTask;

    @Autowired
    private IResourceStatiscsService resourceStatiscsService;

    @Autowired
    private ISystemConfigService systemConfigService;

    @Autowired
    private UserUtils userUtils;




    /*webServiceUrl数据库服务开放网址-数据服务开放时候需要使用到的url地址*/
    @Value("${web.service.url}")
    private String webServiceUrl;

    /*
    *   判断是否有相同的订阅信息，用来去重
    * 判断标准：
    */
    private Boolean getSameFlag(String user, Long resourceId){
        Boolean flag = false;
        //TODO: 测试联调不做严格校验！！！
//        List<SubscribePO> subPoList = subscribeDAO.getByResourceIdAndProposer(resourceId, user);
//        if(subPoList!=null && subPoList.size()>0){
//            for(SubscribePO subPO:subPoList){
//                String status = subPO.getStatus();
//                if(StringUtils.equals(status, SUCCESS.getStatus())){
//                    Date nowTime = new Date();
//                    Date endTime = subPO.getEndDate();
//                    if(endTime.after(nowTime)){
//                        flag = true;
//                    }
//                }else if(StringUtils.equals(status, SubscribeStatusEnum.FAILED.getStatus())){
//                    //订阅状态为失败的时候 ，可以直接重新订阅
//                }else{
//                    //草稿状态时候，如果有相同直接返回
//                    flag = true;
//                }
//            }
//        }
        return flag;
    }

    /*将订阅信息转换成数据存储方式*/
    private SubscribePO transferSubscribeVOToPO(SubscribeVO vo) throws Exception{

        //订阅审批人为资源提供方角色，需要获取资源提供方部门ID
        Long resourceId = vo.getResourceId();
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        if(rcPO==null){
            throw new Exception("不存在订阅资源记录");
        }
        String creator = rcPO.getCreator();
        User creatorInfo = userService.findByUserName(creator);


        //修改资源状态
        SSOUser userSSOInfo = userUtils.getCurrentUserSSO();
        String user = userUtils.getCurrentUserName();
        String userName = userUtils.getCurrentUserRealName();
        String approve = user; //审批人账号，
        String approveName = userName;    //审批人姓名

        //获取系统配置的部门管理员 设置成流程下一处理人
        User subscribeUser = systemConfigService.getSubscribeApprover(creator, creatorInfo.getDeptId());
        if(subscribeUser!=null){
            approve = subscribeUser.getUsername();
            approveName = subscribeUser.getRealName();
        }else{
            throw new Exception("没有配置订阅审批人员，请先配置再注册");
        }

        SubscribePO po = new SubscribePO();
        po.setResourceId(vo.getResourceId());
        po.setEndDate(DateTools.parseDate(vo.getEndDate()));
        po.setDeptName(vo.getDeptName());
        po.setShareMethod(vo.getShareMethod());
        po.setSubscribeReason(vo.getSubscribeReason());

        String url = vo.getServiceUrl();
        if(StringUtils.isNotEmpty(url)){
            po.setServiceUrl(url);
        }
        String subKey = vo.getSubKey();
        if(StringUtils.isNotEmpty(subKey)){
            po.setSubKey(subKey);
        }

        //订阅相关信息
        po.setApprover(approve);
        po.setApproverName(approveName);
//        po.setApproveTime(new Date());

        //提交评审人相关信息
        po.setDeptName(vo.getDeptName());
        po.setSubscribeUserName(userName);
        po.setCreator(user);
        po.setModifier(user);
        po.setCreateTime(new Date());
        po.setModifyTime(new Date());
        return po;
    }

    /*存储订阅数据库IO信息-查询条件和订阅信息*/
    private void saveSubscribeDBIO(String user, Long subscribeId, String ioType, List<ResourceColumnVO> rcList){
        if(rcList==null || rcList.size()==0){
            return;
        }
        for(ResourceColumnVO rcVO : rcList){
            Long columnId = rcVO.getColumnId();
            if(columnId!=null && columnId!=0L){

                SubscribeDbioPO subPo = new SubscribeDbioPO();
                subPo.setSubscribeId(subscribeId);
                subPo.setColumnId(columnId);
                subPo.setParamType(ioType);
                subPo.setCreateTime(new Date());
                subPo.setCreator(user);
                subPo.setModifier(user);
                subPo.setModifyTime(new Date());
                subscribeDbioDAO.insert(subPo);
            }
        }
    }

    @Override
    public SubscribeVO getInitConfig(Long resourcdId) throws Exception {

        if(resourcdId==null || resourcdId==0L){
            throw new Exception("订阅信息资源为空");
        }
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourcdId);
        if(rcPO==null){
            LOG.error("没有该信息资源记录，资源ID为 {}", resourcdId);
            throw new Exception("没有该信息资源记录，资源ID为 "+resourcdId);
        }
        SubscribeVO sVO = new SubscribeVO();
        sVO.setResourceId(resourcdId);
        Long userId = Long.valueOf(userUtils.getCurrentUserSSO().getId());
        Organization organization = userService.getUserOrganizationByUserId(userId);
        String deptName = organization.getDeptName();
        if(StringUtils.isEmpty(deptName)){
            LOG.error("当前用户，没有配置所属部门,当前用户为 {}",userUtils.getCurrentUserName());
            throw new Exception("当前用户，没有配置所属部门");
        }
        sVO.setDeptName(deptName);   //TODO:从UserHolder里面获取部门字段
        sVO.setShareMethod(rcPO.getShareMethod());
        sVO.setEndDate("2099-12-31");

        int dbShareMethod = 0;
        int formatType = rcPO.getFormatType();
        if(ResourceTools.FormatType.getFormatType(formatType)== DB){
            //1数据库，2文件下载，3webservice服务
            dbShareMethod = 1;
            if(rcPO.getShareMethod()==3){
                dbShareMethod = 2;
            }
            List<ResourceColumnPO> inputList = resourceColumnDAO.getColumnByResourceId(resourcdId);
            List<ResourceColumnVO> inputIOList = new ArrayList<ResourceColumnVO>();
            for(ResourceColumnPO rcolumnPO:inputList){
                ResourceColumnVO rcVO = new ResourceColumnVO();

                rcVO.setColumnId(rcolumnPO.getId());
                rcVO.setResourceId(rcolumnPO.getResourceId());
                rcVO.setColName(rcolumnPO.getColName());
                rcVO.setColType(rcolumnPO.getColType());
                rcVO.setDateFormat(rcolumnPO.getDateFormat());
                rcVO.setUniqueFlag(rcolumnPO.getUniqueFlag());
                rcVO.setRequiredFlag(rcolumnPO.getRequiredFlag());
                rcVO.setTableColCode(rcolumnPO.getTableColCode());
                rcVO.setTableColType(rcolumnPO.getTableColType());
                inputIOList.add(rcVO);
            }
            sVO.setInputDbioList(inputIOList);
            sVO.setOutputDbioList(inputIOList);
        }else if(ResourceTools.FormatType.getFormatType(formatType)== SERVICE_INTERFACE){
            Long bindServerId = rcPO.getBindServiceId();
            ServicePO servicePO = serviceDAO.getServiceById(bindServerId);
            if(servicePO==null){
                LOG.error("没有查找到订阅资源对应的服务信息，资源名称为 {}",rcPO.getName());
                throw new Exception("没有查找到订阅资源对应的服务信息，资源名称为 "+rcPO.getName());
            }
            String url = servicePO.getUrl();
            if(StringUtils.isNotEmpty(url)){
                sVO.setServiceUrl(url);
            }
        }
        sVO.setDbShareMethod(dbShareMethod);
        return sVO;
    }

    /*在前置机从中心库复制表信息到前置机*/
    @Transactional(rollbackFor = Exception.class)
    public int copyDBTable(String user, Long subscribeId)throws Exception{

        int destMetaId = 0;
        SubscribePO subscribePO = subscribeDAO.getById(subscribeId);
        Long resourceId = subscribePO.getResourceId();
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        Long bindTableId = rcPO.getBindTableId();

        //获取字段信息，方便后面创建表格
        List<SubscribeDbioPO> subOutputList = subscribeDbioDAO.getBySubscribeIdAndType(
                subscribePO.getId(), "input");
        List<String> propertyList = new ArrayList<String>();
        List<Long> columnIdList = new ArrayList<Long>();
        if(CollectionUtils.isNotEmpty(subOutputList)){
            for(SubscribeDbioPO sPO : subOutputList){
                //从元数据拿取表格，在将数据存储到目标表格
                columnIdList.add(sPO.getColumnId());
            }
        }

        List<String> columnNameList = new ArrayList<String>();
        //必须包含字段：数据批次ds_batch,数据同步时间ds_sync_time，数据同步操作ds_sync_flag
        columnNameList.add("ds_batch");
        columnNameList.add("ds_sync_time");
        columnNameList.add("ds_sync_flag");
        if(CollectionUtils.isNotEmpty(columnIdList)){
            for(Long columnId: columnIdList){
                ResourceColumnPO resourceColumnPO = resourceColumnDAO.getColumnById(columnId);
                if(rcPO!=null){
                    columnNameList.add(resourceColumnPO.getTableColCode());
                }
            }
        }

        //获取元数据字段
        List<MetadataField> metadataOriginFields = new ArrayList<MetadataField>();
        ResultBean<QueryMetadataFieldsResult> metaResult = metacubeCatalogService.getMetadataFieldsByMetaId(bindTableId.intValue());
        if(metaResult.isSuccess()){
            metadataOriginFields = metaResult.getData().getMetadataField();
        }else{
            throw new Exception("审批订阅 "+subscribePO.getSubNo() +"失败，订阅资源所绑定的表结构获取失败，" + metaResult.getMsg());
        }
        List<MetadataField> metaFinalFields = new ArrayList<MetadataField>();
        for(MetadataField field:metadataOriginFields){
            for(String columnName : columnNameList){
                //达梦,oracle,mysql 存在大小写的差异
                if(StringUtils.equalsIgnoreCase(columnName, field.getColName())){
                    metaFinalFields.add(field);
                }
            }
        }

        //获取前置机数据数据库，并且创建表格
        MetadataTable meta = new MetadataTable();
        meta.setMetaid(bindTableId.intValue());  //需要复制的元数据ID


        //订阅部门绑定在rc_tm表中绑定数据
        TerminalManagePO tPO = terminalManageDAO.getTerminalManageRecordByDeptId(subscribePO.getDeptId());
        if(tPO==null){
            throw new Exception("审批订阅 "+subscribePO.getSubNo() +"异常，资源订阅方还没有配置前置机，请在前置机管理配置");
        }
        Long deptId =Long.valueOf(tPO.getDeptFinalId());
        Long dsId = Long.valueOf(tPO.getTmDBId());
        //参数dsId和storeDatabase 存储一个值
        if(StringUtils.isNotBlank(tPO.getSchemaId())){
            meta.setSchemeId(Long.valueOf(tPO.getSchemaId()));
        }else if(StringUtils.isNotBlank(tPO.getTmDBId())){
            meta.setSchemeId(Long.valueOf(tPO.getTmDBId()));
        }else{
            throw new Exception("审批订阅 "+subscribePO.getSubNo() +"异常，前置机配置异常核对前置机参数。");
        }

        //记录上次订阅最大metaId
        Long lastMetaId = subscribeDAO.getDetpSubscribeMaxMetaId(bindTableId, deptId, resourceId);
        meta.setPreviousMetaid(lastMetaId==null?0:lastMetaId.intValue());


        //现有软件需要支持 数据库之间相互订阅
//        //判断资源绑定数据库类型和前置机配置数据库类型是否一致
//        if(!StringUtils.equalsIgnoreCase(rcPO.getFormatInfo(), tPO.getTmDBType())){
//            LOG.error(DateTools.formatDate(new Date())+"数据库类型不一致,资源数据库类型-"+rcPO.getFormatInfo().toUpperCase()
//                    +"前置机数据库类型-"+tPO.getTmDBType().toUpperCase());
//            throw new Exception("在部门前置机上创建数据表失败,资源绑定数据库和前置机配置数据库不一致: 资源绑定数据库类型-"+rcPO.getFormatInfo().toUpperCase()
//                    +"，前置机数据库类型-"+tPO.getTmDBType().toUpperCase());
//        }

        //获取前置机数据数据库，并且创建表格
        //2019/3/11       1.前置机绑定的数据库为当前用户部门，有权限的数据库，metaId中不存在部门一说
        ResultBean<SubscribeCrtTbResult> tbResult = metacubeCatalogService.createTableBySubscribe(user, meta, metaFinalFields);
        if(tbResult.isSuccess()){
            destMetaId = tbResult.getData().getMetaId();
        }else{
            String tmInfo = "前置机地址："+ tPO.getTmIP() + "，前置机名称："+ tPO.getTmName() + " ,订阅方部门："+tPO.getDeptName()
                    + " ,数据库类型：" + tPO.getTmDBType() + " ,绑定数据库：" + tPO.getTmDBName();

            LOG.error(DateTools.formatDate(new Date())+"-在部门前置机上创建数据表失败,失败原因: "+ tbResult.getMsg());
            throw new Exception("审批订阅 "+subscribePO.getSubNo() +"异常，在部门前置机上创建数据表失败,失败原因: "+ tbResult.getMsg()+"。" +
                    tmInfo);
        }

        //创建完表格时候，对中心库进行授权，
        // 2019/03/11   1.  前置机配置的时候 已经通过权限过滤，部门配置在有权限数据库下面，
        //              2.  订阅的时候需要将权限配置成订阅部门，需要强制授权一下。
        ResultBean<Boolean> baseResult = metacubeCatalogService.authorizedTableForUser(subscribePO.getCreator(), bindTableId.intValue(),
                tPO.getDeptCode(), SUBSCRIBED);
        if(!baseResult.isSuccess()){
            throw new Exception("审批订阅 "+subscribePO.getSubNo() +"异常，资源对应数据表授权失败，失败原因："+baseResult.getMsg());
        }
        return destMetaId;
    }


    private List<ResourceColumnVO> getRCVoList(List<SubscribeDbioPO> subList){
        if(CollectionUtils.isEmpty(subList)){
            return null;
        }
        List<ResourceColumnVO> rcList = new ArrayList<ResourceColumnVO>();
        for(SubscribeDbioPO subPO:subList){
            ResourceColumnVO rcVO = new ResourceColumnVO();
            ResourceColumnPO rcPO = resourceColumnDAO.getColumnById(subPO.getColumnId());
            if(rcPO==null){
                return null;
            }
            rcVO.setColumnId(rcPO.getId());
            rcVO.setId(subPO.getId());
            rcVO.setResourceId(rcPO.getResourceId());
            rcVO.setColName(rcPO.getColName());
            rcVO.setColType(rcPO.getColType());
            rcVO.setDateFormat(rcPO.getDateFormat());
            rcVO.setUniqueFlag(rcPO.getUniqueFlag());
            rcVO.setRequiredFlag(rcPO.getRequiredFlag());
            rcVO.setTableColCode(rcPO.getTableColCode());
            rcVO.setTableColType(rcPO.getTableColType());

            //处理返回给前端脱敏的特殊字段
            int dataMaskingFlag = 0;
            if(!rcPO.getUniqueFlag()) {
                if (StringUtils.isNotEmpty(subPO.getDataMaskingType())) {
                    rcVO.setDataMaskingType(subPO.getDataMaskingType());
                    rcVO.setDataStartIndex(subPO.getDataStartIndex());
                    rcVO.setDataLength(subPO.getDataLength());
                    dataMaskingFlag = 1;
                } else {
                    Long resourceId = rcPO.getResourceId();
                    ResourceConfigPO resourceCfgPO = resourceConfigDAO.getConfigById(resourceId);
                    String dbType = resourceCfgPO.getFormatInfo();

                    if (DataMaskingTypeEnum.verifyDataMaskingType(dbType, rcPO.getTableColType())) {
                        dataMaskingFlag = 1;
                        rcVO.setDataStartIndex(0);  //默认起始位置
                        rcVO.setDataLength(1);    //默认长度
                    }
                }
            }
            rcVO.setDataMaskingFlag(dataMaskingFlag);
            rcList.add(rcVO);
        }
        return rcList;
    }

    @Override
    public SubscribeVO getSubscribeById(Long id) throws Exception {

        if(id==null || id==0L){
            throw new Exception("订阅信息为空");
        }
        SubscribePO sPO = subscribeDAO.getById(id);
        if(sPO==null){
            throw new Exception("订阅信息记录中不存在");
        }

        SubscribeVO sVO = new SubscribeVO();

        sVO.setId(sPO.getId());
        sVO.setSubNo(sPO.getSubNo());
        sVO.setResourceId(sPO.getResourceId());
        sVO.setSubscribeReason(sPO.getSubscribeReason());
        sVO.setDeptName(sPO.getDeptName());  //获取资源名称
        sVO.setShareMethod(sPO.getShareMethod());
        sVO.setEndDate(DateTools.formatDate(sPO.getEndDate(), "yyyy-MM-dd"));

        sVO.setSubscribeUserName(sPO.getSubscribeUserName());
        sVO.setSubscribeTime(DateTools.formatDate(sPO.getCreateTime()));
        sVO.setStatus(SubscribeStatusEnum.getStatusZH(sPO.getStatus()));

        String approver_name = sPO.getApproverName();
        if(StringUtils.isNotEmpty(approver_name)){
            sVO.setApproverName(approver_name);
        }
        String approver = sPO.getApprover();
        if(StringUtils.isNotEmpty(approver)){
            sVO.setApprover(approver);
        }
        String suggestion = sPO.getSuggestion();
        if(StringUtils.isNotEmpty(suggestion)){
            sVO.setSuggestion(suggestion);
        }
        Date approveTime = sPO.getApproveTime();
        if(approveTime!=null){
            sVO.setApproveTime(DateTools.formatDate(approveTime));
        }

        int dbShareMethod = 0;

        /*共享方式：1数据库，2文件下载，3webservice服务*/
        int shareMethod = sPO.getShareMethod();
        if(shareMethod==1 || shareMethod==3){
            dbShareMethod = 1;
            List<SubscribeDbioPO> subList = subscribeDbioDAO.getBySubscribeIdAndType(
                    id, "input");
            sVO.setInputDbioList(getRCVoList(subList));
            List<SubscribeDbioPO> subOutputList = subscribeDbioDAO.getBySubscribeIdAndType(
                    id, "output");
            sVO.setOutputDbioList(getRCVoList(subOutputList));

            if(shareMethod==3) {
                //是数据分享方式，如果为数据类型，只要订阅成功才能显示url和key信息
                Long resourceId = sVO.getResourceId();
                ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
                if(rcPO==null){
                    throw new Exception("信息资源记录中不存在该资源");
                }
                int formatType = rcPO.getFormatType();
                if(ResourceTools.FormatType.getFormatType(formatType)==DB){
                    dbShareMethod = 2;
                }

                if(ResourceTools.FormatType.getFormatType(formatType)==DB &&
                    !StringUtils.equals(sPO.getStatus(),SUCCESS.getStatus())){
                    //不做任何操作
                }else if(StringUtils.equals(sPO.getStatus(),SUCCESS.getStatus())){
                    sVO.setServiceUrl(sPO.getServiceUrl());
                    sVO.setSubKey(sPO.getSubKey());
                }
            }else{
                Long deptId = sPO.getDeptId();
                TerminalManagePO tPO = terminalManageDAO.getTerminalManageRecordByDeptId(deptId);
                if(tPO!=null){
                    sVO.setTerminalName(tPO.getTmName());
                    sVO.setTernimalDbName(tPO.getTmDBName());
                }else{
                    throw new Exception("资源订阅方还没有配置前置机，请在前置机管理配置");
                }
            }
        }
        sVO.setDbShareMethod(dbShareMethod);
        return sVO;
    }

    /* dbShareMethod 数据库分享方式：0 非数据库资源， 1数据库-数据库分享， 2数据库-服务分享
    *  shareMethod 共享方式：1数据库，2文件下载，3webservice服务*/
    private int getDbShareMethod(Long resourceId){
        int dbShareMethod = 0;
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        if(rcPO!=null) {
            int formatType = rcPO.getFormatType();
            if (ResourceTools.FormatType.getFormatType(formatType) == DB) {
                //1数据库，2文件下载，3webservice服务
                dbShareMethod = 1;
                if (rcPO.getShareMethod() == 3) {
                    dbShareMethod = 2;
                }
            }
        }
        return dbShareMethod;
    }

    /*增加订阅*/
    @Override
    public void addSubscribe(String user, SubscribeVO subscribeVO)throws Exception {
        //去重校验
        Long resourceId = subscribeVO.getResourceId();
        if(resourceId==null || resourceId==0L){
            throw new Exception("订阅的资源ID为空");
        }
        if(getSameFlag(user,subscribeVO.getResourceId())){
            throw new Exception("该用户已经配置订阅流程，流程还未处理或者上一次还在有效期内");
        }

        //入库新订阅数据:订阅是一锤子买卖，需要重新订阅资源
        Long id = subscribeVO.getId();
        SubscribePO po = transferSubscribeVOToPO(subscribeVO);
        po.setRentId(userUtils.getCurrentUserRentId());

        //存储订阅部门ID信息
        Long userId = Long.valueOf(userUtils.getCurrentUserId());
        Organization organization = userService.getUserOrganizationByUserId(userId);
        po.setDeptId(organization.getId());

         /*共享方式：1数据库，2文件下载，3webservice服务*/
        int shareMethod = subscribeVO.getShareMethod();
        if(shareMethod==3){
            String uuid = CommonUtils.generateUUID();
            po.setSubKey(uuid);

            //都是服务方式时候，根据资源类型，如果是数据库类型，需要从获取url，服务方式直接保存就可以
            ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
            if(rcPO==null){
                throw new Exception("信息资源记录中不存在该资源");
            }
            int formatType = rcPO.getFormatType();
            if(ResourceTools.FormatType.getFormatType(formatType)== DB){
                //TODO:需要从系统里面读取 服务地址url信息
                if(StringUtils.isEmpty(webServiceUrl)){
                    throw new Exception("数据库服务共享方式配置未完成，请先运行idatrixWebService服务，设置好参数web.service.url");
                }
                else if(!StringUtils.contains(webServiceUrl, "wsdl")){
                    throw new Exception("配置文件中init.properties中参数web.service.url配置错误，" +
                            "配置样例 web.service.url=http://10.0.0.122:8888/webservice/idatrixWebService?wsdl");
                }
                po.setServiceUrl(webServiceUrl);
            }else{

                Long bindServerId = rcPO.getBindServiceId();
                String url = serviceDAO.getServiceById(bindServerId).getUrl(); //getInitConfig已经获取过，这里直接用
                if(StringUtils.isNotEmpty(url)){
                    po.setServiceUrl(url);
                }
            }
        }

        Long seqNum = sequenceNumberManager.getSubscribeSeqNum();
        po.setSeq(seqNum);
        NumberFormat f=new DecimalFormat("00000000");
        String subNum = CommonConstants.PREFIX_SUBSCRIBE+ f.format(seqNum);
        po.setSubNo(subNum);

        if(id==null || id==0L){
            po.setStatus(SubscribeStatusEnum.WAIT_APPROVE.getStatus());
            subscribeDAO.insert(po);
            id = po.getId();
        }

        //如果是数据库类型，需要入库column信息
        if(shareMethod==1 || shareMethod==3){
            List<ResourceColumnVO> rcList = subscribeVO.getInputDbioList();
            saveSubscribeDBIO(user, id, "input", rcList);

            List<ResourceColumnVO> rcOutputList = subscribeVO.getOutputDbioList();
            saveSubscribeDBIO(user, id, "output", rcOutputList);
        }
    }

    /*订阅概览*/
    @Override
    public ResultPager<SubscribeOverviewVO> queryOverview(Map<String, String> con, Integer pageNum, Integer pageSize) {
        return querySubscribe(con, pageNum, pageSize);
    }

    /*获取订阅审批情况概览*/
    @Override
    public ResultPager<SubscribeOverviewVO> queryWaitApproveOverview(Map<String, String> con, Integer pageNum, Integer pageSize) {
        //判断当前状态是否可以审批，根据
        String approver = con.get("user");
        con.remove("user");
        con.put("approver", approver);
        return querySubscribe(con, pageNum, pageSize);
    }

    /*获取已经评审订阅概览*/
    @Override
    public ResultPager<SubscribeOverviewVO> queryProcessedApproveOverview(Map<String, String> con, Integer pageNum, Integer pageSize) {
        String approver = con.get("user");
        con.remove("user");
        con.put("approver", approver);
        con.put("processedApprove", "true");  //只要配置数值就可以
        return querySubscribe(con, pageNum, pageSize);
    }


    /*获取已经评审成功订阅概览*/
    @Override
    public ResultPager<SubscribeOverviewVO> queryApproverSuccessOverview(Map<String, String> con, Integer pageNum, Integer pageSize) {
        String approver = con.get("user");
        con.remove("user");
        con.put("approver", approver);

        //con.put("subStatus", "success");
        //maintainStatus
        con.put("maintainStatus", "failed");
        con.put("processedApprove", "true");  //只要配置数值就可以
        return querySubscribe(con, pageNum, pageSize);
    }

    @Override
    public void processApprove(String user, Long subscribeId, String action, String suggestion) throws Exception {
        if(subscribeId==null||subscribeId==0L){
            throw new Exception("提交审核的订阅序号为空");
        }
        SubscribePO subPO = subscribeDAO.getById(subscribeId);
        if(subPO==null){
            throw new Exception("不存在提交审核的订阅记录");
        }
        //权限校验
        if(!StringUtils.equals(user, subPO.getApprover())){
            throw new Exception("当前E用户对该订阅操作没有审批权限");
        }
        String status = null;
        if(StringUtils.equals(action, "agree")){
            status = "success";
        }else{
            status = "failed";
        }

        //通过元数据借口创建的数据库
        //TODO:需要检测是否元数据是否已经创建，避免部门重复订阅出现创建失败
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(subPO.getResourceId());
        Long bindTableId = rcPO.getBindTableId();
        String subscribeUser = subPO.getCreator();

        Long rentId = userUtils.getCurrentUserRentId();

        if(subPO.getShareMethod()==1 && StringUtils.equals(status, "success")) {

            int metaId = copyDBTable(subscribeUser, subscribeId);
            Long resourceId = subPO.getResourceId();
            Long srcMetaId = rcPO.getBindTableId();

            /*  第三方平台接入，执行过的交换任务需要能够进行查看和管理，将数据存储到交换任务表中，同时
             *   造成问题交换任务和订阅流程不能一一对应，但是可以根据SUB00000029 sub_no对应
             */
            // SubTaskPO subTaskPO = subTaskDAO.getById(subPO.getId());  //调整 2018/11/10
            SubTaskPO subTaskPO = subTaskDAO.getBySubTaskId(subPO.getSubNo());
            if(subTaskPO==null){
                subTaskPO = new SubTaskPO();
                subTaskPO.setEndTime(subPO.getEndDate());
                subTaskPO.setTaskType(subPO.getShareMethod() == 2 ? CommonConstants.DATA_TYPE_FILE : CommonConstants.DATA_TYPE_DB);
                //subTaskPO.setId(subPO.getId());
                subTaskPO.setStatus(CommonConstants.WAIT_IMPORT);
                subTaskPO.setRentId(rentId);
                subTaskPO.setSubTaskId(subPO.getSubNo());
                subTaskPO.setSrcMetaId(srcMetaId);
                subTaskPO.setDestMetaId(new Long(metaId));
                subTaskPO.setCreator(subscribeUser);
                subTaskPO.setModifier(subscribeUser);
                subTaskPO.setCreateTime(new Date());
                subTaskPO.setModifyTime(new Date());
                subTaskDAO.insert(subTaskPO);
            }else{
                subTaskPO.setEndTime(subPO.getEndDate());
                subTaskPO.setTaskType(subPO.getShareMethod() == 2 ? CommonConstants.DATA_TYPE_FILE : CommonConstants.DATA_TYPE_DB);
                //subTaskPO.setId(subPO.getId());
                subTaskPO.setStatus(CommonConstants.WAIT_IMPORT);
                subTaskPO.setRentId(rentId);
                subTaskPO.setSubTaskId(subPO.getSubNo());
                subTaskPO.setSrcMetaId(srcMetaId);
                subTaskPO.setDestMetaId(new Long(metaId));
                subTaskPO.setCreator(subscribeUser);
                subTaskPO.setModifier(subscribeUser);
                subTaskPO.setCreateTime(new Date());
                subTaskPO.setModifyTime(new Date());
                subTaskDAO.updateById(subTaskPO);
            }

            //更新订阅数量
            resourceStatiscsService.increaseSubCount(subPO.getResourceId());
        }

        subPO.setStatus(status);
        subPO.setApproveTime(new Date());
        subPO.setSuggestion(suggestion);

        subPO.setModifier(subscribeUser);
        subPO.setModifyTime(new Date());
        subscribeDAO.updateById(subPO);
    }

    @Override
    public void processApprove(String user, SubscribeApproveRequestVO subscribeVO) throws Exception {
        List<ResourceColumnVO> inputDbioList = subscribeVO.getInputDbioList();
        if(CollectionUtils.isNotEmpty(inputDbioList)){
            for(ResourceColumnVO rcVO:inputDbioList){
                SubscribeDbioPO sdPO = subscribeDbioDAO.getById(rcVO.getId());
                if(StringUtils.isNotEmpty(rcVO.getDataMaskingType())){
                    sdPO.setDataMaskingType(rcVO.getDataMaskingType());
                    sdPO.setDataLength(rcVO.getDataLength());
                    sdPO.setDataStartIndex(rcVO.getDataStartIndex());
                    subscribeDbioDAO.updateById(sdPO);
                }
            }
        }
        processApprove(user, subscribeVO.getId(), subscribeVO.getAction(), subscribeVO.getSuggestion());
    }

    @Override
    public void batchProcessApprove(String user, List<Long> subscibeIds) throws Exception {
        if(subscibeIds==null||subscibeIds.size()==0){
            throw new Exception("批量处理数据为空");
        }
        for(Long id:  subscibeIds){
            SubscribePO po = subscribeDAO.getById(id);
            if(po==null){
                throw new Exception("不存在提交审核的订阅记录");
            }
            String status = po.getStatus();
            if(!StringUtils.equals(status, "wait_approve")){
                throw new Exception("当前订阅流程不可以审核");
            }
        }
        for(Long id:  subscibeIds){
            processApprove(user, id, "agree", "同意。");
        }
    }

    @Override
    public void stopSubscribe(Long subscirbeId) throws Exception {
        SubscribePO po = subscribeDAO.getById(subscirbeId);
        if(po==null || !StringUtils.equals(po.getStatus(), "success")){
            throw new Exception("不能终止订阅，订阅为空或者订阅未审核成功");
        }

        /*共享方式：1数据库，2文件下载，3webservice服务*/
        if(po.getShareMethod()!= 1){

        }else{
            /*  第三方平台接入，执行过的交换任务需要能够进行查看和管理，将数据存储到交换任务表中，同时
             *   造成问题交换任务和订阅流程不能一一对应，但是可以根据SUB00000029 sub_no对应
             */
            // SubTaskPO taskPo = subTaskDAO.getById(subscirbeId);  //调整 2018/11/10
            SubTaskPO taskPo = subTaskDAO.getBySubTaskId(po.getSubNo());

            if(taskPo==null){
                throw new Exception("订阅交换任务信息为空");
            }

            if(StringUtils.isEmpty(taskPo.getEtlSubscribeId())){
                //还未启动交换任务，将任务状态切换成STOP_IMPORT，不会创建交换任务
                taskPo.setStatus(CommonConstants.STOP_IMPORT);
            }else{
                if(exchangeTask.stopSubscribeTask(po.getCreator(), taskPo.getEtlSubscribeId())){
                    taskPo.setStatus(CommonConstants.STOP_IMPORT);
                }
            }
            subTaskDAO.updateById(taskPo);
        }

        po.setStatus("stop");
        subscribeDAO.updateById(po);
    }

    @Override
    public void resumeSubscibe(Long subscribeId) throws Exception {
        SubscribePO po = subscribeDAO.getById(subscribeId);
        if(po==null || !StringUtils.equals(po.getStatus(), "stop")){
            throw new Exception("不能恢复订阅，订阅为空或者未终止过该订阅");
        }

        /*共享方式：1数据库，2文件下载，3webservice服务*/
        if(po.getShareMethod()!= 1){

        }else{

            /*  第三方平台接入，执行过的交换任务需要能够进行查看和管理，将数据存储到交换任务表中，同时
             *   造成问题交换任务和订阅流程不能一一对应，但是可以根据SUB00000029 sub_no对应
             */
            // SubTaskPO taskPo = subTaskDAO.getById(subscirbeId);  //调整 2018/11/10
            SubTaskPO taskPo = subTaskDAO.getBySubTaskId(po.getSubNo());

            if(StringUtils.isEmpty(taskPo.getEtlSubscribeId())){
                //还未启动交换任务，将任务状态切换成WAIT_IMPORT，能够继续创建交换任务
                taskPo.setStatus(CommonConstants.WAIT_IMPORT);
            }else{
                if(exchangeTask.startSubscribeTask(po.getCreator(), taskPo.getEtlSubscribeId())){
                    taskPo.setStatus(CommonConstants.IMPORTING);
                }
            }
            subTaskDAO.updateById(taskPo);
        }

        po.setStatus("success");
        subscribeDAO.updateById(po);
    }

    @Override
    public SubscribeWebServiceVO getWebserviceDescription(Long subscribeId) throws Exception {
        SubscribePO po = subscribeDAO.getById(subscribeId);
        if(po==null || !StringUtils.equals(po.getStatus(), "success")){
            throw new Exception("获取数据库服务失败，订阅为空或者订阅未审核成功");
        }
        String subKey = po.getSubKey();
        SubscribeWebServiceVO webVO = new SubscribeWebServiceVO();
        webVO.setSubKey(subKey);
        webVO.setWebUrl(webServiceUrl);
        return webVO;
    }


    private ResultPager<SubscribeOverviewVO> querySubscribe(Map<String, String> con, Integer pageNum, Integer pageSize){

        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);

        //根据订阅类型来查询
        String shareType = con.get("type");
        if(StringUtils.isNotEmpty(shareType)){
            shareType = shareType.toLowerCase();
            if(StringUtils.equals("db", shareType)){
                con.put("shareMethod", "1");
            }else if(StringUtils.equals("file", shareType)){
                con.put("shareMethod", "2");
            }else if(StringUtils.equals("service", shareType)){
                con.put("shareMethod", "3");
            }
        }

        List<SubscribePO> subsribeList = subscribeDAO.queryByCondition(con);
        List<SubscribeOverviewVO> rcVOList = transferSubscribePoToOverviewVO(subsribeList);

        //用PageInfo对结果进行包装
        PageInfo<SubscribePO> pi = new PageInfo<SubscribePO>(subsribeList);
        Long totalNums = pi.getTotal();
        ResultPager<SubscribeOverviewVO> rp = new ResultPager<SubscribeOverviewVO>(pi.getPageNum(), totalNums, rcVOList);
        return rp;
    }

    private List<SubscribeOverviewVO> transferSubscribePoToOverviewVO(List<SubscribePO> poList){

        if(CollectionUtils.isEmpty(poList)){
            return null;
        }

        List<SubscribeOverviewVO> soList = new ArrayList<SubscribeOverviewVO>();
        Map<Long, ResourceConfigPO>  resourceIdMap = new HashMap<Long, ResourceConfigPO>();
        for(SubscribePO po : poList){

            LOG.info("订阅状态信息：{}", po.toString());
            SubscribeOverviewVO soVO = new SubscribeOverviewVO();

            soVO.setResourceId(po.getResourceId());
            soVO.setSubNo(po.getSubNo());
            soVO.setSubscribeUserName(po.getCreator());
            soVO.setSubscribeStatus(SubscribeStatusEnum.getStatusZH(po.getStatus()));
            soVO.setEndTime(DateTools.formatDate(po.getEndDate(),"yyyy-MM-dd"));
            soVO.setId(po.getId());
            soVO.setApplyDate(DateTools.formatDate(po.getCreateTime()));
            soVO.setShareMethod(po.getShareMethod());
            soVO.setSubscribeDeptName(po.getDeptName());
            soVO.setDbShareMethod(getDbShareMethod(po.getResourceId()));
            soVO.setApprover(po.getApprover());

            String status = po.getStatus();
            if(StringUtils.isNotEmpty(status)){
                soVO.setStatus(status);
            }
            String suggestion = po.getSuggestion();
            if(StringUtils.isNotEmpty(suggestion)){
                soVO.setSuggestion(suggestion);
            }
            Date approveTime = po.getApproveTime();
            if(approveTime!=null){
                soVO.setApproveTime(DateTools.formatDate(approveTime));
            }

            //从资源里面获取信息
            Long reousrceId = po.getResourceId();
            ResourceConfigPO rcPO = resourceIdMap.get(reousrceId);
            if(rcPO==null){
                rcPO = resourceConfigDAO.getConfigById(reousrceId);
                if(rcPO==null){
                    LOG.error("订阅的资源不存在，请在rc_resource表后台核对, 资源ID为{}",reousrceId);
                    continue;
                }
                resourceIdMap.put(reousrceId, rcPO);
            }
            soVO.setResourceType(ResourceFormatTypeEnum.getFormatType(rcPO.getFormatType()));
            soVO.setCode(rcPO.getCode());
            soVO.setDeptName(rcPO.getDeptName());
            soVO.setName(rcPO.getName());
            soList.add(soVO);
        }
        return soList;
    }
}

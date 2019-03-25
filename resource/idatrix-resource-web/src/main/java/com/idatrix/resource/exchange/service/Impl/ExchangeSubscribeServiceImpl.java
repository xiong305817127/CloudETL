package com.idatrix.resource.exchange.service.Impl;

import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.common.cache.SequenceNumberManager;
import com.idatrix.resource.common.utils.*;
import com.idatrix.resource.datareport.dao.DataUploadDAO;
import com.idatrix.resource.datareport.dao.DataUploadDetailDAO;
import com.idatrix.resource.datareport.po.DataUploadDetailPO;
import com.idatrix.resource.exchange.dao.ExchangeSubscribeInfoDAO;
import com.idatrix.resource.exchange.dao.ExchangeSubscribeTaskDAO;
import com.idatrix.resource.exchange.exception.InnerTerminalConfigException;
import com.idatrix.resource.exchange.exception.RequestDataException;
import com.idatrix.resource.exchange.po.ExchangeSubscribeInfoPO;
import com.idatrix.resource.exchange.po.ExchangeSubscribeTaskPO;
import com.idatrix.resource.exchange.service.IExchangeSubscribeService;
import com.idatrix.resource.exchange.vo.request.ETLFileInfo;
import com.idatrix.resource.exchange.vo.request.ExchangeSubscribeVO;
import com.idatrix.resource.exchange.vo.request.LocalFileInfo;
import com.idatrix.resource.taskmanage.dao.SubTaskDAO;
import com.idatrix.resource.taskmanage.po.SubTaskPO;
import com.idatrix.resource.terminalmanage.dao.TerminalManageDAO;
import com.idatrix.resource.terminalmanage.po.TerminalManagePO;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.idatrix.unisecurity.api.service.UserService;
import com.ys.idatrix.cloudetl.subscribe.api.dto.CreateJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.SubscribeResultDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.FileTransmitDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.OutputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.SearchFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.*;
import com.ys.idatrix.cloudetl.subscribe.api.service.SubscribeService;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import com.ys.idatrix.metacube.api.beans.MetadataDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.dataswap.*;
import com.ys.idatrix.metacube.api.service.MetadataToDataSwapService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ys.idatrix.metacube.api.beans.dataswap.AuthorizedFlowType.SUBSCRIBED;


/**
 * 神马订阅交换实现
 */

@Transactional
@PropertySource("classpath:init.properties")
@Service("exchangeSubscribeService")
public class ExchangeSubscribeServiceImpl implements IExchangeSubscribeService {

    private final Logger LOG= org.slf4j.LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    private RDBDao rdbDao;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private TerminalManageDAO terminalManageDAO;

    @Autowired(required=false)
    private MetadataToDataSwapService metacubeCatalogService;

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private SequenceNumberManager sequenceNumberManager;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ExchangeSubscribeInfoDAO exchangeInfoDAO;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ISystemConfigService systemConfigService;

    @Autowired
    private DataUploadDAO dataUploadDAO;

    @Autowired
    private DataUploadDetailDAO dataUploadDetailDAO;

    @Autowired
    private HdfsUnrestrictedService hdfsUnDaoHessian;

    @Autowired
    private ExchangeSubscribeTaskDAO exchangeSubscribeTaskDAO;

    @Autowired
    private SubTaskDAO subTaskDAO;


    /*sftp服务器名称*/
    @Value("${sftp.service.name}")
    private String sftpServiceName;
    /*SFTP服务器端口信息*/
    @Value("${sftp.service.port}")
    private String sftpServicePort;
    /*SFTP服务器IP地址信息*/
    @Value("${sftp.service.ip}")
    private String sftpServiceIp;
    /*SFTP服务器超级用户名*/
    @Value("${sftp.service.user}")
    private String sftpServiceUser;
    /*SFTP服务器超级密码*/
    @Value("${sftp.service.passwd}")
    private String sftpServicePasswd;
    /*SFTP服务器根路径*/
    @Value("${sftp.service.dir}")
    private String sftpServiceDir = "/data";
    /*和第三方系统对接资源信息编码前缀*/
    @Value("${resource.code.prefix}")
    private String resourceCodePrefix = "330003";
    /*ETL处理时候将HDFS文件下载到本地然后进行传输*/
    @Value("${etl.local.dir}")
    private String etlLocalDir="/data/sftp/";


    /**
     *  根据获取的数据库资料测试数据库联调和表权限
     * @param exchangeVO type表示数据库类型：接口定义为DM_DATABASE、Oracle、MySql、PostgreSql
     * @return
     * @throws Exception
     */
    private boolean getDbLinkTest(ExchangeSubscribeVO exchangeVO) throws Exception{

        //有效值检测
//        if(StringUtils.isEmpty(exchangeVO.getDbType()) ||
//            StringUtils.isEmpty(exchangeVO.getDbIp()) ||
//                StringUtils.isEmpty(exchangeVO.getDbName()) ||
//                    StringUtils.isEmpty(exchangeVO.getDbUser()) ||
//                        StringUtils.isEmpty(exchangeVO.getDbPassword()) || exchangeVO.getDbPort()!=0L){
//            throw new DbLinkException("数据库参数不完整");   //传递参数存在控制
//        }
//
//        RDBConfiguration config = new RDBConfiguration(exchangeVO.getDbUser(),
//                exchangeVO.getDbPassword(),
//                exchangeVO.getDbType(),
//                exchangeVO.getDbIp(),
//                exchangeVO.getDbPort().toString(),
//                exchangeVO.getDbName());
//        LOG.info(">>>>testLink begin :username =" + config.getUsername() + "| type =" + config.getType() + "|ip" + config.getIp());
//        LOG.info(">>>>testLink begin :port =" + config.getPort() + "| password =" + config.getPassword());
//
//        try {
//            SqlQueryResult result = rdbDao.testDBLink(config);
//            LOG.info("testLink test db result:" + JSON.toJSONString(result));
//            boolean canLinked = result.isSuccess();
//            if (canLinked) {
//                return true;
//            }
//            throw new Exception();   //网络链接测试失败
//        } catch (Exception e) {
//            e.printStackTrace();
//            LOG.error("testLink error :" + e.getMessage());
//            throw new RuntimeException(e.getMessage());
//        }
        return true;
    }


    /**
     *   将接口的数据库类型转换成系统能够处理的数据库类型
     * @param type
     * @return
     */
    String getLocalDbType(String type){

        //数据库匹配
//            MONGODB(1, "MONGODB"),
//            ORACLE(2, "ORACLE"),
//            MYSQL(3, "MYSQL"),
//            HIVE(4, "HIVE"),
//            HBASE(5, "HBASE"),
//            HDFS(6, "HDFS"),
//            FTP(7, "Ftp"),
//            POSTGRESQL(8, "POSTGRESQL"),
//            SQLSERVER(9, "SQLSERVER"),
//            DB2(10, "DB2"),
//            SYBASE(11, "SYBASE"),
//            ACCESS(12, "ACCESS"),
//            HIVE2(13, "HIVE2"),
//            DM7(14, "DM7");

        String dbType = null;
        if(StringUtils.equalsIgnoreCase(type, "dm_database")){
            dbType = "DM7";
        }else if(StringUtils.equalsIgnoreCase(type, "oracle")){
            dbType = "ORACLE";
        }else if(StringUtils.equalsIgnoreCase(type, "mysql")){
            dbType = "MYSQL";
        }else if(StringUtils.equalsIgnoreCase(type, "postgresql")){
            dbType = "POSTGRESQL";
        }
        return dbType;
    }


    private  boolean getDBDataValid(ExchangeSubscribeVO exchangeVO){
        if(StringUtils.isEmpty(exchangeVO.getDbIp()) ||
            StringUtils.isEmpty(exchangeVO.getDbUser()) ||
                StringUtils.isEmpty(exchangeVO.getDbPassword())||
                    StringUtils.isEmpty(exchangeVO.getDbSchemaName()) ||
                        StringUtils.isEmpty(exchangeVO.getDbTableName()) ||
                            StringUtils.isEmpty(exchangeVO.getDbName()) ||
                                StringUtils.isEmpty(exchangeVO.getDbType())
                ){
            return false;
        }
        return true;
    }

    private  boolean getDataValid(ExchangeSubscribeVO exchangeVO){
        if(StringUtils.isEmpty(exchangeVO.getResourceCode()) ||
            StringUtils.isEmpty(exchangeVO.getResourceType()) ||
                StringUtils.isEmpty(exchangeVO.getSubscribeDeptInfo())
//                    StringUtils.isEmpty(exchangeVO.getDbIp()) ||
//                        StringUtils.isEmpty(exchangeVO.getDbUser()) ||
//                            StringUtils.isEmpty(exchangeVO.getDbPassword())||
//                                StringUtils.isEmpty(exchangeVO.getDbSchemaName()) ||
//                                    StringUtils.isEmpty(exchangeVO.getDbTableName()) ||
//                                        StringUtils.isEmpty(exchangeVO.getDbName()) ||
//                                            StringUtils.isEmpty(exchangeVO.getDbType())
                ){
            return false;
        }

        return true;
    }

    //根据部门统一社会信用编码获取部门ID
    private Organization getDeptNameByUnifiedCreditCode(String deptUnifiedCreditCode)throws Exception{

        //处理资源的部门列表ID问题
        Long rentId = userUtils.getCurrentUserRentId();
        if(rentId==null || rentId.equals(0L)){
            throw new InnerTerminalConfigException("第三方用户没有配置租户，用户名为 "+userUtils.getCurrentUserName()); //数据不完整
        }

        List<Integer> parentIds = userService.findParentIdsByUnifiedCreditCode(deptUnifiedCreditCode, rentId);
        if(CollectionUtils.isEmpty(parentIds)){
            throw new InnerTerminalConfigException("第三方用户调用部门编码没有入库，部门编码 " + deptUnifiedCreditCode
                    + "用户名为 "+userUtils.getCurrentUserName()); //数据不完整
        }
        Long deptID = parentIds.get(parentIds.size()-1).longValue();
        Organization organization = null;
        if(deptID!=0L) {
            organization = organizationService.findById(deptID);
        }
        return organization;
    }


    @Override
    public void processExchange(Long rentId, String user, ExchangeSubscribeVO exchangeSubscribeVO) throws Exception {

        String resourceType = exchangeSubscribeVO.getResourceType();

        //数据完整性检测
        if(!getDataValid(exchangeSubscribeVO)){
            throw new RequestDataException("请求参数不完整"); //数据不完整
        }
        String requestDbType = exchangeSubscribeVO.getDbType();
        exchangeSubscribeVO.setDbType(getLocalDbType(requestDbType));

        //数据内容判断:如数据库是否连接正常，数据编码是否正确，部门统一社会信用代码是否有效，文件数据类型判断是否正确
        if(StringUtils.equalsIgnoreCase(resourceType, "db") &&
            !getDbLinkTest(exchangeSubscribeVO)){
            return;
        }

        String deptName = null;
        Organization organization = getDeptNameByUnifiedCreditCode(exchangeSubscribeVO.getSubscribeDeptInfo());
        ExchangeSubscribeInfoPO exchangeInfoPO = new ExchangeSubscribeInfoPO(exchangeSubscribeVO, user, organization.getDeptName());
        exchangeInfoDAO.insert(exchangeInfoPO);

        //获取任务数据序列号
        Long thirdSubsribeNum = sequenceNumberManager.getSubscribeSeqNum();
        NumberFormat f=new DecimalFormat("00000000");
        String subNum = CommonConstants.PREFIX_SUBSCRIBE+ f.format(thirdSubsribeNum);

        String etlID = null;
        Long sourceTableId = 0L;
        int destMetaId = 0;

        //考虑   我们系统资源信息编码="前缀码"+九次方资源信息编码，需要区分是本系统还是在第三方系统里面有记录
        String localResourceCode = resourceCodePrefix+exchangeSubscribeVO.getResourceCode();
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigByResourceCodeAndRentId(rentId, localResourceCode);
        if(rcPO!=null){
            if(StringUtils.equalsIgnoreCase(resourceType, "db")){
                sourceTableId = rcPO.getBindTableId();
                if(sourceTableId==null || sourceTableId.equals(0L)){
                    throw new RequestDataException("交换传输参数为db类型，本系统内部记录类型是 "+
                            ResourceTools.FormatType.getFormatInfoZH(rcPO.getFormatType()) +" ，没有配置绑定的物理表信息，" +
                            "请和本系统相关人员核对资源目录信息内容");
                }
                destMetaId = creatFrontDBMetaId(user, sourceTableId, organization);
                etlID = creatExchangeSubscribeTask(exchangeInfoPO, organization,
                        sourceTableId, (long)destMetaId, rcPO.getId(), thirdSubsribeNum);
            }else{
                if(ResourceTools.FormatType.getFormatType(rcPO.getFormatType())== ResourceTools.FormatType.DB){
                    throw new RequestDataException("交换传输参数为file类型，本系统内部记录类型是 "+
                            ResourceTools.FormatType.getFormatInfoZH(rcPO.getFormatType())+",请和本系统相关人员核对资源目录信息内容");
                }

                etlID = creatExchangeSubscribeTask(exchangeInfoPO, organization, 0L, 0L, rcPO.getId(),
                        thirdSubsribeNum);
            }

            //TODO：在系统内容有的信息资源目录生成订阅记录方便查询

        }else{
            if(StringUtils.equalsIgnoreCase(resourceType, "file") ){
                throw new RequestDataException("交换传输参数为file类型，本系统内部记录没有对应文件记录，请和本系统相关人员核对资源目录信息内容"); //数据不完整
            }else if(StringUtils.equalsIgnoreCase(resourceType, "db")){

                if(!getDBDataValid(exchangeSubscribeVO)){
                    throw new RequestDataException("交换传输参数为db类型，本系统内部不存在该资源目录信息，但是请求的数据库信息不够完整"); //数据不完整
                }
                //调用元数据中接口 采集传过来数据表结构 生成元数据
                sourceTableId = (long)getMetaIdByCollection(user, exchangeSubscribeVO);
                destMetaId = creatFrontDBMetaId(user, sourceTableId, organization);
                etlID = creatExchangeSubscribeTask(exchangeInfoPO, organization,
                        sourceTableId, (long)destMetaId, 0L, thirdSubsribeNum);
            }
        }


        /*  修改rc_sub_task和rc_subscribe主键对应方式，之前是 一一对应，现在rc_sub_task 使用自增主键
         *  修改原因： 第三方平台接入，执行过的交换任务需要能够进行查看和管理，将数据存储到交换任务表中，同时
         *          造成问题交换任务和订阅流程不能一一对应，但是可以根据SUB00000029 sub_no对应
         */
        // SubTaskPO subTaskPO = subTaskDAO.getById(subPO.getId());  //调整 2018/11/10
        SubTaskPO subTaskPO = subTaskDAO.getBySubTaskId(subNum);
        if(subTaskPO==null){
            subTaskPO = new SubTaskPO();
            subTaskPO.setEtlSubscribeId(etlID);
            subTaskPO.setEndTime(exchangeInfoPO.getEndTime());
            subTaskPO.setTaskType(exchangeSubscribeVO.getResourceType().toUpperCase());
            subTaskPO.setStatus(CommonConstants.IMPORTING);
            subTaskPO.setSubTaskId(subNum);
            subTaskPO.setSrcMetaId(sourceTableId);
            subTaskPO.setDestMetaId((long)destMetaId);
            subTaskPO.setRentId(rentId);
            subTaskPO.setCreator(user);
            subTaskPO.setModifier(user);
            subTaskPO.setCreateTime(new Date());
            subTaskPO.setModifyTime(new Date());
            subTaskDAO.insert(subTaskPO);
        }else {
            subTaskPO.setEtlSubscribeId(etlID);
            subTaskPO.setEndTime(exchangeInfoPO.getEndTime());
            subTaskPO.setTaskType(exchangeSubscribeVO.getResourceType().toUpperCase());
            subTaskPO.setStatus(CommonConstants.IMPORTING);
            subTaskPO.setSubTaskId(subNum);
            subTaskPO.setSrcMetaId(sourceTableId);
            subTaskPO.setDestMetaId(new Long(destMetaId));
            subTaskPO.setCreator(user);
            subTaskPO.setModifier(user);
            subTaskPO.setCreateTime(new Date());
            subTaskPO.setModifyTime(new Date());
            subTaskDAO.updateById(subTaskPO);
        }
    }

    /**
     * 通过神州数码传过来的订阅信息，进行数据库表直采生成元数据 metaId
     * @param user
     * @param exchangeSubscribeVO
     * @return
     * @throws Exception
     */
    private int getMetaIdByCollection(String user, ExchangeSubscribeVO exchangeSubscribeVO) throws Exception{
        ExternalTableCollection tableCollection = new ExternalTableCollection();
        tableCollection.setDbIp(exchangeSubscribeVO.getDbIp());
        tableCollection.setUserName(user);
        tableCollection.setDbPort(exchangeSubscribeVO.getDbPort());
        tableCollection.setDbType(exchangeSubscribeVO.getDbType());
        tableCollection.setDbUser(exchangeSubscribeVO.getDbUser());
        tableCollection.setDbPassword(exchangeSubscribeVO.getDbPassword());
        tableCollection.setDbName(exchangeSubscribeVO.getDbName());
        tableCollection.setDbSchemaName(exchangeSubscribeVO.getDbSchemaName());
        tableCollection.setDbTableName(exchangeSubscribeVO.getDbTableName());
        LOG.info("元数据采集调用参数："+ tableCollection.toString());
        ResultBean<CollectExternalTableResult> result= metacubeCatalogService.collectExternalTable(tableCollection);
        if(!result.isSuccess()){
            throw new Exception("元数据采集参数，失败原因："+result.getMsg());
        }
        return result.getData().getMetaId();
    }


    /*
    * 将需要订阅的资源数据结构复制到前置机
    *
    * @param user 交换用户
    * @param bindTableId  源元数据ID
    * @param frontDeptId  订阅数据部门ID
    *
    * @return 在订阅部门前置机上面创建的元数据
    */
    private int creatFrontDBMetaId(String user, Long bindTableId, Organization organization)throws Exception{

        int destMetaId = 0;
        TerminalManagePO tmPO = terminalManageDAO.getTerminalManageRecordByDeptId(organization.getId());
        if(tmPO==null){
            throw new InnerTerminalConfigException("第三方订阅交换部门前置机没有配置,部门名称为 "+ organization.getDeptName()
            + " ,部门租户ID为 " + organization.getRenterId());
        }

       //获取元数据字段
        List<MetadataField> metadataOriginFields = new ArrayList<MetadataField>();
        ResultBean<QueryMetadataFieldsResult> metaResult = metacubeCatalogService.getMetadataFieldsByMetaId(bindTableId.intValue());
        if(metaResult.isSuccess()){
            metadataOriginFields = metaResult.getData().getMetadataField();
        }else{
            throw new Exception("第三方订阅交换获取字段失败 "+metaResult.getMsg());
        }

        //获取前置机数据数据库，并且创建表格
        MetadataTable meta = new MetadataTable();
        meta.setMetaid(bindTableId.intValue());  //需要复制的元数据ID
        //参数dsId和storeDatabase 存储一个值
        meta.setSchemeId(Long.valueOf(tmPO.getSchemaId()));

        //获取上次MetaId
        Long deptId =Long.valueOf(tmPO.getDeptFinalId());
        Long lastMetaId = exchangeSubscribeTaskDAO.getMaxDestMetaId(bindTableId, deptId);
        if(lastMetaId==null || lastMetaId.equals(0L)){
            lastMetaId = -1L;
        }
        meta.setPreviousMetaid(lastMetaId.intValue());

        //获取前置机数据数据库，并且创建表格
        ResultBean<SubscribeCrtTbResult> tbResult = metacubeCatalogService.createTableBySubscribe(user, meta, metadataOriginFields);
        if(tbResult.isSuccess()){
            destMetaId = tbResult.getData().getMetaId();
        }else{
            String tmInfo = "前置机地址："+ tmPO.getTmIP() + "，前置机名称："+ tmPO.getTmName() + " ,订阅方部门："+tmPO.getDeptName()
                    + " ,数据库类型：" + tmPO.getTmDBType() + " ,绑定数据库：" + tmPO.getTmDBName();

            LOG.error(DateTools.formatDate(new Date())+"-在部门前置机上创建数据表失败,失败原因: "+ tbResult.getMsg());
            throw new Exception("第三方订阅交换复制数据库表异常，在部门前置机上创建数据表失败,失败原因: "+ tbResult.getMsg()+"。" +
                    tmInfo);
        }

        //创建完表格时候，对中心库进行授权
        ResultBean<Boolean> baseResult = metacubeCatalogService.authorizedTableForUser(user, bindTableId.intValue(),
                tmPO.getDeptCode(), SUBSCRIBED);
        if(!baseResult.isSuccess()){
            throw new Exception("第三方订阅交换异常，资源对应数据表授权失败，失败原因："+baseResult.getMsg());
        }
        return destMetaId;
    }

    private String getDescpt(ExchangeSubscribeInfoPO taskPO){
        return taskPO.getResourceType()+"-"+taskPO.getDbIp()+":"+taskPO.getDbPort()+"-"+taskPO.getDbName()
                +"-("+taskPO.getDbSchemaName()+")-"+taskPO.getDbTableName();
    }


    private FileCopyDto getFileExchanageDto(Long resourceId, Long deptId) throws Exception{

        /*文件拷贝到HDFS上由原来文件名变成实际名*/
        ETLFileInfo etlFileInfo = getETLFileInfo(resourceId);

        FileCopyDto fileCopyDto = new FileCopyDto();
        fileCopyDto.setDestinationIsAfile(true);
        fileCopyDto.setFiles(etlFileInfo.getFileTransList());


        SftpPutDto sftpPutDto = new SftpPutDto();
        sftpPutDto.setServerName(sftpServiceIp);
        sftpPutDto.setServerPort(sftpServicePort);
        sftpPutDto.setUserName(sftpServiceUser);
        sftpPutDto.setPassword(sftpServicePasswd);

        //目标文件路径：为 根路径（sftpServiceDir)+用户名（user,使用不同部门里面资源录入角色作为路径)
        User staffUser = systemConfigService.getDeptStaff(deptId.intValue());
        String deptUserName = "default";
        if(staffUser!=null){
            deptUserName = staffUser.getUsername();
        }
        String destDir =sftpServiceDir + deptUserName;
        sftpPutDto.setSftpDirectory(destDir);

        //文件路径mask,需要先下载到本地，然后上传到sftp里面去
        LocalFileInfo fileInfo = etlFileInfo.getLocalFileInfo();
        if(fileInfo==null || fileInfo.getFileMask()==null){
            return null;
        }
        sftpPutDto.setLocalDirectory(fileInfo.getFileDir());
        sftpPutDto.setFileMask(fileInfo.getFileMask());

        fileCopyDto.addNextStepDto(sftpPutDto);
        return fileCopyDto;
    }


    //创建交换任务，使用同步方式
    private String creatExchangeSubscribeTask(ExchangeSubscribeInfoPO taskInfoPO, Organization organization,
                                              Long sourceMetaId,Long destMetaId, Long resourceId, Long thirdSubsribeNum) throws Exception{

        Long deptId = organization.getId();
        String deptName = organization.getDeptName();

        ExchangeSubscribeTaskPO exchangeTaskPO = new ExchangeSubscribeTaskPO(taskInfoPO, deptId, deptName);

        //交换任务描述
        String user = taskInfoPO.getCreator();
        StringBuilder descpt = new StringBuilder(user+"-");
        descpt.append(getDescpt(taskInfoPO));
        String userName = user;   //采用数据上报创建者作为ETL任务的userId, ETL的任务号作为ETL的名字

        NumberFormat f=new DecimalFormat("00000000");
        String subNum = CommonConstants.PREFIX_SUBSCRIBE+ f.format(thirdSubsribeNum);
        exchangeTaskPO.setId(taskInfoPO.getId());
        exchangeTaskPO.setSeq(thirdSubsribeNum);
        exchangeTaskPO.setSubNo(subNum);
        exchangeTaskPO.setSrcMetaId(sourceMetaId);
        exchangeTaskPO.setDestMetaId(destMetaId);
        exchangeSubscribeTaskDAO.insert(exchangeTaskPO);


        String jobName = subNum;
        String prefix = "THIRD-PART-EXCHANGE-";
        String group = prefix+taskInfoPO.getResourceType().toUpperCase();
        CreateJobDto createJobDto = new CreateJobDto(userName, jobName, group, descpt.toString());

        try {
            List<StepDto> tableList = new ArrayList<>();
            if(StringUtils.equalsIgnoreCase("db", taskInfoPO.getResourceType())) {
                TableInputDto tableInputDto = assembleTableInputDtoParams(sourceMetaId);
                createJobDto.setDataInput(tableInputDto);

                TableOutputDto tableOutputDto = assembleInsertUpdateDto(destMetaId);
                tableList.add(tableOutputDto);
                createJobDto.setTransDataOutputs(tableList);

            }else{
                List<FileTransmitDto> targetList = new ArrayList<FileTransmitDto>();
                FileCopyDto fileCopyDto = getFileExchanageDto(resourceId, deptId);
                tableList.add(fileCopyDto);
                createJobDto.setJobDataOutputs(tableList);
            }
        } catch (NullPointerException e) {
            throw new Exception("第三方订阅交换初始化失败 "+e.getMessage());
        }

        LOG.info("第三方订阅交换创建参数 :{}", createJobDto.toString());
        SubscribeResultDto subscribeResultDto = subscribeService.createSubscribeJob(createJobDto);
        LOG.info("第三方订阅交换结果内容 :{}", subscribeResultDto.toString());

        String etlSubscribeId = null;
        if (subscribeResultDto.getStatus() == 0) {
            etlSubscribeId = subscribeResultDto.getSubscribeId();
            LOG.info("数据交换记录" + subscribeResultDto.getName() +
                    "创建ETL任务-subscribeId" + etlSubscribeId );
        } else {
            throw new Exception("创建交换任务失败,失败原因："+subscribeResultDto.getErrorMessage());
        }
        return etlSubscribeId;
    }


    /*HDFS文件传输的时候，先将文件下载到本地，然后在使用sftp上传到前置机部门目录下面*/
    private ETLFileInfo getETLFileInfo(Long resourceId) throws Exception{

        ETLFileInfo etlInfo = new ETLFileInfo();
        List<FileTransmitDto> fileInfoList = new ArrayList<>();
        LocalFileInfo fileInfo = new LocalFileInfo();

        //用户上传路径和文件名称
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        if(rcPO==null){
            throw new InnerTerminalConfigException("第三方订阅交换资源信息目录不存在，resourceId="+resourceId);
        }
        String hdfsDir = null;
        SystemConfigPO sysConf = systemConfigService.getSystemConfigByUser(rcPO.getCreator());
        if(sysConf!=null){
            hdfsDir = sysConf.getFileRoot();
        }

        /*本地文件名称为: */
        String filePath = etlLocalDir+FileUtils.getFileDirName();
        fileInfo.setFileDir(filePath);

        List<DataUploadDetailPO> dataUploadPOList = dataUploadDetailDAO.getUploadDetailsByResourceId(resourceId);
        if(dataUploadPOList!=null && dataUploadPOList.size()>0){

           List<String> localFileList = new ArrayList<>();
           StringBuilder fileMask = new StringBuilder();
           for(DataUploadDetailPO dataPO:dataUploadPOList){

               String fileName = dataPO.getPubFileName();
               String downFileName = hdfsDir+dataPO.getOriginFileName();
               localFileList.add(fileName);
               fileMask.append(fileName+"|");

               String destFile = filePath+File.separator+fileName;
               FileTransmitDto fileTrans = new FileTransmitDto(downFileName, destFile);
               fileTrans.setDestinationName("local");
               fileInfoList.add(fileTrans);
           }
           fileInfo.setFileNameList(localFileList);
           fileInfo.setFileMask(fileMask.substring(0, fileMask.length()-1));
        }else{
            LOG.info("第三方订阅交换，该资源目录下已经上传的文件数据为空");
            return etlInfo;
        }
        etlInfo.setFileTransList(fileInfoList);
        etlInfo.setLocalFileInfo(fileInfo);
        return etlInfo;
    }

    /*HDFS文件传输的时候，先将文件下载到本地，然后在使用sftp上传到前置机部门目录下面*/
//    private LocalFileInfo getLocalTransferFileDir(Long resourceId) throws Exception{
//
//        LocalFileInfo fileInfo = new LocalFileInfo();
//
//        //用户上传路径和文件名称
//        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
//        if(rcPO==null){
//            throw new InnerTerminalConfigException("第三方订阅交换资源信息目录不存在，resourceId="+resourceId);
//        }
//        String hdfsDir = null;
//        SystemConfigPO sysConf = systemConfigService.getSystemConfigByUser(rcPO.getCreator());
//        if(sysConf!=null){
//            hdfsDir = sysConf.getFileRoot();
//        }
//
//        /*本地文件名称为: */
//        List<DataUploadDetailPO> dataUploadPOList = dataUploadDetailDAO.getUploadDetailsByResourceId(resourceId);
//        if(dataUploadPOList!=null && dataUploadPOList.size()>0){
//            String localFileDir = FileUtils.createUpdateDirByInfo().getPath();
//            fileInfo.setFileDir(localFileDir);
//
//            List<String> localFileList = new ArrayList<>();
//            StringBuilder fileMask = new StringBuilder();
//            for(DataUploadDetailPO dataPO:dataUploadPOList){
//                String fileName = dataPO.getPubFileName();
//                String downFileName = hdfsDir+dataPO.getOriginFileName();
//                localFileList.add(fileName);
//                fileMask.append(fileName+"|");
//
//                InputStream inputStream = null;
//                try {
//
//                    inputStream = hdfsUnDaoHessian.downloadFileByStream(downFileName);
//                    if (null == inputStream) {
//                        LOG.error("第三方订阅交换文件下载失败，请确定文件是否不存在 "+downFileName);
//                        throw new InnerTerminalConfigException("文件下载失败，请确定文件是否不存在 "+downFileName);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    LOG.error("第三方订阅交换文件下载出现异常："+e.getMessage());
//                    throw new InnerTerminalConfigException("文件下载出现异常："+e.getMessage());
//                }
//
//                FileOutputStream out;
//                String localFileName = localFileDir+File.separator+fileName;
//                try {
//                    out = new FileOutputStream(localFileName);
//                    IOUtils.copy(inputStream, out);
//                    out.flush();
//                    out.close();
//                } catch (IOException e) {
//                    LOG.error("第三方订阅交换文件下载后拷贝失败", e);
//                    throw new RuntimeException("第三方订阅交换文件下载后拷贝失败" + e.getMessage());
//                }
//            }
//            fileInfo.setFileNameList(localFileList);
//            fileInfo.setFileMask(fileMask.substring(0, fileMask.length()-1));
//        }else{
//            LOG.info("第三方订阅交换，该资源目录下已经上传的文件数据为空");
//        }
//        return fileInfo;
//    }


    private TableInputDto assembleTableInputDtoParams(Long metaId) throws Exception {

        TableInputDto tableInputDto = new TableInputDto();
        ResultBean<MetadataDTO> respon = metacubeCatalogService.findTableInfoByID(metaId);
        if(!respon.isSuccess()){
            LOG.error("元数据表metaId "+metaId+",获取元数据信息失败："+respon.getMsg());
            throw new Exception("元数据表metaId "+metaId+",获取元数据信息失败："+respon.getMsg());
        }
        MetadataDTO metadataDTO = respon.getData();
        tableInputDto.setSchemaId(new Long(metadataDTO.getSchemaId()));
        tableInputDto.setTableId(new Long(metadataDTO.getMetaId()));
        tableInputDto.setTable(metadataDTO.getMetaName());

        SearchFieldsDto primaryKey = new SearchFieldsDto();
        List<OutputFieldsDto> outputFields = new ArrayList<OutputFieldsDto>();

        List<String> fields = getSpecilFiled(metaId);

        tableInputDto.setFields(fields);
        //LOG.info("配置任务Input信息： {}", tableInputDto.toString());
        return tableInputDto;
    }

    /*订阅时候，1.就是使用表复制，所有表内容内容是否重复需要在源表保证。 2.无从知道表里面主键结构。 直接使用TableOutputDto结构*/
    private TableOutputDto assembleInsertUpdateDto(Long metaId)throws Exception {

        TableOutputDto tableOutputDto = new TableOutputDto();
        ResultBean<MetadataDTO> respon = metacubeCatalogService.findTableInfoByID(metaId);
        if(!respon.isSuccess()){
            LOG.error("元数据表metaId "+metaId+",获取元数据信息失败："+respon.getMsg());
            throw new Exception("元数据表metaId "+metaId+",获取元数据信息失败："+respon.getMsg());
        }
        MetadataDTO metadataDTO = respon.getData();
        tableOutputDto.setSchemaId(new Long(metadataDTO.getSchemaId()));
        tableOutputDto.setTableId(new Long(metadataDTO.getMetaId()));
        tableOutputDto.setTable(metadataDTO.getMetaName());

        List<OutputFieldsDto> outputFields = new ArrayList<OutputFieldsDto>();
        List<MetadataField> metadataOriginFields = new ArrayList<MetadataField>();
        ResultBean<QueryMetadataFieldsResult> metaResult = metacubeCatalogService.getMetadataFieldsByMetaId(metaId.intValue());
        if(metaResult.isSuccess()){
            metadataOriginFields = metaResult.getData().getMetadataField();
        }else{
            LOG.error("第三方订阅交换获取资源绑定元数据结构异常：metaId {},错误原因：{}", metaId, metaResult.getMsg());
            throw new Exception("第三方订阅交换获取资源绑定元数据结构异常：metaId"+metaId);
        }

        List<String> fields = new ArrayList<String>();
        for(MetadataField field:metadataOriginFields){
            OutputFieldsDto outputFieldsDto = new OutputFieldsDto(field.getColName(), field.getColName());
            outputFieldsDto.setUpdate(true);
        }

        tableOutputDto.setFields(outputFields);
        LOG.info("第三方订阅交换配置任务InsertUpdate信息： {}", tableOutputDto.toString());
        return tableOutputDto;
    }


    /*获取数据库表字段*/
    private List<String> getSpecilFiled(Long metaId)throws Exception{

        List<MetadataField> metadataOriginFields = new ArrayList<MetadataField>();
        ResultBean<QueryMetadataFieldsResult> metaResult = metacubeCatalogService.getMetadataFieldsByMetaId(metaId.intValue());
        if(metaResult.isSuccess()){
            metadataOriginFields = metaResult.getData().getMetadataField();
        }else{
            LOG.error("第三方订阅交换获取资源绑定元数据结构异常：metaId {},错误原因：{}", metaId, metaResult.getMsg());
            throw new Exception("第三方订阅交换获取资源绑定元数据结构异常：metaId"+metaId);
        }

        List<String> fields = new ArrayList<String>();
        for(MetadataField field:metadataOriginFields){
            fields.add(field.getColName());
        }
        return fields;
    }

    public String getSftpServiceName() {
        return sftpServiceName;
    }

    public void setSftpServiceName(String sftpServiceName) {
        this.sftpServiceName = sftpServiceName;
    }

    public String getSftpServicePort() {
        return sftpServicePort;
    }

    public void setSftpServicePort(String sftpServicePort) {
        this.sftpServicePort = sftpServicePort;
    }

    public String getSftpServiceIp() {
        return sftpServiceIp;
    }

    public void setSftpServiceIp(String sftpServiceIp) {
        this.sftpServiceIp = sftpServiceIp;
    }

    public String getSftpServiceUser() {
        return sftpServiceUser;
    }

    public void setSftpServiceUser(String sftpServiceUser) {
        this.sftpServiceUser = sftpServiceUser;
    }

    public String getSftpServicePasswd() {
        return sftpServicePasswd;
    }

    public void setSftpServicePasswd(String sftpServicePasswd) {
        this.sftpServicePasswd = sftpServicePasswd;
    }

    public String getSftpServiceDir() {
        return sftpServiceDir;
    }

    public void setSftpServiceDir(String sftpServiceDir) {
        this.sftpServiceDir = sftpServiceDir;
    }
}

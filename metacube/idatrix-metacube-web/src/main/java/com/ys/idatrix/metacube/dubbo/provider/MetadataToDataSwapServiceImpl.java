package com.ys.idatrix.metacube.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.base.Preconditions;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.MetadataDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.dataswap.*;
import com.ys.idatrix.metacube.api.service.MetadataToDataSwapService;
import com.ys.idatrix.metacube.common.enums.TableColumnStatusEnum;
import com.ys.idatrix.metacube.common.helper.DataTypeHelper;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.*;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefBaseService;
import com.ys.idatrix.metacube.metamanage.service.MySqlDDLService;
import com.ys.idatrix.metacube.metamanage.service.OracleDDLService;
import com.ys.idatrix.metacube.metamanage.vo.request.AlterSqlVO;
import com.ys.idatrix.metacube.metamanage.vo.request.OracleTableVO;
import com.ys.idatrix.metacube.metamanage.vo.response.AuthMetadataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname MetadataToDataSwapServiceImpl
 * @Description 元数据提供给共享数据交换服务
 * @Author robin
 * @Date 2019/3/9 11:49
 * @Version v1.0
 */

@Slf4j
@Service(interfaceClass=MetadataToDataSwapService.class)
@Component
public class MetadataToDataSwapServiceImpl implements MetadataToDataSwapService {

    @Autowired(required = false)
    private SecurityConsumer securityConsumer;

    @Autowired(required = false)
    private MetadataMapper metadataMapper;

    @Autowired(required = false)
    private TableColumnMapper tableColumnMapper;

    @Autowired(required = false)
    private McSchemaMapper schemaMapper;

    @Autowired(required = false)
    private McDatabaseMapper mcDatabaseMapper;

    @Autowired(required = false)
    private OracleDDLService oracleDDLService;

    @Autowired(required = false)
    private MySqlDDLService mySqlDDLService;

    @Autowired(required = false)
    private ResourceAuthMapper resourceAuthMapper;

    @Autowired(required = false)
    private McSchemaMapper mcSchemaMapper;

    @Autowired(required = false)
    private ApprovalProcessMapper approvalProcessMapper;

    @Autowired
    private IMetaDefBaseService metaDefBaseService;



    /**
     * 根据表ID查询表字段
     *
     * @param tableId
     * @return
     */
    @Override
    public ResultBean<MetadataDTO> findTableInfoByID(Long tableId) {

        if(tableId==null ||tableId.equals(0L)){
            log.error("表ID查询表字段请求异常");
            return ResultBean.error("表ID查询表字段请求异常");
        }

        Metadata data = metadataMapper.selectByPrimaryKey(tableId);
        if(data==null){
            log.error("请求表数据不存在，ID为 "+ tableId.toString());
            return ResultBean.error("请求表数据不存在");
        }

        MetadataDTO dataDto = new MetadataDTO();
        dataDto.setMetaName(data.getName());
        dataDto.setSchemaId(data.getSchemaId().intValue());
        dataDto.setMetaId(data.getId().intValue());

        McSchemaPO schemaData = schemaMapper.getSchemaById(data.getSchemaId());
        if(schemaData==null){
            log.error("请求schema数据不存在，schemaId为 "+ data.getSchemaId());
            return ResultBean.error("请求schema数据不存在");
        }
        dataDto.setSchemaName(schemaData.getNameCn());
        dataDto.setDatabaseId(schemaData.getDbId());
        return ResultBean.ok(dataDto);
    }



    /**
     * 创建订阅的表
     *
     * @param userName
     * @param targetDS
     * @param changedMetadataTable
     * @param metadataTable
     * @return
     * @throws Exception
     */
    private Metadata getNewMetaIdAndInsertMetaTable(String userName, DatabaseTypeEnum targetDS,
                                               MetadataTable changedMetadataTable,
                                               Metadata metadataTable) throws Exception {

        /** step2：表名自定义,保证唯一,原表名+”_AUTO_”+schema+”_SUB_”+序号 **/
        //表名后缀
        String tableNameSuffix = "_AUTO_SUB_";

        //表名后缀序号
        int seq = 1;

        //重复订阅：上一次订阅生成的metaId
        int previousMetaId = changedMetadataTable.getPreviousMetaid();
        if (previousMetaId > -1) {
            Metadata previousMetadata = metadataMapper.selectByPrimaryKey(new Long(previousMetaId));
            String previousTableName = previousMetadata.getName();
            String[] splitTableNames = previousTableName.toUpperCase().split(tableNameSuffix);
            if (splitTableNames.length > 1) {
                seq = Integer.parseInt(splitTableNames[1]);
                seq++;
            }
        }

        //表名长度限制
        int maxTableLength = 0;
        switch (targetDS) {
            //Oracle
            case ORACLE:
                maxTableLength = 30;
                break;
            //MySql、PostgreSql
            case MYSQL:
           // case 8:
                maxTableLength = 63;
                break;
            //DM
            case DM:
                maxTableLength = 128;
                break;
            default:
                log.warn("不支持的数据源类型：" + targetDS);
                break;
        }

        //原表名
        String originalTableName = metadataTable.getName();

        //超长后截取
        if (originalTableName.length() >= maxTableLength) {
            int truncateLength = tableNameSuffix.length() + String.valueOf(seq).length();
            originalTableName = originalTableName.substring(0, originalTableName.length() - truncateLength);
        }

        String newTableNameEn = originalTableName + tableNameSuffix + seq;

        //对象拷贝：将请求元数据表变更对象属性 拷贝到 查询出来的元数据表对象中
        Metadata oldData = metadataMapper.selectByPrimaryKey(new Long(changedMetadataTable.getMetaid()));
        Metadata newDataTable = new Metadata();
        BeanUtils.copyProperties(oldData, newDataTable);

        /** step3：插入表信息到 mc_meta_table 表,获取新插入记录的 metaId **/
        newDataTable.setName(newTableNameEn);
        newDataTable.setSchemaId(changedMetadataTable.getSchemeId());
        newDataTable.setDatabaseType(targetDS.getCode());
        newDataTable.setCreator(userName);
        newDataTable.setModifier(userName);
        newDataTable.setCreateTime(new Date());
        newDataTable.setModifyTime(new Date());
        newDataTable.setId(null);
        newDataTable.setRemark("订阅建表自动生成");

        metadataMapper.insertSelective(newDataTable);
        return newDataTable;
    }


    /**
     * 资源目录订阅-创建表
     *
     * @param userName
     * @param changedMetadataTable
     * @param metadataFields
     * @return
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
    public ResultBean<SubscribeCrtTbResult> createTableBySubscribe(String userName, MetadataTable changedMetadataTable, List<MetadataField> metadataFields) {

        if (null == changedMetadataTable) {
            log.error("请求参数 changedMetadataTable");
            return ResultBean.error("请求参数 changedMetadataTable 为空！");
        }

        if (CollectionUtils.isEmpty(metadataFields)) {
            log.error("请求参数 metadataFields 为空！");
            return ResultBean.error("请求参数 metadataFields 为空！");
        }

        int oldMetaId = changedMetadataTable.getMetaid();
        if (oldMetaId == 0) {
            log.error("请求参数订阅源表表中ID为0");
            return ResultBean.error("请求参数订阅源表表中ID为0");
        }

        /** step1：根据 metaId 查询原始元数据表的数据 **/
        Metadata oldMetadataInfo = metadataMapper.selectByPrimaryKey(new Long(oldMetaId));
        if (null == oldMetadataInfo) {
            log.error("根据参数 metaId=" + oldMetaId + " 查询不到元数据表记录");
            return ResultBean.error("源元数据记录查询为空");
        }
        Long oldSchemaId = oldMetadataInfo.getSchemaId();
        McSchemaPO oldSchema = schemaMapper.getSchemaById(oldSchemaId);
        if(oldSchema==null){
            log.error("请求参数订阅源表schma无效");
            return ResultBean.error("请求参数订阅源表schma无效");
        }
//        McDatabasePO oldDatabasePO = mcDatabaseMapper.getDatabaseById(oldSchema.getDbId());
        McSchemaPO newSchema = schemaMapper.getSchemaById(changedMetadataTable.getSchemeId());
        if(newSchema==null){
            log.error("请求参数订阅源表schma无效");
            return ResultBean.error("请求参数订阅源表schma无效");
        }
        McDatabasePO newDatabasePO = mcDatabaseMapper.getDatabaseById(newSchema.getDbId());

        //插入元数据表记录并获取新的metaId
        DatabaseTypeEnum sourceDBType = DatabaseTypeEnum.getInstance(oldMetadataInfo.getDatabaseType());
        DatabaseTypeEnum targetDBType = DatabaseTypeEnum.getInstance(newDatabasePO.getType());
        Metadata newMeta;
        try {
            newMeta = getNewMetaIdAndInsertMetaTable(userName, targetDBType, changedMetadataTable, oldMetadataInfo);
            log.info("订阅建表->保存元数据表，metaid:{}", newMeta.getId());
        } catch (Exception e) {
            return ResultBean.error("订阅建表->保存元数据表失败 "+e.getMessage());
        }
        //创建表以后将数据写到数据地图 2019/03/23
        if(newMeta.getDatabaseType() == DatabaseTypeEnum.MYSQL.getCode()) {
            metaDefBaseService.updateMetadataChangeInfoToGraph(DatabaseType.MySQL, newMeta);
        } else if(newMeta.getDatabaseType() == DatabaseTypeEnum.ORACLE.getCode()) {
            metaDefBaseService.updateMetadataChangeInfoToGraph(DatabaseType.Oracle, newMeta);
        }

        /** step4：插入表字段到 mc_meta_property 表 **/
        List<TableColumn> insertList = new ArrayList<>();
        List<TableColumn> listPros = tableColumnMapper.findTableColumnListByTableId(new Long(oldMetaId));
        if(CollectionUtils.isNotEmpty(listPros)){
            List<String> metaFieldName = metadataFields.stream().map(p->p.getColName()).collect(Collectors.toList());
            insertList = listPros.stream().filter(p-> metaFieldName.contains(p.getColumnName())).collect(Collectors.toList());
            for(TableColumn table:listPros){
                String descrip = table.getDescription();
                if(descrip.startsWith("'") && descrip.endsWith("'")){
                    table.setDescription(trimFirstAndLastChar(descrip, '\''));
                }
            }
        }

        /** step5：不同数据源的字段类型转换 **/
        if (!sourceDBType.equals(targetDBType)) {
            try {
                DataTypeHelper.convertDataTypeOnDiffDsType(sourceDBType, targetDBType, insertList);
            } catch (Exception e) {
                return ResultBean.error(e.getMessage());
            }
        }

        try {
            if(CollectionUtils.isNotEmpty(insertList)){
                insertList.stream().forEach(p->{
                    p.setTableId(newMeta.getId());
                    p.setCreateTime(new Date());
                    p.setModifyTime(new Date());
                    p.setModifier(userName);
                    p.setCreator(userName);
                    p.setId(null);
                    tableColumnMapper.insertSelective(p);
                });
            }

        } catch (Exception e) {
            log.error("插入metaId：" + newMeta.getId() + "的字段异常", e);
            //插入字段失败后，删除 mc_meta_table 中 metaid=newMetaId 的 的数据
            int delRow = metadataMapper.deleteByPrimaryKey(newMeta.getId());
            log.error("插入表字段异常后，成功删除 mc_meta_table 表 metaId:{} 的记录:{} 条", newMeta.getId(), delRow);
            return ResultBean.error("请求参数为空！");
        }


        /** step7：拼接sql操作数据库生效 **/
        try {
            if (targetDBType == DatabaseTypeEnum.MYSQL) {
                // 获取创建表的sql
                ArrayList<String> mysqlList = mySqlDDLService.getCreateTableSql(newMeta, insertList, null, null);
                //直接应用到数据库
                mySqlDDLService.goToDatabase(userName, newMeta, mysqlList);
            } else if (targetDBType == DatabaseTypeEnum.ORACLE) {

                OracleTableVO oracleTable = new OracleTableVO();
                oracleTable.setName(newMeta.getName());

                TablePkOracle pk = new TablePkOracle();
                pk.setTableId(newMeta.getId());
                pk.setSequenceStatus(1);
                oracleTable.setPrimaryKey(pk);

                TableSetOracle tableSet = new TableSetOracle();
                tableSet.setTableId(newMeta.getId());

                //判断当前schema是system/usr
                RdbLinkDto config = oracleDDLService.getConnectionConfig(newMeta);
                // 如果表空间为null，则设置默认的表空间
                if (config.getUsername().equalsIgnoreCase("sys") || config.getUsername()
                        .equalsIgnoreCase("system")) {
                    tableSet.setTablespace("SYSTEM");
                } else {
                    tableSet.setTablespace("USERS");
                }
                oracleTable.setTableSetting(tableSet);
                oracleTable.setColumnList(insertList);

                AlterSqlVO alterOracle = oracleDDLService.getCreateTableSql(oracleTable);
                oracleDDLService.goToDatabase(newMeta, new ArrayList<String>(alterOracle.getAddSql()));
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("物理插入表字段异常 "+e.getMessage());
            return ResultBean.error("物理插入表字段异常 "+e.getMessage());
        }
        return ResultBean.ok(new SubscribeCrtTbResult(newMeta.getId().intValue()));

//
//
//
//        /** step4：插入表字段到 mc_meta_property 表 **/
//        List<MetadataField> metadataProperties = new ArrayList<>();
//        for (MetadataField metadataField : metadataFields) {
//            metadataField.setMetaid(newMetaId);
//            MetadataProperty metadataProperty = new MetadataProperty();
//            BeanCopier bc2 = BeanCopier.create(MetadataField.class, MetadataProperty.class, false);
//            bc2.copy(metadataField, metadataProperty, null);
//            metadataProperties.add(metadataProperty);
//        }
//
//        /** step5：不同数据源的字段类型转换 **/
//        if (sourceDS.getDsType().intValue() != targetDS.getDsType().intValue()) {
//            try {
//                DataTypeHelper.convertDataTypeOnDiffDsType(sourceDsType, targetDsType, metadataProperties);
//            } catch (Exception e) {
//                return new SubscribeCrtTbResult(false, e.getMessage());
//            }
//        }
//
//        try {
//            for (MetadataProperty metadataProperty : metadataProperties) {
//                metadataProsMapper.insert(metadataProperty);
//            }
//        } catch (Exception e) {
//            log.error("插入metaId：" + newMetaId + "的字段异常", e);
//            //插入字段失败后，删除 mc_meta_table 中 metaid=newMetaId 的 的数据
//            int delRow = metadataTableMapper.deleteByPrimaryKey(newMetaId);
//            log.error("插入表字段异常后，成功删除 mc_meta_table 表 metaId:{} 的记录:{} 条", newMetaId, delRow);
//            return new SubscribeCrtTbResult(false, "请求参数为空！");
//        }
//
//        /** step6：调用db-proxy接口创建实体表 **/
//        Map<Integer, String> msgMap = metadataTableService.createOrAlterEntityTable(userName, newMetaId);
//
//        /** step7：db-proxy创建实体表失败，删除step2插入到元数据表、元数据属性表的记录 **/
//        SubscribeCrtTbResult result = new SubscribeCrtTbResult(true, "创建实体表成功", newMetaId);
//        if (MapUtils.isNotEmpty(msgMap)) {
//            String errorMsg = msgMap.get(newMetaId);
//            if (StringUtils.isNotBlank(errorMsg)) {
//                log.error("创建实体表失败！ 错误信息：" + errorMsg);
//
//                //创建失败删除 mc_meta_property 表中记录
//                int delColRow = metadataProsMapper.deleteByMetaId(newMetaId);
//                log.error("插入表字段异常后，成功删除 mc_meta_property 表 metaId:{} 的记录:{} 条", newMetaId, delColRow);
//
//                //创建失败删除 mc_meta_table 表中记录
//                int delTbRow = metadataTableMapper.batchDeleteByIds(Lists.newArrayList(newMetaId));
//                log.error("插入表字段异常后，成功删除 mc_meta_table 表 metaId:{} 的记录:{} 条", newMetaId, delTbRow);
//
//                result = new SubscribeCrtTbResult(false, errorMsg);
//            }
//        }
//
//        log.info("订阅建表->保存元数据表,执行{}", (result.isSuccess() ? "成功!" : ("失败!" + result.getMessage())));
//
//        return result;
    }


        /**
         * 去除字符串首尾出现的某个字符.
         * @param source 源字符串.
         * @param element 需要去除的字符.
         * @return String.
         */
    private String trimFirstAndLastChar(String source,char element){
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do{
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;

    }

    /**
     * 通过注册验证字段
     *
     * @param metaId
     * @param verifiedFields
     * @return
     */
    @Override
    public ResultBean<RegisterVerifyFieldsResult> verifyFieldsByRegister(int metaId, List<String> verifiedFields) {
        return null;
    }

    /**
     * 根据metaId查询表字段（属性）信息
     *
     * @param metaId
     * @return
     */
    @Override
    public ResultBean<QueryMetadataFieldsResult> getMetadataFieldsByMetaId(int metaId) {
        if (metaId == 0) {
            log.error("请求参数 metaId=0");
            return ResultBean.error("参数 metaId=0");
        }

        List<TableColumn> listPros = tableColumnMapper.findTableColumnListByTableId(new Long(metaId));
        if (CollectionUtils.isEmpty(listPros)) {
            log.warn("metaId:{} 没有字段信息！", metaId);
            return ResultBean.error("当前表没有字段数据");
        }

        List<MetadataField> metadataFields = new ArrayList<>();
        //对象拷贝为返回对象
        //BeanCopier bc = BeanCopier.create(MetadataProperty.class, MetadataField.class, false);

        for (TableColumn property : listPros) {
            if(property.getStatus().equals(TableColumnStatusEnum.DELETE.getValue()) ||
                    property.getIsDeleted()){
                continue;
            }
            MetadataField metadataField = new MetadataField();
            metadataField.setColName(property.getColumnName());
            metadataField.setDataType(property.getColumnType());
            metadataField.setMetaid(property.getTableId().intValue());
            metadataField.setLength(property.getTypeLength());
            metadataField.setPrecision(property.getTypePrecision());
            metadataFields.add(metadataField);
        }

        log.info("根据metaId查询表字段信息 {} 条", metadataFields.size());
        return ResultBean.ok(new QueryMetadataFieldsResult(metadataFields));

    }

    /**
     * 根据AuthMetadata获取业务操作权限值
     *
     * @param authMetadataVOList
     * @return
     */
    private ActionTypeEnum getAuthFromAuthMetadata(List<AuthMetadataVO> authMetadataVOList) {
        if (CollectionUtils.isNotEmpty(authMetadataVOList)) {
            AuthMetadataVO vo = authMetadataVOList.get(0);
            if (vo.getAuthTypes().contains(ActionTypeEnum.READ.getCode() + "") && vo.getAuthTypes().contains(ActionTypeEnum.WRITE.getCode() + "")) {
                return ActionTypeEnum.ALL;
            } else if (vo.getAuthTypes().contains(ActionTypeEnum.READ.getCode() + "")) {
                return ActionTypeEnum.READ;
            } else if (vo.getAuthTypes().contains(ActionTypeEnum.WRITE.getCode() + "")) {
                return ActionTypeEnum.WRITE;
            } else {
                return ActionTypeEnum.NONE;
            }
        }
        return ActionTypeEnum.NONE;
    }


    private ActionTypeEnum getTablePermiss(String username, Long metaId) {
        try {
            Preconditions.checkNotNull(username, "用户信息为空");
            Preconditions.checkNotNull(metaId, "索引元数据标识为空");

            //查询用户所属组织
            Organization organization = securityConsumer.getAscriptionDeptByUserName(username);
            String deptCode = organization.getDeptCode();


            Metadata metadata = metadataMapper.selectByPrimaryKey(metaId);
            if (null == metadata) {
                return ActionTypeEnum.NONE;
            }

            //根据schemaId，查询schema
            McSchemaPO mcSchemaPO = mcSchemaMapper.findById(metadata.getSchemaId());
            String regCode = mcSchemaPO.getOrgCode();
            List<String> regCodes = Arrays.asList(regCode.split(","));
            if (CollectionUtils.isNotEmpty(regCodes) && regCodes.contains(deptCode)) {
                return ActionTypeEnum.ALL;
            }

            //查询权限表,获取权限
            List<Integer> authList = resourceAuthMapper.getAllAuthValue();

            List<AuthMetadataVO> authMetadataVOList = approvalProcessMapper.getAuthMetadata(metaId, 2, deptCode, null, authList, null);
            return getAuthFromAuthMetadata(authMetadataVOList);
        } catch (Exception e) {
            log.error("getEsPermiss 失败", e);
            return ActionTypeEnum.NONE;
        }
    }

    /**
     * 授权元数据资源给用户
     *
     * @param username
     * @param metaId
     * @param orgCode
     * @param type
     * @return
     */
    @Override
    public ResultBean<Boolean> authorizedTableForUser(String username, int metaId, String orgCode, AuthorizedFlowType type) {

        ApprovalProcess approvalProcess = new ApprovalProcess();

        //查询权限表,获取权限
        List<Integer> authList = resourceAuthMapper.getAllAuthValue();

        //查询用户所属组织
        Organization organization = securityConsumer.getAscriptionDeptByUserName(username);
        String deptCode = organization.getDeptCode();

        //1.首先查询该用户或者部门是否有表数据权限
        ActionTypeEnum actionType = getTablePermiss(username, new Long(metaId));
        if(actionType==ActionTypeEnum.ALL){
            return ResultBean.ok(true);
        }else if(actionType==ActionTypeEnum.NONE){
            Long rentId = organization.getRenterId();

            Metadata metadata = metadataMapper.selectByPrimaryKey(new Long(metaId));
            if(metadata==null){
                return ResultBean.error("metaId "+metaId + " 查询不到记录");
            }
            approvalProcess.setCreator(username);
            approvalProcess.setCreateTime(new Date());
            approvalProcess.setDeptCode(deptCode);
            approvalProcess.setCause(type.getDespZH());
            approvalProcess.setResourceId( new Long(metaId));
            approvalProcess.setResourceType(metadata.getDatabaseType());
            if(CollectionUtils.isNotEmpty(authList)){
                IntSummaryStatistics collect = authList.stream().collect(Collectors.summarizingInt(value -> value));
                approvalProcess.setAuthValue((int)collect.getSum());
            }
            approvalProcess.setStatus(2);
            approvalProcess.setApprover(username);
            approvalProcess.setModifyTime(new Date());
            approvalProcessMapper.insertSelective(approvalProcess);
        }else{
            //查询权限表,获取权限,没有赋值全部权限的 升级权限
            List<ApprovalProcess> approvalList = approvalProcessMapper.getAuthByResourceIdAndValue(new Long(metaId), orgCode, 2);
            if(CollectionUtils.isNotEmpty(approvalList)){
                approvalProcess = approvalList.get(0);
            }
            approvalProcess.setCause(approvalProcess.getCause()+ type.getDespZH());
            if(CollectionUtils.isNotEmpty(authList)){
                IntSummaryStatistics collect = authList.stream().collect(Collectors.summarizingInt(value -> value));
                approvalProcess.setAuthValue((int)collect.getSum());
            }
            approvalProcess.setModifyTime(new Date());
            approvalProcessMapper.updateByPrimaryKeySelective(approvalProcess);
        }
        //2.没有权限则插入授权记录
        return ResultBean.ok(true);
    }


    /**
     * 采集元数据表：神州数码调用接口其它版本不需要使用：TODO：暂时不实现 2019/03/09
     *
     * @param externalTableCollection
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBean<CollectExternalTableResult> collectExternalTable(ExternalTableCollection externalTableCollection) {

        String userName = externalTableCollection.getUserName();

        //根据用户名称查询用户信息（主要获取userId,部门）
        User user;
        try {
            user = securityConsumer.findByUserName(userName);
            if (null == user) {
                log.error("用户信息不存在");
                return ResultBean.error("用户信息不存在");
            }
        } catch (Exception e) {
            log.error("根据用户名：" + userName + " 查询用户信息失败", e);
            return ResultBean.error("根据用户名：" + userName + " 查询用户信息失败");
        }

//        try {
//            /** step1：插入前置机数据 **/
//            int serverId = saveFrontEndServer(user, externalTableCollection);
//
//            /** step2：插入库数据 **/
//            DataSource dataSource = saveDatasource(user, externalTableCollection, serverId);
//
//            /** step3：插入模式数据 **/
//            Integer schemaId = metadataSchemaService.insertAndQuerySchemaId(dataSource.getDsId(),
//                    externalTableCollection.getDbSchemaName(), userName);
//
//            /** step4：调用ETL直采插入表、字段数据 **/
//            CollectExternalTableResult result = saveTableAndFields(user, externalTableCollection, dataSource, schemaId);
//            if (result.isSuccess()) {
//                log.info("数据交换直采外部表:{}_{}_{}_{} ->成功,生成表信息 metaId:{}", externalTableCollection.getDbIp(),
//                        externalTableCollection.getDbPort(), externalTableCollection.getDbName(),
//                        externalTableCollection.getDbTableName(), result.getMetaId());
//            } else {
//                log.error("数据交换直采外部表失败,{}", result.getMessage());
//                throw new Exception("建表字段异常");
//            }
//            return ResultBean.ok(result);
//        } catch (Exception e) {
//            log.error("数据交换直采外部表异常：" + e.getMessage());
//            if (e instanceof MetacubeException) {
//                return ResultBean.error(e.getMessage());
//            } else {
//                return ResultBean.error("数据交换直采外部表异常:" + e.getMessage());
//            }
//        }


        return null;
    }
}

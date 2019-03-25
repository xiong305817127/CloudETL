package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hive.dto.HiveColumn;
import com.ys.idatrix.db.api.hive.dto.HiveDataType;
import com.ys.idatrix.db.api.hive.dto.HiveTable;
import com.ys.idatrix.db.api.hive.dto.StoredType;
import com.ys.idatrix.db.api.hive.service.HiveService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.enums.TableColumnStatusEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.*;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefBaseService;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefHiveService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHiveVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDefOverviewVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.ys.idatrix.db.api.hive.dto.StoredType.*;
import static com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum.HIVE;
import static com.ys.idatrix.metacube.common.enums.DataStatusEnum.DELETE;
import static com.ys.idatrix.metacube.common.enums.DataStatusEnum.*;
import static com.ys.idatrix.metacube.common.enums.TableColumnStatusEnum.*;

/**
 * 元数据定义-Hive表格
 * @author robin
 *
 */
@Transactional
@Slf4j
@Service("metaDefHiveService")
public class MetaDefHiveServiceImpl implements IMetaDefHiveService {

    @Autowired
    private IMetaDefBaseService metaDefBaseService;

    @Autowired
    public MetadataMapper metadataMapper;

    @Autowired
    public SnapshotMetadataMapper snapshotMetadataMapper;

    @Autowired
    private McMdHiveFieldMapper mcMdHiveFieldMapper;

    @Autowired
    private TableColumnMapper tableColumnMapper;

    @Autowired
    private McSnapshotMdHiveFieldMapper mcSnapshotMdHiveFieldMapper;

    @Autowired
    private SnapshotTableColumnMapper snapshotTableColumnMapper;

    @Autowired
    private HiveService hiveService;

    /**
     * Hive定义查询
     *
     * @param searchVO
     * @return
     */
    @Override
    public PageResultBean<MetaDefOverviewVO> hiveQueryOverview(MetadataSearchVo searchVO) {
        int pageNum = searchVO.getPageNum();
        int pageSize = searchVO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<Metadata> dataList = metadataMapper.queryMetaDataBySearchVO(searchVO);
        List<MetaDefOverviewVO> rcVOList = transferHiveMetaData(dataList);

        //用PageInfo对结果进行包装
        PageInfo<Metadata> pi = new PageInfo<Metadata>(dataList);
        Long totalNums = pi.getTotal();
        PageResultBean<MetaDefOverviewVO> rp = new PageResultBean<MetaDefOverviewVO>(pi.getPageNum(), totalNums, rcVOList);
        return rp;
    }

    private List<MetaDefOverviewVO> transferHiveMetaData(List<Metadata> dataList){
        List<MetaDefOverviewVO> overviewList = new ArrayList<>();
        if(CollectionUtils.isEmpty(dataList)){
            return overviewList;
        }
        for(Metadata data:dataList){
            MetaDefOverviewVO vo = new MetaDefOverviewVO();
            BeanUtils.copyProperties(data, vo);
            overviewList.add(vo);
        }
        return overviewList;
    }

    private Boolean verifyColumn(List<TableColumn> columnList){
        Collection nameSet = new HashSet();
        Collection zhNameSet = new HashSet();
        Collection sizeList = new ArrayList();
        if(CollectionUtils.isNotEmpty(columnList)){
            columnList.stream().forEach( e -> {
                if(!e.getStatus().equals(3)) {
                    nameSet.add(e.getColumnName());
                    zhNameSet.add(e.getDescription());
                    sizeList.add(e.getColumnName());
                }
            });
            if(sizeList.size()!=nameSet.size() || sizeList.size()!=zhNameSet.size()){
                return false;
            }
        }
        return true;
    }

    private boolean verifyConfigParam(Long rentId, MetaDefHiveVO baseVO){

        if(StringUtils.isEmpty(baseVO.getName()) ||
                StringUtils.isEmpty(baseVO.getIdentification())){
            throw new MetaDataException("表名或者表中文名称没有配置");
        }

        if(!verifyColumn(baseVO.getColumnList())){
            throw new MetaDataException("表数据中存在相同的字段名称");
        }

        //数据检验:1.同一个节点下面不能名字相同的配置，不同节点下名字相同没关系。2.中文必须唯一。
        Long id = baseVO.getId();
        if(id==null||id.equals(0L)){
            List<Metadata> sameNameDataList = metadataMapper.queryMetaData(baseVO.getSchemaId(), baseVO.getName(), null);
            List<Metadata> existList = sameNameDataList.stream().filter(p->p.getStatus()!=2).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(existList)){
                throw new MetaDataException("同一模式下存在相同表名");
            }
            List<Metadata> sameDictList = metadataMapper.queryMetaData(baseVO.getSchemaId(), null, baseVO.getIdentification());
            List<Metadata> existSameDictList =sameDictList.stream().filter(p->p.getStatus()!=2).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(existSameDictList)){
                throw new MetaDataException("配置的表中文名称已经存在，请修改");
            }
        }else{
            List<Metadata> sameNameDataList = metadataMapper.queryMetaData(baseVO.getSchemaId(), baseVO.getName(), null);
            List<Metadata> existSameNameDataList =sameNameDataList.stream().filter(p->p.getStatus()!=2).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(existSameNameDataList)){
                if(existSameNameDataList.size()>1){
                    throw new MetaDataException("同一模式下存在相同表名");
                }else{
                    Metadata sameMetaData = existSameNameDataList.get(0);
                    if(!sameMetaData.getId().equals(id)){
                        throw new MetaDataException("同一模式下存在相同表名");
                    }
                }
            }

            List<Metadata> sameDictList = metadataMapper.queryMetaData(baseVO.getSchemaId(), null, baseVO.getIdentification());
            List<Metadata> existSameDictList =sameDictList.stream().filter(p->p.getStatus()!=2).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(existSameDictList)){

                if(sameDictList.size()>1){
                    throw new MetaDataException("配置的表中文名称已经存在，请修改");
                }else{
                    Metadata sameMetaData = existSameDictList.get(0);
                    if(!sameMetaData.getId().equals(id)){
                        throw new MetaDataException("配置的表中文名称已经存在，请修改");
                    }
                }
            }
        }
        return true;
    }

    /**
     * 保存草稿状态
     *
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    @Override
    public Metadata saveDraft(Long rentId, String user, MetaDefHiveVO baseVO) {

        if(!verifyConfigParam(rentId, baseVO)){
            return null;
        }
        return saveHiveInfo(rentId, user, baseVO,false);
    }

    /**
     * 存储Hive列数据
     * @param user
     * @param filedTotalList
     */
    private void saveHiveColumn(String user,List<TableColumn> filedTotalList){
        if(CollectionUtils.isNotEmpty(filedTotalList)){
            for(TableColumn column: filedTotalList){
                Integer status = column.getStatus();
                if(status==null || status.equals(SAME.getValue())){
                    continue;
                }else if(status.equals(CREATE.getValue())){
                    column.setTableId(column.getTableId());
                    column.setCreator(user);
                    column.setModifier(user);
                    column.setCreateTime(new Date());
                    column.setModifyTime(new Date());
                    //新建需要增加location
                    if(column.getLocation()==null || column.getLocation().equals(0)){
                        int max = tableColumnMapper.selectMaxLocationByTableId(column.getTableId());
                        column.setLocation(max+1);
                    }
                    tableColumnMapper.insertSelective(column);
                }else if(status.equals(CHANGE.getValue())){
                    TableColumn tableInfo = tableColumnMapper.selectByPrimaryKey(column.getId());
                    if(tableInfo!=null){

                        BeanUtils.copyProperties(column, tableInfo);
                        column.setModifier(user);
                        column.setModifyTime(new Date());
                        tableColumnMapper.updateByPrimaryKeySelective(column);
                    }

                }else if(status.equals(TableColumnStatusEnum.DELETE.getValue())){
                    TableColumn tableInfo = tableColumnMapper.selectByPrimaryKey(column.getId());
                    if(tableInfo!=null){
                        tableInfo.setStatus(column.getStatus());
                        tableInfo.setIsDeleted(true);
                        tableInfo.setModifier(user);
                        tableInfo.setModifyTime(new Date());
                        tableColumnMapper.updateByPrimaryKeySelective(tableInfo);
                    }
                    //tableColumnMapper.delete(column.getId());
                }
            }
        }
    }

    /**
     * 存储HIVE主表
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    private Metadata saveHiveInfo(Long rentId, String user, MetaDefHiveVO baseVO,boolean execFlag) {
        //更新主表
        Metadata mainData = null;
        Long id = baseVO.getId();
        if(id==null||id.equals(0L)){
            mainData = new Metadata();
            BeanUtils.copyProperties(baseVO, mainData);
            mainData.setCreator(user);
            mainData.setCreateTime(new Date());
            mainData.setRenterId(rentId);
            mainData.setVersion(1);

        }else{
            mainData = metadataMapper.selectByPrimaryKey(id);
            if(execFlag){
                int version = mainData.getVersion();
                mainData.setVersion(version+1);
            }
            BeanUtils.copyProperties(baseVO, mainData);
        }
        mainData.setModifier(user);
        mainData.setModifyTime(new Date());
        int status = DRAFT.getValue();
        if(execFlag){
            status = VALID.getValue();
        }
        mainData.setStatus(status);
        mainData.setDatabaseType(HIVE.getCode());
        if(id==null || id.equals(0L)){
            metadataMapper.insertSelective(mainData);
        }else{
            metadataMapper.updateByPrimaryKeySelective(mainData);
        }

        //更新从表 mc_md_hive_field
        McMdHiveFieldPO hiveField = null;
        if(id==null||id.equals(0L)){
            hiveField = new McMdHiveFieldPO(user);
        }else{
            hiveField = mcMdHiveFieldMapper.selectByPrimaryKey(id);
        }
        hiveField.setFieldsTerminated(baseVO.getFieldsTerminated());
        hiveField.setLinesTerminated(baseVO.getLinesTerminated());
        hiveField.setLocation(baseVO.getLocation());
        hiveField.setIsExternalTable(baseVO.getIsExternalTable());
        hiveField.setNullDefined(baseVO.getNullDefined());
        hiveField.setStoreFormat(baseVO.getStoreFormat());
//        BeanUtils.copyProperties(baseVO, hiveField);
        hiveField.setId(mainData.getId());
        hiveField.setModifier(user);
        hiveField.setModifyTime(new Date());
        if(id==null||id.equals(0L)){
            mcMdHiveFieldMapper.insertSelective(hiveField);
        }else{
            mcMdHiveFieldMapper.updateByPrimaryKeySelective(hiveField);
        }

        List<TableColumn> filedTotalList = new ArrayList<>();

        //处理HIVE分区
        List<TableColumn> partitionList = baseVO.getColumnList();
        if(CollectionUtils.isNotEmpty(partitionList)){
            int partitionLen = partitionList.size();
            int partitionIndexAuto = 1;
            for(int index=0; index<partitionLen; index++){
                TableColumn column = partitionList.get(index);
                column.setTableId(mainData.getId());
                if(column.getIsPartition()&&!column.getStatus().equals(3)) {
                    column.setIndexPartition(partitionIndexAuto++);  //重新分区排序
                }
                filedTotalList.add(column);
            }
        }
        saveHiveColumn(user, filedTotalList);
        return metadataMapper.selectByPrimaryKey(mainData.getId());
    }

    /**
     * 保存生效状态
     *
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    @Override
    public Metadata saveExec(Long rentId, String user, MetaDefHiveVO baseVO){

        if(CollectionUtils.isEmpty(baseVO.getColumnList())){
            throw new MetaDataException("HBase字段没有配置");
        }

        if(!verifyConfigParam(rentId, baseVO)){
            return null;
        }

        //TODO:缺少AVRO支持，需要在dbProxy里面增加，Hive格式支持
        Map<String, StoredType> typeMap = new HashMap<>();
        typeMap.put("SEQUENCEFILE",SEQUENCEFILE);
        typeMap.put("TEXTFILE",TEXTFILE);
        typeMap.put("PARQUET",PARQUET);
        typeMap.put("AVRO",PARQUET);

        //TODO: 目前主要考虑新增情况，修改情况暂不考虑
        HiveTable hiveTable = new HiveTable(baseVO.getName(), baseVO.getDatabaseName(), baseVO.getRemark());
        List<TableColumn>  filedList = baseVO.getColumnList();
        boolean fieldFlag = false;
        if(CollectionUtils.isNotEmpty(filedList) ){
            for(TableColumn pro : filedList) {
                if(pro.getStatus().equals(TableColumnStatusEnum.DELETE.getValue())){
                    continue;
                }
                HiveColumn col = new HiveColumn(pro.getColumnName(),
                        HiveDataType.valueOf(pro.getColumnType().toUpperCase()), pro.getDescription());
                if (pro.getIsPartition()) {
                    col.setPartitionOrder(pro.getIndexPartition());
                }else{
                    fieldFlag = true;
                }
                hiveTable.addColumn(col);
            }
        }
        if(!fieldFlag){
            throw new MetaDataException("元数据定义 Hive表没有配置字段,无法应用生效");
        }

        //Hive 目前只支持新建
        Long id = baseVO.getId();
        int status = 0;
        if(id!=null && !id.equals(0L)){
            Metadata mainData = metadataMapper.selectByPrimaryKey(id);
            if(mainData!=null){
                status = mainData.getStatus();
            }
        }

        if (status==DRAFT.getValue()) {
            if(StringUtils.isNotEmpty(baseVO.getFieldsTerminated())){
                char[] fields = baseVO.getFieldsTerminated().toCharArray();
                hiveTable.setFieldsTerminated(fields[0]);
            }
            if(StringUtils.isNotEmpty(baseVO.getLinesTerminated())) {
                char[] lines = baseVO.getLinesTerminated().toCharArray();
                hiveTable.setLinesTerminated(lines[0]);
            }
            if(StringUtils.isNotEmpty(baseVO.getStoreFormat())){
                hiveTable.setStoredType(typeMap.get(baseVO.getStoreFormat()));
            }
            log.info("元数据创建Hive表格-user:{},hiveTable:{}", user, JSON.toJSONString(hiveTable));
            RespResult<SqlExecRespDto> sr = hiveService.createTable(user, hiveTable);
            log.info("元数据创建Hive返回-{}",sr.toString());
            if(!sr.isSuccess()){
                throw new MetaDataException("Hive创建表格失败 "+sr.getMsg());
            }
        } else {
            //暂时元数据未提供修改 HIVE 操作
            log.warn("HIVE 暂时不提供表修改操作");
        }
        Metadata data = saveHiveInfo(rentId, user, baseVO, true);

        //快照
        saveHiveSnop(data);

        //更新增加或修改操作到数据地图
        metaDefBaseService.updateMetadataChangeInfoToGraph(DatabaseType.Hive, data);
        return data;
    }

    private void saveHiveSnop(Metadata data){
        Long metaDataId = data.getId();
        int version = data.getVersion();
        //先保存主表快照
        SnapshotMetadata snapData = new SnapshotMetadata();
        BeanUtils.copyProperties(data, snapData);
        snapData.setId(null);
        snapData.setMetaId(data.getId());
        String description = getHiveDescription(data);
        snapData.setDetails(description);
        snapData.setVersion(data.getVersion());

        //保存Hive配置字段快照
        McMdHiveFieldPO hiveField = mcMdHiveFieldMapper.selectByPrimaryKey(metaDataId);
        if(hiveField!=null){
            McSnapshotMdHiveFieldPO snapHiveField = new McSnapshotMdHiveFieldPO();
            BeanUtils.copyProperties(hiveField, snapHiveField);
            snapHiveField.setVersion(data.getVersion());
            snapHiveField.setDetails(getHiveDescription(data));
            snapHiveField.setOriginId(data.getId());
            snapHiveField.setId(null);
            mcSnapshotMdHiveFieldMapper.insertSelective(snapHiveField);
        }

        //保存Hive列字段数据快照
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(data.getId());
        if(CollectionUtils.isNotEmpty(columnList)){
            //TableColumn进行快照的时候，需要将table_column中表格数据is_delete为false快照
            for(TableColumn column: columnList){
                if(!column.getIsDeleted()){ //列不为delete时候才增加
                    SnapshotTableColumn snapColumn = new SnapshotTableColumn();
                    BeanUtils.copyProperties(column, snapColumn);
                    snapColumn.setVersion(version);
                    snapColumn.setId(null);
                    snapColumn.setColumnId(column.getId());
                    snapshotTableColumnMapper.insertSelective(snapColumn);
                }
            }
        }
        return;
    }

    /**
     * 获取Hive版本变更描述
     * @param metadata
     * @return
     */
    private String getHiveDescription(Metadata metadata){
        return "数据变更";
    }

    /**
     * HDFS定义删除
     *
     * @param rentId
     * @param user
     * @param id
     * @return
     */
    @Override
    public Long delete(Long rentId, String user, Long id) {

        Metadata data = metadataMapper.selectByPrimaryKey(id);
        if(data==null){
            throw new MetaDataException("存储系统中没有该记录");
        }
        data.setModifier(user);
        data.setModifyTime(new Date());
        data.setStatus(DELETE.getValue());
        metadataMapper.updateByPrimaryKeySelective(data);
        metaDefBaseService.updateMetadataDeleteInfoToGraph(DatabaseType.Hive, id);
        return id;
    }

    /**
     * Hive获取详情
     *
     * @param rentId
     * @param user
     * @param id
     * @return
     */
    @Override
    public MetaDefHiveVO getDetail(Long rentId, String user, Long id){

        Metadata data = metadataMapper.selectByPrimaryKey(id);
        if(data==null){
            throw new MetaDataException("没有该Hive表详细数据");
        }
        McMdHiveFieldPO fieldPO = mcMdHiveFieldMapper.selectByPrimaryKey(id);
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(id);

        MetaDefHiveVO hiveVO = new MetaDefHiveVO();
        BeanUtils.copyProperties(data, hiveVO);

        hiveVO.setFieldsTerminated(fieldPO.getFieldsTerminated());
        hiveVO.setLinesTerminated(fieldPO.getLinesTerminated());
        hiveVO.setLocation(fieldPO.getLocation());
        hiveVO.setIsExternalTable(fieldPO.getIsExternalTable());
        hiveVO.setNullDefined(fieldPO.getNullDefined());
        hiveVO.setStoreFormat(fieldPO.getStoreFormat());

        List<TableColumn> fieldList = new ArrayList<>();
        for(TableColumn column: columnList){
            if(column.getStatus().equals(3)){
                continue;
            }
            fieldList.add(column);
        }
        if(CollectionUtils.isNotEmpty(columnList)){
            hiveVO.setColumnList(fieldList);
        }
        return hiveVO;
    }
}

package com.ys.idatrix.metacube.metamanage.service.impl;

import static com.ys.idatrix.db.api.hive.dto.StoredType.PARQUET;
import static com.ys.idatrix.db.api.hive.dto.StoredType.SEQUENCEFILE;
import static com.ys.idatrix.db.api.hive.dto.StoredType.TEXTFILE;
import static com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum.HIVE;
import static com.ys.idatrix.metacube.common.enums.DataStatusEnum.DELETE;
import static com.ys.idatrix.metacube.common.enums.DataStatusEnum.DRAFT;
import static com.ys.idatrix.metacube.common.enums.DataStatusEnum.VALID;
import static com.ys.idatrix.metacube.common.enums.TableColumnStatusEnum.CHANGE;
import static com.ys.idatrix.metacube.common.enums.TableColumnStatusEnum.CREATE;
import static com.ys.idatrix.metacube.common.enums.TableColumnStatusEnum.SAME;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ys.idatrix.db.api.hive.dto.HiveColumn;
import com.ys.idatrix.db.api.hive.dto.HiveDataType;
import com.ys.idatrix.db.api.hive.dto.HiveTable;
import com.ys.idatrix.db.api.hive.dto.StoredType;
import com.ys.idatrix.db.api.hive.service.HiveService;
import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.McMdHiveFieldPO;
import com.ys.idatrix.metacube.metamanage.domain.McSnapshotMdHiveFieldPO;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.SnapshotMetadata;
import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableColumn;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.mapper.McMdHiveFieldMapper;
import com.ys.idatrix.metacube.metamanage.mapper.McSnapshotMdHiveFieldMapper;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.SnapshotMetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.SnapshotTableColumnMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableColumnMapper;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefBaseService;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefHiveService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHiveVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDefOverviewVO;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private boolean verifyConfigParam(Long rentId, MetaDefHiveVO baseVO){

        if(StringUtils.isEmpty(baseVO.getName()) ||
                StringUtils.isEmpty(baseVO.getIdentification())){
            throw new MetaDataException("表名或者表中文名称没有配置");
        }

        //数据检验:1.同一个节点下面不能名字相同的配置，不同节点下名字相同没关系。2.中文必须唯一。
        Long id = baseVO.getId();
        if(id==null||id.equals(0L)){
            List<Metadata> sameNameDataList = metadataMapper.queryMetaData(baseVO.getSchemaId(), baseVO.getName(), null);
            if(CollectionUtils.isNotEmpty(sameNameDataList)){
                throw new MetaDataException("同一模式下存在相同表名");
            }
            List<Metadata> sameDictList = metadataMapper.queryMetaData(baseVO.getSchemaId(), null, baseVO.getIdentification());
            if(CollectionUtils.isNotEmpty(sameDictList)){
                throw new MetaDataException("配置的表中文名称已经存在，请修改");
            }
        }else{
            List<Metadata> sameNameDataList = metadataMapper.queryMetaData(baseVO.getSchemaId(), baseVO.getName(), null);
            if(CollectionUtils.isNotEmpty(sameNameDataList)){

                if(sameNameDataList.size()>1){
                    throw new MetaDataException("同一模式下存在相同表名");
                }else{
                    Metadata sameMetaData = sameNameDataList.get(0);
                    if(!sameMetaData.getId().equals(id)){
                        throw new MetaDataException("同一模式下存在相同表名");
                    }
                }
            }

            List<Metadata> sameDictList = metadataMapper.queryMetaData(baseVO.getSchemaId(), null, baseVO.getIdentification());
            if(CollectionUtils.isNotEmpty(sameDictList)){

                if(sameDictList.size()>1){
                    throw new MetaDataException("配置的表中文名称已经存在，请修改");
                }else{
                    Metadata sameMetaData = sameDictList.get(0);
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
                        column.setCreator(tableInfo.getCreator());
                        column.setCreateTime(tableInfo.getCreateTime());
                        tableColumnMapper.updateByPrimaryKeySelective(column);
                    }

                }else if(status.equals(DELETE.getValue())){
                    TableColumn tableInfo = tableColumnMapper.selectByPrimaryKey(column.getId());
                    if(tableInfo!=null){
                        tableInfo.setIsDeleted(true);
                        tableInfo.setCreator(tableInfo.getCreator());
                        tableInfo.setCreateTime(tableInfo.getCreateTime());
                        tableColumnMapper.updateByPrimaryKeySelective(column);
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
        }
        BeanUtils.copyProperties(baseVO, mainData);
        mainData.setModifier(user);
        mainData.setModifyTime(new Date());
        int status = DRAFT.getValue();
        if(!execFlag){
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
            mcMdHiveFieldMapper.selectByPrimaryKey(id);
        }
        BeanUtils.copyProperties(baseVO, hiveField);
        hiveField.setId(id);
        hiveField.setModifier(user);
        hiveField.setModifyTime(new Date());
        if(id==null||id.equals(0L)){
            mcMdHiveFieldMapper.updateByPrimaryKeySelective(hiveField);
        }else{
            mcMdHiveFieldMapper.insertSelective(hiveField);
        }

        List<TableColumn> filedTotalList = new ArrayList<>();

        //处理HIVE分区
        List<TableColumn> partitionList = baseVO.getColumnList();
        if(CollectionUtils.isNotEmpty(partitionList)){
            int partitionLen = partitionList.size();
            for(int index=0; index<partitionLen; index++){
                TableColumn column = partitionList.get(index);
                Integer partitionIndex = column.getIndexPartition();
                if(partitionIndex==null || partitionIndex.equals(0)){
                    column.setIndexPartition(index);
                }else{
                    column.setIndexPartition(partitionIndex);
                }
                column.setIsPartition(true);
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
        typeMap.put("TEXFILE",TEXTFILE);
        typeMap.put("PARQUET",PARQUET);
        typeMap.put("AVRO",PARQUET);

        //TODO: 目前主要考虑新增情况，修改情况暂不考虑
        HiveTable hiveTable = new HiveTable(baseVO.getName(), baseVO.getDatabaseName(), baseVO.getRemark());
        List<TableColumn>  filedList = baseVO.getColumnList();
        boolean fieldFlag = false;
        if(CollectionUtils.isNotEmpty(filedList)){
            for(TableColumn pro : filedList) {
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
        if (id==null||id.equals(0L)) {
//            hiveTable.setFieldsTerminated(baseVO.getFieldsTerminated().charAt(0));
//            hiveTable.setLinesTerminated(baseVO.getLinesTerminated().charAt(0));
//            hiveTable.setStoredType(typeMap.get(baseVO.getStoreFormat()));
//            log.info("元数据创建Hive表格-user:{},hiveTable:{}", user, JSON.toJSONString(hiveTable));
//            SqlExecuteResult sr = hiveService.createTable(user, hiveTable);
//            log.info("元数据创建Hive返回-{}",sr.toString());
//            if(!sr.isSuccess()){
//                throw new MetaDataException("Hive创建表格失败 "+sr.getMessage());
//            }
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
        BeanUtils.copyProperties(fieldPO, hiveVO);

        List<TableColumn> fieldList = new ArrayList<>();
        for(TableColumn column: columnList){
            if(column.getIsPartition()){
                fieldList.add(column);
            }else{
                fieldList.add(column);
            }
        }
        if(CollectionUtils.isNotEmpty(columnList)){
            hiveVO.setColumnList(fieldList);
        }
        return hiveVO;
    }
}

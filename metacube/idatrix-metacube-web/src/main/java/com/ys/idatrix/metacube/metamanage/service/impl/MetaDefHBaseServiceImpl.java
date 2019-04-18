package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hbase.dto.DataType;
import com.ys.idatrix.db.api.hbase.dto.HBaseColumn;
import com.ys.idatrix.db.api.hbase.dto.HBaseTable;
import com.ys.idatrix.db.api.hbase.dto.PrimaryKey;
import com.ys.idatrix.db.api.hbase.service.HBaseService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.enums.TableColumnStatusEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.SnapshotMetadata;
import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableColumn;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.SnapshotMetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.SnapshotTableColumnMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableColumnMapper;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefBaseService;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefHBaseService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHbaseVO;
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

import static com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum.HBASE;
import static com.ys.idatrix.metacube.common.enums.DataStatusEnum.DELETE;
import static com.ys.idatrix.metacube.common.enums.DataStatusEnum.*;
import static com.ys.idatrix.metacube.common.enums.TableColumnStatusEnum.*;

/**
 * 元数据定义-Hbase实现
 * @author robin
 *
 */
@Transactional
@Slf4j
@Service("metaDefHBaseService")
public class MetaDefHBaseServiceImpl implements IMetaDefHBaseService {

    @Autowired
    private IMetaDefBaseService metaDefBaseService;

    @Autowired
    private MetadataMapper metadataMapper;

    @Autowired
    private SnapshotMetadataMapper snapshotMetadataMapper;

    @Autowired
    private TableColumnMapper tableColumnMapper;

    @Autowired
    private SnapshotTableColumnMapper snapshotTableColumnMapper;

    @Autowired
    private HBaseService hBaseService;

    /**
     * HBase定义查询
     *
     * @param searchVO
     * @return
     */
    @Override
    public PageResultBean<MetaDefOverviewVO> hbaseQueryOverview(MetadataSearchVo searchVO) {

        int pageNum = searchVO.getPageNum();
        int pageSize = searchVO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<Metadata> dataList = metadataMapper.queryMetaDataBySearchVO(searchVO);
        List<MetaDefOverviewVO> rcVOList = transferHBaseMetaData(dataList);

        //用PageInfo对结果进行包装
        PageInfo<Metadata> pi = new PageInfo<Metadata>(dataList);
        Long totalNums = pi.getTotal();
        PageResultBean<MetaDefOverviewVO> rp = new PageResultBean<MetaDefOverviewVO>(pi.getPageNum(), totalNums, rcVOList);
        return rp;
    }

    private List<MetaDefOverviewVO> transferHBaseMetaData(List<Metadata> dataList){
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

    private boolean verifyConfigParam(Long rentId, MetaDefHbaseVO baseVO){
        //数据检验:1.同一个节点下面不能名字相同的配置，不同节点下名字相同没关系。2.中文必须唯一。
        if(StringUtils.isEmpty(baseVO.getName()) ||
                StringUtils.isEmpty(baseVO.getIdentification())){
            throw new MetaDataException("表名或者表中文名称没有配置");
        }
        if(!verifyColumn(baseVO.getColumnList())){
            throw new MetaDataException("表数据中存在相同的字段名称");
        }

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

                if(existSameDictList.size()>1){
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
     * HBase保存草稿状态
     *
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    @Override
    public Metadata saveDraft(Long rentId, String user, MetaDefHbaseVO baseVO) {

        if(!verifyConfigParam(rentId, baseVO)){
            return null;
        }
        return saveHBaseInfo(rentId, user, baseVO, false);
    }

    /**
     * 存储HBase列数据
     * @param user
     * @param filedTotalList
     */
    private void saveHBaseColumn(String user,Long tableId, List<TableColumn> filedTotalList){
        if(CollectionUtils.isNotEmpty(filedTotalList)){
            for(TableColumn column: filedTotalList){
                Integer status = column.getStatus();
                if(status==null || status.equals(SAME.getValue())){
                    continue;
                }else if(status.equals(CREATE.getValue())){
                    column.setTableId(tableId);
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
                        column.setModifyTime(new Date());
                        column.setModifier(user);
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
    private Metadata saveHBaseInfo(Long rentId, String user, MetaDefHbaseVO baseVO, boolean execFlag) {
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
        mainData.setDatabaseType(HBASE.getCode());
        if(id==null || id.equals(0L)){
            metadataMapper.insertSelective(mainData);
        }else{
            metadataMapper.updateByPrimaryKeySelective(mainData);
        }
        List<TableColumn> filedTotalList = baseVO.getColumnList();
        saveHBaseColumn(user, mainData.getId(), filedTotalList);
        return mainData;
    }


    /**
     * HBase保存生效状态
     *
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    @Override
    public Metadata saveExec(Long rentId, String user, MetaDefHbaseVO baseVO) {

        if(CollectionUtils.isEmpty(baseVO.getColumnList())){
            throw new MetaDataException("HBase字段没有配置");
        }
        if(!verifyConfigParam(rentId, baseVO)){
            return null;
        };


        HBaseTable hBaseTable = new HBaseTable();
        PrimaryKey pk = null;
        hBaseTable.setNamespace(baseVO.getDatabaseName());
        hBaseTable.setTableName(baseVO.getName());
        List<TableColumn> columnList = baseVO.getColumnList();

        for (TableColumn pro : columnList) {

            HBaseColumn col = new HBaseColumn(pro.getDescription(), pro.getColumnName(),
                    DataType.valueOf(pro.getColumnType().toUpperCase()));
            hBaseTable.addColumn(col);
            if (pro.getIsPk()) {
                if (null == pk) {
                    pk = new PrimaryKey("PK", col);
                    hBaseTable.setPrimaryKey(pk);
                } else {
                    pk.getColumns().add(col);
                    hBaseTable.setPrimaryKey(pk);
                }
            }
        }

        //HBase 目前只支持建表
        int status = 0;
        if(baseVO.getId()!=null && !baseVO.getId().equals(0L)){
            Metadata mainData = metadataMapper.selectByPrimaryKey(baseVO.getId());
            if(mainData!=null){
                status = mainData.getStatus();
            }
        }
        if (status==DRAFT.getValue()) {

            log.info("元数据定义HBase调用-user:{},HBaseTable :{}", user, JSON.toJSONString(hBaseTable));
            RespResult<SqlExecRespDto> sr = hBaseService.createTable(user, hBaseTable);
            log.info("元数据定义HBase返回-{}",sr.toString());
            if(!sr.isSuccess()){
                throw new MetaDataException("HBase创建表格失败 "+ sr.getMsg());
            }
        } else {
            //暂时元数据未提供修改 HBase 操作
            log.warn("HBase 暂时不提供表修改操作");
            throw new MetaDataException("HBase 暂时不提供表修改操作");
        }

        Metadata  data = saveHBaseInfo(rentId, user, baseVO, true);
        //HBase快照
        saveHBaseSnop(data);

        //更新创建或者修改操作到Hbase
        metaDefBaseService.updateMetadataChangeInfoToGraph(DatabaseType.Hbase, data);
        return data;
    }

    private void saveHBaseSnop(Metadata data){
        Long metaDataId = data.getId();
        int version = data.getVersion();
        //先保存主表快照
        SnapshotMetadata snapData = new SnapshotMetadata();
        BeanUtils.copyProperties(data, snapData);
        snapData.setId(null);
        snapData.setMetaId(data.getId());
        String description = getHBaseDescription(data);
        snapData.setDetails(description);
        snapData.setVersion(data.getVersion());

        //保存HBase列字段数据快照
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

    private String getHBaseDescription(Metadata data){
        return "数据变更";
    }

    /**
     * HBase删除
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
        //更新删除操作到数据地图
        metaDefBaseService.updateMetadataDeleteInfoToGraph(DatabaseType.Hbase, id);
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
    public MetaDefHbaseVO getDetail(Long rentId, String user, Long id) {

        Metadata data = metadataMapper.selectByPrimaryKey(id);
        if(data==null){
            throw new MetaDataException("没有该HBase表详细数据");
        }

        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(id);
        MetaDefHbaseVO hbaseVO = new MetaDefHbaseVO();
        BeanUtils.copyProperties(data, hbaseVO);
        if(CollectionUtils.isNotEmpty(columnList)){
            hbaseVO.setColumnList(columnList);
        }
        return hbaseVO;
    }
}

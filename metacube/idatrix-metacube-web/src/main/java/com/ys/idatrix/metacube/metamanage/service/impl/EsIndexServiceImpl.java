package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.idatrix.es.api.dto.req.index.FieldDto;
import com.idatrix.es.api.dto.req.index.IndexDto;
import com.idatrix.es.api.dto.req.index.MappingDto;
import com.idatrix.es.api.dto.req.index.NewIndexDto;
import com.idatrix.es.api.dto.resp.RespResult;
import com.idatrix.es.api.enums.FieldType;
import com.idatrix.es.api.service.IIndexManageService;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.enums.EsAnalyzerEnum;
import com.ys.idatrix.metacube.common.enums.EsFieldTypeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.common.utils.XStringUtils;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.*;
import com.ys.idatrix.metacube.metamanage.service.EsIndexService;
import com.ys.idatrix.metacube.metamanage.service.MetadataService;
import com.ys.idatrix.metacube.metamanage.service.TagService;
import com.ys.idatrix.metacube.metamanage.service.ThemeService;
import com.ys.idatrix.metacube.metamanage.vo.request.EsIndexVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EsIndexServiceImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/23
 */
@Slf4j
@Service
public class EsIndexServiceImpl implements EsIndexService {

    @Autowired(required = false)
    private EsMetadataMapper esMetadataMapper;

    @Autowired(required = false)
    private EsSnapshotMetadataMapper esSnapshotMetadataMapper;

    @Autowired(required = false)
    private EsFieldMapper esFieldMapper;

    @Autowired(required = false)
    private EsSnapshotFieldMapper esSnapshotFieldMapper;

    @Autowired(required = false)
    private McSchemaMapper mcSchemaMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private MetadataService metadataService;

    @Reference
    private IIndexManageService indexManageService;

    @Override
    public PageResultBean<EsMetadataPO> search(MetadataSearchVo searchVo) {

        Preconditions.checkNotNull(searchVo, "请求参数为空");

        if (searchVo.getRenterId() == null) {
            searchVo.setRenterId(UserUtils.getRenterId());
        }

        // 分页
        PageHelper.startPage(searchVo.getPageNum(), searchVo.getPageSize());

        // 查询
        List<EsMetadataPO> list = esMetadataMapper.search(searchVo);

        PageInfo<EsMetadataPO> pageInfo = new PageInfo<>(list);

        return PageResultBean.builder(pageInfo.getTotal(), list);

    }


    @Override
    public EsIndexVO queryEsDetail(Long metaId) {

        //查询基本信息
        EsMetadataPO esMetadataPO = esMetadataMapper.selectByPrimaryKey(metaId);

        //查询字段信息
        List<EsFieldPO> esFieldPOList = esFieldMapper.queryFieldsByIndexId(metaId);

        EsIndexVO esIndexVO = new EsIndexVO();
        BeanUtils.copyProperties(esMetadataPO, esIndexVO);

        esIndexVO.setFieldPOList(esFieldPOList);

        metadataService.wrapMetadataBaseVO(esIndexVO);

        return esIndexVO;
    }


    @Override
    public boolean checkExistsIndex(Long schemaId, String schemaName, boolean isDrafted) {

        if (StringUtils.isBlank(schemaName)) {
            schemaName = getSchemaName(schemaId);
        }

        List<EsMetadataPO> metadataList = esMetadataMapper.findBySchemaId(schemaId);
        if (CollectionUtils.isNotEmpty(metadataList)) {

            //是否存在以及创建成功的
            boolean hasCreated = metadataList.stream().anyMatch(metadata -> metadata.getStatus().equals(1));
            if (hasCreated) {
                log.warn("已创建过，不能再次创建", schemaName);
                throw new MetaDataException("已创建过，不能再次创建");
            }

            //存在一条，且状态是草稿的
            if (metadataList.size() == 1 && metadataList.get(0).getStatus().equals(0)) {
                log.warn("已存在草稿状态记录，不能再次创建", schemaName);
                throw new MetaDataException("已存在草稿状态记录，不能再次创建");
            }
        }

        //es 物理是否存在
        if (!isDrafted) {
            RespResult<Boolean> result = indexManageService.hasExistsIndex(schemaName);
            if (result.isSuccess()) {
                if (result.getData()) {
                    throw new MetaDataException("ES索引库中已存在该索引");
                }
            } else {
                throw new MetaDataException(result.getMsg());
            }
        }

        return false;

    }


    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public boolean saveOrCreatedIndex(EsIndexVO esIndexVO, boolean isDrafted) {

        checkExistsIndex(esIndexVO.getSchemaId(), esIndexVO.getSchemaName(), isDrafted);

        validatedBaseAndField(esIndexVO);

        esIndexVO.setSchemaName(getSchemaName(esIndexVO.getSchemaId()));

        wrapPrepIndexFields(esIndexVO, true);

        if (isDrafted) {
            esIndexVO.setStatus(0);
            processSaveToDB(esIndexVO, false, false);
        } else {
            esIndexVO.setStatus(1);
            processSaveToDB(esIndexVO, true, false);
        }

        return true;

    }


    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public boolean updateIndex(EsIndexVO esIndexVO) {

        if (null == esIndexVO.getId()) {
            throw new MetaDataException("修改记录主键标识空值");
        }

        validatedBaseAndField(esIndexVO);

        esIndexVO.setSchemaName(getSchemaName(esIndexVO.getSchemaId()));

        wrapPrepIndexFields(esIndexVO, false);

        processSaveToDB(esIndexVO, true, true);

        return true;
    }


    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public boolean softDeleteIndex(List<Long> ids) {

        boolean success = true;
        for (Long id : ids) {

            EsMetadataPO metadata = esMetadataMapper.selectByPrimaryKey(id);
            String indexName = getSchemaName(metadata.getSchemaId());

            RespResult<Boolean> result = indexManageService.deleteIndex(Lists.newArrayList(indexName));
            if (!result.isSuccess()) {
                throw new MetaDataException(result.getMsg());
            }

            if (result.getData()) {
                int row = esMetadataMapper.softDelete(id);
                log.info("删除索引:{} -> {}", id, (row == 1));

                //主题递减使用数
                themeService.decreaseProgressively(metadata.getThemeId());

            } else {
                success = false;
                break;
            }
        }

        return success;

    }


    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public boolean openOrStartIndex(Long id, boolean isOpen) {

        EsMetadataPO metadata = esMetadataMapper.selectByPrimaryKey(id);
        String indexName = getSchemaName(metadata.getSchemaId());

        RespResult<Boolean> result = indexManageService.openOrStopIndex(indexName, isOpen);
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        } else {
            if (result.getData()) {
                esMetadataMapper.updateIsOpen(id, isOpen);
            }
        }

        return result.getData();
    }


    @Override
    public List<Map<Long, Integer>> queryVersionsByMetaId(Long id) {
        List<EsSnapshotMetadata> metadataList = esSnapshotMetadataMapper.getSnapshotMetadataByMetaId(id);
        List<Map<Long, Integer>> snapshotVersions = Lists.newArrayList();
        for (EsSnapshotMetadata snapshotMetadata : metadataList) {
            Map<Long, Integer> snapshotMetaMap = Maps.newHashMap();
            snapshotMetaMap.put(snapshotMetadata.getId(), snapshotMetadata.getVersion());
            snapshotVersions.add(snapshotMetaMap);
        }
        return snapshotVersions;
    }


    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public boolean switchIndex(Long id, int targetVersion) {

        EsMetadataPO metadata = esMetadataMapper.selectByPrimaryKey(id);
        String indexName = getSchemaName(metadata.getSchemaId());
        esMetadataMapper.switchVersion(id, targetVersion);

        //字段表切换操作：先清空，再从快照表中恢复目标版本的数据到字段表
        esFieldMapper.deleteByIndexId(id);
        List<EsSnapshotFieldPO> snapshotFieldPOList = esSnapshotFieldMapper.selectByMetaIdAndVersion(id, targetVersion);
        List<EsFieldPO> switchFields = Lists.newArrayList();
        for (EsSnapshotFieldPO snapshotField : snapshotFieldPOList) {
            EsFieldPO esFieldPO = new EsFieldPO();
            BeanUtils.copyProperties(snapshotField, esFieldPO);
            switchFields.add(esFieldPO);
        }
        esFieldMapper.batchInsert(switchFields);

        //主表切换操作：直接查询快照表，然后修改表字段值
        EsSnapshotMetadata snapshotMetadata = esSnapshotMetadataMapper.selectByMetaIdAndVersion(id, targetVersion);
        EsMetadataPO switchMetadata = new EsMetadataPO();
        BeanUtils.copyProperties(snapshotMetadata, switchMetadata);
        switchMetadata.setMaxLocation(metadata.getMaxLocation());
        switchMetadata.setMaxVersion(metadata.getMaxVersion());
        esMetadataMapper.updateByPrimaryKeySelective(switchMetadata);

        String oldIndexName = indexName + "_" + metadata.getVersion();
        String newIndexName = indexName + "_" + targetVersion;
        RespResult<Boolean> result = indexManageService.switchIndexByVersion(indexName, oldIndexName, newIndexName);
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        } else {
            log.info("索引:{} 由版本[{}] -> [{}] 切换成功", indexName, metadata.getVersion(), targetVersion);
        }

        return result.getData();
    }


    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public void processSaveToDB(EsIndexVO esIndexVO, boolean hasSnapshot, boolean hasUpdated) {

        EsMetadataPO metadata = new EsMetadataPO();
        BeanUtils.copyProperties(esIndexVO, metadata);

        EsMetadataPO oldMetadata = null;
        if (hasUpdated) {

            oldMetadata = esMetadataMapper.selectByPrimaryKey(metadata.getId());

            metadata.fillModifyInfo(metadata, esIndexVO.getModifier());

            //主表-修改
            esMetadataMapper.updateByPrimaryKeySelective(metadata);

            //修改字段表:先删-在新增
            esFieldMapper.deleteByIndexId(metadata.getId());

            metadata.setCreator(oldMetadata.getCreator());
            metadata.setCreateTime(oldMetadata.getCreateTime());

        } else {

            metadata.fillCreateInfo(metadata, esIndexVO.getCreator());

            //主表-新增
            esMetadataMapper.insertSelective(metadata);
        }

        // 生成标签
        tagService.insertTags(esIndexVO.getTags(), UserUtils.getRenterId(), esIndexVO.getCreator(), new Date());

        //主题新增使用数
        themeService.increaseProgressively(metadata.getThemeId());

        //字段-保存-始终新增
        List<EsFieldPO> fields = esIndexVO.getFieldPOList();
        for (EsFieldPO field : fields) {
            field.setIndexId(metadata.getId());
        }
        esFieldMapper.batchInsert(fields);

        //快照数据
        if (hasSnapshot) {

            //快照-主表
            StringBuilder changeDetail = new StringBuilder();
            if (esIndexVO.getVersion().equals(1)) {
                changeDetail.append("创建索引");
            } else {

                if (esIndexVO.getHasChangeBase()) {
                    changeDetail.append("变更基本信息");
                }

                if (null != esIndexVO.getAddCnt() && esIndexVO.getAddCnt() > 0) {
                    changeDetail.append("新增字段数:" + esIndexVO.getAddCnt());
                }

                if (null != esIndexVO.getUpdateCnt() && esIndexVO.getUpdateCnt() > 0) {
                    changeDetail.append("修改字段数:" + esIndexVO.getUpdateCnt());
                }

                if (null != esIndexVO.getDeleteCnt() && esIndexVO.getDeleteCnt() > 0) {
                    changeDetail.append("删除字段数:" + esIndexVO.getDeleteCnt());
                }
            }

            EsSnapshotMetadata snapshotMetadata = new EsSnapshotMetadata();
            BeanUtils.copyProperties(metadata, snapshotMetadata);
            snapshotMetadata.setId(null)
                    .setMetaId(metadata.getId())
                    .setDetails(changeDetail.toString())
                    .setVersion(metadata.getVersion());
            esSnapshotMetadataMapper.insertSelective(snapshotMetadata);

            //快照-字段表
            List<EsSnapshotFieldPO> snapshotFields = Lists.newArrayList();
            for (EsFieldPO field : fields) {
                EsSnapshotFieldPO snapshotField = new EsSnapshotFieldPO();
                BeanUtils.copyProperties(field, snapshotField);
                snapshotField.setVersion(esIndexVO.getVersion());
                snapshotFields.add(snapshotField);
            }
            esSnapshotFieldMapper.batchInsert(snapshotFields);

            //创建索引
            createIndex(esIndexVO);

            //修改，索引自动升级切换
            if (hasUpdated) {
                String schemaName = getSchemaName(metadata.getSchemaId());
                String oldIndexName = schemaName + "_" + oldMetadata.getVersion();
                String newIndexName = schemaName + "_" + esIndexVO.getVersion();
                RespResult<Boolean> result = indexManageService.switchIndexByVersion(schemaName, oldIndexName, newIndexName);
                if (!result.isSuccess()) {
                    throw new MetaDataException(result.getMsg());
                } else {
                    log.info("索引:{} 由版本[{}] -> [{}] 升级成功", schemaName, oldMetadata.getVersion(), esIndexVO.getVersion());
                }
            }
        }
    }


    /**
     * es 索引字段校验
     *
     * @param esIndexVO
     */
    private void validatedBaseAndField(EsIndexVO esIndexVO) {

        //索引中文描述验证
        int cnt = esMetadataMapper.queryCntBySelectiveParam(esIndexVO.getIdentification(), null, esIndexVO.getRenterId());
        if (cnt > 0) {
            throw new MetaDataException("租户下已存在该中文描述的索引，请重新录入");
        }

        List<EsFieldPO> fieldPOList = esIndexVO.getFieldPOList();

        if (CollectionUtils.isEmpty(fieldPOList)) {
            throw new MetaDataException("索引字段空值");
        }

        //过滤得到非删除的字段集合
        fieldPOList = fieldPOList.stream().filter(field -> !field.getOpFlag().equals(-1)).collect(Collectors.toList());


        //字段名集合
        List<String> fieldNames = fieldPOList.stream().map(field -> field.getFieldName().toLowerCase()).collect(Collectors.toList());

        //检查字段类型
        for (EsFieldPO esFieldPO : fieldPOList) {

            String fieldName = esFieldPO.getFieldName();
            String fieldType = esFieldPO.getFieldType();
            Integer opFlag = esFieldPO.getOpFlag();

            if (!StringUtils.endsWithIgnoreCase(EsFieldTypeEnum.Text.name(), fieldType)) {
                if (StringUtils.isNotBlank(esFieldPO.getAnalyzer())) {
                    throw new MetaDataException("非[text]类型字段不用设置分析器");
                }
            }

            //修改操作
            if (null != esIndexVO.getId() && null != opFlag) {
                if (opFlag.intValue() == 0 || opFlag.intValue() == 2) {
                    if (null == esFieldPO.getId()) {
                        throw new MetaDataException("修改/未更改字段缺失唯一标识");
                    }
                }
            }

            //空值
            if (StringUtils.isBlank(fieldName) || StringUtils.isBlank(fieldType)) {
                throw new MetaDataException("字段名称或类型不能空值，请重新录入");
            }

            //字段名称重名
            long matchCount = fieldNames.stream().filter(name -> name.equals(fieldName.toLowerCase())).count();
            if (matchCount > 1) {
                throw new MetaDataException("出现同名字段 [" + fieldName + "]");
            }

            //字段类型
            boolean typeMatch = Arrays.stream(EsFieldTypeEnum.values()).anyMatch(type -> type.name().toLowerCase().equals(fieldType.trim().toLowerCase()));
            if (!typeMatch) {
                throw new MetaDataException("字段类型不在索引字段类型范围");
            }

            //分析器
            String analyzer = esFieldPO.getAnalyzer();
            if (StringUtils.isNotBlank(analyzer)) {
                boolean analyzerMatch = Arrays.stream(EsAnalyzerEnum.values()).anyMatch(type -> type.name().equals(analyzer.trim().toLowerCase()));
                if (!analyzerMatch) {
                    throw new MetaDataException("分析器类型不在索引分析器范围");
                }
            }

        }

    }


    /**
     * 获取Es NewIndexDto 对象
     *
     * @param esIndexVO
     * @return
     */
    private NewIndexDto wrapNewIndexDto(EsIndexVO esIndexVO) {

        NewIndexDto newIndexDto = new NewIndexDto()
                .setUsername(esIndexVO.getCreator())
                .setTenantId(String.valueOf(esIndexVO.getRenterId()));

        IndexDto indexDto = new IndexDto()
                .setIndexCode(esIndexVO.getSchemaName())
                .setVersion(esIndexVO.getVersion().intValue() + "");
        newIndexDto.setIndex(indexDto);

        MappingDto mappingDto = new MappingDto()
                .setType(esIndexVO.getName());
        List<MappingDto> mappingDtoList = Lists.newArrayList(mappingDto);
        newIndexDto.setMappings(mappingDtoList);

        List<FieldDto> fieldDtoList = Lists.newArrayList();
        List<EsFieldPO> indexFieldPOList = esIndexVO.getFieldPOList();
        for (EsFieldPO field : indexFieldPOList) {

            String fileType = XStringUtils.toUpperCaseFirstOne(StringUtils.lowerCase(field.getFieldType()));
            String searchAnalyzer = field.getAnalyzer();
            if (StringUtils.isNotBlank(searchAnalyzer) && StringUtils.equals(searchAnalyzer, EsAnalyzerEnum.index_ansj.name())) {
                searchAnalyzer = "query_ansj";
            }

            FieldDto fieldDto = new FieldDto()
                    .setName(field.getFieldName())
                    .setType(FieldType.valueOf(fileType))
                    .setCanIndexed(field.getCanIndex())
                    .setAnalyzer(field.getAnalyzer())
                    .setSearchAnalyzer(searchAnalyzer)
                    .setCanStore(field.getCanStore())
                    .setCanSource(field.getCanSource())
                    .setCanSearch(field.getCanAll());

            fieldDtoList.add(fieldDto);

        }

        mappingDto.setFieldDtoList(fieldDtoList);

        return newIndexDto;

    }


    /**
     * 根据页面操作，分别获新增、删除、修改/不变的字段
     *
     * @param esIndexVO
     * @param isNew
     */
    private void wrapPrepIndexFields(EsIndexVO esIndexVO, boolean isNew) {

        List<EsFieldPO> fieldPOList = esIndexVO.getFieldPOList();

        if (isNew) {
            for (int i = 1; i <= fieldPOList.size(); i++) {
                EsFieldPO fieldPO = fieldPOList.get(i - 1);
                fieldPO.setLocation(i);
                fieldPO.fillCreateInfo(fieldPO, esIndexVO.getCreator());
            }

            esIndexVO.setVersion(1);
            esIndexVO.setMaxVersion(1);
            esIndexVO.setMaxLocation(fieldPOList.size());
        } else {
            //判断表基本信息是否变更
            EsMetadataPO metadata = new EsMetadataPO();
            BeanUtils.copyProperties(esIndexVO, metadata);
            EsMetadataPO oldMetadata = esMetadataMapper.selectByPrimaryKey(esIndexVO.getId());
            if (!metadata.equals(oldMetadata)) {
                esIndexVO.setHasChangeBase(true);
            }

            //当前版本之前所有的字段、位置值
            List<EsFieldPO> oldFieldPOList = esFieldMapper.queryFieldsByIndexId(esIndexVO.getId());
            Map<Long, Integer> fieldLocationMap = oldFieldPOList.stream().collect(Collectors.toMap((key -> key.getId()), (value -> value.getLocation())));

            //最终的索引字段数据-创建索引用
            List<EsFieldPO> newFieldPOList = Lists.newArrayList();

            //获取最大location值
            Integer maxLocation = esMetadataMapper.findMaxLocation(esIndexVO.getId());
            if (null == maxLocation) {
                maxLocation = 0;
            }

            //未变更记录
            List<EsFieldPO> normalFields = fieldPOList.stream().filter(field -> field.getOpFlag().equals(0)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(normalFields)) {
                normalFields.forEach(field -> {
                    field.setLocation(fieldLocationMap.get(field.getId()));
                    field.setId(null);
                });
                newFieldPOList.addAll(normalFields);
            }

            //待修改记录
            List<EsFieldPO> upFields = fieldPOList.stream().filter(field -> field.getOpFlag().equals(2)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(upFields)) {
                upFields.forEach(field -> {
                    field.setLocation(fieldLocationMap.get(field.getId()));
                    field.setId(null);
                });
                newFieldPOList.addAll(upFields);
                esIndexVO.setUpdateCnt(upFields.size());
            }

            //待新增记录
            List<EsFieldPO> addFields = fieldPOList.stream().filter(field -> field.getOpFlag().equals(1)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(addFields)) {
                for (EsFieldPO newField : addFields) {
                    maxLocation++;
                    newField.setLocation(maxLocation);
                }
                newFieldPOList.addAll(addFields);
                esIndexVO.setAddCnt(addFields.size());
            }

            //设置用户、时间
            newFieldPOList.forEach(field -> {
                field.setId(null);
                field.fillCreateInfo(field, esIndexVO.getModifier());
            });

            esIndexVO.setStatus(1);
            esIndexVO.setMaxLocation(maxLocation);
            esIndexVO.setVersion(oldMetadata.getMaxVersion() + 1);
            esIndexVO.setMaxVersion(oldMetadata.getMaxVersion() + 1);
            esIndexVO.setFieldPOList(newFieldPOList);
        }

    }


    /**
     * 创建ES索引
     *
     * @param esIndexVO
     * @return
     */
    private boolean createIndex(EsIndexVO esIndexVO) {

        NewIndexDto newIndexDto = wrapNewIndexDto(esIndexVO);
        RespResult<Boolean> result = indexManageService.createIndex(newIndexDto);
        if (result.isSuccess()) {
            if (result.getData()) {
                log.info("索引:{},版本:{} 创建成功", esIndexVO.getSchemaName(), esIndexVO.getVersion());
                return true;
            } else {
                log.info("索引:{},版本:{} 创建不成功", esIndexVO.getSchemaName(), esIndexVO.getVersion());
                throw new MetaDataException("索引创建不成功");
            }
        } else {
            throw new MetaDataException(result.getMsg());
        }

    }


    /**
     * 根据 schemaId 查询 schemaName
     *
     * @param schemaId
     * @return
     */
    private String getSchemaName(Long schemaId) {
        McSchemaPO mcSchemaPO = mcSchemaMapper.findById(schemaId);
        if (null == mcSchemaPO) {
            throw new MetaDataException("模式不存在");
        }
        return mcSchemaPO.getName();
    }


    @Override
    public EsMetadataPO findById(Long id) {
        EsMetadataPO esMetadataPO = esMetadataMapper.selectByPrimaryKey(id);
        return esMetadataPO;
    }

}

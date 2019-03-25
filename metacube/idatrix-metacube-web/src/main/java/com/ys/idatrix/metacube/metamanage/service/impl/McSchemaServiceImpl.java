package com.ys.idatrix.metacube.metamanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.ys.idatrix.graph.service.api.dto.node.SchemaNodeDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.helper.GraphDatabaseTypeConvert;
import com.ys.idatrix.metacube.dubbo.consumer.GraphConsumer;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.McServerPO;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefBaseService;
import com.ys.idatrix.metacube.metamanage.service.McDatabaseService;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.McServerService;
import com.ys.idatrix.metacube.metamanage.vo.request.SchemaSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.response.SchemaListVO;
import com.ys.idatrix.metacube.sysmanage.service.SystemSettingsService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("schemaServiceImpl")
public class McSchemaServiceImpl implements McSchemaService {

    @Autowired
    @Qualifier("mySqlSchemaService")
    private McSchemaService mySqlSchemaService;

    @Autowired
    @Qualifier("oracleSchemaService")
    private McSchemaService oracleSchemaService;

    @Autowired
    @Qualifier("hdfsSchemaService")
    private McSchemaService hdfsSchemaService;

    @Autowired
    @Qualifier("hbaseSchemaService")
    private McSchemaService hbaseSchemaService;

    @Autowired
    @Qualifier("hiveSchemaService")
    private McSchemaService hiveSchemaService;

    @Autowired
    @Qualifier("esSchemaService")
    private McSchemaService esSchemaService;

    @Autowired
    private McSchemaMapper schemaMapper;

    @Autowired
    private McServerService serverService;

    @Autowired
    private McDatabaseService databaseService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private IMetaDefBaseService metaDefBaseService;

    @Autowired
    private GraphConsumer graphConsumer;

    @Autowired
    private OrganizationService organizationService;

    @Override
    public McSchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    private McServerPO getServerById(Long serverId) {
        return serverService.getServerById(serverId);
    }

    private McDatabasePO getDatabaseById(Long dbId) {
        return databaseService.getDatabaseById(dbId);
    }

    /**
     * 校验权限 数据中心管理员或是数据库管理员才有权限维护模式
     */
    private void authentication() {
        if (!systemSettingsService.isDataCentreAdmin()
                && !systemSettingsService.isDatabaseAdmin()) {
            throw new MetaDataException("权限不足");
        }
    }

    /**
     * 新建模式
     */
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public McSchemaPO create(McSchemaPO schemaPO) {
        authentication();

        McSchemaPO result = null;
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.MYSQL.getCode())) {
            result = mySqlSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.ORACLE.getCode())) {
            result = oracleSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.HDFS.getCode())) {
            result = hdfsSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.HBASE.getCode())) {
            result = hbaseSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.HIVE.getCode())) {
            result = hiveSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.ELASTICSEARCH.getCode())) {
            result = esSchemaService.create(schemaPO);
        }

        // 新建模式节点
        createGraphSchemaNode(schemaPO);

        // TODO 需回写安全的所属组织使用计数器
        return result;
    }

    /**
     * 注册模式 只在模式表新增记录
     */
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public McSchemaPO register(McSchemaPO schemaPO) {
        authentication();
        McSchemaPO result;
        McDatabasePO databasePO = databaseService.getDatabaseById(schemaPO.getDbId());
        // mysql修改信息需测试连接
        if (databasePO.getType().equals(DatabaseTypeEnum.MYSQL.getCode())) {
            result = mySqlSchemaService.register(schemaPO);
            createGraphSchemaNode(result);
            return result;
        }
        if (databasePO.getType().equals(DatabaseTypeEnum.ELASTICSEARCH.getCode())) {
            return esSchemaService.register(schemaPO);
        }

        result = insert(schemaPO);
        // 新建模式节点
        createGraphSchemaNode(result);
        // TODO 需回写安全的所属组织使用计数器
        return result;
    }

    /**
     * 新建数据地图模式节点
     */
    private void createGraphSchemaNode(McSchemaPO schemaPO) {
        if (!schemaPO.getDbType().equals(DatabaseTypeEnum.ELASTICSEARCH.getCode())) {
            SchemaNodeDto nodeDto = new SchemaNodeDto();
            nodeDto.setDatabaseId(schemaPO.getDbId());
            nodeDto.setDatabaseType(
                    GraphDatabaseTypeConvert.getGraphDatabaseType(schemaPO.getDbType()));
            nodeDto.setRenterId(schemaPO.getRenterId());
            nodeDto.setSchemaId(schemaPO.getId());
            nodeDto.setSchemaName(schemaPO.getName());
            nodeDto.setServerId(databaseService.getDatabaseById(schemaPO.getDbId()).getServerId());
            graphConsumer.createSchemaNode(nodeDto);
        }
    }

    /**
     * 删除模式
     */
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public McSchemaPO delete(McSchemaPO schemaPO) {

        McDatabasePO database = getDatabaseById(schemaPO.getDbId());
        if (metaDefBaseService.verifySchemaUse(DatabaseTypeEnum.getInstance(database.getType()),
                schemaPO.getId()) > 0L) {
            throw new MetaDataException("模式已被使用，不允许删除");
        }
        schemaPO.setIsDeleted(1);
        schemaMapper.update(schemaPO);

        if (database.getType().equals(DatabaseTypeEnum.MYSQL.getCode())) {
            mySqlSchemaService.delete(schemaPO);
        }
        if (database.getType().equals(DatabaseTypeEnum.HDFS.getCode())) {
            hdfsSchemaService.delete(schemaPO);
        }
        if (database.getType().equals(DatabaseTypeEnum.HBASE.getCode())) {
            hbaseSchemaService.delete(schemaPO);
        }
        if (database.getType().equals(DatabaseTypeEnum.HIVE.getCode())) {
            hiveSchemaService.delete(schemaPO);
        }

        if (!database.getType().equals(DatabaseTypeEnum.ELASTICSEARCH.getCode())) {
            graphConsumer.deleteSchemaNode(schemaPO.getId());
        }
        return schemaPO;
    }

    /**
     * 修改模式信息 模式名称不能修改
     */
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public McSchemaPO update(McSchemaPO schemaPO) {
        authentication();
        McSchemaPO oldSchemaPO = schemaMapper.getSchemaById(schemaPO.getId());
        McDatabasePO databasePO = databaseService.getDatabaseById(oldSchemaPO.getDbId());
        // mysql修改信息需测试连接
        if (databasePO.getType().equals(DatabaseTypeEnum.MYSQL.getCode())) {
            return mySqlSchemaService.update(schemaPO);
        }
        schemaMapper.update(schemaPO);
        return schemaPO;
    }

    /**
     * 模式列表
     */
    @Override
    public PageResultBean<SchemaListVO> listByPage(SchemaSearchVO searchVO) {
        // 普通用户返回空
        if (!systemSettingsService.isDataCentreAdmin()
                && !systemSettingsService.isDatabaseAdmin()) {
            return PageResultBean.empty();
        }

        PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<McSchemaPO> schemaPOList = schemaMapper.listByPage(searchVO);
        PageInfo<McSchemaPO> info = new PageInfo<>(schemaPOList);

        List<SchemaListVO> schemaVOList = convertSchemaListVO(schemaPOList);

        // 填充组织名称
        List<String> orgCodeList =
                schemaVOList.stream().map(e -> e.getOrgCode()).collect(Collectors.toList());
        String orgCodes = String.join(",", orgCodeList);
        List<Organization> orgList = organizationService.findByCodes(orgCodes);
        List<SchemaListVO> result = fillOrgNameIntoSchemaVO(schemaVOList, orgList);

        return PageResultBean.of(searchVO.getPageNum(), info.getTotal(), result);
    }

    @Override
    public SchemaListVO getSchemaListVOById(Long id) {
        McSchemaPO schemaPO = getSchemaById(id);
        List<Organization> orgList = organizationService.findByCodes(schemaPO.getOrgCode());
        List<SchemaListVO> schemaVOList = new ArrayList<>();
        SchemaListVO schemaVO = new SchemaListVO();
        BeanUtils.copyProperties(schemaPO, schemaVO);
        schemaVOList.add(schemaVO);
        List<SchemaListVO> result = fillOrgNameIntoSchemaVO(schemaVOList, orgList);
        return result.get(0);
    }
}

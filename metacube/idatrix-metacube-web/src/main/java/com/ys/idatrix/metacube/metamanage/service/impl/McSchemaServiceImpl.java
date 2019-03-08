package com.ys.idatrix.metacube.metamanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.unisecurity.api.domain.Organization;
import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.graph.service.api.dto.node.SchemaNodeDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.GraphConsumer;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.McServerPO;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefBaseService;
import com.ys.idatrix.metacube.metamanage.service.McDatabaseService;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.McServerService;
import com.ys.idatrix.metacube.metamanage.service.SystemSettingsService;
import com.ys.idatrix.metacube.metamanage.vo.request.SchemaSearchVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    private SecurityConsumer securityConsumer;

    @Autowired
    private GraphConsumer graphConsumer;

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
    @Override
    public McSchemaPO create(McSchemaPO schemaPO) {
        authentication();

        if (schemaPO.getDbType().equals(DatabaseTypeEnum.MYSQL.getCode())) {
            return mySqlSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.ORACLE.getCode())) {
            return oracleSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.HDFS.getCode())) {
            return hdfsSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.HBASE.getCode())) {
            return hbaseSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.HIVE.getCode())) {
            return hiveSchemaService.create(schemaPO);
        }
        if (schemaPO.getDbType().equals(DatabaseTypeEnum.ELASTICSEARCH.getCode())) {
            return esSchemaService.create(schemaPO);
        }

        // 新建模式节点
        if (!schemaPO.getDbType().equals(DatabaseTypeEnum.ELASTICSEARCH.getCode())) {
            SchemaNodeDto nodeDto = new SchemaNodeDto();
            nodeDto.setDatabaseId(schemaPO.getDbId());
            nodeDto.setDatabaseType(getGraphDatabaseType(schemaPO.getDbType()));
            nodeDto.setRenterId(schemaPO.getRenterId());
            nodeDto.setSchemaId(schemaPO.getId());
            nodeDto.setSchemaName(schemaPO.getName());
            nodeDto.setServerId(databaseService.getDatabaseById(schemaPO.getDbId()).getServerId());
            graphConsumer.createSchemaNode(nodeDto);
        }

        // TODO 需回写安全的所属组织使用计数器
        return null;
    }

    private DatabaseType getGraphDatabaseType(int type) {
        if (type == DatabaseTypeEnum.MYSQL.getCode()) {
            return DatabaseType.MySQL;
        }
        if (type == DatabaseTypeEnum.ORACLE.getCode()) {
            return DatabaseType.Oracle;
        }
        if (type == DatabaseTypeEnum.POSTGRESQL.getCode()) {
            return DatabaseType.PostgreSQL;
        }
        if (type == DatabaseTypeEnum.HDFS.getCode()) {
            return DatabaseType.HDFS;
        }
        if (type == DatabaseTypeEnum.HIVE.getCode()) {
            return DatabaseType.Hive;
        }
        if (type == DatabaseTypeEnum.HBASE.getCode()) {
            return DatabaseType.Hbase;
        }
        return null;
    }

    /**
     * 注册模式 只在模式表新增记录
     */
    @Override
    public McSchemaPO register(McSchemaPO schemaPO) {
        authentication();
        // TODO 需回写安全的所属组织使用计数器
        return insert(schemaPO);
    }

    /**
     * 删除模式 逻辑删除
     */
    @Override
    public McSchemaPO delete(McSchemaPO schemaPO) {
        McDatabasePO database = getDatabaseById(schemaPO.getDbId());
        DatabaseTypeEnum.getInstance(database.getType());

        if (metaDefBaseService.verifySchemaUse(DatabaseTypeEnum.getInstance(database.getType()),
                schemaPO.getId()) > 0L) {
            throw new MetaDataException("模式已被使用，不允许删除");
        }
        schemaPO.setIsDeleted(1);
        update(schemaPO);

        graphConsumer.deleteSchemaNode(schemaPO.getId());
        return schemaPO;
    }

    /**
     * 修改模式信息 模式名称不能修改
     */
    @Override
    public McSchemaPO update(McSchemaPO schemaPO) {
        authentication();
        schemaMapper.update(schemaPO);
        return schemaPO;
    }

    /**
     * 模式列表
     */
    @Override
    public PageResultBean<List<McSchemaPO>> listByPage(SchemaSearchVO searchVO) {
        // 普通用户返回空
        if (!systemSettingsService.isDataCentreAdmin()
                && !systemSettingsService.isDatabaseAdmin()) {
            return PageResultBean.empty();
        }

        // 数据库管理员只能查看本部门数据
        if (systemSettingsService.isDatabaseAdmin()) {
            Organization org =
                    securityConsumer.getAscriptionDeptByUserName(UserUtils.getUserName());
            searchVO.setOrgCode(org.getDeptCode());
        }

        PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<McSchemaPO> schemaPOList = schemaMapper.listByPage(searchVO);
        PageInfo<McSchemaPO> info = new PageInfo<>(schemaPOList);
        return PageResultBean.of(searchVO.getPageNum(), info.getTotal(), schemaPOList);
    }
}

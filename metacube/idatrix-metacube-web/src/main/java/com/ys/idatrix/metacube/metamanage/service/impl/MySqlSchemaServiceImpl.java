package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbCreateDatabase;
import com.ys.idatrix.db.api.rdb.dto.RdbDropDatabase;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.RdbService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.metacube.common.enums.SchemaOperationTypeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.McServerService;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * MySql模式服务实现
 *
 * @author wzl
 */
@Service("mySqlSchemaService")
public class MySqlSchemaServiceImpl implements McSchemaService {

    @Autowired
    private McSchemaMapper schemaMapper;

    @Autowired
    private McDatabaseMapper databaseMapper;

    @Autowired
    private RdbService rdbService;

    @Autowired
    private McServerService serverService;

    /**
     * 为McSchemaService接口注入McSchemaMapper数据访问接口
     */
    @Override
    public McSchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    /**
     * 新建模式 新建需要生成物理模式o
     */
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public McSchemaPO create(McSchemaPO schemaPO) {
        insert(schemaPO);
        RdbLinkDto dto = buildRdbLinkDTO(schemaPO);
        RdbCreateDatabase database = new RdbCreateDatabase(schemaPO.getName(),
                schemaPO.getUsername(), schemaPO.getPassword(), false);

        RespResult<SqlExecRespDto> result = rdbService.createDatabase(UserUtils.getUserName(), dto,
                database);
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        }
        return schemaPO;
    }

    /**
     * 注册模式 只在模式表新增记录
     */
    @Override
    public McSchemaPO register(McSchemaPO schemaPO) {
        testDbLink(buildRdbLinkDTO(schemaPO));
        return insert(schemaPO);
    }

    /**
     * 测试连接
     */
    @Override
    public RespResult<Boolean> testDbLink(RdbLinkDto dto) {
        RespResult<Boolean> result = rdbService.testDBLink(dto);
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        }
        return result;
    }

    private RdbLinkDto buildRdbLinkDTO(McSchemaPO schemaPO) {
        DatasourceVO datasourceVO = databaseMapper.getDatasourceInfoById(schemaPO.getDbId());
        RdbLinkDto dto = new RdbLinkDto(datasourceVO.getUsername(),
                datasourceVO.getPassword(), "MYSQL", datasourceVO.getIp(), datasourceVO.getPort(),
                null);
        return dto;
    }

    @Override
    public McSchemaPO update(McSchemaPO schemaPO) {
        testDbLink(buildRdbLinkDTO(schemaPO));
        schemaMapper.update(schemaPO);
        return schemaPO;
    }

    /**
     * 删除模式
     */
    @Override
    public McSchemaPO delete(McSchemaPO schemaPO) {
        if (schemaPO.getType() == SchemaOperationTypeEnum.REGISTER.getCode()) {
            return schemaPO;
        }
        RdbLinkDto dto = buildRdbLinkDTO(schemaPO);
        RdbDropDatabase database = new RdbDropDatabase();
        database.setUserName(schemaPO.getUsername());
        database.setDatabase(schemaPO.getName());
        RespResult<SqlExecRespDto> result =
                rdbService.dropDatabase(UserUtils.getUserName(), dto, database);
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        }
        return schemaPO;
    }
}

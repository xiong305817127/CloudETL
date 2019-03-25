package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hive.service.HiveService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.metacube.common.enums.SchemaOperationTypeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("hiveSchemaService")
public class HiveSchemaServiceImpl implements McSchemaService {

    @Autowired
    private McSchemaMapper schemaMapper;

    @Autowired
    private HiveService hiveService;

    /**
     * 为McSchemaService接口注入McSchemaMapper数据访问接口
     */
    @Override
    public McSchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    /**
     * 新建模式 新建需要生成物理模式（表、视图、hdfs目录、es索引等）
     */
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public McSchemaPO create(McSchemaPO schemaPO) {
        insert(schemaPO);
        RespResult<SqlExecRespDto> result =
                hiveService.createDatabase(UserUtils.getUserName(), schemaPO.getName());
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
        return insert(schemaPO);
    }

    /**
     * 删除模式
     */
    @Override
    public McSchemaPO delete(McSchemaPO schemaPO) {
        if (schemaPO.getType() == SchemaOperationTypeEnum.REGISTER.getCode()) {
            return schemaPO;
        }
        RespResult<SqlExecRespDto> result =
                hiveService.dropDatabase(schemaPO.getUsername(), schemaPO.getName());
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        }
        return schemaPO;
    }
}

package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("hdfsSchemaService")
public class HdfsSchemaServiceImpl implements McSchemaService {

    @Autowired
    private McSchemaMapper schemaMapper;

    @Autowired
    private HdfsUnrestrictedService hdfsService;

    /**
     * 为McSchemaService接口注入McSchemaMapper数据访问接口
     */
    @Override
    public McSchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    /**
     * 新建模式 新建需要在平台库上创建目录
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public McSchemaPO create(McSchemaPO schemaPO) {
        insert(schemaPO);
        RespResult<Boolean> result =
                hdfsService.createDir(UserUtils.getUserName(), schemaPO.getName());
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
}

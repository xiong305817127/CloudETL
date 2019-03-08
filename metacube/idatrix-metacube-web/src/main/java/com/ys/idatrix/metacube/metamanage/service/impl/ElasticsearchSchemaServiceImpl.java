package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("esSchemaService")
public class ElasticsearchSchemaServiceImpl implements McSchemaService {

    @Autowired
    private McSchemaMapper schemaMapper;

    /**
     * 为McSchemaService接口注入McSchemaMapper数据访问接口
     */
    @Override
    public McSchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    /**
     * 新建模式 es新建等同于注册
     */
    @Override
    public McSchemaPO create(McSchemaPO schemaPO) {
        return insert(schemaPO);
    }

    /**
     * 注册模式 只在模式表新增记录
     */
    @Override
    public McSchemaPO register(McSchemaPO schemaPO) {
        return insert(schemaPO);
    }
}

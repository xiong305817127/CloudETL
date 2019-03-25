package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.metacube.common.exception.MetaDataException;
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
        return register(schemaPO);
    }

    /**
     * 注册模式 只在模式表新增记录
     */
    @Override
    public McSchemaPO register(McSchemaPO schemaPO) {
        checkIndexContainsUpperCase(schemaPO.getName());
        return insert(schemaPO);
    }

    /**
     * 校验索引名称是否包含大写
     *
     * @param index 索引名称
     * @return 包含大写返回true 否则返回false
     */
    private static boolean verifyUpperCase(String index) {
        for (int i = 0; i < index.length(); i++) {
            char ch = index.charAt(i);
            if (Character.isUpperCase(ch)) {
                return true;
            }
        }
        return false;
    }

    private static void checkIndexContainsUpperCase(String index) {
        if (verifyUpperCase(index)) {
            throw new MetaDataException("索引名称不能包含大写字母");
        }
    }
}

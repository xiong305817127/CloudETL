package com.ys.idatrix.metacube.api.service;

import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.Schema;
import com.ys.idatrix.metacube.api.beans.SchemaDetails;
import java.util.List;

/**
 * 模式服务接口
 *
 * @author wzl
 */
public interface MetadataSchemaService {

    /**
     * 根据ip和数据库类型返回用户有权限访问的模式列表
     *
     * @param username 用户名
     * @param ip 服务器ip
     * @param databaseType 数据库类型
     * @param module 模块
     * @param actionType 权限类型
     * @return ResultBean <List<Schema>>
     */
    ResultBean<List<Schema>> listSchemaByIpAndDatabaseType(String username, String ip,
            DatabaseTypeEnum databaseType, ModuleTypeEnum module, ActionTypeEnum actionType);

    /**
     * 根据模式id返回模式详情
     *
     * @param username 用户名
     * @param schemaId 模式id
     * @return ResultBean<SchemaDetails>
     */
    ResultBean<SchemaDetails> getSchemaById(String username, Long schemaId);
}

package com.ys.idatrix.metacube.api.service;

import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.Database;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import java.util.List;

/**
 * 数据库服务接口
 *
 * @author wzl
 */
public interface MetadataDatabaseService {

    /**
     * 注册平台数据库信息
     *
     * @param renterId 租户id
     * @return ResultBean<Boolean>
     */
    ResultBean<Boolean> registerOrUpdatePlatformDatabaseInfo(Long renterId);

    /**
     * 返回用户所属组织的数据库列表
     *
     * @param username 用户名
     * @return ResultBean <List<Database>>
     */
    ResultBean<List<Database>> listDatabase(String username);

    /**
     * 返回用户有权限访问的数据库列表（所属组织 + 模块授权）
     *
     * @param username 用户名
     * @param module 模块名称
     * @param actionType 操作类型 读/写/全部
     * @return ResultBean <List<Database>>
     */
    ResultBean<List<Database>> listDatabaseWithModuleAuth(String username, ModuleTypeEnum module,
            ActionTypeEnum actionType);

    /**
     * 根据数据库id获取数据库信息
     *
     * @param databaseId 数据库id
     * @return ResultBean<Database>
     */
    ResultBean<Database> getDatabaseById(Long databaseId);
}

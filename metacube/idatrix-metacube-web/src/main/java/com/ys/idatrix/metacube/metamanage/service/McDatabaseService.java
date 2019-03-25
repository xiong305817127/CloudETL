package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.vo.request.DatabaseServerAggregationVO;
import java.util.List;

/**
 * 数据库服务接口
 *
 * @author wzl
 */
public interface McDatabaseService extends Connectable{

    /**
     * 注册数据库 鉴权、数据校验、业务处理
     */
    McDatabasePO register(McDatabasePO databasePO);

    /**
     * 注册数据库 业务表写入
     */
    McDatabasePO insert(McDatabasePO databasePO);

    boolean exists(String ip, int type, Long renterId);

    List<McDatabasePO> getDatabaseByServerIds(List<Long> serverIds);

    /**
     * 根据租户id获取系统自动注册的平台数据库
     */
    List<McDatabasePO> getPlatformDatabaseByRenterId(Long renterId, List<Integer> typeList);

    /**
     * 获取数据库详情
     *
     * @param id 数据库id
     */
    McDatabasePO getDatabaseById(Long id);

    /**
     * 注销数据库
     *
     * @param id 数据库id
     * @param username 当前用户
     */
    void delete(Long id, String username);

    /**
     * 更新数据库
     */
    McDatabasePO update(McDatabasePO databasePO);

    /**
     * 获取数据库列表
     *
     * @param dbIds 数据库id列表
     */
    List<DatabaseServerAggregationVO> list(List<Long> dbIds, Long renterId, List<Integer> dbTypes);
}

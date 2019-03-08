package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.vo.request.DatabaseServerAggregationVO;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * database数据访问接口
 *
 * @author wzl
 */
public interface McDatabaseMapper {

    /**
     * 根据ip和type查询数据库
     *
     * @param ip 服务器ip
     * @param type 数据库类型
     * @return McDatabasePO
     */
    McDatabasePO getDatabase(@Param("ip") String ip, @Param("type") int type,
            @Param("renterId") long renterId);

    int insert(McDatabasePO databasePO);

    /**
     * 根据服务器id列表查询数据库
     */
    List<McDatabasePO> listDatabaseByServerIds(@Param("serverIds") List<Long> serverIds);

    /**
     * 根据租户id获取系统自动注册的平台数据库
     */
    List<McDatabasePO> listPlatformDatabase(@Param("renterId") Long renterId,
            @Param("typeList") List<Integer> typeList);

    DatasourceVO getDatasourceInfoById(@Param("id") Long id);

    /**
     * 根据id获取数据库详情
     *
     * @param id 数据库id
     */
    McDatabasePO getDatabaseById(@Param("id") Long id);

    int update(McDatabasePO databasePO);

    List<DatabaseServerAggregationVO> listDatabaseByDbIds(@Param("dbIds") List<Long> dbIds);
}

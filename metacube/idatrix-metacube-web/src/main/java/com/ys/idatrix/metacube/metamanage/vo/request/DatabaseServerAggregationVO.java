package com.ys.idatrix.metacube.metamanage.vo.request;

import java.util.Objects;
import lombok.Data;

/**
 * 数据库、服务器聚合对象 用以接收关联查询结果
 *
 * @author wzl
 */
@Data
public class DatabaseServerAggregationVO {

    /**
     * 服务器id
     */
    private Long serverId;

    /**
     * 服务器ip
     */
    private String ip;

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 服务器主机名
     */
    private String hostname;

    /**
     * 数据库id
     */
    private Long databaseId;

    /**
     * 数据库类型
     */
    private Integer databaseType;

    /**
     * 数据库端口
     */
    private Integer port;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatabaseServerAggregationVO that = (DatabaseServerAggregationVO) o;
        return Objects.equals(databaseId, that.databaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseId);
    }
}

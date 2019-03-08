package com.idatrix.resource.catalog.es.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ES集群配置信息
 *
 * @author wzl
 */
@Component
public class ElasticsearchProperties {

    /**
     * 集群名称.
     */
    @Value("${elasticsearch.cluster.name}")
    private String clusterName;

    /**
     * 以逗号分隔的集群节点地址列表
     */
    @Value("${elasticsearch.cluster.nodes}")
    private String clusterNodes;

    public String getClusterName() {
        return this.clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterNodes() {
        return this.clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }
}

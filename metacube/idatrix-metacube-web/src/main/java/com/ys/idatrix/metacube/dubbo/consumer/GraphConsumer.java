package com.ys.idatrix.metacube.dubbo.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ys.idatrix.graph.service.api.NodeService;
import com.ys.idatrix.graph.service.api.RelationshipService;
import com.ys.idatrix.graph.service.api.dto.node.DatabaseNodeDto;
import com.ys.idatrix.graph.service.api.dto.node.SchemaNodeDto;
import com.ys.idatrix.graph.service.api.dto.node.ServerNodeDto;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 数据地图服务消费
 *
 * @author wzl
 */
@Component
public class GraphConsumer {

    @Reference
    private NodeService nodeService;

    @Reference
    private RelationshipService relationshipService;

    @Bean
    public NodeService getNodeService() {
        return nodeService;
    }

    @Bean
    public RelationshipService relationshipService() {
        return relationshipService;
    }

    public Long createServerNode(ServerNodeDto node) {
        return nodeService.createServerNode(node);
    }

    public void deleteServerNode(Long serverId) {
        nodeService.deleteServerNode(serverId);
    }

    public Long createDatabaseNode(DatabaseNodeDto node) {
        return nodeService.createDatabaseNode(node);
    }

    public void deleteDatabaseNode(Long databaseId) {
        nodeService.deleteDatabaseNode(databaseId);
    }

    public Long createSchemaNode(SchemaNodeDto node) {
        return nodeService.createSchemaNode(node);
    }

    public void deleteSchemaNode(Long schemaId) {
        nodeService.deleteSchemaNode(schemaId);
    }
}

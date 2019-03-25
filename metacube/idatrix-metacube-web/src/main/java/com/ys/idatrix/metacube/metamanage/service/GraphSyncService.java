package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.graph.service.api.dto.edge.FkRelationshipDto;
import com.ys.idatrix.graph.service.api.dto.node.TableNodeDto;

import java.util.List;

/**
 * @ClassName GraphSyncService
 * @Description
 * @Author ouyang
 * @Date
 */
public interface GraphSyncService {

    // table

    // 保存表节点
    void graphSaveTableNode(Long id);

    // 删除表节点
    void graphDeleteTableNode(Long id);

    // 保存表外键关系
    Long saveFkRlat(List<FkRelationshipDto> list);

    // 删除表外键关系
    void deleteFkRlat(Long tableId, String fkName);

    TableNodeDto getTableNodeDto(Long id);

    // view

    // 数据地图保存视图节点
    void graphSaveViewNode(Long id);

    // 删除数据地图的视图节点
    void graphDeleteViewNode(Long id);

}
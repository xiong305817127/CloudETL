package com.ys.idatrix.metacube.metamanage.service;

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


    // view

    // 数据地图保存视图节点
    void graphSaveViewNode(Long id);

    // 删除数据地图的视图节点
    void graphDeleteViewNode(Long id);
}
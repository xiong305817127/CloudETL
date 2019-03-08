package com.idatrix.resource.catalog.service;

/**
 *  主要是更新数据统计信息
 *  2018/09/06  robin
 */
public interface IResourceStatiscsService {
    //更新订阅次数
    void increaseSubCount(Long resourceId);

    //更新文件个数，文件类型是文件个数，数据库是记录条数
    void increaseDataCount(Long resourceId, Long count);

    //更新交换数据
    void increaseShareDataCount(Long resource, Long count);

    //更新交换数据
    void increaseViewDataCount(Long resource);

    /*更新统计数据*/
    void refreshStatisticsData(Long resourceId, int subCount, Long dataCount, Long shareDataCount);
}



package com.idatrix.resource.portal.service;

/**
 * @ClassName: IResourceIndexService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/7
 */
public interface IResourceIndexService {

    /**
     * 根据资源id -> 索引发布资源数据（资源名称、资源摘要、信息项）
     *
     * @param resourceId
     * @return
     */
    void indexPublishedResourceByResourceId(String username,Long resourceId);


    /**
     * 根据租户id索引发布资源数据（资源名称、资源摘要、信息项）
     *
     * @param renterId
     * @return
     */
    void indexPublishedResourceByRenterId(Long renterId);


    /**
     * 建发布资源索引
     *
     * @param username
     * @param renterId
     * @return
     */
    boolean createResourceIndex(String username, Long renterId);

}

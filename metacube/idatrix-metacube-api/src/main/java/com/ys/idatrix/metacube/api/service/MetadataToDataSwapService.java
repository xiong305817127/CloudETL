package com.ys.idatrix.metacube.api.service;

import com.ys.idatrix.metacube.api.beans.MetadataDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.dataswap.*;

import java.util.List;

/**
 * @Classname MetadataToDataSwapService
 * @Description  元数据提供给共享数据交换服务
 * @Author robin
 * @Date 2019/3/9 11:33
 * @Version v1.0
 */
public interface MetadataToDataSwapService {


    /**
     * 根据表ID查询表字段
     * @param tableId
     * @return
     */
    ResultBean<MetadataDTO> findTableInfoByID(Long tableId);

    /**
     * 资源目录订阅-创建表
     *
     * @param userName
     * @param changedMetadataTable
     * @param metadataFields
     * @return
     */
    ResultBean<SubscribeCrtTbResult> createTableBySubscribe(String userName, MetadataTable changedMetadataTable, List<MetadataField> metadataFields);


    /**
     * 通过注册验证字段
     *
     * @param metaId
     * @param verifiedFields
     * @return
     */
    ResultBean<RegisterVerifyFieldsResult> verifyFieldsByRegister(int metaId, List<String> verifiedFields);


    /**
     * 根据metaId查询表字段（属性）信息
     *
     * @param metaId
     * @return
     */
    ResultBean<QueryMetadataFieldsResult> getMetadataFieldsByMetaId(int metaId);


    /**
     * 授权元数据资源给用户
     *
     * @param username
     * @param metaId
     * @param orgCode
     * @param type
     * @return
     */
    ResultBean<Boolean> authorizedTableForUser(String username, int metaId, String orgCode, AuthorizedFlowType type);


    /**
     * 采集元数据表
     *
     * @param externalTableCollection
     * @return
     */
    ResultBean<CollectExternalTableResult> collectExternalTable(ExternalTableCollection externalTableCollection);

}

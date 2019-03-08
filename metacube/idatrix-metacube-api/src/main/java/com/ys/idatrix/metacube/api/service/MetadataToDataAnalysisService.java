package com.ys.idatrix.metacube.api.service;

import com.ys.idatrix.metacube.api.beans.*;

import java.util.HashMap;
import java.util.List;

/**
 * @Interface: MetadataToDataAnalysisService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/29
 */
public interface MetadataToDataAnalysisService {


    /**
     * 获取用户可操作的数据库资源（模式-表数据）
     *
     * @param username
     * @return
     */
    ResultBean<List<MetaDbResourceDTO>> getDatabaseResource(String username);


    /**
     * 查询schemaId下用户所有可操作的表的字段数据
     * 用于ide中输入sql做自动提示
     *
     * @param username
     * @param schemaId
     * @return
     */
    ResultBean<HashMap<String, List<MetaFieldDTO>>> getTablesAndFields(String username, Long schemaId);


    /**
     * 根据用户获取ES信息
     * 1.所属组织归属的
     * 2.授权的
     *
     * @param username
     * @return
     */
    ResultBean<List<MetaEsDTO>> getEsIndices(String username);


    /**
     * 查询metaId下用户所有可操作的索引类型字段
     * 用于添加文档数据做字段验证
     *
     * @param username
     * @param metaId
     * @return
     */
    ResultBean<List<MetaEsFieldDTO>> getEsFields(String username, Long metaId);


    /**
     * 根据用户获取hdfs信息
     * 1.正常创建或所属组织归属的
     * 2.授权的
     *
     * @param username
     * @return
     */
    ResultBean<List<MetaHdfsDTO>> getHdfsPaths(String username);


    /**
     * 根据 schema 获取数据库连接信息
     *
     * @param username
     * @param schemaId
     * @return
     */
    ResultBean<MetaDatabaseDTO> getDatabaseInfo(String username, Long schemaId);


    /**
     * 获取用户操作索引的权限
     *
     * @param username
     * @param metaId
     * @return
     */
    ResultBean<ActionTypeEnum> getEsPermiss(String username, Long metaId);


    /**
     * 根据schemaId,表,查询用户可操作的权限
     *
     * @param username
     * @param schemaId
     * @param tableName
     * @return
     */
    ResultBean<ActionTypeEnum> getTbPermiss(String username, Long schemaId, String tableName);


    /**
     * 获取 hdfs 操作权限
     * 必须根据最大的正向匹配路径的元数据hdfs匹配
     *
     * @param username
     * @param hdfsPath
     * @return
     */
    ResultBean<ActionTypeEnum> getHdfsPermiss(String username, String hdfsPath);


}

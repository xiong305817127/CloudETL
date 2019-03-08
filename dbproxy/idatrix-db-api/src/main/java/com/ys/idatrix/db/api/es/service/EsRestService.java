package com.ys.idatrix.db.api.es.service;


import com.ys.idatrix.db.api.common.RespResult;

/**
 * @ClassName: EsRestService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public interface EsRestService {

    /**
     * 全文搜索
     *
     * @param username
     * @param alias
     * @param keyword
     * @return
     */
    RespResult<String> queryDocsWithFull(String username, String alias, String keyword);


    /**
     * 自定义搜索
     *
     * @param username
     * @param alias
     * @param endpoint
     * @param dsl
     * @return
     */
    RespResult<String> queryDocsWithCustom(String username, String alias, String method, String endpoint, String dsl);


    /**
     * 查询索引元数据定义（settings,mapping）
     *
     * @param username
     * @param alias
     * @return
     */
    RespResult<String> queryIndexMetadata(String username, String alias);


    /**
     * 中文分词-自定义词库
     * @param endpoint
     * @param text
     * @return
     */
    RespResult<String> catAnsj(String endpoint, String text);


    /**
     * 中文分词-查询 ansj 全部配置
     * @param endpoint
     * @return
     */
    RespResult<String> catAnsjConfig(String endpoint);

    /**
     * 中文分词-刷新自定义词典
     * @param endpoint
     * @return
     */
    RespResult<Boolean> flushAnsjDic(String endpoint);


    /**
     * 中文分词-刷新所有配置
     * @param endpoint
     * @return
     */
    RespResult<Boolean> flushAnsjConfig(String endpoint);

}

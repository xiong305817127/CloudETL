package com.idatrix.resource.portal.common;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @ClassName: IndexResourceConstant
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/8
 */
public class IndexResourceConstant {

    /**
     * 发布资源索引名称前缀
     */
    public final static String INDEX_PRENAME = "idx_resource_";


    /**
     * 发布资源索引版本号
     */
    public final static String INDEX_VERSION = "1.0";

    /**
     * 发布资源索引类型，不用区分租户
     */
    public final static String INDEX_TYPE = "typ_pub_success";


    /**
     * 统一批量更新发布资源索引用户
     */
    public final static String REINDEX_USER = "admin";


    /**
     * 资源信息项索引字段分隔符
     */
    public final static String ITEMS_SEPARATE = ",";


    /**
     * 发布资源索引字段
     * 资源名称、资源摘要、资源信息项
     */
    public final static List<String> RESOURCE_FIELDS = Lists.newArrayList("name","digest","infoItem");


    /**
     * 找不到索引错误信息
     */
    public final static String NO_INDEX_MSG = "no such index";


    /**
     * 资源状态
     */
    public final static String STATUS_PUBLISHED = "pub_success";

}

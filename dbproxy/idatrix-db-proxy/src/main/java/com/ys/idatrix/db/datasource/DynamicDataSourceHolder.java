package com.ys.idatrix.db.datasource;

import java.util.Set;

/**
 *动态数据源
 *
 * @ClassName: DynamicDataSourceHolder
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public class DynamicDataSourceHolder {

    /**
     * 保存当前线程的数据源对应的key
     */
    private static final ThreadLocal<String> DATA_SOURCE_KEY = new ThreadLocal<String>();


    /**
     * 所有数据源的key集合
     */
    public static Set<Object> dataSourceKeys;


    /**
     * 设置数据源key
     *
     * @param dataSourceKey
     */
    public static void switchSource(String dataSourceKey) {
        DATA_SOURCE_KEY.set(dataSourceKey);
    }


    /**
     * 获取数据源key
     *
     * @return
     */
    public static String getDataSource() {
        return DATA_SOURCE_KEY.get();
    }


    /**
     * 清除
     */
    public static void clearDataSource() {
        DATA_SOURCE_KEY.remove();
    }


    /**
     * 判断指定DataSource当前是否存在
     *
     * @param dataSourceId
     * @return
     */
    public static boolean containsDataSource(String dataSourceId) {
        return dataSourceKeys.contains(dataSourceId);
    }


}

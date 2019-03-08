package com.ys.idatrix.db.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Map;


/**
 * 动态数据源
 *
 * @ClassName: DynamicDataSource
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * AbstractRoutingDataSource抽象类实现方法，即获取当前线程数据源的key
     * 根据Key获取数据源的信息，上层抽象函数的钩子
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHolder.getDataSource();
    }


    /**
     * 在获取key的集合，目的只是为了添加一些告警日志
     */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        try {
            Field sourceMapField = AbstractRoutingDataSource.class.getDeclaredField("resolvedDataSources");
            sourceMapField.setAccessible(true);
            Map<Object, DataSource> sourceMap = (Map<Object, DataSource>) sourceMapField.get(this);
            DynamicDataSourceHolder.dataSourceKeys = sourceMap.keySet();
            sourceMapField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

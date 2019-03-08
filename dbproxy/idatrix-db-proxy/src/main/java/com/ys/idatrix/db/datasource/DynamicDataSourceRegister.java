package com.ys.idatrix.db.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 多数据源注册
 *
 * @ClassName: DynamicDataSourceRegister
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public class DynamicDataSourceRegister implements EnvironmentAware, ImportBeanDefinitionRegistrar {

    /**
     * 别名
     */
    private final static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();

    static {
        //由于部分数据源配置不同，所以在此处添加别名，避免切换数据源出现某些参数无法注入的情况
        aliases.addAliases("url", "jdbc-url");
        aliases.addAliases("username", "user");
    }

    /**
     * 环境配置器
     */
    private Environment evn;

    /**
     * 数据源列表
     */
    private Map<String, DataSource> sourceMap;


    /**
     * 参数绑定工具
     */
    private Binder binder;


    /**
     * EnvironmentAware接口的实现方法，通过aware的方式注入，此处是environment对象
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.evn = environment;
        //绑定配置器
        binder = Binder.get(evn);
    }


    /**
     * ImportBeanDefinitionRegistrar接口的实现方法，通过该方法可以按照自己的方式注册bean
     *
     * @param metadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        //获取所有数据源配置
        Map properties, defaultConfig = binder.bind("spring.datasource", Map.class).get();

        //默认配置
        sourceMap = Maps.newHashMap();
        properties = defaultConfig;

        //默认数据源类型
        String typeStr = evn.getProperty("spring.datasource.type");

        //获取数据源类型
        Class<? extends DataSource> clazz = getDataSourceType(typeStr);

        //绑定默认数据源参数
        DataSource consumerDatasource, defaultDatasource = bind(clazz, properties);

        //获取其他数据源配置
        List<Map> multiConfigs = binder.bind("spring.datasource.multi", Bindable.listOf(Map.class)).get();

        //遍历 Multi 配置生成其他数据源
        for (Map multiConfig : multiConfigs) {

            clazz = getDataSourceType((String) multiConfig.get("type"));

            //获取 extend 字段，未定义或为true则为继承状态
            if ((boolean) multiConfig.getOrDefault("extend", Boolean.TRUE)) {

                //继承默认数据源配置
                properties = new HashMap(defaultConfig);

                //添加数据源参数
                properties.putAll(multiConfig);

            } else {
                //不继承默认配置
                properties = multiConfig;
            }

            //绑定参数
            consumerDatasource = bind(clazz, properties);

            //获取数据源的key，以便通过该key可以定位到数据源
            sourceMap.put(multiConfig.get("key").toString(), consumerDatasource);
        }

        // 创建DynamicDataSource
        //获取数据源的key，以便通过该key可以定位到数据源
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

        //设置bean的类型，此处 DynamicDataSource 是继承 AbstractRoutingDataSource 的实现类
        beanDefinition.setBeanClass(DynamicDataSource.class);

        //需要注入的参数，类似spring配置文件中的<property/>
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();

        //添加默认数据源，避免key不存在的情况没有数据源可用
        mpv.add("defaultTargetDataSource", defaultDatasource);

        //将该bean注册为datasource，不使用springboot自动生成的datasource
        mpv.add("targetDataSources", sourceMap);

        //添加其他数据源
        registry.registerBeanDefinition("datasource", beanDefinition);
    }


    /**
     * 通过字符串获取数据源class对象
     *
     * @param typeStr
     * @return
     */
    private Class<? extends DataSource> getDataSourceType(String typeStr) {
        Class<? extends DataSource> type;
        try {
            //字符串不为空则通过反射获取class对象
            if (StringUtils.hasLength(typeStr)) {
                type = (Class<? extends DataSource>) Class.forName(typeStr);
            } else {
                //默认为hikariCP数据源，与springboot默认数据源保持一致
                //type = HikariDataSource.class;
                type = DruidDataSource.class;
            }
            return type;
        } catch (Exception e) {
            //无法通过反射获取class对象的情况则抛出异常，该情况一般是写错了，所以此次抛出一个runtimeexception
            throw new IllegalArgumentException("can not resolve class with type: " + typeStr);
        }
    }


    /**
     * 绑定参数，以下三个方法都是参考DataSourceBuilder的bind方法实现的，目的是尽量保证我们自己添加的数据源构造过程与springboot保持一致
     *
     * @param result
     * @param properties
     */
    private void bind(DataSource result, Map properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(new ConfigurationPropertySource[]{source.withAliases(aliases)});
        //将参数绑定到对象
        binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(result));
    }


    private <T extends DataSource> T bind(Class<T> clazz, Map properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(new ConfigurationPropertySource[]{source.withAliases(aliases)});
        //通过类型绑定参数并获得实例对象
        return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(clazz)).get();
    }


    /**
     * @param clazz
     * @param sourcePath 参数路径，对应配置文件中的值，如: spring.datasource
     * @param <T>
     * @return
     */
    private <T extends DataSource> T bind(Class<T> clazz, String sourcePath) {
        Map properties = binder.bind(sourcePath, Map.class).get();
        return bind(clazz, properties);
    }

}

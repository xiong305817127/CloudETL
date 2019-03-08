package com.idatrix.resource.catalog.es.config;

import com.idatrix.resource.catalog.es.utils.ElasticsearchUtil;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lease.Releasable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

/**
 * 初始化ES client连接
 *
 * @author wzl
 */
@Configuration
public class ElasticsearchConfig implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

    @Autowired
    private ElasticsearchProperties properties;
    private Releasable releasable;

    @Bean
    @Conditional(InitEsBeanCondition.class)
    public Client elasticsearchClient() {
        try {
            return createTransportClient();
        } catch (Exception var2) {
            throw new IllegalStateException(var2);
        }
    }

    @Bean
    @Conditional(InitEsBeanCondition.class)
    public ElasticsearchUtil createUtils() {
        return ElasticsearchUtil.of();
    }

    private Client createTransportClient() throws Exception {
        TransportClientFactoryBean factory = new TransportClientFactoryBean();
        factory.setClusterNodes(this.properties.getClusterNodes());
        factory.setClusterName(this.properties.getClusterName());
        factory.afterPropertiesSet();
        TransportClient client = factory.getObject();
        this.releasable = client;
        return client;
    }

    @Override
    public void destroy() throws Exception {
        if (this.releasable != null) {
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("Closing Elasticsearch client");
                }

                try {
                    this.releasable.close();
                } catch (NoSuchMethodError var2) {
                    ReflectionUtils
                            .invokeMethod(ReflectionUtils.findMethod(Releasable.class, "release"),
                                    this.releasable);
                }
            } catch (Exception var3) {
                if (logger.isErrorEnabled()) {
                    logger.error("Error closing Elasticsearch client: ", var3);
                }
            }
        }

    }

}

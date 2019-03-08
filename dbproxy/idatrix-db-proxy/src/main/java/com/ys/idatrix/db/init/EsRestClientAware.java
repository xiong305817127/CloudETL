package com.ys.idatrix.db.init;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Http协议 操作 Es Rest API 客户端:RestClient 初始化
 *
 * @ClassName: EsRestClient
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/10/31
 */
@Slf4j
@Component(value = "esRestClient")
public class EsRestClientAware implements FactoryBean<RestClient>, InitializingBean, DisposableBean {

    /**
     * clusterNode ip和port连接符
     */
    static final String COLON = ":";

    /**
     * 多nodes 连接符
     */
    static final String COMMA = ",";

    /**
     * es 是否启用
     */
    @Value("${custom.es.enabled}")
    private boolean canEnabled;

    /**
     * es 节点名称 e.g 10.0.0.126:9200 多个节点逗号隔开
     */
    @Value("${custom.es.http-nodes}")
    private String httpNodes;


    private RestClient restClient;


    @Override
    public void destroy() throws Exception {
        try {
            log.info("Closing elasticSearch RestClient ... ");
            if (restClient != null) {
                restClient.close();
            }
        } catch (final Exception e) {
            log.error("Error closing ElasticSearch RestClient: ", e);
        }
    }


    @Override
    public RestClient getObject() throws Exception {
        return restClient;
    }


    @Override
    public Class<?> getObjectType() {
        return RestClient.class;
    }


    @Override
    public boolean isSingleton() {
        return false;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (canEnabled && StringUtils.isNotEmpty(httpNodes)) {
            buildClient();
        }
    }


    protected void buildClient() throws Exception {
        Assert.hasText(httpNodes, "[Assertion failed] httpNodes settings missing.");
        ArrayList<HttpHost> hosts = new ArrayList<>();
        for (String clusterNode : split(httpNodes, COMMA)) {
            String hostName = substringBeforeLast(clusterNode, COLON);
            String port = substringAfterLast(clusterNode, COLON);
            Assert.hasText(hostName, "[Assertion failed] missing host name in 'http protocol httpNodes'");
            Assert.hasText(port, "[Assertion failed] missing port in 'http protocol httpNodes'");
            log.info("adding transport node : " + clusterNode);
            hosts.add(new HttpHost(hostName, Integer.valueOf(port), "http"));
        }
        if (!hosts.isEmpty()) {
            HttpHost[] httpHosts = new HttpHost[hosts.size()];
            restClient = RestClient.builder(hosts.toArray(httpHosts)).build();
            log.info("initializing elasticSearch client,http protocol cluster nodes:{}", httpNodes);
        }
    }

}

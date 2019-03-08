package com.ys.idatrix.db.core.es;

import com.sun.research.ws.wadl.HTTPMethods;
import com.ys.idatrix.db.exception.DbProxyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * @ClassName: EsRestExecService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
@Service
public class EsRestExecService {


    @Autowired(required = false)
    private RestClient restClient;


    /**
     * dsl json格式 查询
     *
     * @param endpoint
     * @param dsl
     * @return
     */
    public String queryDsl(String method, String endpoint, String dsl) throws Exception {
        Response response;
        if (StringUtils.isNotBlank(dsl)) {
            HttpEntity entity = new NStringEntity(dsl, ContentType.APPLICATION_JSON);
            response = restClient.performRequest(method, endpoint, Collections.<String, String>emptyMap(), entity);
        } else {
            response = restClient.performRequest(method, endpoint);
        }
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            return EntityUtils.toString(response.getEntity());
        } else {
            throw new DbProxyException(response.getStatusLine().getReasonPhrase());
        }
    }


    /**
     * key value 参数传入查询
     *
     * @param method
     * @param endpoint
     * @param params
     * @return
     */
    public String queryKV(String method, String endpoint, Map<String, String> params) throws Exception {
        Response response = restClient.performRequest(method, endpoint, params);
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            return EntityUtils.toString(response.getEntity());
        } else {
            throw new DbProxyException(response.getStatusLine().getReasonPhrase());
        }
    }


    /**
     * put dsl 执行
     *
     * @param endpoint
     * @param dsl
     * @return
     */
    public Boolean putExecuteDsl(String endpoint, String dsl) throws Exception {
        String method = HTTPMethods.PUT.name();
        Response response;
        if (StringUtils.isNotBlank(dsl)) {
            HttpEntity entity = new NStringEntity(dsl, ContentType.APPLICATION_JSON);
            response = restClient.performRequest(method, endpoint, Collections.<String, String>emptyMap(), entity);
        } else {
            response = restClient.performRequest(method, endpoint);
        }

        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            return Boolean.TRUE;
        } else {
            throw new DbProxyException(response.getStatusLine().getReasonPhrase());
        }

    }


    /**
     * put  key value 执行
     *
     * @param endpoint
     * @param params
     * @return
     */
    public Boolean putExecuteKV(String endpoint, Map<String, String> params) throws Exception {
        String method = HTTPMethods.PUT.name();
        Response response = restClient.performRequest(method, endpoint, params);
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            return Boolean.TRUE;
        } else {
            throw new DbProxyException(response.getStatusLine().getReasonPhrase());
        }
    }

}

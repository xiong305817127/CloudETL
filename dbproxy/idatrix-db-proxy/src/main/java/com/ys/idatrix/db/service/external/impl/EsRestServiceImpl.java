package com.ys.idatrix.db.service.external.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Maps;
import com.sun.research.ws.wadl.HTTPMethods;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.es.service.EsRestService;
import com.ys.idatrix.db.core.es.EsRestExecService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @ClassName: EsRestServiceImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/10/31
 */
@Slf4j
@Service(protocol = "dubbo", timeout = 60000, interfaceClass = EsRestService.class)
@Component
public class EsRestServiceImpl implements EsRestService {

    @Autowired(required = false)
    private EsRestExecService esRestExecService;

    /**
     * 全文检索端点内容
     **/
    private final String fullEndpoint = "/{0}/_search";

    /**
     * 查询settings,mapping endpoint
     **/
    private final String metadataEndpoint = "/{0}/_settings,_mapping";

    /**
     * 全文检索，查询所有
     **/
    private final String fullDsl = "{\n" +
            "    \"query\": {\n" +
            "        \"match_all\": {}\n" +
            "    }\n" +
            "}";

    /**
     * 全文检索，关键子查询
     **/
    private final String keywordDsl = "{ \n" +
            "    \"query\": { \n" +
            "        \"query_string\": { \n" +
            "            \"query\": \"$keyword\" \n" +
            "        } \n" +
            "    } \n" +
            "}";


    @Override
    public RespResult<String> queryDocsWithFull(String username, String alias, String keyword) {

        if (StringUtils.isBlank(username)) {
            log.error("queryDocsWithFull,username is null");
            return RespResult.buildFailWithMsg("用户名为空");
        }

        if (StringUtils.isBlank(alias)) {
            log.error("queryDocsWithFull,index is null");
            return RespResult.buildFailWithMsg("索引为空");
        }

        try {
            String endpoint = MessageFormat.format(fullEndpoint, alias);
            String dsl = StringUtils.isBlank(keyword) ? fullDsl : keywordDsl.replace("$keyword", keyword);

            String result = esRestExecService.queryDsl(HTTPMethods.POST.name(), endpoint, dsl);
            return RespResult.buildSuccessWithData(result);
        } catch (Exception e) {
            return RespResult.buildFailWithMsg(wrapExecException(e));
        }
    }


    @Override
    public RespResult<String> queryDocsWithCustom(String username, String alias, String method, String endpoint, String dsl) {

        if (StringUtils.isBlank(username)) {
            log.error("queryDocsWithCustom,username is null");
            return RespResult.buildFailWithMsg("用户名为空");
        }

        if (StringUtils.isBlank(alias)) {
            log.error("queryDocsWithCustom,alias is null");
            return RespResult.buildFailWithMsg("索引为空");
        }

        if (StringUtils.isBlank(method)) {
            log.error("queryDocsWithCustom,method is null");
            return RespResult.buildFailWithMsg("请求方式为空");
        }

        if (StringUtils.isBlank(endpoint)) {
            log.error("queryDocsWithCustom,endpoint is null");
            return RespResult.buildFailWithMsg("查询端点内容为空");
        }

        if (StringUtils.isBlank(dsl)) {
            log.error("queryDocsWithCustom,dsl is null");
            return RespResult.buildFailWithMsg("查询DSL为空");
        }

        try {
            String result = esRestExecService.queryDsl(method.trim().toUpperCase(), endpoint, dsl);
            return RespResult.buildSuccessWithData(result);
        } catch (Exception e) {
            return RespResult.buildFailWithMsg(wrapExecException(e));
        }

    }


    @Override
    public RespResult<String> queryIndexMetadata(String username, String alias) {

        if (StringUtils.isBlank(username)) {
            log.error("queryDocsWithCustom,username is null");
            return RespResult.buildFailWithMsg("用户名为空");
        }

        if (StringUtils.isBlank(alias)) {
            log.error("queryDocsWithFull,index is null");
            return RespResult.buildFailWithMsg("索引为空");
        }

        try {
            String endpoint = MessageFormat.format(metadataEndpoint, alias);
            String result = esRestExecService.queryDsl(HTTPMethods.GET.name(), endpoint, null);
            return RespResult.buildSuccessWithData(result);
        } catch (Exception e) {
            return RespResult.buildFailWithMsg(wrapExecException(e));
        }
    }


    @Override
    public RespResult<String> catAnsj(String endpoint, String text) {

        String defaultEndpoint = "/_cat/ansj?type=index_ansj&pretty";

        if (StringUtils.isBlank(endpoint)) {
            endpoint = defaultEndpoint;
        }

        try {
            String result;
            if (StringUtils.isNotBlank(text)) {
                Map<String, String> params = Maps.newHashMap();
                params.put("text", text);
                result = esRestExecService.queryKV(HTTPMethods.GET.name(), endpoint, params);
            } else {
                result = esRestExecService.queryDsl(HTTPMethods.GET.name(), endpoint, null);
            }
            return RespResult.buildSuccessWithData(result);
        } catch (Exception e) {
            return RespResult.buildFailWithMsg(wrapExecException(e));
        }

    }


    @Override
    public RespResult<String> catAnsjConfig(String endpoint) {

        String defaultEndpoint = "/_cat/ansj/config";

        if (StringUtils.isBlank(endpoint)) {
            endpoint = defaultEndpoint;
        }
        try {
            return RespResult.buildSuccessWithData(esRestExecService.queryDsl(HTTPMethods.GET.name(), endpoint, null));
        } catch (Exception e) {
            return RespResult.buildFailWithMsg(wrapExecException(e));
        }
    }

    @Override
    public RespResult<Boolean> flushAnsjDic(String endpoint) {

        String defaultEndpoint = "/_ansj/flush/dic";

        if (StringUtils.isBlank(endpoint)) {
            endpoint = defaultEndpoint;
        }

        try {
            esRestExecService.queryDsl(HTTPMethods.GET.name(), endpoint, null);
            return RespResult.buildSuccessWithData(Boolean.TRUE);
        } catch (Exception e) {
            return RespResult.buildFailWithMsg(wrapExecException(e));
        }
    }


    @Override
    public RespResult<Boolean> flushAnsjConfig(String endpoint) {

        String defaultEndpoint = "/_ansj/flush/config";

        if (StringUtils.isBlank(endpoint)) {
            endpoint = defaultEndpoint;
        }

        try {
            esRestExecService.queryDsl(HTTPMethods.GET.name(), endpoint, null);
            return RespResult.buildSuccessWithData(Boolean.TRUE);
        } catch (Exception e) {
            return RespResult.buildFailWithMsg(wrapExecException(e));
        }
    }


    /**
     * 统一处理操作异常，返回异常信息。
     *
     * @param e
     * @return
     */
    private String wrapExecException(Exception e) {
        e.printStackTrace();
        String msg = e.getMessage();
        if (e instanceof ResponseException) {
            ResponseException rex = (ResponseException) e;
            try {
                msg = EntityUtils.toString(rex.getResponse().getEntity());
            } catch (IOException e1) {
                msg = e1.getMessage();
                e1.printStackTrace();
            }
        }
        return msg;
    }

}

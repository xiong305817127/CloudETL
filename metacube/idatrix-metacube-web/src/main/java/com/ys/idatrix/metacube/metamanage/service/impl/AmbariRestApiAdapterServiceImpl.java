package com.ys.idatrix.metacube.metamanage.service.impl;

import com.google.gson.Gson;
import com.ys.idatrix.metacube.common.utils.Base64Utils;
import com.ys.idatrix.metacube.config.UniomPlatformConfig;
import com.ys.idatrix.metacube.metamanage.beans.AmbariCluesterInfoBean;
import com.ys.idatrix.metacube.metamanage.beans.AmbariHostInfoBean;
import com.ys.idatrix.metacube.metamanage.beans.Host;
import com.ys.idatrix.metacube.metamanage.service.AmbariRestApiAdapterService;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * ambari平台rest api适配服务实现类
 *
 * @author wzl
 */
@Service
public class AmbariRestApiAdapterServiceImpl implements AmbariRestApiAdapterService {

    /**
     * 获取主机名url后缀
     */
    private final String HOST_URL_SUFFIX = "/hosts/%s?fields=Hosts/ip";

    private final String PREFIX_HBASE = "jdbc:phoenix:";
    private Pattern PATTERN_HBASE = Pattern.compile(PREFIX_HBASE);
    private final String PREFIX_HIVE = "jdbc:hive2://";
    private Pattern PATTERN_HIVE = Pattern.compile(PREFIX_HIVE);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UniomPlatformConfig config;

    /**
     * ambari rest api 接口认证请求头
     */
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_TYPE = "Basic ";
    private String AUTHORIZATION_VALUE;

    /**
     * 服务配置版本 对外实现
     */
    @Override
    public Object getServiceConfigVersions(String header) {
        return getResultByRestTemplate(config.getUrl(), Object.class);
    }

    /**
     * 服务配置版本 内部使用
     */
    @Override
    public AmbariCluesterInfoBean getServiceConfigVersions() {
        return getResultByRestTemplate(config.getUrl(), AmbariCluesterInfoBean.class);
    }

    /**
     * 根据主机名获取主机信息
     */
    @Override
    public AmbariHostInfoBean getHostInfoByHostname(String hostname) {
        return getResultByRestTemplate(buildHostUrl(hostname), AmbariHostInfoBean.class);
    }

    /**
     * 返回hdfs主机信息
     */
    @Override
    public Host resolveHDFSHost(String string) {
        return resolveCommonHost(string);
    }

    /**
     * 返回hive主机信息
     */
    @Override
    public Host resolveHIVEHost(String source) {
        return resolveHost(PATTERN_HIVE, source);
    }

    /**
     * 返回hbase主机信息
     */
    @Override
    public Host resolveHBASEHost(String source) {
        return resolveHost(PATTERN_HBASE, source);
    }

    /**
     * 返回es主机信息
     */
    @Override
    public Host resolveESHost(String source) {
        return resolveCommonHost(source);
    }

    /**
     * 根据主机名构建获取主机信息的url
     *
     * @param hostname 主机名
     */
    private String buildHostUrl(String hostname) {
        return String.format(config.getAmbariClusterUrl() + HOST_URL_SUFFIX, hostname);
    }

    /**
     * 通用restTemplate请求封装
     */
    private <T> T getResultByRestTemplate(String url, Class<T> tClass) {
        if (StringUtils.isBlank(AUTHORIZATION_VALUE)) {
            AUTHORIZATION_VALUE = AUTHORIZATION_TYPE + Base64Utils
                    .encode(config.getUsername() + ":" + config.getPassword());
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION, AUTHORIZATION_VALUE);

        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        return new Gson().fromJson(response.getBody(), tClass);
    }

    /**
     * 解析通用格式的主机名和端口 格式为 hostname:port
     */
    private static Host resolveCommonHost(String source) {
        if (StringUtils.isBlank(source) || "none".equals(source)) {
            return null;
        }
        String[] array = source.split(":");
        Host host = new Host();
        host.setHostname(array[0]).setPort(array[1]);
        return host;
    }

    /**
     * 解析hive、hbase的主机名和端口
     */
    private Host resolveHost(Pattern pattern, String source) {
        Matcher matcher = pattern.matcher(source);
        matcher.find();
        int index = matcher.end();

        String hostname = source.substring(index);
        String[] array = hostname.split(":");

        Host host = new Host();
        host.setPort(array[1]);

        String[] hostArray = array[0].split(",");

        Random random = new Random();
        int i = random.nextInt(hostArray.length);
        host.setHostname(hostArray[i]);
        return host;
    }
}

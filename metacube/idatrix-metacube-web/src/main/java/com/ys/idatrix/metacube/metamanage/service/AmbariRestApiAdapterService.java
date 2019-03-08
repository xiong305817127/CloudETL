package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.beans.AmbariCluesterInfoBean;
import com.ys.idatrix.metacube.metamanage.beans.AmbariHostInfoBean;
import com.ys.idatrix.metacube.metamanage.beans.Host;

/**
 * ambari平台rest api适配服务接口
 *
 * @author wzl
 */
public interface AmbariRestApiAdapterService {

    /**
     * 服务配置版本 对外实现
     */
    Object getServiceConfigVersions(String header);

    /**
     * 服务配置版本 内部使用
     */
    AmbariCluesterInfoBean getServiceConfigVersions();

    /**
     * 根据主机名名获取主机信息
     */
    AmbariHostInfoBean getHostInfoByHostname(String hostname);

    /**
     * 返回hdfs主机信息
     */
    Host resolveHDFSHost(String str);

    /**
     * 返回hive主机信息
     */
    Host resolveHIVEHost(String str);

    /**
     * 返回hbase主机信息
     */
    Host resolveHBASEHost(String str);

    /**
     * 返回es主机信息
     */
    Host resolveESHost(String str);
}

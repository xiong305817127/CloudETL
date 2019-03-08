package com.ys.idatrix.metacube.api.service;

import com.ys.idatrix.metacube.api.beans.AmbariClusterInfoDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;

/**
 * ambari平台dubbo服务接口
 *
 * @author wzl
 */
public interface AmbariPlatformService {

    /**
     * 获取平台集群信息
     */
    ResultBean<AmbariClusterInfoDTO> getClusterInfo();
}

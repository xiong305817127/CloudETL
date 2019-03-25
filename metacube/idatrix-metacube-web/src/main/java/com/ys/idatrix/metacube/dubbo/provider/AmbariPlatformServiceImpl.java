package com.ys.idatrix.metacube.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.ys.idatrix.metacube.api.beans.AmbariClusterInfoDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.service.AmbariPlatformService;
import com.ys.idatrix.metacube.common.utils.AmbariClusterInfoBeanUtils;
import com.ys.idatrix.metacube.metamanage.beans.AmbariCluesterInfoBean;
import com.ys.idatrix.metacube.metamanage.service.AmbariRestApiAdapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ambari平台dubbo服务实现
 *
 * @author wzl
 */
@Service
@Component
public class AmbariPlatformServiceImpl implements AmbariPlatformService {

    @Autowired
    private AmbariRestApiAdapterService restApiAdapterService;

    /**
     * 获取平台集群信息
     */
    @Override
    public ResultBean<AmbariClusterInfoDTO> getClusterInfo() {
        AmbariCluesterInfoBean bean = restApiAdapterService.getServiceConfigVersions();
        AmbariClusterInfoDTO dto = new AmbariClusterInfoDTO();
        String hdfs = AmbariClusterInfoBeanUtils.getHDFS(bean);
        String hbase = AmbariClusterInfoBeanUtils.getHBASE(bean);
        String hive = AmbariClusterInfoBeanUtils.getHIVE(bean);
        String es = AmbariClusterInfoBeanUtils.getELASTICSEARCH(bean);
        String cloudETL = AmbariClusterInfoBeanUtils.getCLOUDETL(bean);
        dto.setCloudETL(cloudETL).setHdfs(hdfs).setHbase(hbase).setHive(hive).setElasticsearch(es);
        return ResultBean.ok(dto);
    }
}

package com.ys.idatrix.db.service.external.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.MetaDatabaseDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.service.MetadataToDataAnalysisService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @ClassName: MetadataConsumer
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/5
 */
@Component
public class MetadataConsumer {

    /**
     * (url = "10.0.0.108:20892")
     */
    @Reference
    private MetadataToDataAnalysisService dataAnalysisService;

    @Bean
    public MetadataToDataAnalysisService getMetadataToDataAnalysisService() {
        return dataAnalysisService;
    }

    public ResultBean<MetaDatabaseDTO> getDatabaseInfo(String username,Long schemaId){
        return dataAnalysisService.getDatabaseInfo(username, schemaId);
    }

    public ResultBean<ActionTypeEnum> getTbPermiss(String username,Long schemaId,String tableName) {
        return dataAnalysisService.getTbPermiss(username, schemaId, tableName);
    }

    public ResultBean<ActionTypeEnum> getHdfsPermiss(String username,String remoteFileAbsolutePath) {
        return dataAnalysisService.getHdfsPermiss(username, remoteFileAbsolutePath);
    }

}

package com.ys.idatrix.metacube.dubbo.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.idatrix.es.api.dto.req.index.NewIndexDto;
import com.idatrix.es.api.dto.resp.RespResult;
import com.idatrix.es.api.service.IIndexManageService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName: EsConsumer
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/19
 */
@Component
public class EsConsumer {

    @Reference
    private IIndexManageService indexManageService;


    @Bean
    public IIndexManageService getIndexManageService() {
        return indexManageService;
    }


    public RespResult<Boolean> createIndex(NewIndexDto newIndexDto) {
        return indexManageService.createIndex(newIndexDto);
    }


    public RespResult<Boolean> hasExistsIndex(String indexName) {
        return indexManageService.hasExistsIndex(indexName);
    }


    public RespResult<Boolean> deleteIndex(List<String> indices) {
        return indexManageService.deleteIndex(indices);
    }


    public RespResult<Boolean> openOrStopIndex(String indexName, boolean isOpen) {
        return indexManageService.openOrStopIndex(indexName, isOpen);
    }

    public RespResult<Boolean> switchIndexByVersion(String alias, String oldIndexName, String newIndexName) {
        return indexManageService.switchIndexByVersion(alias, oldIndexName, newIndexName);
    }


}

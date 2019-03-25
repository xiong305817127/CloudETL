package com.idatrix.resource.report.service;

import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.ResourceSearchVO;
import com.idatrix.resource.report.vo.response.ResourceCountVO;
import com.idatrix.resource.report.vo.response.ResourceVO;
import java.util.List;

/**
 * 资源报表接口
 *
 * @author wzl
 */
public interface IResourceReportService {

    /**
     * 统计注册、订阅、发布、资源使用频率
     */
    ResourceCountVO count(BaseSearchVO searchVO);

    /**
     * 统计资源注册量
     */
    long countRegister(BaseSearchVO searchVO);

    /**
     * 统计资源订阅量
     */
    long countSubscription(BaseSearchVO searchVO);

    /**
     * 统计资源发布量
     */
    long countPublication(BaseSearchVO searchVO);

    /**
     * 统计资源使用频率
     */
    long countFrequency(BaseSearchVO searchVO);


    /**
     * 查看资源列表
     */
    List<ResourceVO> list(ResourceSearchVO resourceSearchVO);
}

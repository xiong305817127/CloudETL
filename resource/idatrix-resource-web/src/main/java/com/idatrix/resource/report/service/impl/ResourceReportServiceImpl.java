package com.idatrix.resource.report.service.impl;

import com.idatrix.resource.report.dao.ResourceReportDAO;
import com.idatrix.resource.report.enums.ResourceCountTypeEnum;
import com.idatrix.resource.report.service.IResourceReportService;
import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.ResourceSearchVO;
import com.idatrix.resource.report.vo.response.ResourceCountVO;
import com.idatrix.resource.report.vo.response.ResourceVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 资源报表接口实现
 *
 * @author wzl
 */
@Service
public class ResourceReportServiceImpl implements IResourceReportService {

    @Autowired
    private ResourceReportDAO resourceReportDAO;

    /**
     * 统计注册、订阅、发布、资源使用频率
     */
    @Override
    public ResourceCountVO count(BaseSearchVO searchVO) {
        ResourceCountVO countVO = new ResourceCountVO();
        countVO.setRegisterCount(countRegister(searchVO))
                .setSubscriptionCount(countSubscription(searchVO))
                .setPublicationCount(countPublication(searchVO))
                .setFrequencyCount(countFrequency(searchVO));
        return countVO;
    }

    /**
     * 统计资源注册量
     */
    @Override
    public long countRegister(BaseSearchVO searchVO) {
        return resourceReportDAO.countRegister(searchVO);
    }

    /**
     * 统计资源订阅量
     */
    @Override
    public long countSubscription(BaseSearchVO searchVO) {
        return resourceReportDAO.countSubscription(searchVO);
    }

    /**
     * 统计资源发布量
     */
    @Override
    public long countPublication(BaseSearchVO searchVO) {
        return resourceReportDAO.countPublication(searchVO);
    }

    /**
     * 统计资源使用频率
     */
    @Override
    public long countFrequency(BaseSearchVO searchVO) {
        return resourceReportDAO.countFrequency(searchVO);
    }

    /**
     * 查看资源列表
     */
    @Override
    public List<ResourceVO> list(ResourceSearchVO searchVO) {
        if (searchVO.getResourceType().equals(ResourceCountTypeEnum.REGISTER.getCode())) {
            return resourceReportDAO.listRegister(searchVO);
        }
        if (searchVO.getResourceType().equals(ResourceCountTypeEnum.SUBSCRIPTION.getCode())) {
            return resourceReportDAO.listSubscription(searchVO);
        }
        if (searchVO.getResourceType().equals(ResourceCountTypeEnum.PUBLICATION.getCode())) {
            return resourceReportDAO.listPublication(searchVO);
        }
        if (searchVO.getResourceType().equals(ResourceCountTypeEnum.Frequency.getCode())) {
            return resourceReportDAO.listFrequency(searchVO);
        }
        return null;
    }
}

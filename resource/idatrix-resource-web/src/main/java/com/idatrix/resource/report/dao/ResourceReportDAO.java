package com.idatrix.resource.report.dao;

import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.response.ResourceVO;
import java.util.List;

public interface ResourceReportDAO {

    long countRegister(BaseSearchVO searchVO);

    long countSubscription(BaseSearchVO searchVO);

    long countPublication(BaseSearchVO searchVO);

    long countFrequency(BaseSearchVO searchVO);

    List<ResourceVO> listRegister(BaseSearchVO searchVO);

    List<ResourceVO> listSubscription(BaseSearchVO searchVO);

    List<ResourceVO> listPublication(BaseSearchVO searchVO);

    List<ResourceVO> listFrequency(BaseSearchVO searchVO);
}

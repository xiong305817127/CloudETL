package com.idatrix.resource.portal.service;

import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.portal.vo.*;

/**
 *  主要门户查询接口
 */
public interface IQueryService {

    /**
     * 门户页面查询
     * @param requestVO
     * @return
     */
    ResultPager<ResourceQueryVO> queryResourceByCondition(ResourceQueryRequestVO requestVO);


    /**
     * 资源目录搜索(已发布状态)
     *
     * @param searchRequestVO
     * @return
     */
    ResultPager<ResourceSearchVO> queryResourceByKeyword(ResourceSearchRequestVO searchRequestVO) throws Exception;


}

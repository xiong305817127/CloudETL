package com.idatrix.resource.portal.service;

import com.idatrix.resource.portal.vo.CatalogResourceInfo;
import com.idatrix.resource.portal.vo.DeptResourceStatisticsVO;
import com.idatrix.resource.portal.vo.PubCount;
import com.idatrix.resource.portal.vo.ResourcePubInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/12/17.
 */
public interface IHomepageService {

    List<CatalogResourceInfo> getCatalogClassifyInfo(Long rentId, Long id);

    /**
     * 资源分布查询
     * @param rentId
     * @param type
     * @return
     */
    List<DeptResourceStatisticsVO> getDistributeInfo(Long rentId, String type);


    /**
     * 根据租户和数量获取最新的发布消息
     * @param rentId
     * @param count
     * @return
     */
    List<ResourcePubInfo> getResourcePubInfo(Long rentId, Long count);

    /**
     * 获取租户的资源发布统计
     * @param rentId
     * @return
     */
    PubCount getPubTotalCount(Long rentId);

}

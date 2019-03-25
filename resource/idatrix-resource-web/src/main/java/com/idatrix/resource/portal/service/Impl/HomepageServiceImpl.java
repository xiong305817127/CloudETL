package com.idatrix.resource.portal.service.Impl;

import com.idatrix.resource.catalog.dao.CatalogNodeDAO;
import com.idatrix.resource.catalog.dao.CatalogResourceDAO;
import com.idatrix.resource.catalog.po.CatalogNodePO;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.portal.common.ResourceFormatTypeEnum;
import com.idatrix.resource.portal.dao.PortalResourceDAO;
import com.idatrix.resource.portal.service.IHomepageService;
import com.idatrix.resource.portal.vo.CatalogResourceInfo;
import com.idatrix.resource.portal.vo.DeptResourceStatisticsVO;
import com.idatrix.resource.portal.vo.PubCount;
import com.idatrix.resource.portal.vo.ResourcePubInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/17.
 */

@Transactional
@Service("homepageService")
public class HomepageServiceImpl implements IHomepageService {

    @Autowired
    private CatalogResourceDAO catalogResourceDAO;

    @Autowired
    private CatalogNodeDAO catalogNodeDAO;

    @Autowired
    private PortalResourceDAO portalResourceDAO;


    @Override
    public List<CatalogResourceInfo> getCatalogClassifyInfo(Long rentId, Long id) {

        List<CatalogNodePO> cnList = catalogNodeDAO.getCatalogByParentId(rentId, id);
        if(cnList==null || cnList.size()==0){
            return null;
        }

        List<CatalogResourceInfo> crList = new ArrayList<>();
        for(CatalogNodePO cnPO:cnList){
            Long count = catalogResourceDAO.getCatalogCount(cnPO.getId());
            count=count==null?0L:count;
            crList.add(new CatalogResourceInfo(cnPO.getId(), count));
        }
        if(crList==null || crList.size()==0 ){
            crList.add(new CatalogResourceInfo(id, 0L));
        }
        return crList;
    }

    /**
     * 资源分布查询
     *
     * @param rentId
     * @param type
     * @return
     */
    @Override
    public List<DeptResourceStatisticsVO> getDistributeInfo(Long rentId, String type) {


        final String queryResource[] ={"interface","data","resource"};
        List<DeptResourceStatisticsVO> voList = new ArrayList<>();

        for(String tmp: queryResource) {
            List<DeptResourceStatisticsVO> interfaceList
                    = portalResourceDAO.getResourceStatisticsInfo(rentId, tmp, type);
            if (interfaceList != null && interfaceList.size() > 0) {
                voList.addAll(interfaceList);
            }
        }
        return voList;
    }

    /**
     * 根据租户和数量获取最新的发布消息
     * @param rentId
     * @param count
     * @return
     */
    public List<ResourcePubInfo> getResourcePubInfo(Long rentId, Long count){
        List<ResourcePubInfo> pubInfoList = portalResourceDAO.getLastestResourceByCount(rentId, count);
        if(pubInfoList!=null && pubInfoList.size()>0){
            for(ResourcePubInfo pubInfo:pubInfoList){
                pubInfo.setResourceType(ResourceFormatTypeEnum.getFormatDescription(pubInfo.getFormatType()));
//                switch (pubInfo.getFormatType()){
//                    case 3:
//                        pubInfo.setResourceType("数据库");
//                        break;
//                    case 7:
//                        pubInfo.setResourceType("接口");
//                        break;
//                    default:
//                        pubInfo.setResourceType("文件类型");
//                }
            }
        }
        return pubInfoList;
    }

    /**
     * 获取租户的资源发布统计
     *
     * @param rentId
     * @return
     */
    @Override
    public PubCount getPubTotalCount(Long rentId) {

        PubCount countInfo = portalResourceDAO.getPubTotalCount(rentId);
        if(countInfo==null){
            countInfo = new PubCount(0L, 0L, 0L, 0L);
        }else{
            countInfo.setFileCount(countInfo.getTotal()-countInfo.getDbCount()-countInfo.getInterfaceCount());
        }
        return countInfo;
    }
}

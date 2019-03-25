package com.idatrix.resource.common.task;

import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.dao.ResourceStatisticsDAO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.po.ResourceStatisticsPO;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.portal.dao.PortalResourceDAO;
import com.idatrix.resource.portal.dao.ResourceStatisticsVisitDAO;
import com.idatrix.resource.portal.po.ResourceStatisticsVisitPO;
import com.idatrix.resource.report.dao.RcStatisticsResourceVisitDAO;
import com.idatrix.resource.report.po.RcStatisticsResourceVisitPO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.idatrix.resource.common.utils.ResourceTools.ResourceStatus.PUB_SUCCESS;

/**
 * 主要是日常统计，执行周期为日
 */

@Component
public class DailyStaticsTask {

    @Autowired
    private PortalResourceDAO portalResourceDAO;

    @Autowired
    private ResourceStatisticsVisitDAO statisticsVisitDAO;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private ResourceStatisticsDAO resourceStatisticsDAO;

    @Autowired
    private RcStatisticsResourceVisitDAO rcStatisticsResourceVisitDAO;

    public void startTask() {
        //统计日目录访问量：1.每天执行一次， 2.查询的时候执行一次
        String user = "dailyStatics";
        //rentId 根据租户ID来扫,rc_resource left join  getTotalVisitByRentID
        String saveDay = DateTools.formatDate(DateTools.getDateBefore(new Date(), 1), "yyyy-MM-dd");
        String beforeSaveDay = DateTools.formatDate(DateTools.getDateBefore(new Date(), 2), "yyyy-MM-dd");

        List<Long> rentList = resourceConfigDAO.getResourceRentList();
        if(CollectionUtils.isEmpty(rentList)){
            return;
        }

        for(Long rentId : rentList){
            ResourceStatisticsVisitPO visitPO = statisticsVisitDAO.getVisitStatisticsByDayTime(rentId, saveDay);
            if (visitPO==null){
                //获取统计前一天时间和数据
                visitPO = new ResourceStatisticsVisitPO(rentId, user, saveDay);
                Long lastTotalCount = 0L;
                ResourceStatisticsVisitPO visitBeforePO = statisticsVisitDAO.getVisitStatisticsByDayTime(rentId, beforeSaveDay);
                if(visitBeforePO!=null){
                    lastTotalCount = visitBeforePO.getVisitCount()+visitBeforePO.getLastVisitTotal();
                }
                visitPO.setLastVisitTotal(lastTotalCount);
                Long totalCount = portalResourceDAO.getTotalVisitByRentID(rentId);
                if(totalCount==null || totalCount<lastTotalCount){
                    totalCount = 0L;
                }else{
                    totalCount = totalCount-lastTotalCount;
                }
                visitPO.setVisitCount(totalCount);
                statisticsVisitDAO.insert(visitPO);
            }else{
                Long totalCount = portalResourceDAO.getTotalVisitByRentID(rentId);
                if(totalCount==null || totalCount<visitPO.getLastVisitTotal()){
                    totalCount = 0L;
                }else{
                    totalCount = totalCount-visitPO.getLastVisitTotal();
                }
                visitPO.setVisitCount(totalCount);
                visitPO.setUpdateTime(new Date());
                visitPO.setUpdater(user);
                statisticsVisitDAO.updateByPrimaryKey(visitPO);
            }
        }

        //对资源数据进行日访问统计
        List<ResourceConfigPO> resourceIdList = resourceConfigDAO.getResourceIdList(PUB_SUCCESS.getStatusCode());
        if(CollectionUtils.isEmpty(resourceIdList)){
            return;
        }
        resourceIdList.stream().forEach(p->{
            ResourceStatisticsPO rsPO = resourceStatisticsDAO.getLatestByResourceId(p.getId());
            if(rsPO!=null){
                RcStatisticsResourceVisitPO resourceVisitPO = rcStatisticsResourceVisitDAO.getStatisticsByDayTime(p.getId(), saveDay);
                if(resourceVisitPO==null){
                    resourceVisitPO = new RcStatisticsResourceVisitPO(user, p.getRentId(), p.getId());
                    Long resourceLastCount= 0L;
                    RcStatisticsResourceVisitPO lastDayVisitPO = rcStatisticsResourceVisitDAO.getStatisticsByDayTime(p.getId(), beforeSaveDay);
                    if(lastDayVisitPO!=null){
                        resourceLastCount = lastDayVisitPO.getLastVisitTotal()+lastDayVisitPO.getVisitCount();
                    }
                    resourceVisitPO.setDayTime(saveDay);
                    resourceVisitPO.setLastVisitTotal(resourceLastCount);
                    Long visitCount = 0L;
                    if( rsPO.getVisitCount()>resourceLastCount){
                        visitCount = rsPO.getVisitCount()-resourceLastCount;
                    }
                    resourceVisitPO.setVisitCount(visitCount);
                    rcStatisticsResourceVisitDAO.insert(resourceVisitPO);


                }else{
                    int count = 0;
                    if(rsPO.getVisitCount()>resourceVisitPO.getLastVisitTotal().intValue()){
                        count = rsPO.getVisitCount()-resourceVisitPO.getLastVisitTotal().intValue();
                    }
                    resourceVisitPO.setVisitCount(new Long(count));
                    resourceVisitPO.setUpdateTime(new Date());
                    resourceVisitPO.setUpdater(user);
                    rcStatisticsResourceVisitDAO.insertSelective(resourceVisitPO);
                }
            }
        });

    }





}

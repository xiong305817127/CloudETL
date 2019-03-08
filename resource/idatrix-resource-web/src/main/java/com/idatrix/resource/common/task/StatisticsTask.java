package com.idatrix.resource.common.task;

import com.idatrix.resource.catalog.dao.MonthStatisticsDAO;
import com.idatrix.resource.catalog.po.MonthStatisticsPO;
import com.idatrix.resource.catalog.po.StatisticsPO;
import com.idatrix.resource.common.utils.DateTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *  资源的统计数据，主要统计月度为单位-资源的 注册、发布、订阅总量
 *  cron表达式是："0 0 0 1/1 * ? " 表示每天执行一次，执行时间为0点0分
 *
 */
@Component
public class StatisticsTask{

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    private final static int COUNT = 6;
    private final static String USER="admin";

    @Autowired
    private MonthStatisticsDAO monthStatisticsDAO;


    public void startTask(){

//        LOG.info("##############Statistics Task##############");

        //1.月度数据统计
        MonthStatics();

        //2.政务信息资源统计
        ResourceStatics();
    }

    //政务信息资源统计
    private void ResourceStatics(){

    }

    //阅读信息统计
    private void MonthStatics(){
        //1.根据月份从 rc_month_statistics 里面获取当前月份是否有统计数据
        String thisMonth = DateTools.formatDate(new Date(), "yyyyMM");

        List<StatisticsPO> statPoList = monthStatisticsDAO.getRecentMonth(COUNT);
        if(statPoList!=null && statPoList.size()>0){
            for(StatisticsPO stat:statPoList){
                String month = stat.getMonth();
                MonthStatisticsPO monthStatPO = monthStatisticsDAO.getByMonth(month);
                if(monthStatPO!=null) {
                    if (monthStatPO.getRegCount() != stat.getRegCount() ||
                            monthStatPO.getPubCount() != stat.getPubCount() ||
                            monthStatPO.getSubCount() != stat.getSubCount()) {
                        monthStatPO.setRegCount(stat.getRegCount().intValue());
                        monthStatPO.setPubCount(stat.getPubCount().intValue());
                        monthStatPO.setSubCount(stat.getSubCount().intValue());
                        monthStatPO.setModifyTime(new Date());
                        monthStatisticsDAO.updateById(monthStatPO);

                    }
                }else{
                    monthStatPO = new MonthStatisticsPO();
                    monthStatPO.setMonth(stat.getMonth());
                    monthStatPO.setRegCount(stat.getRegCount().intValue());
                    monthStatPO.setPubCount(stat.getPubCount().intValue());
                    monthStatPO.setSubCount(stat.getSubCount().intValue());
                    monthStatPO.setModifyTime(new Date());
                    monthStatPO.setCreateTime(new Date());
                    monthStatPO.setCreator(USER);
                    monthStatPO.setModifier(USER);
                    monthStatisticsDAO.insert(monthStatPO);
                }
            }
        }
    }


}

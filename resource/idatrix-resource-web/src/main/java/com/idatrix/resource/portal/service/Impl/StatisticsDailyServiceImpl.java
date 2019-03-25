package com.idatrix.resource.portal.service.Impl;

import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.LibInfo;
import com.idatrix.resource.portal.common.ResourceFormatTypeEnum;
import com.idatrix.resource.portal.common.StatisticsDailyEnum;
import com.idatrix.resource.portal.dao.ResourceStatisticsDailyDAO;
import com.idatrix.resource.portal.po.ResourceStatisticsDailyPO;
import com.idatrix.resource.portal.service.IStatisticsDailyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 统计资源发布和上报信息
 */
@Slf4j
@Transactional
@Service("statisticsDailyService")
public class StatisticsDailyServiceImpl implements IStatisticsDailyService {

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private ResourceStatisticsDailyDAO resourceStatisticsDailyDAO;

    /**
     * 初次以及更新存储 资源统计数据
     *
     * @param resourceId
     * @param importFieldName  需要设置的变量参数名
     * @param value      配置数值
     */
    @Override
    public void saveStatisticsDaily(Long resourceId, StatisticsDailyEnum importFieldName, String value)
        throws  Exception{
        String queryTime = DateTools.formatDate(new Date(), "yyyy-MM-dd");
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        Long rentId = rcPO.getRentId();
        ResourceStatisticsDailyPO dailyPO = resourceStatisticsDailyDAO.getStatisticsDailyByDayTime(rentId, queryTime, resourceId);
        if(dailyPO==null){
            dailyPO = new ResourceStatisticsDailyPO(rentId, rcPO.getCreator());
            dailyPO.setResourceId(rcPO.getId());
            dailyPO.setDayTime(queryTime);

            Long deptId = 0L;
            String[] depts = rcPO.getDeptNameIds().split(",");
            if (depts.length > 1) {
                deptId = Long.valueOf(depts[depts.length - 1]);
            }
            dailyPO.setProvideDeptId(deptId);
            dailyPO.setProvideDeptName(rcPO.getDeptName());
            dailyPO.setResourceLibType(LibInfo.getLibName(rcPO.getCatalogCode()));
            dailyPO.setResourceType(ResourceFormatTypeEnum.getFormatType(rcPO.getFormatType()));
            CommonUtils.refreshAddValue(dailyPO, importFieldName.getParaName(), Long.valueOf(value));
            resourceStatisticsDailyDAO.insert(dailyPO);
        }else{
            dailyPO.setUpdateTime(new Date());
            dailyPO.setUpdater(rcPO.getCreator());
            CommonUtils.refreshAddValue(dailyPO, importFieldName.getParaName(), Long.valueOf(value));
            resourceStatisticsDailyDAO.updateByPrimaryKey(dailyPO);
        }
    }

    /**
     * 初次以及更新存储 资源统计数据
     *
     * @param resourceId
     */
    @Override
    public void saveStatisticsDaily(Long resourceId) throws Exception {
        saveStatisticsDaily(resourceId, null, null);
    }

    /**
     * 资源统计数据新增存储
     *
     * @param resourceId
     * @param value
     */
    @Override
    public void saveStatisticsDaily(Long resourceId, Long value) {
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        try {
            if (StringUtils.equalsAnyIgnoreCase(
                    ResourceFormatTypeEnum.getFormatType(rcPO.getFormatType()),"file")) {
                saveStatisticsDaily(resourceId, StatisticsDailyEnum.FILE_COUNT, value.toString());
            }else if(StringUtils.equalsAnyIgnoreCase(
                    ResourceFormatTypeEnum.getFormatType(rcPO.getFormatType()),"db")){
                saveStatisticsDaily(resourceId, StatisticsDailyEnum.DB_COUNT, value.toString());
            }else if(StringUtils.equalsAnyIgnoreCase(
                    ResourceFormatTypeEnum.getFormatType(rcPO.getFormatType()),"interface")){
                saveStatisticsDaily(resourceId, StatisticsDailyEnum.INTERFACE_COUNT, value.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("保存资源发布和上报异常："+e.getMessage());
        }
    }

    /**
     * 增加文件类型统计
     *
     * @param resourceId
     * @param value
     */
    @Override
    public void saveFileStatisticsDaily(Long resourceId, Long value) {
        try {
            saveStatisticsDaily(resourceId, StatisticsDailyEnum.FILE_COUNT, value.toString());
        }catch (Exception e){
            e.printStackTrace();
            log.error("保存资源发布和上报异常："+e.getMessage());
    }
    }

    /**
     * 增加数据库类型统计
     *
     * @param resourceId
     * @param value
     */
    @Override
    public void saveDBStatisticsDaily(Long resourceId, Long value) {
        try {
            saveStatisticsDaily(resourceId, StatisticsDailyEnum.DB_COUNT,  value.toString());
        }catch (Exception e){
            e.printStackTrace();
            log.error("保存资源发布和上报异常："+e.getMessage());
        }
    }

    /**
     * 增加接口类型统计
     *
     * @param resourceId
     * @param value
     */
    @Override
    public void saveInterfaceStatisticsDaily(Long resourceId, Long value)  {
        try {
            saveStatisticsDaily(resourceId, StatisticsDailyEnum.INTERFACE_COUNT,  value.toString());
        }catch (Exception e){
            e.printStackTrace();
            log.error("保存资源发布和上报异常："+e.getMessage());
        }
    }


}

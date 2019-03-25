package com.idatrix.resource.catalog.service.Impl;

import com.idatrix.resource.catalog.dao.ResourceStatisticsDAO;
import com.idatrix.resource.catalog.po.ResourceStatisticsPO;
import com.idatrix.resource.catalog.service.IResourceStatiscsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Administrator on 2018/9/6.
 */

@Transactional
@Service("resourceStatiscsService")
public class ResourceStatiscsServiceImpl implements IResourceStatiscsService{

    private String user = "admin";

    @Autowired
    private ResourceStatisticsDAO resourceStatisticsDAO;


    @Override
    public void increaseSubCount(Long resourceId) {

        ResourceStatisticsPO rsPO = resourceStatisticsDAO.getLatestByResourceId(resourceId);
        if(rsPO==null){
            ResourceStatisticsPO rsSavePO = new ResourceStatisticsPO();
            rsSavePO.setSubCount(1);
            rsSavePO.setCreator(user);
            rsSavePO.setCreateTime(new Date());
            rsSavePO.setModifier(user);
            rsSavePO.setModifyTime(new Date());
            rsSavePO.setId(resourceId);
            //rsSavePO.setDataUpdateTime(new Date());
            resourceStatisticsDAO.insert(rsSavePO);
        }else {
            rsPO.setModifier(user);
            rsPO.setModifyTime(new Date());
            rsPO.setSubCount(rsPO.getSubCount() + 1);
            resourceStatisticsDAO.updateById(rsPO);
        }

}

    @Override
    public void increaseDataCount(Long resourceId, Long count) {
        ResourceStatisticsPO rsPO = resourceStatisticsDAO.getLatestByResourceId(resourceId);
        if(rsPO==null){
            ResourceStatisticsPO rsSavePO = new ResourceStatisticsPO();
            rsSavePO.setDataCount(count);
            rsSavePO.setCreator(user);
            rsSavePO.setCreateTime(new Date());
            rsSavePO.setModifier(user);
            rsSavePO.setModifyTime(new Date());
            rsSavePO.setId(resourceId);
            rsSavePO.setDataUpdateTime(new Date());
            resourceStatisticsDAO.insert(rsSavePO);
        }else {
            rsPO.setModifier(user);
            rsPO.setModifyTime(new Date());
            rsPO.setDataCount(rsPO.getDataCount() + count);
            rsPO.setDataUpdateTime(new Date());
            resourceStatisticsDAO.updateById(rsPO);
        }
    }

    @Override
    public void refreshDataCount(Long resourceId, Long count) {
        ResourceStatisticsPO rsPO = resourceStatisticsDAO.getLatestByResourceId(resourceId);
        if(rsPO==null){
            ResourceStatisticsPO rsSavePO = new ResourceStatisticsPO();
            rsSavePO.setDataCount(count);
            rsSavePO.setCreator(user);
            rsSavePO.setCreateTime(new Date());
            rsSavePO.setModifier(user);
            rsSavePO.setModifyTime(new Date());
            rsSavePO.setId(resourceId);
            rsSavePO.setDataUpdateTime(new Date());
            resourceStatisticsDAO.insert(rsSavePO);
        }else {
            rsPO.setModifier(user);
            rsPO.setModifyTime(new Date());
            rsPO.setDataCount(count);
            rsPO.setDataUpdateTime(new Date());
            resourceStatisticsDAO.updateById(rsPO);
        }
    }

    @Override
    public void increaseShareDataCount(Long resourceId, Long count) {
        ResourceStatisticsPO rsPO = resourceStatisticsDAO.getLatestByResourceId(resourceId);
        if(rsPO==null){
            ResourceStatisticsPO rsSavePO = new ResourceStatisticsPO();
            rsSavePO.setShareDataCount(count);
            rsSavePO.setCreator(user);
            rsSavePO.setCreateTime(new Date());
            rsSavePO.setModifier(user);
            rsSavePO.setModifyTime(new Date());
            rsSavePO.setId(resourceId);
            //rsSavePO.setDataUpdateTime(new Date());
            resourceStatisticsDAO.insert(rsSavePO);
        }else {
            rsPO.setModifier(user);
            rsPO.setModifyTime(new Date());
            rsPO.setShareDataCount(rsPO.getShareDataCount() + count);
            resourceStatisticsDAO.updateById(rsPO);
        }
    }

    private void saveResourceStatistics(ResourceStatisticsPO rsSavePO){
        String user = "admin";
        Long id = rsSavePO.getId();

        rsSavePO.setModifier(user);
        rsSavePO.setModifyTime(new Date());
        if(id==null || id==0){

            rsSavePO.setCreator(user);
            rsSavePO.setCreateTime(new Date());
            resourceStatisticsDAO.insert(rsSavePO);
        }else{
            resourceStatisticsDAO.updateById(rsSavePO);
        }
    }

    @Override
    public void refreshStatisticsData(Long resourceId, int subCount, Long dataCount, Long shareDataCount) {
        ResourceStatisticsPO rsPO = resourceStatisticsDAO.getLatestByResourceId(resourceId);
        if(rsPO==null) {
            rsPO = new ResourceStatisticsPO();

            rsPO.setShareDataCount(shareDataCount);
            rsPO.setDataCount(dataCount);
            rsPO.setSubCount(subCount);

            rsPO.setCreator(user);
            rsPO.setCreateTime(new Date());
            rsPO.setModifier(user);
            rsPO.setModifyTime(new Date());
            rsPO.setId(resourceId);
            //rsSavePO.setDataUpdateTime(new Date());
            resourceStatisticsDAO.insert(rsPO);
        }else {
            rsPO.setModifier(user);
            rsPO.setModifyTime(new Date());
            rsPO.setShareDataCount(shareDataCount);
            rsPO.setDataCount(dataCount);
            rsPO.setSubCount(subCount);
            resourceStatisticsDAO.updateById(rsPO);
        }

    }

    @Override
    public void increaseViewDataCount(Long resourceId) {
        ResourceStatisticsPO rsPO = resourceStatisticsDAO.getLatestByResourceId(resourceId);
        if(rsPO==null){
            ResourceStatisticsPO rsSavePO = new ResourceStatisticsPO();
            rsSavePO.setVisitCount(1);
            rsSavePO.setCreator(user);
            rsSavePO.setCreateTime(new Date());
            rsSavePO.setModifier(user);
            rsSavePO.setModifyTime(new Date());
            rsSavePO.setId(resourceId);
            resourceStatisticsDAO.insert(rsSavePO);
        }else {
            rsPO.setModifier(user);
            rsPO.setModifyTime(new Date());
            rsPO.setVisitCount(rsPO.getVisitCount() + 1);
            resourceStatisticsDAO.updateById(rsPO);
        }
    }
}

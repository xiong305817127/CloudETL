package com.idatrix.resource.portal.service.Impl;

import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.portal.common.ResourceFormatTypeEnum;
import com.idatrix.resource.portal.common.StatisticsDailyEnum;
import com.idatrix.resource.portal.dao.ResourceStatisticsDeptDAO;
import com.idatrix.resource.portal.po.ResourceStatisticsDeptPO;
import com.idatrix.resource.portal.service.IStatisticsDeptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Administrator on 2018/12/28.
 */
@Slf4j
@Transactional
@Service("statisticsDeptService")
public class StatisticsDeptServiceImpl implements IStatisticsDeptService {

    @Autowired
    private ResourceStatisticsDeptDAO resourceStatisticsDeptDAO;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    /**
     * 部门交互成功时候记录数据
     *
     * @param deptId
     * @param deptName
     * @param resourceId
     * @param importFieldName
     * @param value
     * @throws Exception
     */
    @Override
    public void saveStatisticsDept(Long deptId, String deptName, Long resourceId, StatisticsDailyEnum importFieldName, String value) throws Exception {

        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        Long rentId = rcPO.getRentId();
        ResourceStatisticsDeptPO deptPO = resourceStatisticsDeptDAO.getStatisticsDept(rentId, deptId, resourceId);
        if(deptPO==null){
            deptPO = new ResourceStatisticsDeptPO(rentId, rcPO.getCreator(), deptId, deptName);
            deptPO.setResourceId(rcPO.getId());
            deptPO.setResourceType(ResourceFormatTypeEnum.getFormatType(rcPO.getFormatType()));
            CommonUtils.refreshAddValue(deptPO, importFieldName.getParaName(), Long.valueOf(value));
            resourceStatisticsDeptDAO.insert(deptPO);
        }else{
            deptPO.setUpdater(rcPO.getCreator());
            deptPO.setUpdateTime(new Date());
            CommonUtils.refreshAddValue(deptPO, importFieldName.getParaName(), Long.valueOf(value));
            resourceStatisticsDeptDAO.updateByPrimaryKey(deptPO);
        }
    }

    /**
     * 记录部门分享数值
     *
     * @param deptId
     * @param deptName
     * @param resourceId
     * @param value
     */
    @Override
    public void saveDeptShareInfo(Long deptId, String deptName, Long resourceId, Long value) {
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
        try {
            if (StringUtils.equalsAnyIgnoreCase(
                    ResourceFormatTypeEnum.getFormatType(rcPO.getFormatType()), "file")) {
                saveStatisticsDept(deptId, deptName, resourceId, StatisticsDailyEnum.FILE_COUNT, value.toString());
            } else if (StringUtils.equalsAnyIgnoreCase(
                    ResourceFormatTypeEnum.getFormatType(rcPO.getFormatType()), "db")) {
                saveStatisticsDept(deptId, deptName, resourceId, StatisticsDailyEnum.DB_COUNT, value.toString());
            } else if (StringUtils.equalsAnyIgnoreCase(
                    ResourceFormatTypeEnum.getFormatType(rcPO.getFormatType()), "interface")) {
                saveStatisticsDept(deptId, deptName, resourceId, StatisticsDailyEnum.INTERFACE_COUNT, value.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("根据部门信息保存时提示异常："+e.getMessage());
        }



    }

}

package com.idatrix.resource.taskmanage.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.datareport.dao.DataUploadDAO;
import com.idatrix.resource.taskmanage.po.UploadTaskOverviewPO;
import com.idatrix.resource.taskmanage.service.IUploadTaskService;
import com.idatrix.resource.taskmanage.vo.DescribeInfoVO;
import com.idatrix.resource.taskmanage.vo.RunnningTaskVO;
import com.idatrix.resource.taskmanage.vo.TaskStatisticsVO;
import com.idatrix.resource.taskmanage.vo.UploadTaskOverviewVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 上报作业管理
 */

@Transactional
@Service("uploadTask")
public class UploadTaskServiceImpl implements IUploadTaskService {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataUploadDAO dataUploadDAO;

    @Override
    public ResultPager<UploadTaskOverviewVO> queryOverview(Map<String, String> con, Integer pageNum, Integer pageSize) {

        //数据类型
        String dataType = con.get("dataType");
        if(StringUtils.isNotEmpty(dataType)) {
            dataType = dataType.toUpperCase();
            con.remove("dataType");
            if(StringUtils.contains(dataType, "ALL")){

            }else if (StringUtils.contains(dataType, "DB")) {
                con.put("dataType", "DB");
            }else{
                con.put("dataType", "FILE");
            }
        }

        //任务状态：当前状态:wait_import等待入库,importing入库中,import_complete已入库,import_error入库失败
        String status = con.get("status");
        if(StringUtils.isNotEmpty(status)) {
            con.remove("status");
            status=status.toUpperCase();
            if(StringUtils.contains(status, "ALL")){

            }else if (StringUtils.contains(status, "WAIT_IMPORT")) {
                con.put("status", "WAIT_IMPORT");
            }else if (StringUtils.contains(status, "IMPORTING")) {
                con.put("status", "IMPORTING");
            }else if (StringUtils.contains(status, "IMPORT_COMPLETE")) {
                con.put("status", "IMPORT_COMPLETE");
            }else if (StringUtils.contains(status, "IMPORT_ERROR")) {
                con.put("status", "IMPORT_ERROR");
            }else if (StringUtils.contains(status, "STOP_IMPORT")) {
                con.put("status", "STOP_IMPORT");
            }
        }
        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);

        List<UploadTaskOverviewPO> overviewList = dataUploadDAO.queryOverview(con);
        List<UploadTaskOverviewVO> soVOList = transferUploadTaskOverviewPoToVO(overviewList);

        //用PageInfo对结果进行包装
        PageInfo<UploadTaskOverviewPO> pi = new PageInfo<UploadTaskOverviewPO>(overviewList);
        Long totalNums = pi.getTotal();
        ResultPager<UploadTaskOverviewVO> rp = new ResultPager<UploadTaskOverviewVO>(pi.getPageNum(), totalNums, soVOList);
        return rp;
    }

    @Override
    public void startTask(String user, Long taskId) {

    }

    @Override
    public void stopTask(String user, Long taskId) {

    }

    @Override
    public TaskStatisticsVO getTaskStatistics(Long num) {

        TaskStatisticsVO statisVO = new TaskStatisticsVO();
        //作业总数
        Long allCount = dataUploadDAO.getTaskCount();
        statisVO.setCount(allCount);
        //最近几个月数字
        List<DescribeInfoVO> infoList = dataUploadDAO.getTaskInfoByMonth(num);
        List<DescribeInfoVO> finalInfoList = new ArrayList<DescribeInfoVO>();
        List<String> monthList = CommonUtils.getRecentMonthStr(num.intValue());
        for(String month :monthList){

            boolean ownFlag = false;
            DescribeInfoVO newInfoVO=new DescribeInfoVO();
            newInfoVO.setMonth(CommonUtils.formatMonthStr(month));
            if(infoList!=null && infoList.size()>0){
                for (DescribeInfoVO model : infoList) {
                    if (StringUtils.equals(model.getMonth(), month)) {
                        ownFlag = true;
                        newInfoVO.setDateCount(model.getDateCount());
                        newInfoVO.setTaskCount(model.getTaskCount());
                        break;
                    }
                }
            }
            if(!ownFlag){
                newInfoVO.setDateCount(0L);
                newInfoVO.setTaskCount(0L);
            }
            finalInfoList.add(newInfoVO);
        }
        statisVO.setDescribes(finalInfoList);
        return statisVO;
    }

    @Override
    public RunnningTaskVO getRunningTask(Long num) {

        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("status", "IMPORTING");
        List<UploadTaskOverviewPO> overviewList = dataUploadDAO.queryOverview(conditionMap);

        RunnningTaskVO taskVO = new RunnningTaskVO();
        if(overviewList==null || overviewList.size()==0){
            taskVO.setCount(0L);
            taskVO.setTaskInfo(null);
        }else{
            taskVO.setCount(new Long(overviewList.size()));
            List<UploadTaskOverviewVO> soVOList = transferUploadTaskOverviewPoToVO(overviewList);
            if(soVOList!=null) {
                taskVO.setTaskInfo(num < soVOList.size() ? soVOList.subList(0, num.intValue()) : soVOList);
            }else{
                taskVO.setTaskInfo(null);
            }
        }
        return taskVO;
    }


    private List<UploadTaskOverviewVO> transferUploadTaskOverviewPoToVO(List<UploadTaskOverviewPO> poList){
        List<UploadTaskOverviewVO>  uploadVOList= new ArrayList<UploadTaskOverviewVO>();
        if(poList==null){
            return uploadVOList;
        }

        for(UploadTaskOverviewPO po : poList){
            UploadTaskOverviewVO vo = new UploadTaskOverviewVO();

            vo.setTaskName(po.getTaskName());
            vo.setDeptName(po.getProvideDept());
            vo.setTaskType(po.getTaskType());
            vo.setDataCount(po.getDataCount());
            vo.setStatus(po.getStatus());
            vo.setEtlSubcribeId(po.getEtlSubscribeId());
            vo.setCreator(po.getCreator());

            Date startTime = po.getLastStartTime();
            if(startTime!=null){
                vo.setStartTime(DateTools.formatDate(startTime));
            }
            Date endTime = po.getLastEndTime();
            if(endTime!=null){
                vo.setEndTime(DateTools.formatDate(endTime));
            }

            uploadVOList.add(vo);
        }
        return uploadVOList;
    }



}

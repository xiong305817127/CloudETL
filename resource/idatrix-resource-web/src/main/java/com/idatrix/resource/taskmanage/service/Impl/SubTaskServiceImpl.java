package com.idatrix.resource.taskmanage.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.taskmanage.dao.SubTaskDAO;
import com.idatrix.resource.taskmanage.dao.SubTaskExecDAO;
import com.idatrix.resource.taskmanage.po.SubTaskExecPO;
import com.idatrix.resource.taskmanage.po.SubTaskOverviewPO;
import com.idatrix.resource.taskmanage.service.ISubTaskService;
import com.idatrix.resource.taskmanage.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *  交换任务服务
 */

@Transactional
@Service("subTaskService")
public class SubTaskServiceImpl implements ISubTaskService {


    @Autowired
    private SubTaskDAO subTaskDAO;

    @Autowired
    private SubTaskExecDAO subTaskExecDAO;

    @Override
    public ResultPager<SubTaskOverviewVO> queryOverview(Map<String, String> con, Integer pageNum, Integer pageSize) {

        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);

        List<SubTaskOverviewPO> overviewList = subTaskDAO.queryOverview(con);
        List<SubTaskOverviewVO> soVOList = transferUploadTaskOverviewPoToVO(overviewList);

        //用PageInfo对结果进行包装
        PageInfo<SubTaskOverviewPO> pi = new PageInfo<SubTaskOverviewPO>(overviewList);
        Long totalNums = pi.getTotal();
        ResultPager<SubTaskOverviewVO> rp = new ResultPager<SubTaskOverviewVO>(pi.getPageNum(), totalNums, soVOList);
        return rp;
    }


    /*根据传入参数就是 订阅时候生成的 subXXXXXXXX字符*/
    @Override
    public List<SubTaskHistoryVO> getHistory(String user, String taskId) throws Exception{

        List<SubTaskExecPO> taskList = subTaskExecDAO.getExecInfoByTaskId(taskId);
        if(taskList==null || taskList.size()==0){
            return null;
        }
        return transferTaskExecPoTOHistory(taskList);
    }

    @Override
    public TaskStatisticsVO getTaskStatistics(Long num) {

        TaskStatisticsVO statisVO = new TaskStatisticsVO();
        //作业总数
        Long allCount = subTaskDAO.getTaskCount();
        statisVO.setCount(allCount);
        //最近几个月数字
        List<DescribeInfoVO> infoList = subTaskDAO.getTaskInfoByMonth(num);

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
        List<SubTaskOverviewPO> overviewList = subTaskDAO.queryOverview(conditionMap);
        RunnningTaskVO taskVO = new RunnningTaskVO();

        if(overviewList==null || overviewList.size()==0){
            taskVO.setCount(0L);
            taskVO.setExchangTaskInfo(null);
        }else{
            taskVO.setCount(new Long(overviewList.size()));
            List<SubTaskOverviewVO> soVOList = transferUploadTaskOverviewPoToVO(overviewList);
            if(soVOList!=null) {
                taskVO.setExchangTaskInfo(num<soVOList.size()?soVOList.subList(0, num.intValue()):soVOList);
            }else{
                taskVO.setExchangTaskInfo(null);
            }
        }
        return taskVO;
    }

    @Override
    public void startTask(String user, Long taskId) {

    }

    @Override
    public void stopTask(String user, Long taskId) {

    }

    private List<SubTaskHistoryVO> transferTaskExecPoTOHistory(List<SubTaskExecPO> poList){
        List<SubTaskHistoryVO> historyList = new ArrayList<SubTaskHistoryVO>();
        if(poList==null || poList.size()==0){
            return historyList;
        }

        for(SubTaskExecPO po:poList){
            SubTaskHistoryVO historyVO = new SubTaskHistoryVO();

            historyVO.setStatus(po.getStatus());
            historyVO.setTaskId(po.getSubTaskId());
            historyVO.setEtlSubscribeId(po.getEtlSubscribeId());
            historyVO.setEtlRunningId(po.getEtlRunningId());

            historyVO.setDataCount(po.getImportCount());
            Date startTime = po.getStartTime();
            if(startTime!=null){
                historyVO.setStartTime(DateTools.formatDate(startTime));
            }
            Date endTime = po.getEndTime();
            if(endTime!=null){
                historyVO.setEndTime(DateTools.formatDate(endTime));
            }

            historyList.add(historyVO);
        }
        return historyList;
    }

    private List<SubTaskOverviewVO> transferUploadTaskOverviewPoToVO(List<SubTaskOverviewPO> poList){
        List<SubTaskOverviewVO> uploadVOList= new ArrayList<SubTaskOverviewVO>();
        if(poList==null){
            return uploadVOList;
        }

        for(SubTaskOverviewPO po : poList){
            SubTaskOverviewVO vo = new SubTaskOverviewVO();

            vo.setDataCount(po.getDataCount());
            vo.setCode(po.getCode());
            vo.setName(po.getResourceName());
            Date endTime = po.getEndTime();
            if(endTime!=null){
                vo.setEndTime(DateTools.formatDate(endTime));
            }
            Date lastRunTime = po.getLastRunTime();
            if(lastRunTime!=null) {
                vo.setLastRunTime(DateTools.formatDate(lastRunTime));
                vo.setStartTime(DateTools.formatDate(lastRunTime));
            }
            vo.setProvideDept(po.getProvideDept());
            vo.setSubscribeDept(po.getSubscribeDept());
            vo.setDataCount(po.getDataCount());
            vo.setSubscribeId(po.getEtlSubscribeId());
            vo.setTaskStatus(po.getTaskStatus());
            vo.setTaskName(po.getSubTaskId());
            vo.setTaskType(po.getTaskType());
            vo.setCreator(po.getSubscribeUser());
            uploadVOList.add(vo);
        }
        return uploadVOList;
    }
}

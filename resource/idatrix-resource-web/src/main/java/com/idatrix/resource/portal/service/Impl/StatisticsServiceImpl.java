package com.idatrix.resource.portal.service.Impl;

import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.portal.dao.PortalResourceDAO;
import com.idatrix.resource.portal.dao.ResourceStatisticsVisitDAO;
import com.idatrix.resource.portal.po.ResourceStatisticsVisitPO;
import com.idatrix.resource.portal.service.IStatisticsService;
import com.idatrix.resource.portal.vo.*;
import com.idatrix.unisecurity.api.domain.LoginDateInfo;
import com.idatrix.unisecurity.api.domain.NowLoginResult;
import com.idatrix.unisecurity.api.domain.OrganizationUserLoginInfo;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.idatrix.unisecurity.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 主要是资源门户统计服务
 */

@Slf4j
@Transactional
@Service("statisticsService")
public class StatisticsServiceImpl implements IStatisticsService {

    @Autowired
    private PortalResourceDAO portalResourceDAO;

    @Autowired
    private ResourceStatisticsVisitDAO visitDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    /**
     * 获取目录日访问统计
     *
     * @param rentId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<VisitStatisticsVO> getVisitStatisticsByDay(Long rentId, Date startTime, Date endTime) {

        List<ResourceStatisticsVisitPO> visitPOList = visitDAO.queryRcVisitByTime(rentId, startTime, endTime);
        List<VisitStatisticsVO> voList = new ArrayList<>();
        if(visitPOList==null || visitPOList.size()==0){
            return getContinuousInfo(startTime, endTime, voList);
        }
        for(ResourceStatisticsVisitPO po:visitPOList){
            VisitStatisticsVO visitVO = new VisitStatisticsVO(po.getVisitCount(), po.getDayTime());
            voList.add(visitVO);
        }
        return getContinuousInfo(startTime, endTime, voList);
    }

    private List<VisitStatisticsVO> getContinuousInfo(Date startTime, Date endTime, List<VisitStatisticsVO> voList){
        List<VisitStatisticsVO> targetList = new ArrayList<>();
        List<String> timeList = CommonUtils.getRecentDayList(startTime, endTime);
        if(voList==null || voList.size()==0){
            for(String timeVO:timeList){
                if(DateTools.parseDate(timeVO).before(startTime)){
                    continue;
                }
                targetList.add(new VisitStatisticsVO(timeVO));
            }
            return targetList;
        }
        for(String timeStr:timeList){
            for(VisitStatisticsVO vo: voList){
                if(DateTools.parseDate(vo.getVisitTime()).equals(DateTools.parseDate(timeStr))){
                    targetList.add(vo);
                    break;
                }
            }
            targetList.add(new VisitStatisticsVO(timeStr));
        }
        return targetList;
    }



    /**
     * 获取资源分享类型统计数据
     *
     * @param rentId
     * @return
     */
    @Override
    public ShareTypeStatisticsVO getShareTypeStatistics(Long rentId) {
          return portalResourceDAO.getShareStatistics(rentId);
    }

    /**
     * 目录录入填报情况统计
     *
     * @param rentId
     * @return
     */
    @Override
    public TypeInStatisticsVO getTypeInStatistics(Long rentId) {

        //统计DAO 还剩下deptTotal未统计
        TypeInStatisticsVO typeVO = portalResourceDAO.getTypeInStatisticsByRentId(rentId);
        Integer totalDept = organizationService.findAscriptionDeptCountByRenterId(rentId);
        if(totalDept!=null){
            typeVO.setDeptTotal(Long.valueOf(totalDept));
        }
        return typeVO;
    }

    /**
     * 资源数据录入填报情况统计
     *
     * @param rentId
     * @return
     */
    @Override
    public ResourceUseStatisticsVO getResourceUseStatistics(Long rentId) {
        ResourceUseStatisticsVO useVO = portalResourceDAO.getResourceUseStatisticsByRentId(rentId);
        useVO.setTotalUploadData(useVO.getDeptCount());
        return useVO;
    }

    /**
     * 获取部门提供资源统计
     *
     * @param rentId
     * @param num
     * @return
     */
    @Override
    public List<DeptResourceStatisticsVO> getDeptSupplyStatistics(Long rentId, Long num) {
        return portalResourceDAO.getDeptSupplyData(rentId, num);
    }

    /**
     * 获取部门调用资源情况
     *
     * @param rentId
     * @param num
     * @return
     */
    @Override
    public List<DeptResourceStatisticsVO> getDeptUseStatistics(Long rentId, Long num) {

        List<DeptResourceStatisticsVO> voList = new ArrayList<>();
        //db/file/interface
        String[] typeList={"db", "file", "interface"};
        for(String type: typeList){
            List<DeptResourceStatisticsVO> dbList = portalResourceDAO.getDeptUseResource(rentId, type, num);
            if(dbList!=null && dbList.size()>0){
                voList.addAll(dbList);
            }
        }
        return voList;
    }

    /**
     * 根据租户获取平台运行情况
     *
     * @param rentId
     * @return
     */
    @Override
    public PlatformRunningVO getPlatformRunningStatistics(Long rentId) {

        PlatformRunningVO runningVO = new PlatformRunningVO();

        //TODO: 需要和安全那边对接 - findNowLoginInfoByRenterId
        NowLoginResult nowLogin = userService.findNowLoginInfoByRenterId(rentId);
        if(nowLogin==null){
            return null;
        }
        runningVO.setLoginTotal(nowLogin.getAllLoginCount().longValue());
        runningVO.setLoginDeptDailyCount(nowLogin.getNowLoginDeptCount().longValue());
        runningVO.setLoginDailyCount(nowLogin.getNowLoginCount().longValue());

        Long visitCount=portalResourceDAO.getTotalVisitByRentID(rentId);
        runningVO.setVisitTotal(visitCount);

        //获取最新资源浏览统计
        String lastDayTime = DateTools.formatDate(DateTools.getDateBefore(new Date(), 1), "yyyy-MM-dd");
        ResourceStatisticsVisitPO visitPO = visitDAO.getVisitStatisticsByDayTime(rentId, lastDayTime);
        Long dayVisitCount = 0L;
        if(visitPO!=null){
            dayVisitCount = visitCount-visitPO.getLastVisitTotal()-visitPO.getVisitCount();
        }else{
            dayVisitCount = visitCount;
        }
        runningVO.setVisitDailyCount(dayVisitCount);
        return runningVO;
    }

    /**
     * 获取所属不猛登陆情况统计
     *
     * @param rentId
     * @param num
     * @return
     */
    @Override
    public List<DeptResourceStatisticsVO> getDeptLoginStatistics(Long rentId, Long num) {

        //TODO: 需要和安全对接 - findDeptUserLoginInfoByRentId
        List<DeptResourceStatisticsVO> deptList = new ArrayList<>();

        List<OrganizationUserLoginInfo> loginList = userService.findDeptUserLoginInfoByRentId(rentId);
        if(loginList==null || loginList.size()==0){
            return null;
        }
        for(OrganizationUserLoginInfo loginInfo: loginList){
            String deptName = loginInfo.getDeptName();
            Long loginCount =new Long(loginInfo.getCount());
            deptList.add(new DeptResourceStatisticsVO("login", deptName, loginCount, null));
        }
        return deptList;
    }

    /**
     * 根据租户获取每天登陆次数和登陆单位统计
     *
     * @param rentId
     * @return
     */
    @Override
    public List<LoginStatisticsVO> getDailyLoginStatistics(Long rentId,Date startTime, Date endTime) {

        //TODO: 需要和安全对接 - findUserLoginInfoByRenterIdAndTimeSlot
        List<LoginStatisticsVO> voList = new ArrayList<>();

        List<LoginDateInfo> infoList = userService.findUserLoginInfoByRenterIdAndTimeSlot(rentId, startTime, endTime);
        if(infoList==null || infoList.size()==0) {
            List<String> timeList = CommonUtils.getRecentDayList(startTime, endTime);
            for(String timeVO:timeList) {
                if(DateTools.parseDate(timeVO).before(startTime)){
                    continue;
                }
                voList.add(new LoginStatisticsVO(timeVO));
            }
            return voList;
        }
        for(LoginDateInfo info:infoList){
            Long loginCount = new Long(info.getLoginCount());
            String loginTime = info.getLoginDate();
            Long loginDeptCount = new Long(info.getLoginDeptCount());
            voList.add(new LoginStatisticsVO(loginCount, loginDeptCount, loginTime));
        }
        return voList;
    }

}

package com.idatrix.resource.portal.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.portal.service.IPortalSystemConfigService;
import com.idatrix.resource.portal.service.IStatisticsService;
import com.idatrix.resource.portal.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 *  资源门口资源统计情况
 */

@Slf4j
@Controller
@RequestMapping("/portal/statistics")
@Api(value = "portal-query" , tags="资源门户系统-资源统计接口")
public class StatisticsController extends BaseController {


    @Autowired
    private IStatisticsService statisticsService;

    @Autowired
    private IPortalSystemConfigService portalSystemConfigService;


    /**
     * 获取资源日访问统计数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @ApiOperation(value = "获取资源日访问统计数据", notes="获取资源日访问统计数据,没有传递参数则获取30天的数据统计", httpMethod = "GET",consumes="application/x-www-form-urlencoded")
    @ApiImplicitParams({
            @ApiImplicitParam(name="startTime", value="查询开始时间，传值格式为yyyy-MM-dd", required=false, dataType="String", paramType="query"),
            @ApiImplicitParam(name="endTime", value="查询结束时间，传值格式为yyyy-MM-dd", required=false, dataType = "String", paramType="query"),
    })
    @RequestMapping("/getVisitStatistics")
    @ResponseBody
    public Result<List<VisitStatisticsVO>> getVisitStatistics(@RequestParam(value="startTime", required =false)String startTime,
                                     @RequestParam(value="endTime", required=false)String endTime){
        Long rentId = portalSystemConfigService.getPortalRentId();
        String user = portalSystemConfigService.getPortalUserName();

        Date start  = null;
        Date end = null;

        if(StringUtils.isEmpty(startTime)){
            start = DateTools.getDateBefore(new Date(), 30);
        }else{
            start = DateTools.parseDate(startTime);
        }
        if(StringUtils.isEmpty(endTime)){
            end = new Date();
        }else{
            end = DateTools.parseDate(endTime);
        }
        start = DateTools.getDateBefore(start, 1);
        end = DateTools.getDateAfter(end, 1);
        List<VisitStatisticsVO> crList = statisticsService.getVisitStatisticsByDay( rentId, start, end);
        log.info("资源日访问统计数据 {}", crList.toString());
        return Result.ok(crList);
    }

//    private List<VisitStatisticsVO> getContinuousList(int days, List<VisitStatisticsVO> originList){
//        List<String> monthList = CommonUtils.getRecentDayList(days);
//        for (String month : monthList) {
//
//            boolean ownFlag = false;
//            MonthStatisticsVO mvo = new MonthStatisticsVO();
//            mvo.setMonthName(CommonUtils.formatMonthStr(month));
//            if (statisticslist != null && statisticslist.size() > 0) {
//                for (MonthStatisticsPO model : statisticslist) {
//                    if (StringUtils.equals(model.getMonth(), month)) {
//                        ownFlag = true;
//                        mvo.setRegCount(model.getRegCount());
//                        mvo.setPubCount(model.getPubCount());
//                        mvo.setSubCount(model.getSubCount());
//                        break;
//                    }
//                }
//            }
//            if (!ownFlag) {
//                mvo.setRegCount(0);
//                mvo.setPubCount(0);
//                mvo.setSubCount(0);
//            }
//            targetList.add(mvo);
//    }

    /**
     * 获取资源共享类型统计
     * @return
     */
    @ApiOperation(value = "获取资源共享类型统计", notes="获取资源共享类型统计", httpMethod = "GET")
    @RequestMapping("/getShareTypeStatistics")
    @ResponseBody
    public Result<ShareTypeStatisticsVO> getShareTypeStatistics(){
        Long rentId = portalSystemConfigService.getPortalRentId();
        ShareTypeStatisticsVO vo = statisticsService.getShareTypeStatistics(rentId);
        log.info("获取资源共享类型统计 {}", vo.toString());
        return Result.ok(vo);
    }

    /**
     *  获取资源目录录入情况统计
     * @return
     */
    @ApiOperation(value = "获取资源目录录入情况统计", notes="获取资源目录录入情况统计", httpMethod = "GET")
    @RequestMapping("/getTypeInStatistics")
    @ResponseBody
    public Result<TypeInStatisticsVO> getTypeInStatistics(){
        Long rentId = portalSystemConfigService.getPortalRentId();
        TypeInStatisticsVO vo = statisticsService.getTypeInStatistics(rentId);
        log.info("获取资源目录录入情况统计 {}", vo.toString());
        return Result.ok(vo);
    }

    /**
     *  获取资源使用情况统计
     * @return
     */
    @ApiOperation(value = "获取记录资源统计情况概括", notes="获取记录资源统计情况概括", httpMethod = "GET")
    @RequestMapping("/getResourceUserStatistics")
    @ResponseBody
    public Result<ResourceUseStatisticsVO> getResourceUserStatistics(){
        Long rentId = portalSystemConfigService.getPortalRentId();
        ResourceUseStatisticsVO vo = statisticsService.getResourceUseStatistics(rentId);
        log.info("获取记录资源统计情况概括 {}", vo.toString());
        return Result.ok(vo);
    }


    /**
     * 部门资源提供数目统计
     * @return
     */
    @ApiOperation(value = "获取部门资源提供情况统计", notes="获取部门资源提供情况统计，返回contentType为all", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="count", value="需要展示个数", required=false, dataType="Long", paramType="query"),
    })
    @RequestMapping("/getDeptSupplyStatistics")
    @ResponseBody
    public Result<List<DeptResourceStatisticsVO>> getDeptSupplyStatistics(@RequestParam(value="count",required = false)Long count){
        Long rentId = portalSystemConfigService.getPortalRentId();
        List<DeptResourceStatisticsVO> deptList = statisticsService.getDeptSupplyStatistics(rentId, count);
        log.info("获取部门资源提供情况统计 {}", deptList.toString());
        return Result.ok(deptList);
    }


    /**
     * 获取部门资源使用情况统计
     * @return
     */
    @ApiOperation(value = "获取部门资源使用情况统计", notes="获取部门资源使用情况统计，一次请求返回不同类型资源使用统计," +
            "包含：数据库/文件/接口content分布使用db/file/interface", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="count", value="需要展示个数", required=false, dataType="Long", paramType="query"),
    })
    @RequestMapping("/getDeptUseStatistics")
    @ResponseBody
    public Result<List<DeptResourceStatisticsVO>> getDeptUseStatistics(@RequestParam(value="count",required = false)Long count){
        Long rentId = portalSystemConfigService.getPortalRentId();
        List<DeptResourceStatisticsVO> deptList = statisticsService.getDeptUseStatistics(rentId, count);
        log.info("获取部门资源使用情况统计 {}", deptList.toString());
        return Result.ok(deptList);
    }

    /**
     * 获取平台运行情况
     * @return
     */
    @ApiOperation(value = "获取平台日常运行情况", notes="获取平台每天运行情况", httpMethod = "GET")
    @RequestMapping("/getPlatformRunningStatistics")
    @ResponseBody
    public Result<PlatformRunningVO> getPlatformRunningStatistics(){
        Long rentId = portalSystemConfigService.getPortalRentId();
        return Result.ok(statisticsService.getPlatformRunningStatistics(rentId));
    }

    /**
     * 获取所属部门登陆统计
     * @return
     */
    @ApiOperation(value = "获取所属部门登陆统计", notes="获取所属部门登陆统计，contentType中内容为 login", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="count", value="需要展示个数", required=false, dataType="Long", paramType="query"),
    })
    @RequestMapping("/getDeptLoginStatistics")
    @ResponseBody
    public Result<List<DeptResourceStatisticsVO>> getDeptLoginStatistics(@RequestParam(value="count",required = false)Long count){
        Long rentId = portalSystemConfigService.getPortalRentId();
        List<DeptResourceStatisticsVO> deptList = statisticsService.getDeptLoginStatistics(rentId, count);
        log.info("获取每天登陆次数和登陆单位个数统计 {}", deptList.toString());
        return Result.ok(deptList);
    }

    /**
     * 获取每天登陆次数和登陆单位个数统计
     * @return
     */
    @ApiOperation(value = "获取每天登陆次数和登陆单位个数统计", notes="获取每天登陆次数和登陆单位个数统计", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="startTime", value="查询开始时间，传值格式为yyyy-MM-dd", required=false, dataType="String", paramType="query"),
            @ApiImplicitParam(name="endTime", value="查询结束时间，传值格式为yyyy-MM-dd", required=false, dataType = "String", paramType="query"),
    })
    @RequestMapping("/getDailyLoginStatistics")
    @ResponseBody
    public Result<List<LoginStatisticsVO>> getDailyLoginStatistics(@RequestParam(value="startTime", required =false)String startTime,
                                                                   @RequestParam(value="endTime", required=false)String endTime){
        Long rentId = portalSystemConfigService.getPortalRentId();

        Date start  = null;
        Date end = null;

        if(StringUtils.isEmpty(startTime)){
            start = DateTools.getDateBefore(new Date(), 30);
        }else{
            start = DateTools.parseDate(startTime);
        }
        if(StringUtils.isEmpty(endTime)){
            end = new Date();
        }else{
            end = DateTools.parseDate(endTime);
        }
        start = DateTools.getDateBefore(start, 1);
        end = DateTools.getDateAfter(end, 1);
        List<LoginStatisticsVO> voList = statisticsService.getDailyLoginStatistics(rentId, start, end);
        log.info("获取每天登陆次数和登陆单位个数统计 {}", voList.toString());
        return Result.ok(voList);
    }

}

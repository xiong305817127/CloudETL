package com.idatrix.resource.terminalmanage.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.*;
import com.idatrix.resource.terminalmanage.service.ITerminalManageService;
import com.idatrix.resource.terminalmanage.vo.TerminalManageVO;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.sso.client.UserHolder;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 前置管理
 */

@Controller
@RequestMapping("/terminalManage")
public class TerminalManageController extends BaseController {

    @Autowired
    private ITerminalManageService iTerminalManageService;

    @Autowired
    private UserService userService;


    private static final Logger LOG = LoggerFactory.getLogger(TerminalManageController.class);

    /*新增或修改前置机信息*/
    @RequestMapping(value="/saveOrUpdateTerminalManage", method= RequestMethod.POST)
    @ResponseBody
    public Result saveOrUpdateTerminalManage(@RequestBody TerminalManageVO terminalManageVO) {
        //前置机展示或者存储的时候按照租户名来
        String userName = getRentUserName(); //getUserName(); //"admin";

        try {
            iTerminalManageService.saveOrUpdateTerminalManageRecord(terminalManageVO, userName);
        } catch (Exception e) {
            LOG.error(DateTools.getDateTime() + ", 保存前置机信息失败: "+e.getMessage());
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(true);
    }

    /*根据ID获取前置机信息*/
    @RequestMapping(value="/getTerminalManageRecordById", method= RequestMethod.GET)
    @ResponseBody
    public Result getTerminalManageRecordById(@RequestParam("id") Long id) {
        try {
            TerminalManageVO terminalManageVO = iTerminalManageService.getTerminalManageRecordById(id);
            return Result.ok(terminalManageVO);
        } catch (RuntimeException e) {
            LOG.error(DateTools.getDateTime() + ", 获取前置机信息失败");
            LOG.error(e.getMessage());
            return Result.error(CommonConstants.EC_INCORRECT_VALUE, "获取前置机信息失败");
        }
    }

    /*根据ID删除前置机信息*/
    @RequestMapping(value="/deleteTerminalManageRecordById", method= RequestMethod.GET)
    @ResponseBody
    public Result deleteTerminalManageRecordById(@RequestParam("id") Long id) {
        try {
            iTerminalManageService.deleteTerminalManageRecordById(id);
            return Result.ok("删除前置机信息成功");
        } catch (RuntimeException e) {
            LOG.error(DateTools.getDateTime() + ", 删除前置机信息失败");
            LOG.error(e.getMessage());
            return Result.error(CommonConstants.EC_INCORRECT_VALUE, "删除前置机信息失败："+e.getMessage());
        }
    }

    /*根据部门ID判断是否已存在前置机关联信息*/
    @RequestMapping(value="/isExistedTerminalManageRecord", method= RequestMethod.GET)
    @ResponseBody
    public Result isExistedTerminalManageRecord(@RequestParam("id") Long id,
                                                @RequestParam("deptId") String deptId) {
        try {
            String result = iTerminalManageService.isExistedTerminalManageRecord(id, deptId);

            if (!CommonUtils.isEmptyStr(result))
                return Result.error(6001000, "已存在跟" + result + "关联前置机,请修改原有配置或者重新配置部门");
            else
                return Result.ok("");
        } catch (RuntimeException e) {
            LOG.error(DateTools.getDateTime() + ", 校验前置机信息出现异常");
            LOG.error(e.getMessage());
            return Result.error(CommonConstants.EC_INCORRECT_VALUE, "校验前置机信息出现异常");
        }
    }

    /*根据查询条件前置关联信息信息*/

    /**
     *
     * @param dbName	数据库名称
     * @param deptName	部门名称
     * @param pageNum	当前页数
     * @param pageSize
     * @return
     */
    @RequestMapping(value="/getTerminalManageRecordsByCondition", method= RequestMethod.GET)
    @ResponseBody
    public Result getTerminalManageRecordsByCondition(@RequestParam(value = "dbName", required = false) String  dbName,
                                                 @RequestParam(value = "deptName", required = false) String  deptName,
                                                 @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            Map<String, String> queryCondition = new HashMap<String, String>();

            //用户只能查看当前租户下面配置的前置机
            String rentUserName = getRentUserName();
            queryCondition.put("userName", rentUserName);

            if(StringUtils.isNotEmpty(dbName)
                    && !CommonUtils.isOverLimitedLength(dbName, 100)){
                queryCondition.put("dbName", dbName);
            }
            if(StringUtils.isNotEmpty(deptName)
                    && !CommonUtils.isOverLimitedLength(deptName, 100)){
                queryCondition.put("deptName", deptName);
            }

            ResultPager tasks = iTerminalManageService.getTerminalManageRecordsByCondition(queryCondition, pageNum, pageSize);
            return Result.ok(tasks);
        } catch (RuntimeException e) {
            LOG.error(DateTools.getDateTime() + ", 获取前置机列表信息失败");
            LOG.error(e.getMessage());
            return Result.error(CommonConstants.EC_INCORRECT_VALUE, "获取前置机列表信息失败");
        }
    }

    private String getRentUserName(){
        SSOUser ssoUser = UserHolder.getUser();
        Object obj = ssoUser.getProperty("renterId");
        Long rentId = Long.valueOf((String) obj);

        String userName =(String)ssoUser.getProperty("username");
        User rentUser = userService.findRenterByRenterId(rentId);
        return rentUser.getUsername();
    }
}

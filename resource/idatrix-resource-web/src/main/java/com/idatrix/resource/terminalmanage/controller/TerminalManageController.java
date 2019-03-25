package com.idatrix.resource.terminalmanage.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.*;
import com.idatrix.resource.terminalmanage.service.ITerminalManageService;
import com.idatrix.resource.terminalmanage.vo.TerminalManageVO;
import com.idatrix.unisecurity.api.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "/terminalManage" , tags="前置管理-前置机配置接口")
public class TerminalManageController extends BaseController {

    @Autowired
    private ITerminalManageService iTerminalManageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    private static final Logger LOG = LoggerFactory.getLogger(TerminalManageController.class);

    /*新增或修改前置机信息*/
    @ApiOperation(value = "增加或修改前置机配置", notes="增加或修改前置机配置", httpMethod = "POST")
    @RequestMapping(value="/saveOrUpdateTerminalManage", method= RequestMethod.POST)
    @ResponseBody
    public Result saveOrUpdateTerminalManage(@RequestBody TerminalManageVO terminalManageVO) {
        //前置机展示或者存储的时候按照租户名来
        String userName = getUserName(); //"admin";
        Long rentId = userUtils.getCurrentUserRentId();
        try {
            iTerminalManageService.saveOrUpdateTerminalManageRecord(rentId, terminalManageVO, userName);
        } catch (Exception e) {
            LOG.error(DateTools.getDateTime() + ", 保存前置机信息失败: "+e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /*根据ID获取前置机信息*/
    @ApiOperation(value = "获取前置机配置详情", notes="获取前置机配置详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="前置机ID", required=true, dataType="Long")
    })
    @RequestMapping(value="/getTerminalManageRecordById", method= RequestMethod.GET)
    @ResponseBody
    public Result getTerminalManageRecordById(@RequestParam("id") Long id) {
        try {
            TerminalManageVO terminalManageVO = iTerminalManageService.getTerminalManageRecordById(id);
            return Result.ok(terminalManageVO);
        } catch (RuntimeException e) {
            LOG.error(DateTools.getDateTime() + ", 获取前置机信息失败:" + e.getMessage());
            return Result.error("获取前置机信息失败 "+e.getMessage());
        }
    }

    /*根据ID删除前置机信息*/
    @ApiOperation(value = "删除前置机信息", notes="删除前置机信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="前置机ID", required=true, dataType="Long")
    })
    @RequestMapping(value="/deleteTerminalManageRecordById", method= RequestMethod.GET)
    @ResponseBody
    public Result deleteTerminalManageRecordById(@RequestParam("id") Long id) {
        try {
            iTerminalManageService.deleteTerminalManageRecordById(id);
            return Result.ok("删除前置机信息成功");
        } catch (RuntimeException e) {
            LOG.error(DateTools.getDateTime() + ", 删除前置机信息失败");
            LOG.error(e.getMessage());
            return Result.error("删除前置机信息失败："+e.getMessage());
        }
    }

    /*根据部门ID判断是否已存在前置机关联信息*/
    @ApiOperation(value = "前置机存在查询", notes="根据部门ID判断是否已存在前置机关联信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="前置机ID", required=true, dataType="Long"),
            @ApiImplicitParam(name="deptId", value="部门ID", required=true, dataType="String")
    })
    @RequestMapping(value="/isExistedTerminalManageRecord", method= RequestMethod.GET)
    @ResponseBody
    public Result isExistedTerminalManageRecord(@RequestParam("id") Long id,
                                                @RequestParam("deptId") String deptId) {
        try {
            String result = iTerminalManageService.isExistedTerminalManageRecord(id, deptId);

            if (!CommonUtils.isEmptyStr(result))
                return Result.error("已存在跟" + result + "关联前置机,请修改原有配置或者重新配置部门");
            else
                return Result.ok("");
        } catch (RuntimeException e) {
            LOG.error(DateTools.getDateTime() + ", 校验前置机信息出现异常");
            LOG.error(e.getMessage());
            return Result.error("校验前置机信息出现异常");
        }
    }

   /**
     *
     * @param dbName	数据库名称
     * @param deptName	部门名称
     * @param pageNum	当前页数
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "前置机信息查询", notes="前置机信息查询", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dbName", value="数据库名称", required=false, dataType="String"),
            @ApiImplicitParam(name="deptName", value="部门名称", required=false, dataType="String"),
            @ApiImplicitParam(name="pageNum", value="分页页面起始页", required=false, dataType="Long"),
            @ApiImplicitParam(name="pageSize", value="分页页面大小", required=false, dataType="Long")
    })
    @RequestMapping(value="/getTerminalManageRecordsByCondition", method= RequestMethod.GET)
    @ResponseBody
    public Result getTerminalManageRecordsByCondition(@RequestParam(value = "dbName", required = false) String  dbName,
                                                 @RequestParam(value = "deptName", required = false) String  deptName,
                                                 @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            Map<String, String> queryCondition = new HashMap<String, String>();

            //用户只能查看当前租户下面配置的前置机
            String rentUserName = getUserName();
            queryCondition.put("rentId", userUtils.getCurrentUserRentId().toString());

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
            return Result.error("获取前置机列表信息失败 "+e.getMessage());
        }
    }
}

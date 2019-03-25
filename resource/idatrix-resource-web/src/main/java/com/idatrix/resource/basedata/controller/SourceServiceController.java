package com.idatrix.resource.basedata.controller;

import com.idatrix.resource.basedata.service.ISourceServiceService;
import com.idatrix.resource.basedata.vo.SourceServiceVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ;lz
 * 源服务增删改查
 */

@Controller
@RequestMapping("/srcService")
@Api(value = "/srcService" , tags="服务管理-源服务处理接口")
public class SourceServiceController extends BaseController {

	@Autowired
	private ISourceServiceService iSourceServiceService;

	@Autowired
    private UserUtils userUtils;

	private static final Logger LOG = LoggerFactory.getLogger(SourceServiceController.class);

	/*新增源服务*/
    @ApiOperation(value = "新增源服务", notes="新增源服务", httpMethod = "POST")
	@RequestMapping(value="/saveOrUpdate", method= RequestMethod.POST)
	@ResponseBody
	public Result saveOrUpdateSourceService(@RequestBody SourceServiceVO sourceServiceVO) {
		try {
			String user = getUserName(); //"admin";
            Long rentId = userUtils.getCurrentUserRentId();
			String errMsg = iSourceServiceService.saveOrUpdateSourceService(rentId, user, sourceServiceVO);

			if (errMsg.equals("")) {
                return Result.ok("保存或更新操作成功");
            } else {
                return Result.error(errMsg);
            }
		} catch (Exception e) {
		    e.printStackTrace();
			return Result.error("原服务保存失败" + e.getMessage());
		}
	}

	/*根据ID查询源服务信息*/
    @ApiOperation(value = "获取源服务详情", notes="获取源服务详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "源服务ID", required = true, dataType="Long"),
    })
	@RequestMapping("/getSourceServiceById")
	@ResponseBody
	public Result getSourceServiceById(@RequestParam(value = "id", required = true) Long id) {
		SourceServiceVO sourceServiceVO;
		try {
			sourceServiceVO =  iSourceServiceService.getSourceServiceById(id);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(e.getMessage());
		}
        return Result.ok(sourceServiceVO);
	}

	/*根据一个或多个ID删除删除源服务信息*/
    @ApiOperation(value = "删除源服务", notes="根据一个或多个ID删除删除源服务信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "源服务ID", required = true, dataType="String"),
    })
	@RequestMapping("/deleteSourceServiceById")
	@ResponseBody
	public Result deleteSourceServiceById(@RequestParam(value = "id") String id) {

		try {
			if (!CommonUtils.isEmptyStr(id)) {
				String[] ids = id.split(",");

				List<Long> idList = new ArrayList<Long>();
				for (int i = 0; i < ids.length; i++) {
					Long idValue = Long.valueOf(ids[i]);
					idList.add(idValue);
				}
				iSourceServiceService.deleteSourceServiceByIds(idList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(e.getMessage());
		}
		return Result.ok("删除成功");
	}

	/*查询所有源服务信息*/
    @ApiOperation(value = "查询所有源服务信息", notes="查询所有源服务信息", httpMethod = "GET")
	@RequestMapping("/getAllSourceService")
	@ResponseBody
	public Result<List<SourceServiceVO>> getAllSourceService() {
	    Long rentId = userUtils.getCurrentUserRentId();
		List<SourceServiceVO> servicesList = iSourceServiceService.getAllSourceService(rentId);
		return Result.ok(servicesList);
	}

	/*查询所有源服务信息*/
    @ApiOperation(value = "查询源服务", notes="根据服务名称、服务编码、服务类型、服务提供方等信息查询源服务", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceName", value = "源服务ID", required = false, dataType="String"),
            @ApiImplicitParam(name = "serviceCode", value = "源服务ID", required = false, dataType="String"),
            @ApiImplicitParam(name = "serviceType", value = "源服务ID", required = false, dataType="String"),
            @ApiImplicitParam(name = "providerName", value = "源服务ID", required = false, dataType="String"),
            @ApiImplicitParam(name = "page", value = "分页起始页", required = false, dataType="Long"),
            @ApiImplicitParam(name = "pageSize", value = "分页页面大小", required = false, dataType="Long"),

    })
	@RequestMapping("/getAllSourceServicePages")
	@ResponseBody
	public Result getSourceServicesByCondition(
			@RequestParam(value = "serviceName", required = false) String  serviceName,
	   		@RequestParam(value = "serviceCode", required = false) String  serviceCode,
	   		@RequestParam(value = "serviceType", required = false) String  serviceType,
	   		@RequestParam(value = "providerName", required = false) String providerName,
	   		@RequestParam(value = "page", required = false) Integer pageNum,
	   		@RequestParam(value = "pageSize", required = false) Integer pageSize) {

		Map<String, String> queryCondition = new HashMap<String, String>();
        queryCondition.put("rentId", userUtils.getCurrentUserRentId().toString());
		if(StringUtils.isNotEmpty(serviceName)
				&& !CommonUtils.isOverLimitedLength(serviceName, 255)){
			queryCondition.put("serviceName", serviceName);
		}
		if(StringUtils.isNotEmpty(serviceCode)
				&& !CommonUtils.isOverLimitedLength(serviceCode, 100)){
			queryCondition.put("serviceCode", serviceCode);
		}
		if(StringUtils.isNotEmpty(serviceType)
				&& (serviceType.equals(CommonConstants.SERVICE_TYPE_SOAP)
				|| serviceType.equals(CommonConstants.SERVICE_TYPE_RESTFUL))){
			queryCondition.put("serviceType", serviceType);
		}
		if(StringUtils.isNotEmpty(providerName)
				&& !CommonUtils.isOverLimitedLength(providerName, 100)){
			queryCondition.put("providerName", providerName);
		}

		try {
			ResultPager	tasks
					= iSourceServiceService.getSourceServicesByCondition(queryCondition, pageNum, pageSize);

			return Result.ok(tasks);
		}catch(Exception e){
			e.printStackTrace();
			return Result.error(e.getMessage()); //调试Ajax屏蔽掉
		}
	}
}

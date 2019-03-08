package com.idatrix.resource.basedata.controller;

import com.idatrix.resource.basedata.service.IServiceService;
import com.idatrix.resource.basedata.vo.ServiceVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.CommonConstants;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
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
 * 资源服务增删改查
 */

@Controller
@RequestMapping("/service")
public class ServiceController extends BaseController {

	@Autowired
	private IServiceService iServiceService;

	private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);

	/*根据ID查询源服务信息*/
	@RequestMapping("/getAllServices")
	@ResponseBody
	public Result getAllService() {
		List<ServiceVO> servicesList = iServiceService.getAllService();
        return Result.ok(servicesList);

//		if (servicesList != null)
//			return Result.ok(servicesList);
//		else
//			return Result.error(CommonConstants.EC_NOT_EXISTED_VALUE, "资源服务不存在");
	}

	/*新增资源服务*/
	@RequestMapping(value="/saveOrUpdate", method= RequestMethod.POST)
	@ResponseBody
	public Result saveOrUpdateService(@RequestBody ServiceVO serviceVO) {
		String user = getUserName(); //"admin";
		String errMsg = iServiceService.saveOrUpdateService(user, serviceVO);

		if (errMsg.equals("")) {
			return Result.ok("保存或更新操作成功");
		} else {
			return Result.error(CommonConstants.FAILURE_VALUE, errMsg);
		}
	}

	/*根据ID查询资源服务信息*/
	@RequestMapping("/getServiceById")
	@ResponseBody
	public Result getSourceServiceById(@RequestParam(value = "id", required = true) Long id) {
		ServiceVO serviceVO;
		try {
			serviceVO =  iServiceService.getServiceById(id);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(6001000, e.getMessage());
		}
		return Result.ok(serviceVO);

//		if (serviceVO != null)
//			return Result.ok(serviceVO);
//		else
//			return Result.error(CommonConstants.EC_NOT_EXISTED_VALUE, "当前资源服务资源不存在");
	}

	/*根据一个或多个ID删除资源服务信息*/
	@RequestMapping("/deleteServiceById")
	@ResponseBody
	public Result deleteServiceById(@RequestParam(value = "id") String id) {

		try {
			if (!CommonUtils.isEmptyStr(id)) {
				String[] ids = id.split(",");

				List<Long> idList = new ArrayList<Long>();

				for (int i = 0; i < ids.length; i++) {
					Long idValue = Long.valueOf(ids[i]);
					idList.add(idValue);
				}

				String result = iServiceService.deleteServiceByIds(idList);

				if (result != null)
					return Result.error(CommonConstants.FAILURE_VALUE,
							"共享服务: " + result + " 已经与已发布的资源绑定，无法删除");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(CommonConstants.EC_UNEXPECTED, "删除失败" + e.getMessage());
		}

		return Result.ok("删除成功");
	}

	/*查询所有资源服务信息*/
	@RequestMapping("/getAllServicePages")
	@ResponseBody
	public Result getServicesByCondition(
			@RequestParam(value = "serviceName", required = false) String  serviceName,
			@RequestParam(value = "serviceCode", required = false) String  serviceCode,
			@RequestParam(value = "serviceType", required = false) String  serviceType,
			@RequestParam(value = "providerName", required = false) String providerName,
			@RequestParam(value = "page", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {

		Map<String, String> queryCondition = new HashMap<String, String>();

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
			ResultPager tasks
					= iServiceService.getServicesByCondition(queryCondition, pageNum, pageSize);

			return Result.ok(tasks);
		}catch(Exception e){
			e.printStackTrace();
			return Result.error(6001000, e.getMessage()); //调试Ajax屏蔽掉
		}
	}

	/*根据ID查询资源服务信息*/
	@RequestMapping("/getWSDLContents")
	@ResponseBody
	public Result getWSDLContentsByRemoteAddress(@RequestParam(value = "url") String url) {

		try {
			String content = CommonUtils.getWSDLContentsByRemoteAddress(url);
			return Result.ok(content);

		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(6001000, e.getMessage());
		}
	}
}

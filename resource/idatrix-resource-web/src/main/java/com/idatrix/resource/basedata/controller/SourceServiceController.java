package com.idatrix.resource.basedata.controller;

import com.idatrix.resource.basedata.service.ISourceServiceService;
import com.idatrix.resource.basedata.vo.SourceServiceVO;
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
 * ;lz
 * 源服务增删改查
 */

@Controller
@RequestMapping("/srcService")
public class SourceServiceController extends BaseController {

	@Autowired
	private ISourceServiceService iSourceServiceService;

	private static final Logger LOG = LoggerFactory.getLogger(SourceServiceController.class);

	/*新增源服务*/
	@RequestMapping(value="/saveOrUpdate", method= RequestMethod.POST)
	@ResponseBody
	public Result saveOrUpdateSourceService(@RequestBody SourceServiceVO sourceServiceVO) {
		try {
			String user = getUserName(); //"admin";
			String errMsg = iSourceServiceService.saveOrUpdateSourceService(user, sourceServiceVO);

			if (errMsg.equals("")) {
                return Result.ok("保存或更新操作成功");
            } else {
                return Result.error(CommonConstants.FAILURE_VALUE, errMsg);
            }
		} catch (Exception e) {
			return Result.error(CommonConstants.FAILURE_VALUE, "原服务保存失败" + e.getMessage());
		}
	}

	/*根据ID查询源服务信息*/
	@RequestMapping("/getSourceServiceById")
	@ResponseBody
	public Result getSourceServiceById(@RequestParam(value = "id", required = true) Long id) {
		SourceServiceVO sourceServiceVO;
		try {
			sourceServiceVO =  iSourceServiceService.getSourceServiceById(id);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(6001000, e.getMessage());
		}
        return Result.ok(sourceServiceVO);
//		if (sourceServiceVO != null)
//			return Result.ok(sourceServiceVO);
//		else
//			return Result.error(CommonConstants.EC_NOT_EXISTED_VALUE, "当前源服务资源不存在");
	}

	/*根据一个或多个ID删除删除源服务信息*/
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
			return Result.error(6001000, e.getMessage());
		}
		return Result.ok("删除成功");
	}

	/*查询所有源服务信息*/
	@RequestMapping("/getAllSourceService")
	@ResponseBody
	public Result getAllSourceService() {
		List<SourceServiceVO> servicesList = iSourceServiceService.getAllSourceService();
		return Result.ok(servicesList);

//		if (servicesList != null)
//			return Result.ok(servicesList);
//		else
//			return Result.error(CommonConstants.EC_NOT_EXISTED_VALUE, "资源服务不存在");
	}

	/*查询所有源服务信息*/
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
			return Result.error(6001000, e.getMessage()); //调试Ajax屏蔽掉
		}
	}
}

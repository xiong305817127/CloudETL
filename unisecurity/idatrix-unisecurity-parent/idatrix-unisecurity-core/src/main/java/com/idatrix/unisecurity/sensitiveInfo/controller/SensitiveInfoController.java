package com.idatrix.unisecurity.sensitiveInfo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.idatrix.unisecurity.common.domain.SensitiveInfo;
import com.idatrix.unisecurity.common.utils.HttpCodeUtils;
import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.common.utils.SensitiveInfoUtils;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.sensitiveInfo.service.SensitiveInfoService;

import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping("/sensitiveInfo")
public class SensitiveInfoController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	SensitiveInfoService sensitiveInfoService;

	/**
	 * 新增
	 * @param info
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Map<String, Object> addSensitiveInfo(SensitiveInfo info) {
		Map resultMap = new HashMap();
		try{
			logger.info("addSensitiveInfo :::: "+JSON.toJSONString(info));
			if(info.getBegin()>info.getEnd()){
				resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
				resultMap.put("message", "开始位置大于结束位置");
				return resultMap;
			}
			if (StringUtils.isBlank(info.getOriginalInfo())) {
				resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
				resultMap.put("message", "敏感词样例为空");
				return resultMap;
	        }
			String sentiveInfo = SensitiveInfoUtils.getSensitiveInfo(info.getOriginalInfo(), info.getBegin(),
					info.getEnd(), info.getSymbol());

			info.setSentiveInfo(sentiveInfo);
			resultMap =  sensitiveInfoService.addSentiveInfo(info);
		}catch(Exception e){
			resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
			resultMap.put("message", "新增脱敏规则失败");
			LoggerUtils.fmtError(getClass(), e, "新增脱敏规则错误。来源[%s]", info.toString());
		}
		return resultMap;
	}

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Map<String, Object> deleteSensitiveInfo(int id) {
		logger.info("deleteSensitiveInfo params :::: "+id);
		Map resultMap = new HashMap();
		try{
			resultMap =  sensitiveInfoService.deleteSentiveInfo(id);
		}catch(Exception e){
			resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
			resultMap.put("message", "新增脱敏规则失败");
			LoggerUtils.fmtError(getClass(), e, "新增脱敏规则错误。来源[%s]", id);
		}
		return resultMap;
	}


	/**
	 * 查询
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "index", method = RequestMethod.POST)
	public Pagination<SensitiveInfo> index(String findContent, ModelMap modelMap, Integer pageNo, Integer pageSize,
			HttpServletRequest request) {
		logger.info("index  params :::: "+JSON.toJSONString(findContent));
		Map resultMap = new HashMap();
		try{
			modelMap.put("findContent", findContent);
			Pagination<SensitiveInfo> renters = sensitiveInfoService.findPage(modelMap, pageNo, pageSize);
			return renters;
		}catch(Exception e){
			resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
			resultMap.put("message", "新增脱敏规则失败");
			LoggerUtils.fmtError(getClass(), e, "新增脱敏规则错误。来源[%s]", "");
		}
		return null;
	}

	/**
	 * 修改
	 * @param info
	 * @return
	 */
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public Map<String, Object> updateSensitiveInfo(SensitiveInfo info) {
		logger.info("updateSensitiveInfo  params :::: "+JSON.toJSONString(info));
		Map resultMap = new HashMap();
		try{
			if(info.getBegin()>info.getEnd()){
				resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
				resultMap.put("message", "开始位置大于结束位置");
				return resultMap;
			}

			if (StringUtils.isBlank(info.getOriginalInfo())) {
				resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
				resultMap.put("message", "敏感词样例为空");
				return resultMap;
	        }
			String sentiveInfo = SensitiveInfoUtils.getSensitiveInfo(info.getOriginalInfo(), info.getBegin(),
					info.getEnd(), info.getSymbol());
			info.setSentiveInfo(sentiveInfo);

			resultMap =  sensitiveInfoService.updateSentiveInfo(info);
		}catch(Exception e){
			resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
			resultMap.put("message", "新增脱敏规则失败");
			LoggerUtils.fmtError(getClass(), e, "新增脱敏规则错误。来源[%s]", info.toString());
		}
		return resultMap;
	}

}

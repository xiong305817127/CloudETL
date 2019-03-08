package com.idatrix.unisecurity.sensitiveInfo.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.idatrix.unisecurity.common.dao.SensitiveInfoMapper;
import com.idatrix.unisecurity.common.domain.SensitiveInfo;
import com.idatrix.unisecurity.core.mybatis.BaseMybatisDao;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.sensitiveInfo.service.SensitiveInfoService;

/**
 * Created by james on 2017/5/26.
 */
@Service
public class SensitiveInfoServiceImpl extends BaseMybatisDao<SensitiveInfoMapper> implements SensitiveInfoService {

	@Autowired
	SensitiveInfoMapper sensitiveInfoMapper;
	
	@Override
	public Map addSentiveInfo(SensitiveInfo info) {
		Map resultMap = new HashMap<>();
		try{
			sensitiveInfoMapper.insert(info);
			resultMap.put("status", 200);
			resultMap.put("message", "新增脱敏规则成功");
		}catch(Exception e){
			resultMap.put("status", 700);
			resultMap.put("message", "新增脱敏规则失败");
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Map deleteSentiveInfo(int id) {
		Map resultMap = new HashMap<>();
		try{
			sensitiveInfoMapper.deleteSentiveInfoById(id);
			resultMap.put("status", 200);
			resultMap.put("message", "删除脱敏规则成功");
		}catch(Exception e){
			resultMap.put("status", 700);
			resultMap.put("message", "删除脱敏规则失败");
		}
		return resultMap;
	}


	@Override
	public Pagination<SensitiveInfo> findPage(ModelMap modelMap, Integer pageNo, Integer pageSize) {
		return super.findPage(modelMap, pageNo, pageSize);
	}

	@Override
	public Map updateSentiveInfo(SensitiveInfo info) {
		Map resultMap = new HashMap<>();
		try{
			sensitiveInfoMapper.update(info);
			resultMap.put("status", 200);
			resultMap.put("message", "修改脱敏规则成功");
		}catch(Exception e){
			resultMap.put("status", 700);
			resultMap.put("message", "修改脱敏规则失败");
		}
		return resultMap;
	}
}

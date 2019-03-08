package com.idatrix.unisecurity.sensitiveInfo.service;

import com.idatrix.unisecurity.common.domain.SensitiveInfo;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import org.springframework.ui.ModelMap;

import java.util.Map;

/**
 * @author huangyi
 */
public interface SensitiveInfoService{

	Map addSentiveInfo(SensitiveInfo info);

	Map deleteSentiveInfo(int id);

	Pagination<SensitiveInfo> findPage(ModelMap modelMap, Integer pageNo, Integer pageSize);

	Map updateSentiveInfo(SensitiveInfo info);

}

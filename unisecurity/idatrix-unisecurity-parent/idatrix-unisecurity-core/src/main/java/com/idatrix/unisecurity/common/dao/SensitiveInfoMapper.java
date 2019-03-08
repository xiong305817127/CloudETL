package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.SensitiveInfo;

/**
 * 
 * @author Administrator
 *
 */
public interface SensitiveInfoMapper {
	
	void insert(SensitiveInfo info);

	void deleteSentiveInfoById(int id);

	void update(SensitiveInfo info);
	
}

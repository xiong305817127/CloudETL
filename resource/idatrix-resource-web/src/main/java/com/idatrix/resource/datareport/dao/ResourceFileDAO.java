package com.idatrix.resource.datareport.dao;

import com.idatrix.resource.datareport.po.ResourceFilePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ResourceFileDAO {

	void insertResourceFile(ResourceFilePO resourceFilePO);

	ResourceFilePO isExistedResourceFile(Map<String, Object> condition);

	ResourceFilePO 	getResourceFileById(Long id);

	void updateResourceFile(ResourceFilePO resourceFilePO);

	List<ResourceFilePO> queryResourceFile(Map<String, String> condition);
}

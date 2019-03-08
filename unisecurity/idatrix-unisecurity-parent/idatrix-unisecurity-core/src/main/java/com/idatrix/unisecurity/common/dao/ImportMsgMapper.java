package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.ImportMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ImportMsgMapper {
	void insert(@Param("batchId")String batchId, @Param("fileName")String fileName,
			@Param("userName")String username, @Param("msg")String msg);

	List<ImportMsg> findByBatchId(@Param("batchId")String batchId);

	void insert(ImportMsg importMsg);
}
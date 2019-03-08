package com.ys.idatrix.cloudetl.metacube.api.service;

import com.ys.idatrix.cloudetl.metacube.api.dto.DbSchemaDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.DbTableFieldsDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.DbTableFieldsListDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.DbTablesDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.MetaCubeDbDto;

public interface CloudDbInfoService {
	
	public DbSchemaDto getDbSchema(MetaCubeDbDto metaCubeDbDto) throws Exception;
	
	public DbSchemaDto getDbSchemas(MetaCubeDbDto metaCubeDbDto) throws Exception;

	public DbTablesDto getDbTables(MetaCubeDbDto metaCubeDbDto) throws Exception;

	public DbTableFieldsDto getTableFields(MetaCubeDbDto metaCubeDbDto) throws Exception;

	public DbTableFieldsListDto getBatchTableFields(MetaCubeDbDto metaCubeDbDto) throws Exception;
}

package com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto;

import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.service.impl.McDirectMiningServiceImpl.MiningTaskDto;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatabaseConnect {

	private String databaseType ;
	private Long schemaId ;
	
	private McSchemaPO schema ;
	private DatasourceVO datasource ;
	
}

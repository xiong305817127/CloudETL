package com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DependencyNotExistException extends RuntimeException{


	private static final long serialVersionUID = 1L;
	
	private String dependencyTableName;
	private Long dependencySchemaId;
	
	private String currentTableName ;
	private Long currentSchemaId ;
	
}

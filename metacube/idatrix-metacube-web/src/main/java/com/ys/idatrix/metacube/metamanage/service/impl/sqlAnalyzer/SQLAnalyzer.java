package com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.TablesDependency;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;

@Component
public class SQLAnalyzer {
	
	  @Autowired
	  List<BaseSQLAnalyzer> sqlAnalyzers;
	  
	  public BaseSQLAnalyzer getSqlAnalyzer(String type) {
		  return sqlAnalyzers.stream().filter(a -> { return a.getDbType().equalsIgnoreCase(type); }).findAny().orElse(null);
	  }
	  
	  public List<? extends TableVO>  getAllTables(String type,Long schemaId) {
		 BaseSQLAnalyzer analyzer = getSqlAnalyzer(type);
		 if(analyzer != null ) {
			return  analyzer.getTablesFromDB(schemaId);
		 }
		  return null ;
	  }
	  
	  public  List<? extends ViewVO>  getAllViews(String type,Long schemaId) {
			 BaseSQLAnalyzer analyzer = getSqlAnalyzer(type);
			 if(analyzer != null ) {
				return  analyzer.getViewsFromDB(schemaId);
			 }
			  return null ;
		  }
	  
	  public  List<TablesDependency> getTablesDependency(String type,Long schemaId) {
			 BaseSQLAnalyzer analyzer = getSqlAnalyzer(type);
			 if(analyzer != null ) {
				return  analyzer.getTablesDependency(schemaId);
			 }
			  return null ;
		  }
	  
	  
	
}

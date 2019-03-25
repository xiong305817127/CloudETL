package com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ys.idatrix.metacube.api.beans.MetadataDTO;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.DatabaseConnect;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.TablesDependency;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;

@Component
public class SQLAnalyzer {
	
	  @Autowired
	  List<BaseSQLAnalyzer> sqlAnalyzers;
	  
	  @Autowired
	  MetadataMapper metadataMapper;
	  
	  /**
	   * 根据类型获取 相应的分析器
	   * @param type
	   * @return
	   */
	  public BaseSQLAnalyzer getSqlAnalyzer(String type) {
		  return sqlAnalyzers.stream().filter(a -> { return a.getDbType().equalsIgnoreCase(type); }).findAny().orElse(null);
	  }
	  
	  /**
	   * 获取 schemaId 对应的数据库实体下的表列表
	   * @param type 数据库类型
	   * @param schemaId schemaId
	   * @param isMiningEnable 是否只返回可采集的表 ,  true:剔除已经采集过的表,  false:返回所有的表
	   * @return
	   */
	  public List<? extends TableVO>  getAllTables(DatabaseConnect dbInfo, boolean isMiningEnable) {
		 BaseSQLAnalyzer analyzer = getSqlAnalyzer(dbInfo.getDatabaseType());
		 if(analyzer != null ) {
			 
			 List<? extends TableVO> results =   analyzer.getTablesFromDB(dbInfo);
			 if( results != null && !results.isEmpty() && isMiningEnable ) {
				//过滤已经采集过的表
				 List<MetadataDTO>  existMetas = metadataMapper.findListBySchemaIdAndResourceType(dbInfo.getSchemaId(), 1);
				 if(existMetas!= null && !existMetas.isEmpty()) {
					 List<String> existNames = existMetas.stream().map(m -> m.getMetaName().toUpperCase() ).collect(Collectors.toList());
					return  results.stream().filter(m -> { return !existNames.contains(m.getName().toUpperCase()) ;}).collect(Collectors.toList()) ;
				 }
			  }
			 return results;
		 }
		  return null ;
	  }
	  
	  /**
	   * 获取 schemaId 对应的数据库实体下的视图列表
	   * @param type 数据库类型
	   * @param schemaId schemaId
	   * @param isMiningEnable 是否只返回可采集的视图 ,  true:剔除已经采集过的视图,  false:返回所有的视图
	   * @return
	   */
	  public  List<? extends ViewVO>  getAllViews(DatabaseConnect dbInfo, boolean isMiningEnable) {
			 BaseSQLAnalyzer analyzer = getSqlAnalyzer(dbInfo.getDatabaseType());
			 if(analyzer != null ) {
				List<? extends ViewVO> results = analyzer.getViewsFromDB(dbInfo);
				if( results != null && !results.isEmpty() && isMiningEnable ) {
					//过滤已经采集过的视图
					 List<MetadataDTO>  existMetas = metadataMapper.findListBySchemaIdAndResourceType(dbInfo.getSchemaId(), 2);
					 if(existMetas!= null && !existMetas.isEmpty()) {
						 List<String> existNames = existMetas.stream().map(m -> m.getMetaName().toUpperCase() ).collect(Collectors.toList());
						return  results.stream().filter(m -> { return !existNames.contains(m.getName().toUpperCase()) ;}).collect(Collectors.toList()) ;
					 }
				  }
				 return results;
			 }
			  return null ;
		  }
	  
	  public  List<TablesDependency> getTablesDependency(DatabaseConnect dbInfo ) {
			 BaseSQLAnalyzer analyzer = getSqlAnalyzer(dbInfo.getDatabaseType());
			 if(analyzer != null ) {
				return  analyzer.getTablesDependency(dbInfo);
			 }
			  return null ;
		  }
	  
}

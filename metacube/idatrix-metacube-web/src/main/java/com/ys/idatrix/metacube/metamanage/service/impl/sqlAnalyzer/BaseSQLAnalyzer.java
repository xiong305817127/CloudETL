package com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.sql.dto.SqlExecReqDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.api.sql.service.SqlExecService;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.TablesDependency;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

public abstract class BaseSQLAnalyzer {
	
	/**
	 * 分析器类型   JdbcConstants.MYSQL , JdbcConstants.ORACLE , ...
	 * @return
	 */
	public abstract String getDbType() ;
	
	/**
	 * 通过schemaId 获取 当前schema下所有的表的信息
	 * @param schemaId 
	 * @return
	 */
	public abstract List<? extends TableVO> getTablesFromDB(Long schemaId, String... tableFilter) ;
	
	/**
	 * 通过schemaId 获取 当前schema下所有的视图的信息
	 * @param schemaId 
	 * @return
	 */
	public abstract List<? extends ViewVO> getViewsFromDB(Long  schemaId, String... viewFilter) ;
	
	/**
	 * 通过schemaId 获取 当前schema下所有的表的依赖关系信息
	 * @param schemaId 
	 * @return
	 */
	public abstract List<TablesDependency> getTablesDependency(Long schemaId, String... tableFilter) ;
	
	/**
	 * 通过 已经生成的视图  ,解析输出的字段
	 * @param schemaId
	 * @param viewName
	 * @param viewSql
	 * @return
	 */
	public abstract List<TableColumn> getViewColumns(Long  schemaId, String viewName, String viewSql) ;
	
	/**
	 * sql分析对象
	 * @param sql
	 * @return
	 */
	public SQLStatement analyzerSql(String sql) {
		List<SQLStatement> statement = SQLUtils.parseStatements(sql, getDbType()) ;
		return  statement.get(0);
	}
	
	
	protected SqlExecReqDto getSqlCommand(DatasourceVO datasource,  McSchemaPO schema  , String sql  ) {
		SqlExecReqDto sc = new SqlExecReqDto();
		sc.setCommand(sql);
		sc.setNeedPermission(false);

		sc.setType(getDbType(Integer.valueOf(datasource.getType())).toUpperCase());
		sc.setIp(datasource.getIp());
		sc.setPort(datasource.getPort());
		sc.setUsername(schema.getUsername());
		sc.setPassword(schema.getPassword());
		sc.setSchemaName(StringUtils.isEmpty(schema.getServiceName())?schema.getName():schema.getServiceName());

		return sc ;
	}
	
	
	protected boolean execSqlCommand( SqlExecService sqlExecService , DatasourceVO datasource,  McSchemaPO schema  , String sql ,dealRowInterface dealRows ) throws MetaDataException {
		if( sqlExecService == null ) {
			return false;
		}
		RespResult<SqlQueryRespDto> sqlRes = sqlExecService.executeQuery(UserUtils.getUserName(), getSqlCommand(datasource, schema, sql));
		if( sqlRes.isSuccess()  ) {
			SqlQueryRespDto res = sqlRes.getData() ;
			if( res != null  && dealRows != null  ) {
				for(int i=0 ; i<  res.getData().size(); i++ ){
					 Map<String, Object> map  =  res.getData().get(i) ;
					 if( map == null || map.isEmpty() ) {
						 continue ;
					 }
					dealRows.dealRow(i , map , res.getColumns());
				}
			}
			return true ;
		}
		return false;
	}
	
	
	private String getDbType( Integer databaseType ) {
		switch( databaseType) {
		case 1: return JdbcConstants.MYSQL ;
		case 2: return JdbcConstants.ORACLE ;
		
		default : return JdbcConstants.MYSQL ;
		}
	}
	
	public String deleteQuoted(String fieldName) {
		return SQLUtils.normalize(fieldName);
	}
	
	public interface dealRowInterface{
		void dealRow(int index ,  Map<String, Object> map , List<String> columnNames) throws MetaDataException ;
	}
	
	
	@Data
	@AllArgsConstructor
	public class DependencyNotExistException extends MetaDataException{
		
		private static final long serialVersionUID = 1L;
		
		private String dependencyTableName;
		private Long dependencySchemaId;
		
		private String currentTableName ;
		private Long currentSchemaId ;
		
	}
}

package com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.StringUtils;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.sql.dto.SchemaDetailsDto;
import com.ys.idatrix.db.api.sql.dto.SchemaModeEnum;
import com.ys.idatrix.db.api.sql.dto.SqlExecReqDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.api.sql.service.SqlExecService;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.DatabaseConnect;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.TablesDependency;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;

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
	public abstract List<? extends TableVO> getTablesFromDB(DatabaseConnect dbInfo, String... tableFilter) ;
	
	/**
	 * 通过schemaId 获取 当前schema下所有的视图的信息
	 * @param schemaId 
	 * @return
	 */
	public abstract List<? extends ViewVO> getViewsFromDB(DatabaseConnect dbInfo, String... viewFilter) ;
	
	/**
	 * 通过schemaId 获取 当前schema下所有的表的依赖关系信息
	 * @param schemaId 
	 * @return
	 */
	public abstract List<TablesDependency> getTablesDependency(DatabaseConnect dbInfo, String... tableFilter) ;
	
	/**
	 * 通过 已经生成的视图  ,解析输出的字段
	 * @param schemaId
	 * @param viewName
	 * @param viewSql
	 * @return
	 */
	public abstract List<TableColumn> getViewColumns(DatabaseConnect dbInfo, String viewName, String viewSql) ;
	
	/**
	 * sql分析对象
	 * @param sql
	 * @return
	 */
	public SQLStatement analyzerSql(String sql) {
		List<SQLStatement> statement = SQLUtils.parseStatements(sql, getDbType()) ;
		return  statement.get(0);
	}
	
	
	protected SqlExecReqDto getSqlCommand(DatabaseConnect dbInfo, String sql  ) {
		SqlExecReqDto sc = new SqlExecReqDto();
		sc.setCommand(sql);
		sc.setNeedPermission(false);
		sc.setSchemaModeEnum(SchemaModeEnum.detail);

		SchemaDetailsDto schemaDetails = new SchemaDetailsDto();
		schemaDetails.setType(dbInfo.getDatabaseType().toUpperCase());
		schemaDetails.setIp(dbInfo.getDatasource().getIp());
		schemaDetails.setPort(dbInfo.getDatasource().getPort());
		schemaDetails.setUsername(dbInfo.getSchema().getUsername());
		schemaDetails.setPassword(dbInfo.getSchema().getPassword());
		schemaDetails.setSchemaName(StringUtils.isEmpty(dbInfo.getSchema().getServiceName())?dbInfo.getSchema().getName():dbInfo.getSchema().getServiceName());
		sc.setSchemaDetails(schemaDetails);

		return sc ;
	}
	
	
	protected boolean execSqlCommand( SqlExecService sqlExecService ,DatabaseConnect dbInfo , String sql ,dealRowInterface dealRows ) throws MetaDataException {
		if( sqlExecService == null ) {
			return false;
		}
		RespResult<SqlQueryRespDto> sqlRes = sqlExecService.executeQuery(UserUtils.getUserName(), getSqlCommand(dbInfo, sql));
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
	
	public String deleteQuoted(String fieldName) {
		return SQLUtils.normalize(fieldName);
	}
	
	public interface dealRowInterface{
		void dealRow(int index ,  Map<String, Object> map , List<String> columnNames) throws MetaDataException ;
	}
	
}

/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.analyzer.script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.util.JdbcConstants;
import com.ys.idatrix.cloudetl.logger.CloudLogger;

/**
 * SQLScriptParser <br/>
 * @author JW
 * @since 2018年1月8日
 * 
 */
public class SQLScriptParser {
	
	/**
	 * 获取druid的数据库类型
	 * @param dbMeta
	 * @return  JdbcConstants.MYSQL
	 */
	public static String getDbType(  DatabaseMeta dbMeta  ) {
		
		String dbType = dbMeta.getPluginId().toLowerCase() ;
		switch(dbType) {
		case "hbasetable": return JdbcConstants.HBASE;
		case "hive3": return JdbcConstants.HIVE;
		case "dm7": return JdbcConstants.ORACLE;
		default: return dbType;
		}
	}
	
	
	public static SQLSelectStatement parserSelectSql( DatabaseMeta dbMeta , String sql) {
		List<SQLStatement> statement = SQLUtils.parseStatements(sql, getDbType(dbMeta)) ;
		return (SQLSelectStatement)statement.get(0);
	}
	
	/**
	 * 通过sql解析对象 获取域对应的表名 <br> <br>
	 * Tips: 当 sql 是  select * from A,B 样式的 多表全量查询 时 无法解析.
	 * <br>
	 * 返回的表无法确认是 表还是视图
	 * 
	 * @param fieldName
	 * @param stmt
	 * @return
	 */
	public static String findTableByField(String fieldName , SQLSelectStatement stmt) {
		SQLSelectQueryBlock query = ((SQLSelectQueryBlock)stmt.getSelect().getQuery()) ;
		SQLTableSource fromTables = query.getFrom() ;
		Map<String, String> result = new HashMap<>();
		getTableAialsMaps(fromTables, result );

		if( result.size() == 1 ) {
			return result.values().iterator().next() ;
		}else {
			for( SQLSelectItem item : query.getSelectList()) {
				if( item.getExpr().toString().toUpperCase().contains(fieldName.toUpperCase()) && item.getExpr() instanceof SQLPropertyExpr ) {
					String ft = result.get(((SQLPropertyExpr)item.getExpr()).getOwnernName());
					if(!Utils.isEmpty(ft)) {
						return ft ;
					}
				}
			}
		}
		CloudLogger.getInstance().error("SQLScriptParser.findTableByField", "sql语义分析未找到表名"+result.toString());
		return null ;
	}
	
	
	private static void  getTableAialsMaps( SQLTableSource fromTables , Map<String,String> result){
		
		if( fromTables instanceof SQLExprTableSource) {
			//from 部分只有一个表 ,所有域都会来自这个表
			SQLExprTableSource tableSource = (SQLExprTableSource)fromTables ;
			String tableName = ((SQLName)tableSource.getExpr()).getSimpleName();
			String alias = tableSource.getAlias() ;
			result.put(Const.NVL(alias, tableName), tableName);
			
		}else if (  fromTables instanceof SQLJoinTableSource ) {
			SQLJoinTableSource tableSource = (SQLJoinTableSource)fromTables ;
			getTableAialsMaps(tableSource.getLeft(), result);
			getTableAialsMaps(tableSource.getRight(), result);
		}
		
		
	}

}

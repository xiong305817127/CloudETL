/**
 * 云化数据集成系统 
 * iDatrix quality
 */
package com.ys.idatrix.quality.service.db;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;

import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.common.TestResultDto;
import com.ys.idatrix.quality.dto.db.DbBriefDto;
import com.ys.idatrix.quality.dto.db.DbConnectionDto;
import com.ys.idatrix.quality.dto.db.DbTableFieldDto;

/**
 * Service interface for cloud DB connection.
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudLocalDbService {
	

	/**
	 * Get DB connection list.
	 * @return
	 * @throws Exception 
	 */
	Map<String,List<DbBriefDto>> getDbConnectionList(String owner, boolean isTest ) throws Exception;
	
	
	/**
	 * 分页Get DB connection list without testing.
	 * @return
	 * @throws Exception 
	 * @throws Exception 
	 */
	public Map<String,PaginationDto<DbBriefDto>> getDbConnectionList(String owner , boolean isMap , Integer page, Integer pageSize, String search) throws Exception;

	/**
	 * Test DB connection.
	 * @param dbMeta
	 * @return
	 */
	String testDbConnection(DatabaseMeta dbMeta);

	/**
	 * Check if DB connection has been changed.
	 * @param connectionName
	 * @return
	 * @throws Exception 
	 */
	boolean isDbConnectionChanged(String owner ,String connectionName) throws Exception;

	/**
	 * Do DB connection test with name.
	 * @param connectionName
	 * @return
	 * @throws Exception 
	 */
	TestResultDto doDbConnectionTest(String owner ,String connectionName) throws Exception;

	/**
	 * Check if DB connection name exists.
	 * @param connectionName
	 * @return
	 * @throws Exception 
	 */
	CheckResultDto checkDbConnectionName(String owner ,String connectionName) throws Exception;

	/**
	 * Get all DB connection in repository.
	 * @return
	 * @throws Exception 
	 */
	List<DatabaseMeta> getAllDbConnection(String owner ) throws Exception;

	/**
	 * Get DB connection by given connection name.
	 * @param connectionName
	 * @return
	 * @throws Exception 
	 */
	DbConnectionDto getDbConnection(String owner ,String connectionName) throws Exception;

	DatabaseMeta getDatabaseMeta(String owner ,String connectionName) throws Exception ;
	/**
	 * Add DB connection into repository.
	 * @param dbConnection
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto addDbConnection( DbConnectionDto dbConnection) throws Exception;

	/**
	 * Update DB connection, add it as new connection if its name does not exist.
	 * @param dbConnection
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto updateDbConnection( DbConnectionDto dbConnection) throws Exception;

	/**
	 * Delete DB connection from repository.
	 * @param connectionName
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto deleteDbConnection(String owner ,String connectionName) throws Exception;

	/**
	 * Get schema name according to DB connection.
	 * @param connectionName
	 * @return
	 * @throws Exception 
	 */
	List<String> getDbSchema(String owner , String connectionName ) throws Exception;

	/**
	 * Get DB tables according to DB connection & schema.
	 * @param connectionName
	 * @param schemaName
	 * @return
	 * @throws Exception 
	 */
	List<String> getDbTables(String owner ,String connectionName, String schemaName) throws Exception;

	/**
	 * Get DB table fields.
	 * @param connectionName
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws Exception 
	 */
	List<DbTableFieldDto> getDbTableFields(String owner ,String connectionName, String schemaName, String tableName,boolean isQuote) throws Exception;
	
	public final LoggingObjectInterface loggingObject = new SimpleLoggingObject("CloudDbConnection", LoggingObjectType.DATABASE, null );

	/**
	 * Get table primary key
	 * @param connection
	 * @param schema
	 * @param table
	 * @return
	 * @throws Exception 
	 */
	public String[] getDbTablePrimaryKey(String owner ,String connection, String schema, String table) throws Exception;


	/**
	 * 请求方法 - 查询数据库连接中的存储过程
	 * @param transName
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	public  String[] getProc(String owner , String connection) throws Exception ;

	/**
	 * 增加Jndi配置
	 * @param dbConnection
	 * @return
	 */
	public ReturnCodeDto createjndi(DbConnectionDto dbConnection) throws Exception ;

	ReturnCodeDto deletejndi(String owner ,String type, String name) throws Exception;

	DbConnectionDto getjndiByName(String owner ,String type, String name) throws Exception;

	List<DbConnectionDto> getjndiList(String owner ,String type) throws Exception;

	CheckResultDto checkJndiName(String owner ,String type, String jndiName) throws Exception;

}
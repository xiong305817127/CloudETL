/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.db;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.TestResultDto;
import com.ys.idatrix.cloudetl.dto.db.DbBriefDto;
import com.ys.idatrix.cloudetl.dto.db.DbConnectionDto;
import com.ys.idatrix.cloudetl.dto.db.DbSchemaDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbTableFieldDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbTableViews;

/**
 * Service interface for cloud DB connection.
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudMetaCubeDbService {
	

	DatabaseMeta getDatabaseMeta(String owner , Long schemaId) throws Exception ;
	
	Map<String,List<DbBriefDto>> getDbConnectionList(String owner, Boolean isRead) throws Exception;
	
	Map<String,PaginationDto<DbBriefDto>> getDbConnectionList(String owner , Boolean isRead , boolean isMap , Integer page, Integer pageSize, String search) throws Exception;

	TestResultDto doDbConnectionTest(String owner , Long schemaId ) throws Exception;

	List<DatabaseMeta> getAllDbConnection(String owner ) throws Exception;

	DbConnectionDto getDbConnection(String owner , Long schemaId) throws Exception;

	List<DbSchemaDto> getDbSchema(String owner ,  Long databaseId , String name, Boolean isRead) throws Exception;

	MetaCubeDbTableViews getDbTables(String owner , Long schemaId, Boolean isRead) throws Exception;

	List<MetaCubeDbTableFieldDto> getDbTableFields(String owner , Long tableId ) throws Exception;
	
	String[] getDbTablePrimaryKey(String owner ,Long schemaId, String table) throws Exception;

	String[] getProc(String owner , Long schemaId) throws Exception ;
	
}
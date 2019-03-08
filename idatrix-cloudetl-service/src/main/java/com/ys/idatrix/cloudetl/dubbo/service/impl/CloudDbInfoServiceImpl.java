
package com.ys.idatrix.cloudetl.dubbo.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.metacube.api.dto.DbSchemaDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.DbTableDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.DbTableFieldDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.DbTableFieldsDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.DbTableFieldsListDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.DbTablesDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.MetaCubeDbDto;
import com.ys.idatrix.cloudetl.metacube.api.service.CloudDbInfoService;

@Component
@Service
public class CloudDbInfoServiceImpl implements CloudDbInfoService {

	public final LoggingObjectInterface loggingObject = new SimpleLoggingObject("CloudDbConnection",
			LoggingObjectType.DATABASE, null);

	// private final Logger logger = Logger.getLogger(CloudDbInfoServiceImpl.class);

	public String getUserId(MetaCubeDbDto metaCubeDbDto) {
		String u = metaCubeDbDto.getUserId();
		if (Utils.isEmpty(u)) {
			u = CloudSession.getLoginUser();
		} else {
			CloudSession.setThreadLoginUser(u);
		}
		if (Utils.isEmpty(u)) {
			return CloudApp.defaut_userId;
		}
		return u;
	}

	/**
	 * 
	 * @param connectionName
	 * @return
	 * @throws KettlePluginException
	 */
	public DbSchemaDto getDbSchema(MetaCubeDbDto metaCubeDbDto) throws Exception {
		DbSchemaDto schema = new DbSchemaDto();
		schema.setSuccess(true);

		CloudLogger.getInstance(getUserId(metaCubeDbDto)).addNumber().info(this, "getDbSchema 参数:" + metaCubeDbDto);
		try {

			DatabaseMeta dbMeta = this.getDatabaseMeta(metaCubeDbDto);

			if (dbMeta != null) {
				if (!Utils.isEmpty(metaCubeDbDto.getTableName())) {
					Database db = new Database(loggingObject, dbMeta);
					CloudLogger.getInstance(getUserId(metaCubeDbDto)).debug(this, "getDbSchema connect");
					db.connect();
					String[] ss = db.getSchemas();
					if (ss != null && ss.length > 0) {
						CloudLogger.getInstance(getUserId(metaCubeDbDto)).debug(this,
								" getDbSchema list " + Arrays.toString(ss));
						for (String s : ss) {
							if (Arrays.asList(db.getTablenames(s, false)).contains(metaCubeDbDto.getTableName())) {
								schema.setSchema(s);
								break;
							}
						}

					}
					db.disconnect();
				} else {
					if ("MYSQL".equalsIgnoreCase(metaCubeDbDto.getPluginId())
							|| "DM7".equalsIgnoreCase(metaCubeDbDto.getPluginId())) {
						schema.setSchema(Utils.isEmpty(dbMeta.getDataTablespace()) ? dbMeta.getDatabaseName()
								: dbMeta.getDataTablespace());
					} else if ("ORACLE".equalsIgnoreCase(metaCubeDbDto.getPluginId())) {
						schema.setSchema(metaCubeDbDto.getUsername());
					} else if ("POSTGRESQL".equalsIgnoreCase(dbMeta.getPluginId())) {
						schema.setSchema("public");
					}
				}

			} else {
				schema.setSchema("");
			}

		} catch (Exception e) {
			CloudLogger.getInstance(getUserId(metaCubeDbDto)).error(this, "getDbSchema fail:", e);
			schema.setSuccess(false);
			schema.setMess("getDbSchema fail," + CloudLogger.getExceptionMessage(e, false));
		}

		CloudLogger.getInstance(getUserId(metaCubeDbDto)).info(this,"getDbSchema result:" + JSON.toJSONString(schema));
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return schema;
	}

	public DbSchemaDto getDbSchemas(MetaCubeDbDto metaCubeDbDto) throws Exception {

		DbSchemaDto schema = new DbSchemaDto();
		schema.setSuccess(true);
		schema.setConnection(metaCubeDbDto.getName());

		CloudLogger.getInstance(getUserId(metaCubeDbDto)).addNumber().info(this, "getDbSchemas 参数:" + metaCubeDbDto);
		try {

			DatabaseMeta dbMeta = this.getDatabaseMeta(metaCubeDbDto);
			if (dbMeta != null) {

				String pschema = dbMeta.getPreferredSchemaName();
				if (Utils.isEmpty(pschema)) {
					if ("ORACLE".equalsIgnoreCase(dbMeta.getPluginId())) {
						pschema = dbMeta.getUsername();
					} else if ("POSTGRESQL".equalsIgnoreCase(dbMeta.getPluginId())) {
						pschema = "public";
					} else {
						// "MYSQL".equalsIgnoreCase(dbMeta.getPluginId()) ||
						// "DM7".equalsIgnoreCase(dbMeta.getPluginId())
						pschema = Utils.isEmpty(dbMeta.getDataTablespace()) ? dbMeta.getDatabaseName()
								: dbMeta.getDataTablespace();
					}
				}
				schema.setSchema(pschema);

				Database db = new Database(loggingObject, dbMeta);
				db.connect();
				String[] ss = db.getSchemas();
				if (ss != null && ss.length > 0) {
					schema.setSchemas(Arrays.asList(ss));
				} else {
					schema.setSchemas(Lists.newArrayList(schema.getSchema()));
				}
				db.disconnect();
			} else {
				schema.setSuccess(false);
				schema.setMess("getDbSchemas fail,dbMeta is null ");
				CloudLogger.getInstance(getUserId(metaCubeDbDto)).error(this,"getDbSchemas fail,dbMeta is null ");
			}

		} catch (Exception e) {
			CloudLogger.getInstance(getUserId(metaCubeDbDto)).error(this, "getDbSchemas fail:", e);
			schema.setSuccess(false);
			schema.setMess("getDbSchemas fail," + CloudLogger.getExceptionMessage(e, false));
		}

		CloudLogger.getInstance(getUserId(metaCubeDbDto)).info(this,"getDbSchemas result:" + JSON.toJSONString(schema));
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return schema;
	}

	public DbTablesDto getDbTables(MetaCubeDbDto metaCubeDbDto) throws Exception {
		CloudLogger.getInstance(getUserId(metaCubeDbDto)).addNumber().info(this, "getDbTables 参数:" + metaCubeDbDto);

		DbTablesDto jdTables = new DbTablesDto();
		jdTables.setSuccess(true);

		DatabaseMeta dbMeta = this.getDatabaseMeta(metaCubeDbDto);
		//Mysql需要配置useInformationSchema获取注释信息
		if( "MYSQL".equalsIgnoreCase(dbMeta.getPluginId()) ) {
			dbMeta.addExtraOption(dbMeta.getPluginId(), "useInformationSchema", "true");
		}else if ("ORACLE".equalsIgnoreCase(dbMeta.getPluginId()) || "DM7".equalsIgnoreCase(dbMeta.getPluginId()) ) {
			dbMeta.addExtraOption(dbMeta.getPluginId(), "includeSynonymsconnection", "true");
			dbMeta.addExtraOption(dbMeta.getPluginId(), "remarksReporting", "true");
		}
				
		jdTables.setType(dbMeta.getPluginId());
		Database db = new Database(loggingObject, dbMeta);
		
		Map<String, Collection<String>> tableMap;
		try {
			db.connect();
			DBCache.getInstance().setActive(false);// 不使用缓存
			
			String schemaName = metaCubeDbDto.getSchemaName();
			
			List<DbTableDto> jdtList = null ;
			try {
				jdtList = getDataBaseTableMetadata(db, schemaName);
			}catch(Exception e) {
				CloudLogger.getInstance(getUserId(metaCubeDbDto)).warn(this, "getDbTables,get TableMetadata from driver fail:" + CloudLogger.getExceptionMessage(e));
			}
			if( jdtList == null || jdtList.isEmpty() ) {
				jdtList = new ArrayList<>();
				if (Utils.isEmpty( schemaName )) {
					tableMap = db.getTableMap();
				} else {
					tableMap = db.getTableMap(metaCubeDbDto.getSchemaName());
				}

				CloudLogger.getInstance(getUserId(metaCubeDbDto)).debug(this, "getDbTables tableMap :" + JSON.toJSONString(tableMap));
				if (tableMap != null) {
					for (String schema : tableMap.keySet()) {
						List<String> tables = new ArrayList<String>(tableMap.get(schema));
						Collections.sort(tables);
						for (String tableName : tables) {
							DbTableDto jdt = new DbTableDto();
							if (Utils.isEmpty(schemaName) && !Utils.isEmpty(schema)) {
								jdt.setTable(schema + "." + tableName);
							} else {
								jdt.setTable(tableName);
							}
							jdtList.add(jdt);
						}
					}
				}
			}
			
			jdTables.setTables(jdtList);
			
		} catch (Exception e) {
			CloudLogger.getInstance(getUserId(metaCubeDbDto)).error(this, "getDbTables fail:", e);
			jdTables.setSuccess(false);
			jdTables.setMess("getDbTables fail," + CloudLogger.getExceptionMessage(e, false));
		} finally {
			db.disconnect();
		}

		CloudLogger.getInstance(getUserId(metaCubeDbDto)).info(this, "getDbTables jdTables :" + JSON.toJSONString(jdTables));
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return jdTables;
	}
	
	private List<DbTableDto> getDataBaseTableMetadata(Database db, String schema ) throws Exception {
		List<DbTableDto> jdtList = new ArrayList<>();
		
	    ResultSet alltables = null;
	    try {
	      alltables = db.getDatabaseMetaData().getTables( null, Const.NVL( schema ,"%"), null, db.getDatabaseMeta().getTableTypes() );
	      while ( alltables.next() ) {
	    	  
	    	  DbTableDto dt = new DbTableDto();
	   
	    	  String table = alltables.getString( "TABLE_NAME" );
	    	  String remarks = alltables.getString( "REMARKS" );
	    	 
	    	  dt.setTable(table);
	    	  dt.setRemark(remarks);
	    	  
	    	  jdtList.add(dt);
	      }
	   }finally {
	      try {
	        if ( alltables != null ) {
	          alltables.close();
	        }
	      } catch ( SQLException e ) {
	      }
	    }
		return jdtList ;
	}

	/**
	 * get table fields
	 * 
	 * @param metaCubeDbDto
	 * @return
	 * @throws KettlePluginException
	 */
	public DbTableFieldsDto getTableFields(MetaCubeDbDto metaCubeDbDto) throws Exception {
		CloudLogger.getInstance(getUserId(metaCubeDbDto)).addNumber().info(this, "getTableFields 参数:" + metaCubeDbDto);

		DbTableFieldsDto jdtFields = new DbTableFieldsDto();
		DatabaseMeta dbMeta = null;
		if (Utils.isEmpty(metaCubeDbDto.getName())) {
			metaCubeDbDto.setName(metaCubeDbDto.getHostname() + ":" + metaCubeDbDto.getDatabaseName() + ":"
					+ metaCubeDbDto.getTableName());
		}
		if (!Utils.isEmpty(metaCubeDbDto.getTableName()) && metaCubeDbDto.getTableName().indexOf(".") != -1) {
			String tableName = metaCubeDbDto.getTableName();
			int position = tableName.indexOf(".");
			metaCubeDbDto.setTableName(tableName.substring(position + 1));
		}

		dbMeta = this.getDatabaseMeta(metaCubeDbDto);
		if (dbMeta == null) {
			CloudLogger.getInstance(getUserId(metaCubeDbDto)).info(this,
					"getTableFields fail : dbMeta == null");
			jdtFields.setFields(new ArrayList<>());
			jdtFields.setSuccess(false);
			jdtFields.setMess("getTableFields fail,数据库信息获取失败.");
			return jdtFields;
		}
		//Mysql需要配置useInformationSchema获取注释信息
		if( "MYSQL".equalsIgnoreCase(dbMeta.getPluginId()) ) {
			dbMeta.addExtraOption(dbMeta.getPluginId(), "useInformationSchema", "true");
		}else if ("ORACLE".equalsIgnoreCase(dbMeta.getPluginId())  || "DM7".equalsIgnoreCase(dbMeta.getPluginId()) ) {
			dbMeta.addExtraOption(dbMeta.getPluginId(), "includeSynonymsconnection", "true");
			dbMeta.addExtraOption(dbMeta.getPluginId(), "remarksReporting", "true");
		}
				
		List<String> keys = new ArrayList<String>();
		Database db = new Database(loggingObject, dbMeta);
		try {
			db.connect();
			DBCache.getInstance().setActive(false);// 不使用缓存
			CloudLogger.getInstance(getUserId(metaCubeDbDto)).debug(this, "getTableFields , db conneted");

			String schemaTable = dbMeta.getQuotedSchemaTableCombination(metaCubeDbDto.getSchemaName(),
					metaCubeDbDto.getTableName());
			// if ("POSTGRESQL".equals(dbMeta.getPluginId()) ||
			// "HIVE2".equals(dbMeta.getPluginId())) {
			// schemaTable = metaCubeDbDto.getTableName();
			// }
			CloudLogger.getInstance(getUserId(metaCubeDbDto)).debug(this,
					"getTableFields,db schemaTable:" + schemaTable);

			// get primary keys
			String[] KeyColumnNames = db.getPrimaryKeyColumnNames(metaCubeDbDto.getSchemaName(),metaCubeDbDto.getTableName());
			keys = Arrays.asList(KeyColumnNames);

			List<DbTableFieldDto> jdtfList = null ;
			try {
			 jdtfList = getDataBaseColumnMetadata(db, metaCubeDbDto.getSchemaName(), metaCubeDbDto.getTableName(), keys);
			}catch(Exception e) {
				CloudLogger.getInstance(getUserId(metaCubeDbDto)).warn(this, "getTableFields,get ColumnMetadata from driver fail:" + CloudLogger.getExceptionMessage(e));
			}
			if (jdtfList == null || jdtfList.isEmpty()) {

				RowMetaInterface fields = db.getTableFields(schemaTable);
				jdtfList = new ArrayList<>();
				if (fields != null) {
					for (int i = 0; i < fields.size(); i++) {
						ValueMetaInterface field = fields.getValueMeta(i);
						DbTableFieldDto jdtField = new DbTableFieldDto();
						jdtField.setName(field.getName());
						jdtField.setLength(field.getLength());
						jdtField.setType(field.getOriginalColumnTypeName());

						CloudLogger.getInstance(getUserId(metaCubeDbDto)).debug(this,
								jdtField.getName() + " nullable :" + field.isOriginalNullable());

						jdtField.setNullable(field.isOriginalNullable());
						jdtField.setPrecision(field.getPrecision());
						if (keys.contains(jdtField.getName())) {
							jdtField.setIsPrimaryKey(1);
						}
						// 处理mysql int，tinyint 默认返回4位 长度，
						if ("tinyint".equalsIgnoreCase(field.getOriginalColumnTypeName())) {
							jdtField.setLength(3);
						} else if ("INT".equalsIgnoreCase(field.getOriginalColumnTypeName())
								|| "INTEGER".equalsIgnoreCase(field.getOriginalColumnTypeName())
								|| "INTEGER".indexOf(field.getOriginalColumnTypeName()) != -1) {
							jdtField.setLength(11);
						}

						// binay,varbinary 长度取original precision
						else if ("binary".equalsIgnoreCase(field.getOriginalColumnTypeName())
								|| "varbinary".equalsIgnoreCase(field.getOriginalColumnTypeName())) {
							jdtField.setLength(field.getOriginalPrecision());
						} else if ("year".equalsIgnoreCase(field.getOriginalColumnTypeName())) {
							jdtField.setLength(4); // year type only support 4 length
						}

						if ("VARCHAR".equalsIgnoreCase(field.getOriginalColumnTypeName()) && field.isLargeTextField()) {
							// [tinytext String(85)], [longtext String(715827882)], [text String(21845)],
							// [mediuTExt String(5592405)]
							if (field.getLength() == 85) {
								jdtField.setType("tinytext");
							} else if (field.getLength() == 21845) {
								jdtField.setType("text");
							} else if (field.getLength() == 5592405) {
								jdtField.setType("mediumtext");
							} else {
								jdtField.setType("longtext");
							}
							jdtField.setLength(null);
						}

						// 其他date，time ，timestamp 等类型返回长度为null
						if (jdtField.getLength() != null && Integer.compare(jdtField.getLength(), 0) != 1) {
							jdtField.setLength(null);
						}
						// clob 去除长度
						if ("clob".equalsIgnoreCase(jdtField.getType())) {
							jdtField.setLength(null);
						}

						if (Integer.compare(jdtField.getPrecision(), 0) == -1) {
							jdtField.setPrecision(0);
						}
						jdtfList.add(jdtField);
					}
				}

			}

			jdtFields.setFields(jdtfList);
			jdtFields.setTable(metaCubeDbDto.getTableName());
			jdtFields.setSchema(metaCubeDbDto.getSchemaName());
			jdtFields.setSuccess(true);
			jdtFields.setMess("get table fields success");

		} catch (Exception e) {
			CloudLogger.getInstance(getUserId(metaCubeDbDto)).error(this, "getTableFields fail:", e);
			jdtFields.setFields(new ArrayList<>());
			jdtFields.setSuccess(false);
			jdtFields.setMess("getTableFields fail," + CloudLogger.getExceptionMessage(e, false));
			return jdtFields;
		} finally {
			db.disconnect();
		}

		CloudLogger.getInstance(getUserId(metaCubeDbDto)).info(this, "getTableFields result :" + JSON.toJSONString(jdtFields));
		//清理线程用户信息
		CloudSession.clearThreadInfo();
				
		return jdtFields;
	}

	private List<DbTableFieldDto> getDataBaseColumnMetadata(Database db, String schema, String table,
			List<String> primaryKeys) throws Exception {
		List<DbTableFieldDto> dtfs = new ArrayList<>();
		ResultSet columns =null;
		try {
			columns = db.getDatabaseMetaData().getColumns(null, Const.NVL(schema, Const.NVL(db.getDatabaseMeta().getPreferredSchemaName(), "%")), table, "%");
			while (columns.next()) {
	
				DbTableFieldDto tf = new DbTableFieldDto();
				String name = columns.getString("COLUMN_NAME");
				tf.setName(name);
				String type = columns.getString("TYPE_NAME");
				tf.setType(type);
				tf.setMetaType(columns.getString("SOURCE_DATA_TYPE"));
	
				int length = columns.getInt("COLUMN_SIZE");
				if ("clob".equalsIgnoreCase(type)) {
					tf.setLength(null);
				} else if (length <= 0) {
					if ("tinyint".equalsIgnoreCase(type)) {
						tf.setLength(3);
					} else if ("INT".equalsIgnoreCase(type) || "INTEGER".equalsIgnoreCase(type)
							|| "INTEGER".indexOf(type) != -1) {
						tf.setLength(11);
					} else if ("year".equalsIgnoreCase(type)) {
						tf.setLength(4); // year type only support 4 length
					} else {
						tf.setLength(null);
					}
				} else {
					tf.setLength(length);
				}
	
				tf.setPrecision(columns.getInt("DECIMAL_DIGITS"));
				tf.setRemarks(columns.getString("REMARKS"));
	
				if (primaryKeys.contains(name)) {
					tf.setIsPrimaryKey(1);
				} else {
					tf.setIsPrimaryKey(0);
				}
	
				/**
				 * 0 (columnNoNulls) - 该列不允许为空 1 (columnNullable) - 该列允许为空 2
				 * (columnNullableUnknown) - 不确定该列是否为空
				 */
	
				tf.setNullable(columns.getInt("NULLABLE"));
				tf.setDefaultValue(columns.getString("COLUMN_DEF"));
				try {
					//DM数据库不支持该列名,会报错
					tf.setAutoincrement(Boolean.valueOf(columns.getString("IS_AUTOINCREMENT")));
				}catch(Exception e) {}
				
	
				dtfs.add(tf);
			}
		}finally {
			try {
		        if ( columns != null ) {
		        	columns.close();
		        }
		      } catch ( SQLException e ) {
		      }
		}
		
		return dtfs;
	}

	/**
	 * get data base meta
	 * 
	 * @param metaCubeDbDto
	 * @return
	 * @throws KettlePluginException
	 */

	private String adjustDatabaseType(String dbType) {
		if ("HBASE".equalsIgnoreCase(dbType)) {
			return "HBASETABLE";
		} else if ("HIVE".equalsIgnoreCase(dbType)) {
			return "HIVE3";
		}
		return dbType;
	}

	private DatabaseMeta getDatabaseMeta(MetaCubeDbDto metaCubeDbDto) throws Exception {

		if (Utils.isEmpty(metaCubeDbDto.getPluginId())) {
			throw new KettleException("数据库类型(pluginId)不能为空");
		}

		CloudLogger.getInstance(getUserId(metaCubeDbDto)).addNumber().debug(this, "getDatabaseMeta start");
		DatabaseMeta databaseMeta = new DatabaseMeta();

		PluginInterface plugin = PluginRegistry.getInstance().getPlugin(DatabasePluginType.class,
				adjustDatabaseType(metaCubeDbDto.getPluginId()));
		if (plugin == null) {
			throw new KettleException("数据库类型(pluginId:[" + metaCubeDbDto.getPluginId() + "[)不存在!");
		}
		DatabaseInterface databaseInterface = (DatabaseInterface) PluginRegistry.getInstance().loadClass(plugin);
		databaseInterface.setPluginId(adjustDatabaseType(metaCubeDbDto.getPluginId()));
		databaseMeta.setDatabaseInterface(databaseInterface);
		databaseMeta.setName(metaCubeDbDto.getName());
		databaseMeta.setDescription("");
		databaseMeta.setAccessType(DatabaseMeta.getAccessType(metaCubeDbDto.getAccessType()));
		databaseMeta.setHostname(metaCubeDbDto.getHostname());
		databaseMeta.setDBPort(metaCubeDbDto.getPort());
		databaseMeta.setDBName(metaCubeDbDto.getDatabaseName());
		databaseMeta.setUsername(metaCubeDbDto.getUsername());
		databaseMeta.setPassword(metaCubeDbDto.getPassword());
		if (!Utils.isEmpty(metaCubeDbDto.getSchemaName())) {
			databaseMeta.setPreferredSchemaName(metaCubeDbDto.getSchemaName());
		}

		CloudLogger.getInstance(getUserId(metaCubeDbDto)).debug(this,
				"getDatabaseMeta end," + databaseMeta.toString());

		return databaseMeta;
	}

	@Override
	public DbTableFieldsListDto getBatchTableFields(MetaCubeDbDto metaCubeDbDto) throws Exception {
		CloudLogger.getInstance(getUserId(metaCubeDbDto)).addNumber().info(this, "getBatchTableFields 参数:" + metaCubeDbDto);

		DbTableFieldsListDto fieldListDto = new DbTableFieldsListDto();
		List<DbTableFieldsDto> list = new ArrayList<DbTableFieldsDto>();
		try {
			DbTableFieldsDto fieldsDto = null;
			if (!CollectionUtils.isEmpty(metaCubeDbDto.getTableNames())) {
				for (String table : metaCubeDbDto.getTableNames()) {
					MetaCubeDbDto param = new MetaCubeDbDto();
					BeanUtils.copyProperties(metaCubeDbDto, param);
					param.setTableName(table);
					param.setTableNames(null);
					fieldsDto = getTableFields(param);
					if (!fieldsDto.isSuccess()) {
						fieldListDto.setSuccess(false);
						fieldListDto.setMess("getBatchTableFields fail," + fieldsDto.getMess());
						return fieldListDto;
					}
					list.add(fieldsDto);
				}
			} else {
				fieldsDto = getTableFields(metaCubeDbDto);
				if (!fieldsDto.isSuccess()) {
					fieldListDto.setSuccess(false);
					fieldListDto.setMess("getBatchTableFields fail," + fieldsDto.getMess());
					return fieldListDto;
				}
				list.add(fieldsDto);
			}
		} catch (Exception e) {
			CloudLogger.getInstance(getUserId(metaCubeDbDto)).error(this, "getBatchTableFields fail:", e);

			fieldListDto.setSuccess(false);
			fieldListDto.setMess("getBatchTableFields fail," + CloudLogger.getExceptionMessage(e, false));
			return fieldListDto;
		}
		fieldListDto.setSuccess(true);
		fieldListDto.setMess("getBatchTableFields success");
		fieldListDto.setList(list);

		CloudLogger.getInstance(getUserId(metaCubeDbDto)).info(this, "getBatchTableFields result :" + JSON.toJSONString(fieldListDto));
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return fieldListDto;
	}

}
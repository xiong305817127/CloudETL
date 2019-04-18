/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.quality.toolkit.utils;

import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;

import com.ys.idatrix.quality.dto.step.StepFieldDto;
import com.ys.idatrix.quality.ext.utils.FieldValidator;
import com.ys.idatrix.quality.ext.utils.StringEscapeHelper;
import com.ys.idatrix.quality.toolkit.common.NodeLevel;
import com.ys.idatrix.quality.toolkit.common.NodeType;
import com.ys.idatrix.quality.toolkit.common.SystemType;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.property.DataItemProperty;
import com.ys.idatrix.quality.toolkit.domain.property.DataSetProperty;
import com.ys.idatrix.quality.toolkit.domain.property.DatabaseProperty;
import com.ys.idatrix.quality.toolkit.domain.property.DummyProperty;
import com.ys.idatrix.quality.toolkit.domain.property.FieldProperty;
import com.ys.idatrix.quality.toolkit.domain.property.FileProperty;
import com.ys.idatrix.quality.toolkit.domain.property.FileSystemProperty;
import com.ys.idatrix.quality.toolkit.domain.property.InterfaceProperty;
import com.ys.idatrix.quality.toolkit.domain.property.SchemaProperty;
import com.ys.idatrix.quality.toolkit.domain.property.SystemProperty;
import com.ys.idatrix.quality.toolkit.domain.property.TableProperty;

/**
 * ToolkitUtil <br/>
 * 
 * @author JW
 * @since 2018年1月29日
 * 
 */
public class DataNodeUtil {


	/**
	 * 构建数据节点
	 * 
	 * @param nodeType
	 * @param nodeLevel
	 * @return
	 */
	public static DataNode buildDataNode(NodeType nodeType, NodeLevel nodeLevel,String name ,DataNode parentNode) {
		DataNode dataNode = new DataNode();
		dataNode.setType(nodeType);
		dataNode.setLevel(nodeLevel);
		dataNode.setName(name);
		dataNode.setParent(parentNode);
		return dataNode;
	}


	/**
	 * 构建 系统级数据节点
	 * 
	 * @param systemName
	 * @param sysProperty
	 * @return
	 * @throws Exception
	 */
	public static DataNode buildSystemNode(String systemName, SystemProperty sysProperty) throws Exception {
		DataNode sysDataNode = buildDataNode(NodeType.SYSTEM, NodeLevel.SYSTEM,systemName,null);
		sysDataNode.setProperties(sysProperty);
		return sysDataNode;
	}

	/**
	 * 构建数据库级的数据节点
	 * 
	 * @param parentNode
	 * @param dbName
	 * @param dbProperty
	 * @return
	 * @throws Exception
	 */
	public static DataNode buildDatabaseNode(DataNode systemNode, String dbName, DatabaseProperty dbProperty)
			throws Exception {
		DataNode dbDataNode = buildDataNode(NodeType.DATABASE, NodeLevel.DATABASE,dbName,systemNode);
		dbDataNode.setProperties(dbProperty);

		return dbDataNode;
	}

	/**
	 * 构建Schema级的数据节点
	 * 
	 * @param dbNode
	 * @param schemaName
	 * @param schemaProperty
	 * @return
	 * @throws Exception
	 */
	public static DataNode buildSchemaNode(DataNode dbNode, String schemaName, SchemaProperty schemaProperty)
			throws Exception {
		DataNode schemaDataNode = buildDataNode(NodeType.SCHEMA, NodeLevel.SCHEMA,schemaName ,dbNode);
		schemaDataNode.setProperties(schemaProperty);

		return schemaDataNode;
	}

	/**
	 * 构建table级的数据节点
	 * 
	 * @param schemaNode
	 * @param tableName
	 * @param tableProperty
	 * @return
	 * @throws Exception
	 */
	public static DataNode buildTableNode(DataNode schemaNode, String tableName, TableProperty tableProperty)
			throws Exception {
		DataNode tableDataNode = buildDataNode(NodeType.TABLE, NodeLevel.TABLE,tableName,schemaNode);
		tableDataNode.setProperties( tableProperty);

		return tableDataNode;
	}

	/**
	 * 构建 Field级的数据节点
	 * 
	 * @param tableNode
	 * @param tableName
	 * @param tableProperty
	 * @return
	 * @throws Exception
	 */
	public static DataNode buildFieldNode(DataNode tableNode, String fieldName, FieldProperty fieldProperty)
			throws Exception {
		DataNode fieldDataNode = buildDataNode(NodeType.FIELD, NodeLevel.FIELD , fieldName, tableNode);
		fieldDataNode.setProperties(fieldProperty);

		return fieldDataNode;
	}

	/**
	 * dummy 节点解析
	 * 
	 * @param dummyName
	 * @param dummyProperty
	 * @throws Exception
	 */
	public static DataNode BuildDummyNode(String dummyName, DummyProperty dummyProperty) throws Exception {
		
		String systemName= "Dummy" ;
		SystemProperty sysProperty = new SystemProperty(systemName );
		sysProperty.setType(SystemType.Dummy);

		DataNode sysDataNode = buildSystemNode(systemName, sysProperty);
		
		DataNode dummyDataNode = buildDataNode(NodeType.DUMMY, NodeLevel.TABLE, dummyName , sysDataNode);
		dummyDataNode.setProperties(dummyProperty);

		return dummyDataNode;
	}
	
	
	/**
	 * 构建table属性对象
	 * @param fileName
	 * @param charset
	 * @param compress
	 * @param author
	 * @return
	 * @throws Exception
	 */
	public static TableProperty buildTableProperty(String tableName,Database db,String schema ,boolean isView ) throws Exception {
		if ( !Utils.isEmpty(tableName)) {
			TableProperty tableProperty = new TableProperty(tableName);
			tableProperty.setCharset("UTF-8");
			tableProperty.setView(isView);
			
			String[] primaryKeys = db.getPrimaryKeyColumnNames(schema, tableName);
			RowMetaInterface fields = db.getTableFields(db.getDatabaseMeta().getQuotedSchemaTableCombination(schema, tableName));
			tableProperty.setFields(fields.getFieldNames());
			tableProperty.setCreateSQL(db.getDDLCreationTable(tableName, fields));
			tableProperty.setPrimaryKeys(primaryKeys);
			
			return tableProperty ;
		}
		return null ;
	}
	
	/**
	 * 构建Field属性对象
	 * @param fileName
	 * @param charset
	 * @param compress
	 * @param author
	 * @return
	 * @throws Exception
	 */
	public static FieldProperty buildFieldProperty( ValueMetaInterface field ,String[] primaryKeys ) throws Exception {
		if ( field != null) {
			
			String fieldName = Const.NVL(field.getComments(), field.getName());
			
			FieldProperty fieldProperty = new FieldProperty(fieldName);
			fieldProperty.setAliasField(field.getName());
			fieldProperty.setFieldType(field.getTypeDesc());
			fieldProperty.setFieldLength(FieldValidator.fixedLength(field.getLength()));
			fieldProperty.setFieldPricision(FieldValidator.fixedPrecision(field.getPrecision()));
			fieldProperty.setFieldFormat(field.getFormatMask());
			fieldProperty.setPrimaryKey(ArrayUtils.contains(primaryKeys, field.getName()));
			fieldProperty.setDesc(field.getName());
			// 0-不允许,1-允许为空,2-不确定
			fieldProperty.setNull(1 == field.isOriginalNullable());
			return fieldProperty ;
		}
		return null ;
	}
	
	


	/**
	 * 构建文件属性对象
	 * @param fileName
	 * @param charset
	 * @param compress
	 * @param author
	 * @return
	 * @throws FileSystemException 
	 * @throws Exception
	 */
	public static FileProperty buildFileProperty(String fileName, String charset,String compress,String author) throws Exception  {
		if ( !Utils.isEmpty(fileName)) {
			fileName = fileName.trim();
			
			FileProperty fileProperty = new FileProperty(fileName);
			
			FileObject fileObj = KettleVFS.getFileObject(fileName);

			fileProperty.setProtocol( fileObj.getName().getScheme() ) ;
			fileProperty.setRoot(  fileObj.getName().getRootURI() );
			fileProperty.setPath( fileObj.getName().getPath());
			fileProperty.setExts( fileObj.getName().getExtension());
			fileProperty.setCharset( Const.NVL(charset, Charset.defaultCharset().name()));
			try {
				fileProperty.setSize( fileObj.getContent().getSize());
			} catch (FileSystemException e) {
			}
			fileProperty.setCompress( Const.NVL(compress,"") );
			fileProperty.setAuthor(Const.NVL(author,""));

			fileObj.close();
			return fileProperty;
		}
		
		return null ;
	}
	
	public static DataNode streamNodeParse( String fieldName ) throws Exception {
		return buildDataNode(NodeType.STEP_OR_ENTRY, NodeLevel.FIELD, fieldName , null);
	}

	public static DataNode dummyNodeParse( String dummyName ) throws Exception {
		DummyProperty dummyProperty = new DummyProperty(dummyName);
		dummyProperty.setFlag("Dummy");
		return BuildDummyNode(dummyName, dummyProperty );
	}
	
	
	/**
	 * 根据数据库meta信息生成 数据库Schema节点( 包括父节点 )
	 * 
	 * @param dbMeta
	 *            数据库信息
	 * @param schema
	 * @return
	 * @throws Exception
	 */
	public static DataNode dbSchemaNodeParse( DatabaseMeta dbMeta, String schema) throws Exception {
		if (null == dbMeta) {
			return null;
		}

		// 获取/生成 系统根数据节点
		String systemName = Const.NVL(dbMeta.getHostname(), dbMeta.getName() ) ;
		SystemProperty sysProperty = new SystemProperty( systemName );
		sysProperty.setType(SystemType.DateBase);
		sysProperty.setPosition(dbMeta.getHostname()+":"+dbMeta.getDatabasePortNumberString());
		sysProperty.setOwner(dbMeta.getUsername());

		DataNode sysDataNode = buildSystemNode(systemName, sysProperty);

		// 获取/生成 数据库数据节点
		String dbName = Const.NVL(dbMeta.getDatabaseName(), dbMeta.getURL());
		DatabaseProperty dbProperty = new DatabaseProperty(dbName);
		dbProperty.setDbName(dbMeta.getDatabaseName());
		dbProperty.setDbOwner(dbMeta.getUsername());
		dbProperty.setDbType(dbMeta.getPluginId());
		dbProperty.setDbUrl(dbMeta.getURL());
		dbProperty.setHost(dbMeta.getHostname());
		dbProperty.setInstance(dbMeta.getDatabaseName());
		dbProperty.setPort(dbMeta.getDatabasePortNumberString());
		dbProperty.setTableSpace(dbMeta.getDataTablespace());

		DataNode dbDataNode = buildDatabaseNode(sysDataNode, dbName, dbProperty);

		// 获取/生成 schema数据节点
		
		DataNode schemaDataNode = null ;
		schema = Const.NVL(schema, dbMeta.getPreferredSchemaName() );
		if( !Utils.isEmpty(schema )) {
			SchemaProperty schemaProperty = new SchemaProperty( schema );
				
			schemaDataNode = buildSchemaNode(dbDataNode, schema, schemaProperty);
		}
		return schemaDataNode == null ? dbDataNode : schemaDataNode;
	}

	/**
	 * 根据数据库信息 和 表名或者sql,生成table节点和field节点<br>
	 * 当 tableName 不为空时,获取tableName的table节点和field节点,返回<流域名,数据库域节点>列表 <br>
	 * 当 tableName  为空,sql不为空时,根据sql获取tablename并生成table节点和field节点,由于sql可以链表查询,所以可能生成多个table节点和相应的field节点,返回 <流域名,数据库域节点><br>
	 * 
	 * @param dbMeta
	 * @param tableName
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static Map<String,DataNode> dbFieldNodeParse( DatabaseMeta dbMeta , String schema ,String tableName, String sql, boolean isView) throws Exception {
		if (null == dbMeta) {
			return null;
		}
		schema = Const.NVL(schema, dbMeta.getPreferredSchemaName() );
		
		//Mysql需要配置useInformationSchema获取注释信息
		if( "MYSQL".equalsIgnoreCase(dbMeta.getPluginId()) ) {
			dbMeta.addExtraOption(dbMeta.getPluginId(), "useInformationSchema", "true");
			//增加默认的schema
			schema = Utils.isEmpty(schema) ? dbMeta.getDatabaseName() : schema ;
		}else if ("ORACLE".equalsIgnoreCase(dbMeta.getPluginId())  || "DM7".equalsIgnoreCase(dbMeta.getPluginId()) ) {
			dbMeta.addExtraOption(dbMeta.getPluginId(), "includeSynonymsconnection", "true");
			dbMeta.addExtraOption(dbMeta.getPluginId(), "remarksReporting", "true");
			
			//增加默认的schema
			schema = Utils.isEmpty(schema) ? dbMeta.getUsername() : schema ;
		}else {
			
			//增加默认的schema
			schema = Utils.isEmpty(schema) ? dbMeta.getDatabaseName() : schema ;
		}
		
		
		DataNode schemaNode = dbSchemaNodeParse(dbMeta , schema) ;
		
		Map<String,DataNode> fieldNodes = Maps.newHashMap();
		
		Database db = new Database(null, dbMeta) ;
		try {
			
			db.connect();
			if (!Utils.isEmpty(tableName)) {
				// 单表,表和列一定对应
				// 获取/生成 table数据节点
				TableProperty tableProperty = buildTableProperty(tableName, db, schema,isView);
				DataNode tableDataNode = buildTableNode(schemaNode, tableName, tableProperty);

				// 创建表 field
				String[] primaryKeys = db.getPrimaryKeyColumnNames(schema, tableName);
				RowMetaInterface fields = db.getTableFields(dbMeta.getQuotedSchemaTableCombination(schema, tableName));
				if (fields != null) {
					for (int i = 0; i < fields.size(); i++) {
						ValueMetaInterface field = fields.getValueMeta(i);
						FieldProperty fieldProperty = buildFieldProperty(field, primaryKeys);
						fieldNodes.put( field.getName() ,buildFieldNode(tableDataNode, Const.NVL(field.getComments(), field.getName()), fieldProperty) );
					}
				}

			} else if (!Utils.isEmpty(sql)) {
				// 使用sql创建,可能链表查询,多表对应多列
				PreparedStatement pmt = null;
				sql = StringEscapeHelper.decode(sql);
				try {
					RowMetaInterface rowMeta = db.getQueryFields(sql, false);
					ResultSetMetaData metaData = null ;
					Map<String , DataNode> tableNodeCache = Maps.newHashMap() ;
					String[] primaryKeys = db.getPrimaryKeyColumnNames(schema, tableName);
					for (int i = 0; i < rowMeta.size(); i++) {
						ValueMetaInterface field = rowMeta.getValueMeta(i);
						String table = field.getOrigin() ;
						if( Utils.isEmpty(table) ) {
							//table为空,换一种方式获取
							if( metaData == null ) {
								pmt = db.prepareSQL(sql);
								metaData = pmt.getMetaData();
							}
							
							table = metaData.getTableName(i);
							if (Utils.isEmpty(table)) {
								// TODO 需要sql语义分析
								//List<SQLStatement> statement = SQLUtils.parseStatements(sql, getDbType()) ;
								//statement.get(0);
								continue;
							}
						}
						
						DataNode tableDataNode = null ;
						if( tableNodeCache.containsKey(table)) {
							tableDataNode = tableNodeCache.get(table);
						}else {
							TableProperty tableProperty = buildTableProperty(table, db, schema,isView);
							tableDataNode = buildTableNode(schemaNode, table, tableProperty);
							tableNodeCache.put(table, tableDataNode);
						}
						
						FieldProperty fieldProperty = buildFieldProperty(field, primaryKeys);
						fieldNodes.put( field.getName() , buildFieldNode(tableDataNode, Const.NVL(field.getComments(), field.getName()), fieldProperty) );
					}
				
				} finally {
					if (null != pmt) {
						pmt.close();
					}
				}
			}
		}finally {
			db.disconnect();
		}
		return fieldNodes;
	}


	/**
	 * 生成 文件节点
	 * @param fileType 文件类型,Excel,Hdfs,CSV,...
	 * @param fileName 文件全路径
	 * @param charset
	 * @param compress
	 * @param author
	 * @return
	 * @throws Exception
	 */
	public static DataNode fileNodeParse( String fileType , String fileName, String charset ,String compress,String author ) throws Exception {
		if( Utils.isEmpty(fileName)) {
			return null ;
		}
		FileProperty fileProperty = buildFileProperty(fileName, charset, compress, author) ;
		fileProperty.setType(fileType);
		
		// 获取/生成 系统数据节点
		String systemName = "defaultSystem"  ;
		SystemProperty sysProperty = new SystemProperty(systemName);
		sysProperty.setType(SystemType.File);
		sysProperty.setPosition("cloudetl");
		sysProperty.setOwner("cloudetl");

		DataNode sysDataNode = buildSystemNode(systemName, sysProperty);

		String fileSystemName = fileType ;
		FileSystemProperty fsProperty = new FileSystemProperty(fileSystemName);
		fsProperty.setType(fileType);
		fsProperty.setFormat("");
		fsProperty.setRoot(fileProperty.getRoot());
		fsProperty.setAccess("");
		
		DataNode fsDataNode = buildDataNode( NodeType.FILESYSTEM, NodeLevel.DATABASE,fileSystemName,sysDataNode);
		fsDataNode.setProperties(fsProperty);
		
		
		DataNode fileDataNode = buildDataNode(NodeType.FILE, NodeLevel.TABLE ,fileName ,fsDataNode);
		fileDataNode.setProperties(fileProperty);

		return fileDataNode;
	}


	/**
	 * 生成 接口 数据节点
	 * @param interfaceType 接口类型: Http,Tcp,Udp
	 * @param interfaceUrl 
	 * @param dataSet 数据集: json,xml
	 * @param stepName
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public static Map<String,DataNode> interfaceNodeParse( String interfaceType, String interfaceUrl,  String dataSet, String stepName, Collection<StepFieldDto>  fields) throws Exception {

		String systemName = Const.NVL(interfaceType, "Http");
		SystemProperty sysProperty = new SystemProperty(systemName);
		sysProperty.setType(SystemType.Interface);
		sysProperty.setPosition("cloudetl");
		sysProperty.setOwner("cloudetl");
		
		DataNode sysDataNode = buildSystemNode(systemName, sysProperty);

		String interfaceName = interfaceUrl;
		InterfaceProperty interfaceProperty = new InterfaceProperty(interfaceName);
		interfaceProperty.setProtocol(interfaceType);
		interfaceProperty.setAction(interfaceUrl);
		interfaceProperty.setFormat("");
		interfaceProperty.setEncrypt("");
		interfaceProperty.setCompress("");
		interfaceProperty.setAccount("");
		
		DataNode interfaceNode = buildDataNode( NodeType.INTERFACE, NodeLevel.DATABASE,interfaceName,sysDataNode);
		interfaceNode.setProperties(interfaceProperty);

		String setName =  Const.NVL(dataSet, "json");;
		DataSetProperty dataSetProperty = new DataSetProperty(setName);
		dataSetProperty.setType(setName);
		dataSetProperty.setFormat("");
		dataSetProperty.setCompress("");
		dataSetProperty.setDefinition(null);

		DataNode setNode = buildDataNode( NodeType.DATASET, NodeLevel.TABLE,setName,interfaceNode);
		setNode.setProperties(dataSetProperty);

		Map<String,DataNode> itemNodes = Maps.newHashMap();
		
		// 获取/生成 数据项节点
		if (fields != null && fields.size() > 0) {
			for (StepFieldDto stepFieldDto : fields) {
				if (!stepName.equalsIgnoreCase( stepFieldDto.getOrigin() )) {
					continue;
				}
				String dataItemName = Const.NVL(stepFieldDto.getName(), "");
				if(!Utils.isEmpty(dataItemName)) {
					
					DataNode dataItemDataNode = buildDataNode( NodeType.DATAITEM, NodeLevel.FIELD,dataItemName,setNode);
					
					DataItemProperty dataItemProperty = new DataItemProperty( dataItemName );
					dataItemProperty.setFieldType(stepFieldDto.getType());
					dataItemProperty.setFieldLength(Long.parseLong(Const.NVL(stepFieldDto.getLength(),"0")));
					dataItemProperty.setFieldPricision(Long.parseLong(Const.NVL(stepFieldDto.getPrecision(),"0")));
					dataItemProperty.setFieldFormat(stepFieldDto.getStorageType());
					dataItemProperty.setCompress("");
					dataItemDataNode.setProperties(dataItemProperty);

					itemNodes.put(dataItemName, dataItemDataNode);
				}
			}
		}

		return itemNodes;
	}

}

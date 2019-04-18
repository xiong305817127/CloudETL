package com.ys.idatrix.cloudetl.dto.step.steps.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.databaselookup.DatabaseLookupMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.DBLookupStreamKeysDto;
import com.ys.idatrix.cloudetl.dto.step.parts.StreamLookupvalueDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - Database Lookup(数据库查询). 转换
 * org.pentaho.di.trans.steps.databaselookup.DatabaseLookupMeta
 * 
 * @author JW
 * @since 04-28-2018
 *
 */
@Component("SPDBLookup")
@Scope("prototype")
public class SPDBLookup implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {
	
	private Long databaseId;
	private String connection;
	private Long schemaId ;
	private String schema;
	private Long tableId ;
	private String tableType ;
	private String table;
	private boolean cached;
	private boolean loadingAllDataInCache;
	private int cacheSize;
	private boolean failingOnMultipleResults;
	private boolean eatingRowOnLookupFailure;
	private String orderByClause;
	private List<DBLookupStreamKeysDto> lookupKeys;
	private List<StreamLookupvalueDto> returnValues;

	/**
	 * @return connection
	 */
	public String getConnection() {
		return connection;
	}

	/**
	 * @param connection 要设置的 connection
	 */
	public void setConnection(String connection) {
		this.connection = connection;
	}

	public Long getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}

	public Long getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}

	/**
	 * @return schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @param schema 要设置的 schema
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	/**
	 * @return table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table 要设置的 table
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * @return cached
	 */
	public boolean isCached() {
		return cached;
	}

	/**
	 * @param cached 要设置的 cached
	 */
	public void setCached(boolean cached) {
		this.cached = cached;
	}

	/**
	 * @return loadingAllDataInCache
	 */
	public boolean isLoadingAllDataInCache() {
		return loadingAllDataInCache;
	}

	/**
	 * @param loadingAllDataInCache 要设置的 loadingAllDataInCache
	 */
	public void setLoadingAllDataInCache(boolean loadingAllDataInCache) {
		this.loadingAllDataInCache = loadingAllDataInCache;
	}

	/**
	 * @return cacheSize
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	/**
	 * @param cacheSize 要设置的 cacheSize
	 */
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	/**
	 * @return failingOnMultipleResults
	 */
	public boolean isFailingOnMultipleResults() {
		return failingOnMultipleResults;
	}

	/**
	 * @param failingOnMultipleResults 要设置的 failingOnMultipleResults
	 */
	public void setFailingOnMultipleResults(boolean failingOnMultipleResults) {
		this.failingOnMultipleResults = failingOnMultipleResults;
	}

	/**
	 * @return eatingRowOnLookupFailure
	 */
	public boolean isEatingRowOnLookupFailure() {
		return eatingRowOnLookupFailure;
	}

	/**
	 * @param eatingRowOnLookupFailure 要设置的 eatingRowOnLookupFailure
	 */
	public void setEatingRowOnLookupFailure(boolean eatingRowOnLookupFailure) {
		this.eatingRowOnLookupFailure = eatingRowOnLookupFailure;
	}

	/**
	 * @return orderByClause
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * @param orderByClause 要设置的 orderByClause
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * @return lookupKeys
	 */
	public List<DBLookupStreamKeysDto> getLookupKeys() {
		return lookupKeys;
	}

	/**
	 * @param lookupKeys 要设置的 lookupKeys
	 */
	public void setLookupKeys(List<DBLookupStreamKeysDto> lookupKeys) {
		this.lookupKeys = lookupKeys;
	}

	/**
	 * @return returnValues
	 */
	public List<StreamLookupvalueDto> getReturnValues() {
		return returnValues;
	}

	/**
	 * @param returnValues 要设置的 returnValues
	 */
	public void setReturnValues(List<StreamLookupvalueDto> returnValues) {
		this.returnValues = returnValues;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("lookupKeys", DBLookupStreamKeysDto.class);
		classMap.put("returnValues", StreamLookupvalueDto.class);

		return (SPDBLookup) JSONObject.toBean(jsonObj, SPDBLookup.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPDBLookup to = new SPDBLookup();
		DatabaseLookupMeta databaseLookupMeta = (DatabaseLookupMeta) stepMetaInterface;

		DatabaseMeta databaseMeta = databaseLookupMeta.getDatabaseMeta();
		to.setConnection(databaseMeta == null ? "" : databaseMeta.getDisplayName());
		
		// 获取扩展信息
		to.setDatabaseId( getToAttributeLong(stepMeta, "table.databaseId" ) );
		to.setSchemaId( getToAttributeLong(stepMeta, "table.schemaId" ));
		to.setTableId( getToAttributeLong(stepMeta, "table.tableId" ));
		to.setTableType( getToAttribute(stepMeta, "table.tableType" ));
		
		to.setSchema(databaseLookupMeta.getSchemaName());
		to.setTable(databaseLookupMeta.getTableName());
		to.setCached(databaseLookupMeta.isCached() ? true : false);
		to.setCacheSize(databaseLookupMeta.getCacheSize());
		to.setEatingRowOnLookupFailure(databaseLookupMeta.isEatingRowOnLookupFailure() ? true : false);
		to.setFailingOnMultipleResults(databaseLookupMeta.isFailingOnMultipleResults() ? true : false);
		to.setLoadingAllDataInCache(databaseLookupMeta.isLoadingAllDataInCache() ? true : false);
		to.setOrderByClause(databaseLookupMeta.getOrderByClause());
		
		List<DBLookupStreamKeysDto> streamKeys = new ArrayList<>();
		String[] keys = databaseLookupMeta.getTableKeyField();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				DBLookupStreamKeysDto tof = new DBLookupStreamKeysDto();
				tof.setName(keys[i]);
				tof.setCondition(databaseLookupMeta.getKeyCondition()[i]);
				tof.setField1(databaseLookupMeta.getStreamKeyField1()[i]);
				tof.setField2(databaseLookupMeta.getStreamKeyField2()[i]);
				streamKeys.add(tof);
			}
		}
		to.setLookupKeys(streamKeys);
		
		List<StreamLookupvalueDto> retVals = new ArrayList<>();
		String[] vals = databaseLookupMeta.getReturnValueField();
		if (vals != null) {
			for (int i = 0; i < vals.length; i++) {
				StreamLookupvalueDto tof = new StreamLookupvalueDto();
				tof.setName(vals[i]);
				tof.setRename(databaseLookupMeta.getReturnValueNewName()[i]);
				tof.setDefaultValue(databaseLookupMeta.getReturnValueDefault()[i]);
				tof.setType(databaseLookupMeta.getReturnValueDefaultType()[i]);
				retVals.add(tof);
			}
		}
		to.setReturnValues(retVals);

		return to;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		DatabaseLookupMeta databaseLookupMeta = (DatabaseLookupMeta) stepMetaInterface;
		SPDBLookup jto = (SPDBLookup) po;

		DatabaseMeta d = DatabaseMeta.findDatabase(databases,  jto.getSchemaId()!=null?Long.toString(jto.getSchemaId()):null);
		if( d != null) {
			transMeta.addOrReplaceDatabase(d);
			databaseLookupMeta.setDatabaseMeta(d);
		}
		
		// 获取扩展信息
		setToAttribute(stepMeta, "table.databaseId", jto.getDatabaseId() );
		setToAttribute(stepMeta, "table.schemaId", jto.getSchemaId() );
		setToAttribute(stepMeta, "table.tableId" , jto.getTableId() );
		setToAttribute(stepMeta, "table.tableType", jto.getTableType() );
		
		databaseLookupMeta.setSchemaName(jto.getSchema());
		databaseLookupMeta.setTablename(jto.getTable());
		databaseLookupMeta.setCached(jto.isCached());
		databaseLookupMeta.setCacheSize(jto.getCacheSize());
		databaseLookupMeta.setEatingRowOnLookupFailure(jto.isEatingRowOnLookupFailure());
		databaseLookupMeta.setFailingOnMultipleResults(jto.isFailingOnMultipleResults());
		databaseLookupMeta.setLoadingAllDataInCache(jto.isLoadingAllDataInCache());
		databaseLookupMeta.setOrderByClause(jto.getOrderByClause());
		
		List<DBLookupStreamKeysDto> streamKeys = jto.getLookupKeys();
		List<StreamLookupvalueDto> retVals = jto.getReturnValues();
		if (streamKeys != null && retVals != null) {
			databaseLookupMeta.allocate(streamKeys.size(), retVals.size());
			
			for (int i = 0; i < streamKeys.size(); i++) {
				DBLookupStreamKeysDto key = streamKeys.get(i);
				databaseLookupMeta.getTableKeyField()[i] = Const.NVL(key.getName(), "");
				databaseLookupMeta.getKeyCondition()[i] = Const.NVL(key.getCondition(), "");
				databaseLookupMeta.getStreamKeyField1()[i] = Const.NVL(key.getField1(), "");
				databaseLookupMeta.getStreamKeyField2()[i] = Const.NVL(key.getField2(), "");
			}
			
			for (int i = 0; i < retVals.size(); i++) {
				StreamLookupvalueDto key = retVals.get(i);
				databaseLookupMeta.getReturnValueField()[i] = Const.NVL(key.getName(), "");
				databaseLookupMeta.getReturnValueNewName()[i] = Const.NVL(key.getRename(), "");
				databaseLookupMeta.getReturnValueDefault()[i] = Const.NVL(key.getDefaultValue(), "");
				databaseLookupMeta.getReturnValueDefaultType()[i] = key.getType();
			}
		}
	}
	
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {
		// TODO 自动生成的方法存根
		
	}
	
	@Override
	public int stepType() {
		return 6;
	}

}

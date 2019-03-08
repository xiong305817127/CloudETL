/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.InsertUpdatekeyStreamDto;
import com.ys.idatrix.cloudetl.dto.step.parts.InsertUpdateupdateLookupDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeTransParser;
import com.ys.idatrix.cloudetl.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Insert Update(插入/更新).
 * 转换org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta
 * 
 * @author XH
 * @since 2017年6月7日
 *
 */
@Component("SPInsertUpdate")
@Scope("prototype")
public class SPInsertUpdate implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser {

	private Long databaseId;
	private String connection;
	String commit = "1000";
	boolean updateBypassed = false;
	private Long schemaId;
	String schema;
	private Long tableId;
	String table;
	List<InsertUpdatekeyStreamDto> searchFields;
	List<InsertUpdateupdateLookupDto> updateFields;
	
	private String syncTimeFieldName; //数据插入更新时间域名
	private String syncFlagFieldName; //数据同步标志域名 ,值为 I/U

	/**
	 * @return connection
	 */
	public String getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            要设置的 connection
	 */
	public void setConnection(String connection) {
		this.connection = connection;
	}

	/**
	 * @return commit
	 */
	public String getCommit() {
		return commit;
	}

	/**
	 * @param commit
	 *            要设置的 commit
	 */
	public void setCommit(String commit) {
		this.commit = commit;
	}

	/**
	 * @return updateBypassed
	 */
	public boolean isUpdateBypassed() {
		return updateBypassed;
	}

	/**
	 * @param updateBypassed
	 *            要设置的 updateBypassed
	 */
	public void setUpdateBypassed(boolean updateBypassed) {
		this.updateBypassed = updateBypassed;
	}

	/**
	 * @return schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @param schema
	 *            要设置的 schema
	 */
	public void setSchema(String schema) {
		this.schema = schema;
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

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	/**
	 * @return table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table
	 *            要设置的 table
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * @return searchFields
	 */
	public List<InsertUpdatekeyStreamDto> getSearchFields() {
		return searchFields;
	}

	/**
	 * @param searchFields
	 *            要设置的 searchFields
	 */
	public void setSearchFields(List<InsertUpdatekeyStreamDto> searchFields) {
		this.searchFields = searchFields;
	}

	/**
	 * @return updateFields
	 */
	public List<InsertUpdateupdateLookupDto> getUpdateFields() {
		return updateFields;
	}

	/**
	 * @param updateFields
	 *            要设置的 updateFields
	 */
	public void setUpdateFields(List<InsertUpdateupdateLookupDto> updateFields) {
		this.updateFields = updateFields;
	}

	
	public String getSyncTimeFieldName() {
		return syncTimeFieldName;
	}

	public void setSyncTimeFieldName(String syncTimeFieldName) {
		this.syncTimeFieldName = syncTimeFieldName;
	}

	public String getSyncFlagFieldName() {
		return syncFlagFieldName;
	}

	public void setSyncFlagFieldName(String syncFlagFieldName) {
		this.syncFlagFieldName = syncFlagFieldName;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("searchFields", InsertUpdatekeyStreamDto.class);
		classMap.put("updateFields", InsertUpdateupdateLookupDto.class);

		return (SPInsertUpdate) JSONObject.toBean(jsonObj, SPInsertUpdate.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPInsertUpdate spInsertUpdate = new SPInsertUpdate();
		InsertUpdateMeta insertupdatemeta = (InsertUpdateMeta) stepMetaInterface;

		spInsertUpdate.setDatabaseId( getToAttributeLong(stepMeta, "table.databaseId" ) );
		spInsertUpdate.setSchemaId( getToAttributeLong(stepMeta, "table.schemaId" ));
		spInsertUpdate.setTableId( getToAttributeLong(stepMeta, "table.tableId" ));
		
		spInsertUpdate.setCommit(insertupdatemeta.getCommitSizeVar());
		spInsertUpdate.setConnection(
				insertupdatemeta.getDatabaseMeta() != null ? insertupdatemeta.getDatabaseMeta().getName() : "");
		spInsertUpdate.setTable(insertupdatemeta.getTableName());
		spInsertUpdate.setUpdateBypassed(insertupdatemeta.isUpdateBypassed());
		spInsertUpdate.setSchema(insertupdatemeta.getSchemaName());

		List<InsertUpdatekeyStreamDto> keyStreamList = Lists.newArrayList();
		String[] keyStreams = insertupdatemeta.getKeyStream();
		String[] keyLookups = insertupdatemeta.getKeyLookup();
		String[] keyConditions = insertupdatemeta.getKeyCondition();
		String[] keyStream2s = insertupdatemeta.getKeyStream2();
		for (int i = 0; i < keyStreams.length; i++) {
			InsertUpdatekeyStreamDto insertupdatekeystreamdto = new InsertUpdatekeyStreamDto();
			insertupdatekeystreamdto.setKeyStream1(keyStreams[i]);
			insertupdatekeystreamdto.setKeyLookup(keyLookups[i]);
			insertupdatekeystreamdto.setKeyCondition(keyConditions[i]);
			insertupdatekeystreamdto.setKeyStream2(keyStream2s[i]);
			keyStreamList.add(insertupdatekeystreamdto);
		}
		spInsertUpdate.setSearchFields(keyStreamList);

		List<InsertUpdateupdateLookupDto> updateLookupList = Lists.newArrayList();
		String[] updateLookups = insertupdatemeta.getUpdateLookup();
		String[] updateStreams = insertupdatemeta.getUpdateStream();
		Boolean[] updates = insertupdatemeta.getUpdate();
		for (int i = 0; i < updateLookups.length; i++) {
			InsertUpdateupdateLookupDto insertupdateupdatelookupdto = new InsertUpdateupdateLookupDto();
			insertupdateupdatelookupdto.setUpdateLookup(updateLookups[i]);
			insertupdateupdatelookupdto.setUpdateStream(updateStreams[i]);
			insertupdateupdatelookupdto.setUpdate(updates[i]);
			updateLookupList.add(insertupdateupdatelookupdto);
		}
		spInsertUpdate.setUpdateFields(updateLookupList);
		
		spInsertUpdate.setSyncFlagFieldName(insertupdatemeta.getSyncFlagFieldName());
		spInsertUpdate.setSyncTimeFieldName(insertupdatemeta.getSyncTimeFieldName());

		return spInsertUpdate;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPInsertUpdate spInsertUpdate = (SPInsertUpdate) po;
		InsertUpdateMeta insertupdatemeta = (InsertUpdateMeta) stepMetaInterface;

		setToAttribute(stepMeta, "table.databaseId" ,spInsertUpdate.getDatabaseId() );
		setToAttribute(stepMeta, "table.schemaId" , spInsertUpdate.getSchemaId());
		setToAttribute(stepMeta, "table.tableId" , spInsertUpdate.getTableId() );

		DatabaseMeta d = DatabaseMeta.findDatabase(databases, spInsertUpdate.getConnection());
		if(d != null) {
			transMeta.addOrReplaceDatabase(d);
			insertupdatemeta.setDatabaseMeta(d);
			//OsgiBundleUtils.setOsgiField(insertupdatemeta, "databases", databases, true);
			//insertupdatemeta.setConnection(spInsertUpdate.getConnection());
		}
		
		insertupdatemeta.setSchemaName(spInsertUpdate.getSchema());
		insertupdatemeta.setCommitSize(spInsertUpdate.getCommit());
		insertupdatemeta.setTableName(spInsertUpdate.getTable());
		insertupdatemeta.setUpdateBypassed(spInsertUpdate.isUpdateBypassed());

		String updateLookups[] = new String[spInsertUpdate.getUpdateFields().size()];
		String updateStreams[] = new String[spInsertUpdate.getUpdateFields().size()];
		Boolean updates[] = new Boolean[spInsertUpdate.getUpdateFields().size()];
		for (int i = 0; i < spInsertUpdate.getUpdateFields().size(); i++) {
			InsertUpdateupdateLookupDto iuld = spInsertUpdate.getUpdateFields().get(i);
			updates[i] = iuld.getUpdate();
			updateLookups[i] = iuld.getUpdateLookup();
			updateStreams[i] = iuld.getUpdateStream();
		}
		insertupdatemeta.setUpdate(updates);
		insertupdatemeta.setUpdateLookup(updateLookups);
		insertupdatemeta.setUpdateStream(updateStreams);

		String[] keyStreams = new String[spInsertUpdate.getSearchFields().size()];
		String[] keyLookups = new String[spInsertUpdate.getSearchFields().size()];
		String[] keyConditions = new String[spInsertUpdate.getSearchFields().size()];
		String[] keyStream2s = new String[spInsertUpdate.getSearchFields().size()];
		for (int i = 0; i < spInsertUpdate.getSearchFields().size(); i++) {
			InsertUpdatekeyStreamDto insertupdatekeystreamdto = spInsertUpdate.getSearchFields().get(i);
			keyStreams[i] = insertupdatekeystreamdto.getKeyStream1();
			keyLookups[i] = insertupdatekeystreamdto.getKeyLookup();
			keyConditions[i] = insertupdatekeystreamdto.getKeyCondition();
			keyStream2s[i] = insertupdatekeystreamdto.getKeyStream2();
		}
		insertupdatemeta.setKeyStream(keyStreams);
		insertupdatemeta.setKeyLookup(keyLookups);
		insertupdatemeta.setKeyCondition(keyConditions);
		insertupdatemeta.setKeyStream2(keyStream2s);

		insertupdatemeta.setSyncFlagFieldName(spInsertUpdate.getSyncFlagFieldName());
		insertupdatemeta.setSyncTimeFieldName(spInsertUpdate.getSyncTimeFieldName());
		
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		InsertUpdateMeta insertupdatemeta = (InsertUpdateMeta) stepMeta.getStepMetaInterface();
		DatabaseMeta dbMeta = insertupdatemeta.getDatabaseMeta();
		if (null != dbMeta) {

			String tableName = insertupdatemeta.getTableName();
			String schemaName = insertupdatemeta.getSchemaName();

			if (StringUtils.isBlank(tableName)) {
				return;
			}

			// 增加数据库系统节点
			Map<String, DataNode> fieldNodes = DataNodeUtil.dbFieldNodeParse(dbMeta, schemaName, tableName, null, false ) ;
			sdr.getOutputDataNodes().addAll(fieldNodes.values());
			
			// 增加 流节点 和 输出系统节点 的关系
			String[] outFields = insertupdatemeta.getUpdateLookup();
			String[] inFields = insertupdatemeta.getUpdateStream();
			String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
			List<Relationship> relationships = RelationshipUtil.outputStepRelationship(fieldNodes, null, stepMeta.getName(), from,  outFields, inFields) ;
			sdr.getDataRelationship().addAll(relationships);

		}
	}

	@Override
	public boolean isListenerLineKey(String rowKey ,StepLinesDto result) {
		return ResumeTransParser.outputKey.equals(rowKey)|| ResumeTransParser.updateKey.equals(rowKey);
	}
	
	@Override
	public Long getLinekeyIndex(StepLinesDto result) {
		Long rowLine = result.getRowLine();
		if(rowLine ==  null) {
			rowLine = result.getLinesOutput()+result.getLinesUpdated() ;
		}
		return  rowLine;
	}
	
	@Override
	public int stepType() {
		return 14;
	}
	
}

/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.update.UpdateMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.InsertUpdatekeyStreamDto;
import com.ys.idatrix.quality.dto.step.parts.UpdateupdateLookupDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - Update(更新)
 * 转换 org.pentaho.di.trans.steps.update.UpdateMeta
 * @author JW
 * @since 2017年6月9日
 *
 */
@Component("SPUpdate")
@Scope("prototype")
@Deprecated
public class SPUpdate implements StepParameter, StepDataRelationshipParser {

	String connection;
	boolean skipLookup;
	String commit;
	boolean useBatch;
	boolean errorIgnored;
	String ignoreFlagField;
	String schema;
	String table;
	List<InsertUpdatekeyStreamDto> searchFields;
	List<UpdateupdateLookupDto> updateFields;

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

	/**
	 * @return skipLookup
	 */
	public boolean isSkipLookup() {
		return skipLookup;
	}

	/**
	 * @param skipLookup 要设置的 skipLookup
	 */
	public void setSkipLookup(boolean skipLookup) {
		this.skipLookup = skipLookup;
	}

	/**
	 * @return commit
	 */
	public String getCommit() {
		return commit;
	}

	/**
	 * @param commit 要设置的 commit
	 */
	public void setCommit(String commit) {
		this.commit = commit;
	}

	/**
	 * @return useBatch
	 */
	public boolean isUseBatch() {
		return useBatch;
	}

	/**
	 * @param useBatch 要设置的 useBatch
	 */
	public void setUseBatch(boolean useBatch) {
		this.useBatch = useBatch;
	}

	/**
	 * @return errorIgnored
	 */
	public boolean isErrorIgnored() {
		return errorIgnored;
	}

	/**
	 * @param errorIgnored 要设置的 errorIgnored
	 */
	public void setErrorIgnored(boolean errorIgnored) {
		this.errorIgnored = errorIgnored;
	}

	/**
	 * @return ignoreFlagField
	 */
	public String getIgnoreFlagField() {
		return ignoreFlagField;
	}

	/**
	 * @param ignoreFlagField 要设置的 ignoreFlagField
	 */
	public void setIgnoreFlagField(String ignoreFlagField) {
		this.ignoreFlagField = ignoreFlagField;
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
	 * @return searchFields
	 */
	public List<InsertUpdatekeyStreamDto> getSearchFields() {
		return searchFields;
	}

	/**
	 * @param searchFields 要设置的 searchFields
	 */
	public void setSearchFields(List<InsertUpdatekeyStreamDto> searchFields) {
		this.searchFields = searchFields;
	}

	/**
	 * @return updateFields
	 */
	public List<UpdateupdateLookupDto> getUpdateFields() {
		return updateFields;
	}

	/**
	 * @param updateFields 要设置的 updateFields
	 */
	public void setUpdateFields(List<UpdateupdateLookupDto> updateFields) {
		this.updateFields = updateFields;
	}

	/* 
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("searchFields", List.class);
		classMap.put("updateFields", List.class);
		return (SPUpdate) JSONObject.toBean(jsonObj, SPUpdate.class, classMap);
	}

	/* 
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPUpdate spUpdate = new SPUpdate();
		UpdateMeta updatemeta = (UpdateMeta) stepMetaInterface;

		spUpdate.setCommit(updatemeta.getCommitSizeVar());
		spUpdate.setConnection(updatemeta.getDatabaseMeta() != null ? updatemeta.getDatabaseMeta().getName() : "");

		List<InsertUpdatekeyStreamDto> keyStreamList = Lists.newArrayList();
		String[] keyStreams = updatemeta.getKeyStream();
		String[] keyLookups = updatemeta.getKeyLookup();
		String[] keyConditions = updatemeta.getKeyCondition();
		String[] keyStream2s = updatemeta.getKeyStream2();
		for (int i = 0; i < keyStreams.length; i++) {
			InsertUpdatekeyStreamDto updatekeystreamdto = new InsertUpdatekeyStreamDto();
			updatekeystreamdto.setKeyStream1(keyStreams[i]);
			updatekeystreamdto.setKeyLookup(keyLookups[i]);
			updatekeystreamdto.setKeyCondition(keyConditions[i]);
			updatekeystreamdto.setKeyStream2(keyStream2s[i]);
			keyStreamList.add(updatekeystreamdto);
		}
		spUpdate.setSearchFields(keyStreamList);

		spUpdate.setTable(updatemeta.getTableName());

		List<UpdateupdateLookupDto> updateLookupList = Lists.newArrayList();
		String[] updateLookups = updatemeta.getUpdateLookup();
		String[] updateStreams = updatemeta.getUpdateStream();
		for (int i = 0; i < updateLookups.length; i++) {
			UpdateupdateLookupDto updateupdatelookupdto = new UpdateupdateLookupDto();
			updateupdatelookupdto.setUpdateLookup(updateLookups[i]);
			updateupdatelookupdto.setUpdateStream(updateStreams[i]);
			updateLookupList.add(updateupdatelookupdto);
		}
		spUpdate.setUpdateFields(updateLookupList);
		spUpdate.setSchema(updatemeta.getSchemaName());
		spUpdate.setIgnoreFlagField(updatemeta.getIgnoreFlagField());
		spUpdate.setSkipLookup(updatemeta.isSkipLookup());
		spUpdate.setErrorIgnored(updatemeta.isErrorIgnored());

		spUpdate.setUseBatch(updatemeta.useBatchUpdate());
		return spUpdate;
	}

	/* 
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPUpdate spUpdate = (SPUpdate) po;
		UpdateMeta updatemeta = (UpdateMeta) stepMetaInterface;

		DatabaseMeta d = DatabaseMeta.findDatabase(databases, spUpdate.getConnection());
		if( d != null) {
			transMeta.addOrReplaceDatabase(d);
			updatemeta.setDatabaseMeta(d);
			//updatemeta.setConnection(spUpdate.getConnection());
		}

		
		updatemeta.setCommitSize(spUpdate.getCommit());
		updatemeta.setSkipLookup(spUpdate.isSkipLookup());
		
		String[] keyStreams = new String[spUpdate.getSearchFields().size()];
		String[] keyLookups = new String[spUpdate.getSearchFields().size()];
		String[] keyConditions = new String[spUpdate.getSearchFields().size()];
		String[] keyStream2s = new String[spUpdate.getSearchFields().size()];
		for (int i = 0; i < spUpdate.getSearchFields().size(); i++) {
			InsertUpdatekeyStreamDto updatekeystreamdto = spUpdate.getSearchFields().get(i);
			keyStreams[i] = updatekeystreamdto.getKeyStream1();
			keyLookups[i] = updatekeystreamdto.getKeyLookup();
			keyConditions[i] = updatekeystreamdto.getKeyCondition();
			keyStream2s[i] = updatekeystreamdto.getKeyStream2();
		}
		updatemeta.setKeyStream(keyStreams);
		updatemeta.setKeyLookup(keyLookups);
		updatemeta.setKeyCondition(keyConditions);
		updatemeta.setKeyStream2(keyStream2s);
		
		updatemeta.setTableName(spUpdate.getTable());
		
		String[] updateLookups = new String[spUpdate.getUpdateFields().size()];
		String[] updateStreams = new String[spUpdate.getUpdateFields().size()];
		for (int i = 0; i < spUpdate.getUpdateFields().size(); i++) {
			UpdateupdateLookupDto updateupdatelookupdto = spUpdate.getUpdateFields().get(i);
			updateLookups[i] = updateupdatelookupdto.getUpdateLookup();
			updateStreams[i] = updateupdatelookupdto.getUpdateStream();
		}
		updatemeta.setUpdateLookup(updateLookups);
		updatemeta.setUpdateStream(updateStreams);
		
		updatemeta.setErrorIgnored(spUpdate.isErrorIgnored());
		updatemeta.setSchemaName(spUpdate.getSchema());
		updatemeta.setIgnoreFlagField(spUpdate.getIgnoreFlagField());
		updatemeta.setUseBatchUpdate(spUpdate.isUseBatch());
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		// TODO 自动生成的方法存根
		
	}

}

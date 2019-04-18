package com.ys.idatrix.cloudetl.dto.step.steps.lookup;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.dynamicsqlrow.DynamicSQLRowMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import net.sf.json.JSONObject;

/**
 * Step - DynamicSQLRow(动态SQL). 转换
 * org.pentaho.di.trans.steps.dynamicsqlrow.DynamicSQLRowMeta
 * 
 * @author FBZ
 * @since 13-11-2017
 */
@Component("SPDynamicSQLRow")
@Scope("prototype")
public class SPDynamicSQLRow implements StepParameter, StepDataRelationshipParser {

	/** database connection, 数据库连接 */
	private Long databaseId;
	private String connection;
	private Long schemaId;
	private String schema;

	/** SQL Statement, sql 语句 */
	private String sql;

	/**
	 * SQL field name
	 */
	private String sqlfieldname;

	/** Number of rows to return (0=ALL) */
	private String rowLimit;

	/**
	 * false: don't return rows where nothing is found true: at least return one
	 * source row, the rest is NULL
	 * 
	 * Out Join ?
	 */
	private boolean outerJoin;

	/**
	 * Replace variables
	 */
	private boolean replacevars;

	/**
	 * Query only parameters change
	 */
	public boolean queryonlyonchange;

	public String getConnection() {
		return connection;
	}

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

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSqlfieldname() {
		return sqlfieldname;
	}

	public void setSqlfieldname(String sqlfieldname) {
		this.sqlfieldname = sqlfieldname;
	}

	public String getRowLimit() {
		return rowLimit;
	}

	public void setRowLimit(String rowLimit) {
		this.rowLimit = rowLimit;
	}

	public boolean isOuterJoin() {
		return outerJoin;
	}

	public void setOuterJoin(boolean outerJoin) {
		this.outerJoin = outerJoin;
	}

	public boolean isReplacevars() {
		return replacevars;
	}

	public void setReplacevars(boolean replacevars) {
		this.replacevars = replacevars;
	}

	public boolean isQueryonlyonchange() {
		return queryonlyonchange;
	}

	public void setQueryonlyonchange(boolean queryonlyonchange) {
		this.queryonlyonchange = queryonlyonchange;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPDynamicSQLRow) JSONObject.toBean(jsonObj, SPDynamicSQLRow.class);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		DynamicSQLRowMeta input = (DynamicSQLRowMeta) stepMetaInterface;
		SPDynamicSQLRow ds = new SPDynamicSQLRow();

		ds.setSql(Const.NVL(input.getSql(), ""));
		ds.setRowLimit("" + input.getRowLimit());
		ds.setOuterJoin(input.isOuterJoin());
		ds.setReplacevars(input.isVariableReplace());
		ds.setSqlfieldname(Const.NVL(input.getSQLFieldName(), ""));

		ds.setQueryonlyonchange(input.isQueryOnlyOnChange());

		if (input.getDatabaseMeta() != null) {
			ds.setConnection(input.getDatabaseMeta().getDisplayName());
		}
		
		ds.setDatabaseId( getToAttributeLong(stepMeta, "table.databaseId" ) );
		ds.setSchemaId( getToAttributeLong(stepMeta, "table.schemaId" ));
		ds.setSchema( getToAttribute(stepMeta, "table.schemaName" ));

		return ds;
	}

	/*
	 * decode a step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		DynamicSQLRowMeta input = (DynamicSQLRowMeta) stepMetaInterface;
		SPDynamicSQLRow ds = (SPDynamicSQLRow) po;

		input.setRowLimit(Const.toInt(ds.getRowLimit(), 0));
		input.setSql(ds.getSql());
		input.setSQLFieldName(ds.getSqlfieldname());
		input.setOuterJoin(ds.isOuterJoin());
		input.setVariableReplace(ds.isReplacevars());
		input.setQueryOnlyOnChange(ds.isQueryonlyonchange());
		
		DatabaseMeta d = DatabaseMeta.findDatabase(databases, ds.getSchemaId()!= null? Long.toString(ds.getSchemaId()): null);
		if( d != null) {
			transMeta.addOrReplaceDatabase(d);
			input.setDatabaseMeta(d);
		}
		
		setToAttribute(stepMeta, "table.databaseId" ,ds.getDatabaseId() );
		setToAttribute(stepMeta, "table.schemaId" , ds.getSchemaId());
		setToAttribute(stepMeta, "table.schemaName" , ds.getSchema() );

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		DynamicSQLRowMeta dynamicSQLRowMeta =(DynamicSQLRowMeta) stepMeta.getStepMetaInterface();
		DatabaseMeta dbMeta = dynamicSQLRowMeta.getDatabaseMeta();
		if (null != dbMeta ) {

			String sql = dynamicSQLRowMeta.getSql();
			if (StringUtils.isBlank(sql)) {
				//TODO
				//Map<String, DataNode> fieldsNodes = DataNodeUtil.dbFieldNodeParse(dbMeta, "_dynamicSQL", null, sql, false);
				return;
			}
			

			//TODO  流和系统的关系未知		
		}
	}

}

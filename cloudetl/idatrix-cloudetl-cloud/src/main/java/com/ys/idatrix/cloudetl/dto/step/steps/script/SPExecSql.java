/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.SqlScriptParser;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.sql.ExecSQLMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.SqlArgumentDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Execute SQL script(执行SQL脚本). 转换
 * org.pentaho.di.trans.steps.sql.ExecSQLMeta
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Component("SPExecSQL")
@Scope("prototype")
public class SPExecSql implements StepParameter, StepDataRelationshipParser {

	private Long databaseId;
	private String connection;
	private Long schemaId;
	private String schema;
	private String sql;
	private boolean executedEachInputRow;
	private boolean singleStatement;
	private boolean replaceVariables;
	private boolean setParams;
	private boolean quoteString;
	private String insertField;
	private String updateField;
	private String deleteField;
	private String readField;
	private List<SqlArgumentDto> arguments;

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getConnection() {
		return connection;
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

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public void setExecutedEachInputRow(boolean executedEachInputRow) {
		this.executedEachInputRow = executedEachInputRow;
	}

	public boolean getExecutedEachInputRow() {
		return executedEachInputRow;
	}

	public void setSingleStatement(boolean singleStatement) {
		this.singleStatement = singleStatement;
	}

	public boolean getSingleStatement() {
		return singleStatement;
	}

	public void setReplaceVariables(boolean replaceVariables) {
		this.replaceVariables = replaceVariables;
	}

	public boolean getReplaceVariables() {
		return replaceVariables;
	}

	public void setSetParams(boolean setParams) {
		this.setParams = setParams;
	}

	public boolean getSetParams() {
		return setParams;
	}

	public void setQuoteString(boolean quoteString) {
		this.quoteString = quoteString;
	}

	public boolean getQuoteString() {
		return quoteString;
	}

	public void setInsertField(String insertField) {
		this.insertField = insertField;
	}

	public String getInsertField() {
		return insertField;
	}

	public void setUpdateField(String updateField) {
		this.updateField = updateField;
	}

	public String getUpdateField() {
		return updateField;
	}

	public void setDeleteField(String deleteField) {
		this.deleteField = deleteField;
	}

	public String getDeleteField() {
		return deleteField;
	}

	public void setReadField(String readField) {
		this.readField = readField;
	}

	public String getReadField() {
		return readField;
	}

	public void setArguments(List<SqlArgumentDto> arguments) {
		this.arguments = arguments;
	}

	public List<SqlArgumentDto> getArguments() {
		return arguments;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("arguments", SqlArgumentDto.class);

		return (SPExecSql) JSONObject.toBean(jsonObj, SPExecSql.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPExecSql jes = new SPExecSql();
		ExecSQLMeta execSQLMeta = (ExecSQLMeta) stepMetaInterface;

		jes.setDatabaseId( getToAttributeLong(stepMeta, "table.databaseId" ) );
		jes.setSchemaId( getToAttributeLong(stepMeta, "table.schemaId" ));
		jes.setSchema( getToAttribute(stepMeta, "table.schemaName" ));

		
		List<SqlArgumentDto> arguments = new ArrayList<>();
		if (execSQLMeta.getArguments() != null) {
			for (int i = 0; i < execSQLMeta.getArguments().length; i++) {
				String name = execSQLMeta.getArguments()[i];
				SqlArgumentDto argument = new SqlArgumentDto();
				argument.setName(name);
				arguments.add(argument);
			}
		}
		jes.setArguments(arguments);

		jes.setConnection(execSQLMeta.getDatabaseMeta() == null ? "" : execSQLMeta.getDatabaseMeta().getDisplayName() );
		jes.setDeleteField(execSQLMeta.getDeleteField());
		jes.setExecutedEachInputRow(execSQLMeta.isExecutedEachInputRow());
		jes.setInsertField(execSQLMeta.getInsertField());
		jes.setQuoteString(execSQLMeta.isQuoteString());
		jes.setReadField(execSQLMeta.getReadField());
		jes.setReplaceVariables(execSQLMeta.isReplaceVariables());
		jes.setSetParams(execSQLMeta.isParams());
		jes.setSingleStatement(execSQLMeta.isSingleStatement());
		// jes.setSql(execSQLMeta.getSql());
		jes.setSql(StringEscapeHelper.encode(execSQLMeta.getSql()));
		jes.setUpdateField(execSQLMeta.getUpdateField());

		return jes;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ExecSQLMeta execSQLMeta = (ExecSQLMeta) stepMetaInterface;
		SPExecSql jes = (SPExecSql) po;

		setToAttribute(stepMeta, "table.databaseId" ,jes.getDatabaseId() );
		setToAttribute(stepMeta, "table.schemaId" , jes.getSchemaId());
		setToAttribute(stepMeta, "table.schemaName" , jes.getSchema() );
		
		
		List<SqlArgumentDto> arguments = jes.getArguments();
		if (arguments != null) {
			execSQLMeta.allocate(arguments.size()); // !!!
			for (int i = 0; i < arguments.size(); i++) {
				SqlArgumentDto argument = arguments.get(i);
				execSQLMeta.getArguments()[i] = argument.getName();
			}
		}

		execSQLMeta.setDatabaseMeta(DatabaseMeta.findDatabase(databases, jes.getSchemaId()!= null? Long.toString(jes.getSchemaId()) : null  ));

		execSQLMeta.setDeleteField(jes.getDeleteField());
		execSQLMeta.setExecutedEachInputRow(jes.getExecutedEachInputRow());
		execSQLMeta.setInsertField(jes.getInsertField());
		execSQLMeta.setParams(jes.getSetParams());
		execSQLMeta.setQuoteString(jes.getQuoteString());
		execSQLMeta.setReadField(jes.getReadField());
		execSQLMeta.setSingleStatement(jes.getSingleStatement());
		execSQLMeta.setSql(StringEscapeHelper.decode(jes.getSql()));
		execSQLMeta.setUpdateField(jes.getUpdateField());
		execSQLMeta.setVariableReplacementActive(jes.getReplaceVariables());
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		ExecSQLMeta execSQLMeta = (ExecSQLMeta) stepMeta.getStepMetaInterface();
		DatabaseMeta dbMeta = execSQLMeta.getDatabaseMeta();
		if (null != dbMeta ) {

			String sql = execSQLMeta.getSql();
			if (StringUtils.isBlank(sql)) {
				return;
			}
			Database database = new Database(new SimpleLoggingObject("dbNodeParse", LoggingObjectType.DATABASE, null ),dbMeta);
			try {
				database.connect();

				SqlScriptParser sqlScriptParser = dbMeta.getDatabaseInterface().createSqlScriptParser();
				List<String> statements = sqlScriptParser.split(sql);

				if (statements != null) {
					for (String stat : statements) {
						// Deleting all the single-line and multi-line comments
						// from the string
						stat = sqlScriptParser.removeComments(stat);

						if (!Const.onlySpaces(stat)) {
							sql = Const.trim(stat);
							if (sql.toUpperCase().startsWith("SELECT")) {
								//生成系统节点
								Map<String, DataNode> fieldNodes = DataNodeUtil.dbFieldNodeParse(dbMeta, "_execSQL", null, sql, false) ;
								sdr.getOutputDataNodes().addAll(fieldNodes.values());
								
								//增加 系统节点 和 流节点的关系
								String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
								List<Relationship> relationships = RelationshipUtil.inputStepRelationship(fieldNodes, null, sdr.getOutputStream(),  stepMeta.getName(), from);
								sdr.getDataRelationship().addAll(relationships);
								
							} else {
								// TODO ETL PARSE
							}
						}
					}
				}

				//TODO 输入流节点和数据库节点的关系未建立
				
			} finally {
				database.disconnect();
			}
		}

	}

}

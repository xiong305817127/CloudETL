/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.input;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.tableinput.TableInput;
import org.pentaho.di.trans.steps.tableinput.TableInputData;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Table input(表输入). 转换
 * org.pentaho.di.trans.steps.tableinput.TableInputMeta
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Component("SPTableInput")
@Scope("prototype")
public class SPTableInput implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	private Long databaseId;
	private String connection;
	private Long schemaId;
	private String schema;
	private String sql;
	private String limit;
	private boolean executeEachRow = false;
	private boolean variablesActive = false;
	private boolean lazyConversionActive = false;
	private String lookup;

	// 扩展
	private Long tableId;
	private String tableType;
	private String tableName;

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getConnection() {
		return connection;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getLimit() {
		return limit;
	}

	public void setExecuteEachRow(boolean executeEachRow) {
		this.executeEachRow = executeEachRow;
	}

	public boolean getExecuteEachRow() {
		return executeEachRow;
	}

	public void setVariablesActive(boolean variablesActive) {
		this.variablesActive = variablesActive;
	}

	public boolean getVariablesActive() {
		return variablesActive;
	}

	public void setLazyConversionActive(boolean lazyConversionActive) {
		this.lazyConversionActive = lazyConversionActive;
	}

	public boolean getLazyConversionActive() {
		return lazyConversionActive;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public String getLookup() {
		return lookup;
	}
	
	public Long getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Long getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		return (SPTableInput) JSONObject.toBean(jsonObj, SPTableInput.class);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPTableInput jti = new SPTableInput();
		TableInputMeta tableInputMeta = (TableInputMeta) stepMetaInterface;

		jti.setConnection(tableInputMeta.getDatabaseMeta() != null ? tableInputMeta.getDatabaseMeta().getDisplayName() : "");
		jti.setExecuteEachRow(tableInputMeta.isExecuteEachInputRow());
		jti.setLazyConversionActive(tableInputMeta.isLazyConversionActive());
		jti.setLimit(tableInputMeta.getRowLimit());

		StreamInterface infoStream = tableInputMeta.getStepIOMeta().getInfoStreams().get(0);
		jti.setLookup(infoStream.getStepname());

		// jti.setSql(tableInputMeta.getSQL());
		jti.setSql(StringEscapeHelper.encode(tableInputMeta.getSQL()));
		jti.setVariablesActive(tableInputMeta.isVariableReplacementActive());

		// 获取扩展信息
		jti.setTableId( getToAttributeLong(stepMeta, "table.input.tableId") );
		jti.setTableType( getToAttribute(stepMeta, "table.input.tableType") );
		jti.setTableName( getToAttribute(stepMeta, "table.input.tableName") );
		jti.setSchema( getToAttribute(stepMeta, "table.input.schemaName") );
		jti.setSchemaId( getToAttributeLong(stepMeta, "table.input.schemaId") );
		jti.setDatabaseId( getToAttributeLong(stepMeta, "table.input.databaseId") );

		return jti;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		TableInputMeta tableInputMeta = (TableInputMeta) stepMetaInterface;
		SPTableInput jti = (SPTableInput) po;
		
		DatabaseMeta d = DatabaseMeta.findDatabase(databases, jti.getSchemaId()!= null ? Long.toString(jti.getSchemaId()) : null );
		if( d != null) {
			transMeta.addOrReplaceDatabase(d);
			tableInputMeta.setDatabaseMeta(d);
		}
		
		tableInputMeta.setSQL(StringEscapeHelper.decode(jti.getSql()));
		tableInputMeta.setRowLimit(jti.getLimit());

		tableInputMeta.setExecuteEachInputRow(jti.getExecuteEachRow());
		tableInputMeta.setVariableReplacementActive(jti.getVariablesActive());
		tableInputMeta.setLazyConversionActive(jti.getLazyConversionActive());

		String lookupFromStepname = jti.getLookup();
		// StreamInterface infoStream =
		// tableInputMeta.getStepIOMeta().getInfoStreams().get(0);
		// infoStream.setSubject(lookupFromStepname);
		tableInputMeta.setLookupFromStep(transMeta.findStep(lookupFromStepname));

		// 设置扩展信息
		
		// 获取扩展信息
		setToAttribute(stepMeta, "table.input.tableId", jti.getTableId() );
		setToAttribute(stepMeta, "table.input.tableType", jti.getTableType() );
		setToAttribute(stepMeta, "table.input.tableName", jti.getTableName() );
		setToAttribute(stepMeta, "table.input.schemaName", jti.getSchema() );
		setToAttribute(stepMeta, "table.input.schemaId" , jti.getSchemaId() );
		setToAttribute(stepMeta, "table.input.databaseId", jti.getDatabaseId() );
		
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		TableInputMeta tableInputMeta = (TableInputMeta) stepMeta.getStepMetaInterface();
		DatabaseMeta dbMeta = tableInputMeta.getDatabaseMeta();

		if (null != dbMeta) {

			String sql = tableInputMeta.getSQL();
			// 生成系统节点
			String tableType =getToAttribute(stepMeta, "table.input.tableType");
			String tableName = getToAttribute(stepMeta, "table.input.tableName");
			String schemaName = getToAttribute(stepMeta, "table.input.schemaName") ;

			Map<String, DataNode> dbFieldNodes = DataNodeUtil.dbFieldNodeParse(dbMeta, schemaName, tableName, sql, "view".equalsIgnoreCase(tableType));
			sdr.getInputDataNodes().addAll(dbFieldNodes.values());

			// 增加 系统节点 和 流节点的关系
			String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
			List<Relationship> relationships = RelationshipUtil.inputStepRelationship(dbFieldNodes, null, sdr.getOutputStream(), stepMeta.getName(), from);
			sdr.getDataRelationship().addAll(relationships);
		}
	}

	@Override
	public boolean resumeCacheData(Map<Object,Object> cacheData,StepLinesDto linesDto ,TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception {

		TableInputData data = (TableInputData) stepDataInterface;
		TableInputMeta meta = (TableInputMeta) stepMetaInterface;
		TableInput step = (TableInput) stepInterface;

		if (data.rs == null) {
			Object[] parameters;
			RowMetaInterface parametersMeta;

			// Make sure we read data from source steps...
			if (data.infoStream.getStepMeta() != null) {
				if (meta.isExecuteEachInputRow()) {
					data.rowSet = step.findInputRowSet(data.infoStream.getStepname());
					if (data.rowSet == null) {
						throw new KettleException(
								"Unable to find rowset to read from, perhaps step [" + data.infoStream.getStepname()
										+ "] doesn't exist. (or perhaps you are trying a preview?)");
					}
					parameters = step.getRowFrom(data.rowSet);
					parametersMeta = data.rowSet.getRowMeta();
				} else {
					RowMetaAndData rmad = step.readStartDate(); // Read values in lookup table (look)
					parameters = rmad.getData();
					parametersMeta = rmad.getRowMeta();
				}
			} else {
				parameters = new Object[] {};
				parametersMeta = new RowMeta();
			}

			step.doQuery(parametersMeta, parameters);
			step.first = false;
		}

		if (data.rs != null) {
			data.rs.first();
			data.rs.relative( linesDto.getRowLine().intValue() - 1 );
			data.thisrow = data.db.getRow(data.rs, meta.isLazyConversionActive());

		}
		
		return true;
	}

	@Override
	public int stepType() {
		return 1;
	}
}

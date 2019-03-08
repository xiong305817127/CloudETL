/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.TableOutputFieldDto;
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
 * Step - Table output(表输出). 转换
 * org.pentaho.di.trans.steps.tableoutput.TableOutputMeta
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Component("SPTableOutput")
@Scope("prototype")
public class SPTableOutput implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{

	private Long databaseId;
	private String connection;
	private Long schemaId;
	private String schema;
	private Long tableId;
	private String table;
	private String commit = "1000";
	private boolean truncate = false;
	private boolean ignoreErrors = false ;
	private boolean useBatch = true;
	private boolean specifyFields = true;
	private boolean partitioningEnabled = false;
	private String partitioningField;
	private boolean partitioningDaily = false;
	private boolean partitioningMonthly = true;
	private boolean tablenameInField = false;
	private String tablenameField;
	private boolean tablenameInTable = true;
	private boolean returnKeys = false;
	private String returnField;
	private List<TableOutputFieldDto> fields;

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getConnection() {
		return connection;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getSchema() {
		return schema;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTable() {
		return table;
	}

	public void setCommit(String commit) {
		this.commit = commit;
	}

	public String getCommit() {
		return commit;
	}

	public void setTruncate(boolean truncate) {
		this.truncate = truncate;
	}

	public boolean getTruncate() {
		return truncate;
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

	public void setIgnoreErrors(boolean ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}

	public boolean getIgnoreErrors() {
		return ignoreErrors;
	}

	public void setUseBatch(boolean useBatch) {
		this.useBatch = useBatch;
	}

	public boolean getUseBatch() {
		return useBatch;
	}

	public void setSpecifyFields(boolean specifyFields) {
		this.specifyFields = specifyFields;
	}

	public boolean getSpecifyFields() {
		return specifyFields;
	}

	public void setPartitioningEnabled(boolean partitioningEnabled) {
		this.partitioningEnabled = partitioningEnabled;
	}

	public boolean getPartitioningEnabled() {
		return partitioningEnabled;
	}

	public void setPartitioningField(String partitioningField) {
		this.partitioningField = partitioningField;
	}

	public String getPartitioningField() {
		return partitioningField;
	}

	public void setPartitioningDaily(boolean partitioningDaily) {
		this.partitioningDaily = partitioningDaily;
	}

	public boolean getPartitioningDaily() {
		return partitioningDaily;
	}

	public void setPartitioningMonthly(boolean partitioningMonthly) {
		this.partitioningMonthly = partitioningMonthly;
	}

	public boolean getPartitioningMonthly() {
		return partitioningMonthly;
	}

	public void setTablenameInField(boolean tablenameInField) {
		this.tablenameInField = tablenameInField;
	}

	public boolean getTablenameInField() {
		return tablenameInField;
	}

	public void setTablenameField(String tablenameField) {
		this.tablenameField = tablenameField;
	}

	public String getTablenameField() {
		return tablenameField;
	}

	public void setTablenameInTable(boolean tablenameInTable) {
		this.tablenameInTable = tablenameInTable;
	}

	public boolean getTablenameInTable() {
		return tablenameInTable;
	}

	public void setReturnKeys(boolean returnKeys) {
		this.returnKeys = returnKeys;
	}

	public boolean getReturnKeys() {
		return returnKeys;
	}

	public void setReturnField(String returnField) {
		this.returnField = returnField;
	}

	public String getReturnField() {
		return returnField;
	}

	public void setFields(List<TableOutputFieldDto> fields) {
		this.fields = fields;
	}

	public List<TableOutputFieldDto> getFields() {
		return fields;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fields", TableOutputFieldDto.class);

		return (SPTableOutput) JSONObject.toBean(jsonObj, SPTableOutput.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPTableOutput to = new SPTableOutput();
		TableOutputMeta tableOutputMeta = (TableOutputMeta) stepMetaInterface;

		to.setDatabaseId( getToAttributeLong(stepMeta, "table.databaseId" ) );
		to.setSchemaId( getToAttributeLong(stepMeta, "table.schemaId" ));
		to.setTableId( getToAttributeLong(stepMeta, "table.tableId" ));
		
		to.setCommit(tableOutputMeta.getCommitSize());

		DatabaseMeta databaseMeta = tableOutputMeta.getDatabaseMeta();
		to.setConnection(databaseMeta == null ? "" : databaseMeta.getName());

		List<TableOutputFieldDto> tofList = new ArrayList<>();
		String[] fieldDatabase = tableOutputMeta.getFieldDatabase();
		if (fieldDatabase != null) {
			for (int i = 0; i < fieldDatabase.length; i++) {
				TableOutputFieldDto tof = new TableOutputFieldDto();
				tof.setColumnName(fieldDatabase[i]);
				tof.setStreamName(tableOutputMeta.getFieldStream()[i]);
				tofList.add(tof);
			}
		}
		to.setFields(tofList);

		to.setIgnoreErrors(tableOutputMeta.ignoreErrors() ? true : false);
		to.setPartitioningDaily(tableOutputMeta.isPartitioningDaily() ? true : false);
		to.setPartitioningEnabled(tableOutputMeta.isPartitioningEnabled() ? true : false);
		to.setPartitioningField(tableOutputMeta.getPartitioningField());
		to.setPartitioningMonthly(tableOutputMeta.isPartitioningMonthly() ? true : false);

		to.setReturnField(tableOutputMeta.getGeneratedKeyField());
		to.setReturnKeys(tableOutputMeta.isReturningGeneratedKeys() ? true : false);
		to.setSchema(tableOutputMeta.getSchemaName());
		to.setSpecifyFields(tableOutputMeta.specifyFields() ? true : false);
		to.setTable(tableOutputMeta.getTableName());
		to.setTablenameField(tableOutputMeta.getTableNameField());
		to.setTablenameInField(tableOutputMeta.isTableNameInField() ? true : false);
		to.setTablenameInTable(tableOutputMeta.isTableNameInTable() ? true : false);
		to.setTruncate(tableOutputMeta.truncateTable() ? true : false);
		to.setUseBatch(tableOutputMeta.useBatchUpdate() ? true : false);

		return to;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		TableOutputMeta tableOutputMeta = (TableOutputMeta) stepMetaInterface;
		SPTableOutput jto = (SPTableOutput) po;

		setToAttribute(stepMeta, "table.databaseId" ,jto.getDatabaseId() );
		setToAttribute(stepMeta, "table.schemaId" , jto.getSchemaId());
		setToAttribute(stepMeta, "table.tableId" , jto.getTableId() );
		
		DatabaseMeta d = DatabaseMeta.findDatabase(databases, jto.getConnection());
		if(d != null) {
			transMeta.addOrReplaceDatabase(d);
			tableOutputMeta.setDatabaseMeta(d);
		}
		
		tableOutputMeta.setSchemaName(jto.getSchema());
		tableOutputMeta.setTableName(jto.getTable());
		tableOutputMeta.setCommitSize(jto.getCommit());
		tableOutputMeta.setTruncateTable(jto.getTruncate());
		tableOutputMeta.setIgnoreErrors(jto.getIgnoreErrors());
		tableOutputMeta.setUseBatchUpdate(jto.getUseBatch());

		tableOutputMeta.setSpecifyFields(jto.getSpecifyFields());
		tableOutputMeta.setPartitioningEnabled(jto.getPartitioningEnabled());
		tableOutputMeta.setPartitioningField(jto.getPartitioningField());
		tableOutputMeta.setPartitioningDaily(jto.getPartitioningDaily());
		tableOutputMeta.setPartitioningMonthly(jto.getPartitioningMonthly());

		tableOutputMeta.setTableNameInField(jto.getTablenameInField());
		tableOutputMeta.setTableNameField(jto.getTablenameField());
		tableOutputMeta.setTableNameInTable(jto.getTablenameInTable());
		tableOutputMeta.setReturningGeneratedKeys(jto.getReturnKeys());
		tableOutputMeta.setGeneratedKeyField(jto.getReturnField());

		List<TableOutputFieldDto> fields = jto.getFields();
		if (fields != null) {
			tableOutputMeta.allocate(fields.size());
			// String[] fieldDatabase = new String[fields.size()];
			// String[] fieldStream = new String[fields.size()];
			for (int i = 0; i < fields.size(); i++) {
				TableOutputFieldDto field = fields.get(i);
				tableOutputMeta.getFieldDatabase()[i] = Const.NVL(field.getColumnName(), "");
				tableOutputMeta.getFieldStream()[i] = Const.NVL(field.getStreamName(), "");
				// fieldDatabase[i] = field.getColumnName();
				// fieldStream[i] = field.getStreamName();
			}
			// tableOutputMeta.field.setFieldDatabase(fieldDatabase);
			// tableOutputMeta.setFieldStream(fieldStream);
		}
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		TableOutputMeta tableOutputMeta = (TableOutputMeta) stepMeta.getStepMetaInterface();
		DatabaseMeta dbMeta = tableOutputMeta.getDatabaseMeta();
		if (null != dbMeta) {
			String tableName = tableOutputMeta.getTableName();
			String schemaName = tableOutputMeta.getSchemaName();

			if (StringUtils.isBlank(tableName)) {
				return;
			}

			// 增加数据库系统节点
			Map<String, DataNode> fieldNodes = DataNodeUtil.dbFieldNodeParse(dbMeta, schemaName, tableName, null, false ) ;
			sdr.getOutputDataNodes().addAll(fieldNodes.values());
			
			// 增加 流节点 和 输出系统节点 的关系
			String[] outFields = tableOutputMeta.getFieldDatabase();
			String[] inFields = tableOutputMeta.getFieldStream();
			String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
			List<Relationship> relationships = RelationshipUtil.outputStepRelationship(fieldNodes, null, stepMeta.getName(), from,  outFields, inFields) ;
			sdr.getDataRelationship().addAll(relationships);
			
		}

	}

	@Override
	public boolean isListenerLineKey(String rowKey ,StepLinesDto result) {
		if( ResumeTransParser.readKey.equals(rowKey)  &&( Integer.valueOf(commit) == 0 || result.getLinesRead()%Integer.valueOf(commit) == 0)) {
			return true;
		}
		return false;
	}
	
	@Override
	public Long getLinekeyIndex(StepLinesDto result) {
		Long rowLine = result.getRowLine();
		if(rowLine ==  null) {
			int ys = (int) (Integer.valueOf(commit) == 0 ? 0 : (result.getLinesRead()%Integer.valueOf(commit)));
			rowLine = result.getLinesRead() - ys;
		}
		return rowLine;
	}
	
	@Override
	public int stepType() {
		return 6;
	}

}

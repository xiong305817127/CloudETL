package com.ys.idatrix.quality.dto.step.steps.redundance;

import java.util.List;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * 转换 com.ys.idatrix.quality.steps.redundance.RedundanceMeta
 * 
 * @author XH
 * @since 2017-09-05
 */
@Component("SPRedundance")
@Scope("prototype")
public class SPRedundance implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	public String nodeName; // 节点名称,适配日期

	// 直接从数据库查询
	private Long databaseId;
	private String connection;
	private Long schemaId;
	private String schemaName;
	private Long tableId;
	private String tableType;
	private String tableName;

	// 从上一步读取数据
	private boolean acceptingRows = false;
	private String acceptingStepName;

	private String[] fieldkeys;
	private int detailNum = 100;// -1:无限制 , 0:不保存 , >0:限制条数


	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public boolean isAcceptingRows() {
		return acceptingRows;
	}

	public void setAcceptingRows(boolean acceptingRows) {
		this.acceptingRows = acceptingRows;
	}

	public String getAcceptingStepName() {
		return acceptingStepName;
	}

	public void setAcceptingStepName(String acceptingStepName) {
		this.acceptingStepName = acceptingStepName;
	}

	public String[] getFieldkeys() {
		return fieldkeys;
	}

	public void setFieldkeys(String[] fieldkeys) {
		this.fieldkeys = fieldkeys;
	}

	public int getDetailNum() {
		return detailNum;
	}

	public void setDetailNum(int detailNum) {
		this.detailNum = detailNum;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPRedundance) JSONObject.toBean(jsonObj, SPRedundance.class);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPRedundance spBaseAnalysis = new SPRedundance();

		setToObject(stepMetaInterface, spBaseAnalysis);
		
		DatabaseMeta dbMeta = (DatabaseMeta) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getDatabaseMeta");
		if( dbMeta != null ) {
			spBaseAnalysis.setConnection(dbMeta.getDisplayName());
		}

		// 获取扩展信息
		spBaseAnalysis.setTableId( getToAttributeLong(stepMeta, "table.input.tableId") );
		spBaseAnalysis.setTableType( getToAttribute(stepMeta, "table.input.tableType") );
		spBaseAnalysis.setSchemaId( getToAttributeLong(stepMeta, "table.input.schemaId") );
		spBaseAnalysis.setDatabaseId( getToAttributeLong(stepMeta, "table.input.databaseId") );
		
		return spBaseAnalysis;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPRedundance spBaseAnalysis = (SPRedundance) po;

		setToObject(spBaseAnalysis, stepMetaInterface);
		
		if(!Utils.isEmpty( spBaseAnalysis.getConnection() ) ) {
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setDatabaseMeta", DatabaseMeta.findDatabase( databases, spBaseAnalysis.getSchemaId()!= null ? Long.toString(spBaseAnalysis.getSchemaId()):null ));
		}
		
		setToAttribute(stepMeta, "table.input.tableId", spBaseAnalysis.getTableId() );
		setToAttribute(stepMeta, "table.input.tableType", spBaseAnalysis.getTableType() );
		setToAttribute(stepMeta, "table.input.schemaId" , spBaseAnalysis.getSchemaId() );
		setToAttribute(stepMeta, "table.input.databaseId", spBaseAnalysis.getDatabaseId() );
		
	}

	@Override
	public int stepType() {
		return 5;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

	}

}

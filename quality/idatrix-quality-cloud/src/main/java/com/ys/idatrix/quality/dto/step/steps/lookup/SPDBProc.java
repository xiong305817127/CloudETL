package com.ys.idatrix.quality.dto.step.steps.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.dbproc.DBProcMeta;
import org.pentaho.pms.util.Const;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.DBProcDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.property.TableProperty;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - DBProc(DB存储过程). 转换 org.pentaho.di.trans.steps.dbproc.DBProcMeta
 * 
 * @author FBZ
 * @since 12-11-2017
 */
@Component("SPDBProc")
@Scope("prototype")
public class SPDBProc implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	/** database connection */
	private String connection;
	
	private Long databaseId;
	/** proc.-name to be called, 存储过程 */
	
	private Long schemaId ;
	
	private String schema;
	
	private String procedure;

	/** function arguments, 参数--名称 */
	private List<DBProcDto> argument;

	// /** 参数--方向 IN / OUT / INOUT */
	// private String[] argumentDirection;
	//
	// /** value type for OUT, 参数类型 */
	// private int[] argumentType;

	/** function result: new value name, 返回值名称 */
	private String resultName;

	/** function result: new value type, 返回值类型 */
	private String resultType;

	/** The flag to set auto commit on or off on the connection, 开启自动提交 */
	private boolean autoCommit;

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

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	public List<DBProcDto> getArgument() {
		return argument;
	}

	public void setArgument(List<DBProcDto> argument) {
		this.argument = argument;
	}

	public String getResultName() {
		return resultName;
	}

	public void setResultName(String resultName) {
		this.resultName = resultName;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("argument", DBProcDto.class);

		return (SPDBProc) JSONObject.toBean(jsonObj, SPDBProc.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPDBProc db = new SPDBProc();
		DBProcMeta input = (DBProcMeta) stepMetaInterface;

		if (input.getArgument() != null) {
			db.setArgument(new ArrayList<>(input.getArgument().length));
			DBProcDto dto;
			for (int i = 0; i < input.getArgument().length; i++) {
				dto = new DBProcDto();
				db.getArgument().add(dto);
				dto.setField(Const.NVL(input.getArgument()[i], ""));
				dto.setName(Const.NVL(input.getArgumentDirection()[i], ""));
				dto.setType(ValueMetaFactory.getValueMetaName(input.getArgumentType()[i]));
			}
		}

		if (input.getDatabase() != null) {
			db.setConnection(input.getDatabase().getDisplayName());
		}

		if (input.getProcedure() != null) {
			db.setProcedure(input.getProcedure());
		}
		if (input.getResultName() != null) {
			db.setResultName(input.getResultName());
		}

		db.setResultType(ValueMetaFactory.getValueMetaName(input.getResultType()));

		db.setAutoCommit(input.isAutoCommit());
		
		db.setDatabaseId( getToAttributeLong(stepMeta, "table.databaseId" ) );
		db.setSchemaId( getToAttributeLong(stepMeta, "table.schemaId" ));
		db.setSchema( getToAttribute(stepMeta, "table.schemaName" ));

		return db;
	}

	/*
	 * decode a step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		DBProcMeta input = (DBProcMeta) stepMetaInterface;
		SPDBProc db = (SPDBProc) po;

		int nrargs = null == db.getArgument() ? 0 : db.getArgument().size();
		input.allocate(nrargs);

		DBProcDto dto;
		// CHECKSTYLE:Indentation:OFF
		for (int i = 0; i < nrargs; i++) {
			dto = db.getArgument().get(i);
			input.getArgument()[i] = dto.getField();
			input.getArgumentDirection()[i] = dto.getName();
			input.getArgumentType()[i] = ValueMetaFactory.getIdForValueMeta(dto.getType());
		}
		
		DatabaseMeta d = DatabaseMeta.findDatabase(databases,db.getSchemaId()==null?null:Long.toString(db.getSchemaId() ) );
		if( d != null) {
			transMeta.addOrReplaceDatabase(d);
			input.setDatabase(d);
		}
		input.setProcedure(db.getProcedure());
		input.setResultName(db.getResultName());
		input.setResultType(ValueMetaFactory.getIdForValueMeta(db.getResultType()));
		input.setAutoCommit(db.isAutoCommit());
		
		setToAttribute(stepMeta, "table.databaseId" ,db.getDatabaseId() );
		setToAttribute(stepMeta, "table.schemaId",db.getSchemaId() );
		setToAttribute(stepMeta, "table.schemaName" ,db.getSchema());
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		DBProcMeta dbProcMeta = (DBProcMeta)stepMeta.getStepMetaInterface();
		DatabaseMeta dbMeta = dbProcMeta.getDatabase();
		if (null != dbMeta ) {
			String procName = dbProcMeta.getProcedure();
			if (StringUtils.isBlank(procName)) {
				return;
			}
			
			DataNode schemaNode = DataNodeUtil.dbSchemaNodeParse(dbMeta, "_procedure");
			
			TableProperty tableProperty = new TableProperty(procName);
			tableProperty.setCharset("UTF-8");
			tableProperty.setView(false);
			DataNode tableDataNode = DataNodeUtil.buildTableNode(schemaNode, procName, tableProperty);
			sdr.addInputDataNode(tableDataNode);
			
			//构建 节点与流的关系
			String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
			//输出
			String output =  dbProcMeta.getResultName();
			sdr.addRelationship( RelationshipUtil.buildFieldRelationship(tableDataNode, null, from, null, output) ); //输出流与当前存储过程系统有关系
			
			//输入
			if (dbProcMeta.getArgument() != null) {
				for (int i = 0; i < dbProcMeta.getArgument().length; i++) {
					//TODO 存储过程有输出方向(参数输出)问题
					String inputName = dbProcMeta.getArgument()[i];
					sdr.addRelationship( RelationshipUtil.buildFieldRelationship(null, tableDataNode, from, inputName, "") ); //输入流与当前存储过程系统有关系
					sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, inputName, output) );  //输出流与输入流有关系
				}
			}
		}

	}

	@Override
	public int stepType() {
		return 6;
	}

}

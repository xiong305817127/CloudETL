package com.ys.idatrix.quality.steps.redundance;

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Node;

import com.ys.idatrix.quality.steps.common.NodeTypeEnum;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum.RedundanceTypeFieldEnum;

@Step( id = "Redundance", image = "Redundance.svg", name = "Redundance", description = "Redundance  Description", categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.input",documentationUrl = "",i18nPackageName = "" )
public  class RedundanceMeta extends BaseStepMeta implements StepMetaInterface {

	public String nodeName ; //节点名称,适配日期
	
	//直接从数据库查询
	private DatabaseMeta databaseMeta;
	private String connection;
	private String schemaName;
	private String tableName;
	
	//从上一步读取数据
	private boolean acceptingRows = false;
	private String  acceptingStepName;
	
	 private String[] fieldkeys;
	 private int detailNum = 100 ;// -1:无限制 , 0:不保存 , >0:限制条数
	 
	 
	
	public RedundanceMeta() {
		super();
		setDefault();
	}

	@Override
	public void setDefault() {
		
		nodeName= "yyyy-MM-dd" ;
		fieldkeys = new String[0];
		detailNum = 100;
		acceptingRows = false ;
	}
	
    public void allocate( int nrField ) {
		  fieldkeys = new String[nrField];
    }

	@Override
	public Object clone() {
		
		RedundanceMeta retval = (RedundanceMeta) super.clone();
		if (fieldkeys != null) {
			retval.setFieldkeys(Arrays.copyOf(fieldkeys,fieldkeys.length));
		}
		return retval;
	}
	
	@Override
	public void getFields(RowMetaInterface row, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {

		ValueMetaInterface nn = new ValueMetaString( space.getVariable( nodeName+".NodeName" , RedundanceTypeFieldEnum.nodeName.toString() ));
		nn.setOrigin(name);
		row.addValueMeta(nn);
		
		ValueMetaInterface nt = new ValueMetaString( space.getVariable( nodeName+".NodeType" , RedundanceTypeFieldEnum.nodeType.toString() ));
		nt.setOrigin(name);
		row.addValueMeta(nt);
		
		ValueMetaInterface ds = new ValueMetaString(  space.getVariable( nodeName+".DataSource" , RedundanceTypeFieldEnum.dataSource.toString() ) );
		ds.setLength(100, -1);
		ds.setOrigin(name);
		row.addValueMeta(ds);
		
		ValueMetaInterface fs = new ValueMetaString(  space.getVariable( nodeName+".Fields" , RedundanceTypeFieldEnum.fields.toString() ) );
		fs.setLength(200, -1);
		fs.setOrigin(name);
		row.addValueMeta(fs);
		
		ValueMetaInterface ts = new ValueMetaBigNumber(  space.getVariable( nodeName+".Total" , RedundanceTypeFieldEnum.total.toString() ) );
		ts.setLength(20, -1);
		ts.setOrigin(name);
		row.addValueMeta(ts);
		
		ValueMetaInterface nr = new ValueMetaBigNumber(  space.getVariable( nodeName+".NoRepeat" , RedundanceTypeFieldEnum.noRepeat.toString() ) );
		nr.setLength(20, -1);
		nr.setOrigin(name);
		row.addValueMeta(nr);
		
		ValueMetaInterface dp = new ValueMetaString(  space.getVariable( nodeName+".DetailPath" , RedundanceTypeFieldEnum.detailPath.toString() ) );
		dp.setLength(200, -1);
		dp.setOrigin(name);
		row.addValueMeta(dp);
		
	}

	@Override
	public String getXML() throws KettleException {
		StringBuilder retval = new StringBuilder(1024);

		retval.append("    ").append(XMLHandler.addTagValue("nodeName", nodeName));
		
		retval.append("    ").append(XMLHandler.addTagValue("acceptingRows", acceptingRows));
		retval.append("    ").append(XMLHandler.addTagValue("acceptingStepName", acceptingStepName));
		
		retval.append("    ").append(XMLHandler.addTagValue("connection", Const.NVL(connection, databaseMeta!= null? databaseMeta.getName():"")));
		retval.append("    ").append(XMLHandler.addTagValue("schemaName", schemaName));
		retval.append("    ").append(XMLHandler.addTagValue("tableName", tableName));
		retval.append("    ").append(XMLHandler.addTagValue("detailNum", detailNum));
		
		retval.append( "      <fields>" ).append( Const.CR );
		for ( int i = 0; i < fieldkeys.length; i++ ) {
			retval.append( "        " ).append( XMLHandler.addTagValue( "field", fieldkeys[i] ) );
		}
		retval.append( "      </fields>" ).append( Const.CR );

		return retval.toString();
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {

		nodeName = XMLHandler.getTagValue(stepnode, "nodeName");
		
		acceptingRows = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "acceptingRows"));
		acceptingStepName = XMLHandler.getTagValue(stepnode, "acceptingStepName");
		
		connection = XMLHandler.getTagValue(stepnode, "connection");
		databaseMeta = DatabaseMeta.findDatabase( databases, connection );
		schemaName = XMLHandler.getTagValue(stepnode, "schemaName");
		tableName = XMLHandler.getTagValue(stepnode, "tableName");
		detailNum = Integer.parseInt(XMLHandler.getTagValue(stepnode, "detailNum"));
		   
		Node fieldsNode = XMLHandler.getSubNode( stepnode, "fields" );
	    int nrkeys = XMLHandler.countNodes( fieldsNode, "field" );
	    allocate(nrkeys);
	    for ( int i = 0; i < nrkeys; i++ ) {
	    	Node fnode = XMLHandler.getSubNodeByNr( fieldsNode, "field", i );
	    	fieldkeys[i] = XMLHandler.getNodeValue(fnode);
	    }
	    
	}

	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {

		nodeName = rep.getStepAttributeString(id_step, "nodeName");
		
		acceptingRows = rep.getStepAttributeBoolean(id_step, "acceptingRows");
		acceptingStepName = rep.getStepAttributeString(id_step, "acceptingStepName");

		connection = rep.getStepAttributeString(id_step, "connection");
		databaseMeta = DatabaseMeta.findDatabase( databases, connection );
		schemaName = rep.getStepAttributeString(id_step, "schemaName");		
		tableName = rep.getStepAttributeString(id_step, "tableName");		
		detailNum = ((Long)rep.getStepAttributeInteger(id_step, "detailNum")).intValue();
		
		int nrkeys = rep.countNrStepAttributes( id_step, "field" );
	    allocate( nrkeys );
	    for ( int i = 0; i < nrkeys; i++ ) {
	    	fieldkeys[i] = rep.getStepAttributeString( id_step, i, "field" );
	    }
	}

	@Override
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {

		rep.saveStepAttribute(id_transformation, id_step, "nodeName", nodeName);
		
		rep.saveStepAttribute(id_transformation, id_step, "acceptingRows", acceptingRows);
		rep.saveStepAttribute(id_transformation, id_step, "acceptingStepName", acceptingStepName);
		
		rep.saveStepAttribute(id_transformation, id_step, "connection",  Const.NVL(connection, databaseMeta!= null? databaseMeta.getName():"")) ;
		rep.saveStepAttribute(id_transformation, id_step, "schemaName", schemaName);
		rep.saveStepAttribute(id_transformation, id_step, "tableName", tableName);
		rep.saveStepAttribute(id_transformation, id_step, "detailNum", detailNum);

		for ( int i = 0; i < fieldkeys.length; i++ ) {
			rep.saveStepAttribute( id_transformation, id_step, i, "field", fieldkeys[i] );
		}
		
	}
	
	@Override
	public DatabaseMeta[] getUsedDatabaseConnections() {
		if ( databaseMeta != null ) {
			return new DatabaseMeta[] { databaseMeta };
		} else {
			return super.getUsedDatabaseConnections();
		}
	}

	@Override
	public StepDataInterface getStepData() {
		return new RedundanceData();
	}
	
	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new Redundance(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public DatabaseMeta getDatabaseMeta() {
		return databaseMeta;
	}

	public void setDatabaseMeta(DatabaseMeta databaseMeta) {
		this.databaseMeta = databaseMeta;
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

	public NodeTypeEnum getNodeType() {
		return NodeTypeEnum.REDUNDANCE ;
	}
	
}

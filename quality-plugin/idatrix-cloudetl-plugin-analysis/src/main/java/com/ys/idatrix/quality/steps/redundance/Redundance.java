package com.ys.idatrix.quality.steps.redundance;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import org.apache.commons.lang3.time.FastDateFormat;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.utils.UnixPathUtil;
import com.ys.idatrix.quality.logger.CloudLogUtils;

public class Redundance extends BaseStep implements StepInterface {

	protected RedundanceData data;
	protected RedundanceMeta meta;

	public Redundance(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (RedundanceMeta) smi;
		data = (RedundanceData) sdi;

		if (super.init(smi, sdi)) {

			 try {
				 if ( meta.getDatabaseMeta() == null && !meta.isAcceptingRows()) {
						logError(getStepname()+" 未找到输入数据源.");
						return false;
				 }
				 
				 if ( Utils.isEmpty( meta.getFieldkeys() )) {
						logError(getStepname()+" 未找到计算冗余的关键字列名.");
						return false;
				 }
				 
				 if( getTrans().getParentJob() !=  null ) {
					 data.owner = getTrans().getParentJob().getVariable( "idatrix.owner" ) ;
					 data.execId = getTrans().getParentJob().getVariable( "SegmentingPartRinnigId" ) ;
					 if(Utils.isEmpty(data.owner)) {
						 data.owner =  getTrans().getParentJob().getVariable("idatrix.owner");
						 data.execId =  getTrans().getParentJob().getVariable("idatrix.executionId");
					 }
				 }
				 if(Utils.isEmpty(data.owner)) {
					 data.owner = getTrans().getVariable("idatrix.owner");
					 data.execId = getTrans().getVariable("idatrix.executionId");
				 }
				 
				 data.nodeType = meta.getNodeType().toString() ;
				 data.nodeName = environmentSubstitute(meta.getNodeName());
				 data.nodeName = FastDateFormat.getInstance(data.nodeName).format(new Date());
				 
				 data.fields = meta.getFieldkeys();
				 data.fieldIndexes = new int[ meta.getFieldkeys().length ];
				 data.fieldStr = String.join(",", meta.getFieldkeys() );
				 
				 data.detailPath = UnixPathUtil.unixPath(CloudApp.getInstance().getRepositoryRootFolder() + CloudApp.getInstance().getUserLogsRepositoryPath( data.owner ) + "redundance" )+data.execId+"."+ data.nodeName;
				 
				 if( !meta.isAcceptingRows() ) {
					 data.db = new Database( this, meta.getDatabaseMeta() );
					 data.db.shareVariablesWith( this );
					 if ( getTransMeta().isUsingUniqueConnections() ) {
				         synchronized ( getTrans() ) {
				           data.db.connect( getTrans().getTransactionId(), getPartitionID() );
				         }
				     } else {
				         data.db.connect( getPartitionID() );
				     }
					 data.dataSource =String.join(".", meta.getDatabaseMeta().getName(),meta.getSchemaName(),meta.getTableName() );  
					 
					 return true;
				 }else {
					 data.dataSource = meta.getAcceptingStepName() ;
				 }
			   
			 } catch ( KettleException ke ) {
				 logError( "初始化 冗余率组件异常:"  + ke.getMessage() );
				 return false ;
			 }
			return true;
		}
		return false;

	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		meta = (RedundanceMeta) smi;
		data = (RedundanceData) sdi;

		Object[] r = null;
		try {

			if (first) {
				first = false;
				
				data.outputRowMeta = new RowMeta(); // start from scratch!
				meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);

				if (meta.isAcceptingRows()) {
					if( data.rowSet == null ) {
						data.rowSet = findInputRowSet(meta.getAcceptingStepName());
					}
					
					data.rowRegister = new RowDataRegister(this, data, meta.getDetailNum()) ;
					
					r = getRowFrom(data.rowSet);
					data.inputRowMeta = data.rowSet.getRowMeta();
					for(int i = 0; i< data.fields.length; i++) {
						int idx = data.inputRowMeta.indexOfValue( data.fields[i] );
						if( idx < 0 ) {
							logError( "从步骤[" + meta.getAcceptingStepName() + "]中未找到域[" + data.fields[i] + "].");
							setErrors(1);
							stopAll();
							return false;
						}
						data.fieldIndexes[i] = idx ;
					}
				}else if( data.db != null ){
					//从数据库读取 ,只读取一次
					redundanceFromDatabase();
				}else {
					logError(getStepname()+" 未找到输入数据源.");
					setErrors(1);
					stopAll();
					return false;
				}

			}

			if (meta.isAcceptingRows()) {
				//一行一行读取数据 进行处理
				if( r == null ) {
					r = getRowFrom(data.rowSet);
				}
				
				if (log.isRowLevel()) {
					logRowlevel("Read row #" + getLinesRead() + " : " + getInputRowMeta().getString(r));
				}
				
				//处理一行数据
				redundanceFromRow(r);
				
				if (r != null) { 
					//继续下一条数据
					return true;
				}else {
					return false;
				}
			}
			return false;

		} catch (Exception e) {
			logError("步骤处理异常:",e);
			setErrors(1);
			stopAll();
			return false;
		}
	}


	private void redundanceFromDatabase() throws  Exception {
		
		DatabaseMeta databaseMeta = meta.getDatabaseMeta();
		String schemaTable = databaseMeta.getQuotedSchemaTableCombination( meta.getSchemaName() ,  meta.getTableName()) ;
		
		String countTotalSql =  " SELECT  COUNT(*) FROM "+schemaTable ; // " SELECT  COUNT(*) NUM FROM ETL_EXEC_RECORD " ;
		
		StringBuffer fieldsSql = new StringBuffer();
		for ( int i = 0; i < data.fields.length; i++ ) {
			if ( i != 0 ) {
				fieldsSql.append(", ");
			}
			fieldsSql.append( databaseMeta.quoteField( data.fields[i] ) );
		}
		
		StringBuffer countNoRepeatSql = new StringBuffer( ) ; // " SELECT COUNT(*) NUM FROM ( SELECT NAME , COUNT(*) NUM FROM ETL_EXEC_RECORD  GROUP BY NAME  ) t " ;
		countNoRepeatSql.append(" SELECT COUNT(*) FROM ( SELECT ")
						.append(fieldsSql.toString())
						.append(" , COUNT(*) ")
						.append(" FROM ")
						.append(schemaTable)
						.append("  GROUP BY ")
						.append( fieldsSql.toString() )
						.append("  ) t ");
		
		StringBuffer detailSql = new StringBuffer( ) ; // " SELECT NAME , COUNT(*)  NUM  FROM ETL_EXEC_RECORD GROUP BY NAME HAVING COUNT(*) >1 ORDER BY NUM DESC  " ;
		detailSql.append(" SELECT ")
		.append(fieldsSql.toString())
		.append(" , COUNT(*) NUM ")
		.append(" FROM ")
		.append(schemaTable)
		.append("  GROUP BY ")
		.append( fieldsSql.toString() )
		.append(" HAVING COUNT(*) >1 ORDER BY NUM DESC  ");
		
		 //查询total
		 PreparedStatement ps = null ;
		 try {
			 ps = data.db.getConnection().prepareStatement( databaseMeta.stripCR( countTotalSql ) );
			 Object[] totals = data.db.getLookup( ps );
			 if( totals != null && totals.length >0) {
				 ValueMetaInterface retMeta = data.db.getReturnRowMeta().getValueMeta( 0 );
				 data.total = retMeta.getInteger(totals[0]);
			 }
		 }finally {
			 data.db.closePreparedStatement(ps);
			 ps = null;
		 }
		 //查询 不重复数量
		 try {
			 ps = data.db.getConnection().prepareStatement( databaseMeta.stripCR( countNoRepeatSql.toString() ) );
			 Object[] norepeats = data.db.getLookup( ps );
			 if( norepeats != null && norepeats.length >0) {
				 ValueMetaInterface retMeta = data.db.getReturnRowMeta().getValueMeta( 0 );
				 data.noRepeat = retMeta.getInteger(norepeats[0]);
			 }
		 }finally {
			 data.db.closePreparedStatement(ps);
			 ps = null;
		 }
		
		 ResultSet rs = null ;
		if( meta.getDetailNum() != 0 ) {
			//查询 重复详细数据
			 try {
				 ps = data.db.getConnection().prepareStatement( databaseMeta.stripCR( detailSql.toString() ) );
				 rs = ps.executeQuery();
				 
				 RowMetaInterface retMeta = data.db.getRowInfo( rs.getMetaData(), false, false );
				 Object[] detail = data.db.getRow( rs, null, retMeta );
				 
				 int limitNumber =  meta.getDetailNum() ;
				 StringBuffer detailBuffer = new StringBuffer(); 
				 while( detail != null ) {
					 
					 for ( int i = 0; i < retMeta.size(); i++ ) {
						 if ( i > 0 ) {
							 detailBuffer.append( ", " );
						 }
						 
						 String v = retMeta.getString( detail, i ) ;
						 if( v == null || "NULL".equalsIgnoreCase(v.trim()) ) {
							 v = "NULL_VALUE" ;
						 }else if( "".equals(v.trim())) {
							 v = "EMPTY_VALUE" ;
						 }
						 detailBuffer.append( v );
					 }
					 
					 if( detailBuffer.length() > 5000 ) {
						 //插入会在最后自动加换行
						 CloudLogUtils.insertLog( data.detailPath , detailBuffer.toString());
						 detailBuffer.setLength(0);
					 }else {
						 detailBuffer.append("\n");
					 }
					 if( limitNumber > 0) {
						 limitNumber--;
					 }
					 if( limitNumber == 0) {
						 break ;
					 }
					 detail = data.db.getRow( rs, null, retMeta );
				 }
				 
				 if( detailBuffer.length() > 0 ) {
					 CloudLogUtils.insertLog( data.detailPath , detailBuffer.toString());
				 }
				 
			 }finally {
				 data.db.closePreparedStatement(ps);
				 data.db.closeQuery(rs);
			 }

		}
		
		//推送数据到下一步
		putDataTopRow( ) ;
	}
	
	
	private void redundanceFromRow( Object[] row  ) throws Exception {
		if( row == null ) {
			//数据结束, 推送数据
			data.rowRegister.saveDetailData();
			//推送数据到下一步
			putDataTopRow( ) ;
			return ;
		}
		
		StringBuilder rowBuffer = new StringBuilder();
		 for ( int i = 0; i < data.fieldIndexes.length; i++ ) {
			 if ( i > 0 ) {
				 rowBuffer.append( ", " );
			 }
			 rowBuffer.append( data.inputRowMeta.getString( row, data.fieldIndexes[i] ) );
		 }
		
		 String fieldKey = rowBuffer.toString() ;
		 data.rowRegister.addRowField(fieldKey);
		
	}
	
	private void putDataTopRow( ) throws KettleStepException {
		
		Object[] r = new Object[] {data.nodeName , data.nodeType ,data.dataSource , data.fieldStr ,new BigDecimal(data.total),new BigDecimal(data.noRepeat),data.detailPath} ;
		putRow( data.outputRowMeta, r );
		// no more input to be expected...
		setOutputDone();
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (RedundanceMeta) smi;
		data = (RedundanceData) sdi;

		data.outputRowMeta = null;
		data.inputRowMeta = null ;
		
		if( data.db != null ) {
			data.db.disconnect();
		}
		
		if( data.rowRegister != null) {
			data.rowRegister.clear();
		}
		
		data.fields = null ;
		data.fieldIndexes = null ;

		super.dispose(smi, sdi);
	}
	
	
	

}

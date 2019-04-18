package com.ys.idatrix.quality.steps.report;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.google.common.collect.Maps;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper;
import com.ys.idatrix.quality.logger.CloudLogger;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum.CommonTypeFieldEnum;
import com.ys.idatrix.quality.steps.common.NodeTypeStepHandler;

public class AnalysisReport extends BaseStep implements StepInterface {

	protected AnalysisReportData data;
	protected AnalysisReportMeta meta;

	public AnalysisReport(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (AnalysisReportMeta) smi;
		data = (AnalysisReportData) sdi;

		if (super.init(smi, sdi)) {
			if (meta.getCommitSize() != null && meta.getCommitSize() > 0) {
				// 设置批量提交
				DatabaseHelper.setCommitSize(meta.getCommitSize().intValue());
			}

//			data.nodeName = environmentSubstitute(meta.getNodeName());
//			data.nodeName = FastDateFormat.getInstance(data.nodeName).format(new Date());
			
			data.handlers = Maps.newConcurrentMap() ;
			
			if( getTrans().getParentJob() !=  null ) {
				data.execId = getTrans().getParentJob().getVariable( "SegmentingPartRinnigId" ) ;
				data.taskName = getTrans().getParentJob().getVariable( "idatrix.taskName" ) ;
				if(Utils.isEmpty(data.execId)) {
					data.execId =  getTrans().getParentJob().getVariable("idatrix.executionId");
					data.taskName =  getTrans().getParentJob().getVariable("idatrix.taskName");
				}
			}
			if(Utils.isEmpty(data.execId)) {
				data.execId = getTrans().getVariable("idatrix.executionId");
				data.taskName = getTrans().getVariable("idatrix.taskName");
			}

			return true;
		}
		return false;

	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		meta = (AnalysisReportMeta) smi;
		data = (AnalysisReportData) sdi;

		Object[] r = getRow(); // get row, set busy!

		try {

			if (first) {
				first = false;
				
				if ( r == null ) { // no more input to be expected...
					logBasic("未读取到数据,处理结束.");
			        setOutputDone();
			        return false;
			    }

				data.outputRowMeta = getInputRowMeta().clone();
				meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
			}

			if (r == null) { // no more input to be expected...
				setOutputDone();
				return false;
			}
			
			if (log.isRowLevel()) {
				logRowlevel("Read row #" + getLinesRead() + " : " + getInputRowMeta().getString(r));
			}
			
			int inputNodeTypeIndex = getInputRowMeta().indexOfValue( CommonTypeFieldEnum.nodeType );
			if ( inputNodeTypeIndex < 0) {
				handleException("inputNodeType 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + CommonTypeFieldEnum.nodeType ));
			}
			
			String nodeType = getInputRowMeta().getString(r, inputNodeTypeIndex);
			NodeTypeEnum nodeEnum = NodeTypeEnum.getNodeTypeEnum(nodeType) ;
			NodeTypeStepHandler handler = getNodeStepHandler(nodeEnum);
			if( handler != null ) {
				handler.processRow(nodeEnum, this, meta, data, r );
			}else {
				logBasic("未找到["+nodeType+"]的节点处理器.");
			}
			
			putRow( data.outputRowMeta, r );
			
			return true;

		} catch (Exception e) {
			DatabaseHelper.rollbackBatchCommit();
			logError("步骤处理异常:",e);
			setErrors(1);
			stopAll();
			return false;
		}

	}
	
	public NodeTypeStepHandler getNodeStepHandler(NodeTypeEnum nodeEnum ) {
		if(  nodeEnum != null ) {
			NodeTypeStepHandler instance = null ;
			if( data.handlers.containsKey( nodeEnum.getHandlerClass() )) {
				instance = data.handlers.get(nodeEnum.getHandlerClass());
			}
			if( instance == null ) {
				instance = nodeEnum.getHandlerInstance() ;
				data.handlers.put( nodeEnum.getHandlerClass(), instance) ;
			}
			return instance ;
		}
		 return null ;
		
	}

	public void handleException(String message, Exception e) throws Exception {

		message = Const.NVL(message, "处理异常:");

		if (!meta.isIgnoreError()) {
			logError(message, e);
			throw e;
		} else {
			logBasic("[WARN]忽略异常! " + message + "," + CloudLogger.getExceptionMessage(e));
		}
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (AnalysisReportMeta) smi;
		data = (AnalysisReportData) sdi;

		if (meta.getCommitSize() != null && meta.getCommitSize() > 0) {
			// 批量提交的最后提交
			DatabaseHelper.closeBatchCommit();
		}
		
		if( data.handlers != null ) {
			data.handlers.values().forEach(handler -> {
				handler.dispose();
			});
			data.handlers.clear();
			data.handlers = null;
		}

		super.dispose(smi, sdi);
	}

}

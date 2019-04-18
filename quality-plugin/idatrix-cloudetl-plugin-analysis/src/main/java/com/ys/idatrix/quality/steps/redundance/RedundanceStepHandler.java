package com.ys.idatrix.quality.steps.redundance;

import org.pentaho.di.core.exception.KettleStepException;

import com.ys.idatrix.quality.analysis.dao.NodeRecordDao;
import com.ys.idatrix.quality.analysis.dao.NodeResultDao;
import com.ys.idatrix.quality.analysis.dto.NodeRecordDto;
import com.ys.idatrix.quality.analysis.dto.NodeResultDto;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum;
import com.ys.idatrix.quality.steps.common.NodeTypeStepHandler;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum.RedundanceTypeFieldEnum;
import com.ys.idatrix.quality.steps.report.AnalysisReport;
import com.ys.idatrix.quality.steps.report.AnalysisReportData;
import com.ys.idatrix.quality.steps.report.AnalysisReportMeta;

public class RedundanceStepHandler   extends NodeTypeStepHandler {

	private int dataSourceIndex;
	private int fieldsIndex;
	private int totalIndex;
	private int noRepeatIndex;
	private int detailPathIndex;
	private int nodeNameIndex;

	@Override
	public void processRow(  NodeTypeEnum nodeType, AnalysisReport report , AnalysisReportMeta meta, AnalysisReportData data ,Object[] row ) throws Exception {
		this.data = data ;
		this.meta = meta;
		this.report =report;

		if( first ) {
			first = false ;
			nodeNameIndex = report.getInputRowMeta().indexOfValue( RedundanceTypeFieldEnum.nodeName ) ;
			if ( nodeNameIndex < 0) {
				report.handleException("inputFieldName 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + RedundanceTypeFieldEnum.nodeName ));
			}
			dataSourceIndex = report.getInputRowMeta().indexOfValue( RedundanceTypeFieldEnum.dataSource.toString() ) ;
			if ( dataSourceIndex < 0) {
				report.handleException("inputFieldName 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + RedundanceTypeFieldEnum.dataSource.toString() ));
			}
			fieldsIndex = report.getInputRowMeta().indexOfValue( RedundanceTypeFieldEnum.fields.toString() );
			if ( fieldsIndex < 0) {
				report.handleException("inputNodeName 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + RedundanceTypeFieldEnum.fields.toString() ));
			}
			totalIndex = report.getInputRowMeta().indexOfValue( RedundanceTypeFieldEnum.total.toString() );
			if ( totalIndex < 0) {
				report.handleException("inputReferenceValue 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + RedundanceTypeFieldEnum.total.toString() ));
			}
			noRepeatIndex = report.getInputRowMeta().indexOfValue( RedundanceTypeFieldEnum.noRepeat.toString()  );
			if ( noRepeatIndex < 0) {
				report.handleException("inputResult 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + RedundanceTypeFieldEnum.noRepeat.toString() ));
			}
			detailPathIndex = report.getInputRowMeta().indexOfValue( RedundanceTypeFieldEnum.detailPath.toString()  );
			if ( detailPathIndex < 0) {
				report.handleException("inputResult 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + RedundanceTypeFieldEnum.detailPath.toString() ));
			}
			
		}

		String nodeName = report.getInputRowMeta().getString(row, nodeNameIndex);
		String dataSource = report.getInputRowMeta().getString(row, dataSourceIndex);
		String detailPath = report.getInputRowMeta().getString(row, detailPathIndex);
		String fields = report.getInputRowMeta().getString(row, fieldsIndex);
		long total = report.getInputRowMeta().getInteger(row, totalIndex);
		long noRepeat = report.getInputRowMeta().getInteger(row, noRepeatIndex);

		updateRecord(nodeName, nodeType.toString() );
		updateResult(nodeName, nodeType.toString(), dataSource, detailPath, fields, total, noRepeat);
	}
	
	public void updateRecord(String nodeName,String nodeType ) throws Exception {

		NodeRecordDto nrecord = new NodeRecordDto();
		nrecord.setAnalysisName(data.taskName);
		nrecord.setExecId(data.execId);
		nrecord.setNodId(nodeName);
		nrecord.setNodType(nodeType);
		nrecord.setSuccNum(1);
		nrecord.setErrNum(0);

		//数据库新增
		NodeRecordDao.getInstance().insertRecord(nrecord);
	}

	public void updateResult(String nodeName,String nodeType,String dataSource,String detailPath, String fields,long total,long noRepeat ) throws Exception {
		
		NodeResultDto nresult = new NodeResultDto();
		nresult.setAnalysisName(data.taskName);
		nresult.setExecId(data.execId);
		nresult.setNodId(nodeName);
		nresult.setNodeType(nodeType);
		nresult.setFieldName(fields);
		nresult.setReferenceValue(dataSource);
		nresult.setMatch(true);
		nresult.setNumber(total);
		
		nresult.setOptional1(noRepeat+"");
		nresult.addOption("detailPath", detailPath);
		nresult.addOption("total", total);
		nresult.addOption("noRepeat", noRepeat);
		
		//数据库新增
		NodeResultDao.getInstance().insertResult(nresult);
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}

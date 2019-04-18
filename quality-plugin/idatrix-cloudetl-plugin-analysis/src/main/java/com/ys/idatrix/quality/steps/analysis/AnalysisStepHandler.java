package com.ys.idatrix.quality.steps.analysis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.pentaho.di.core.exception.KettleStepException;
import com.ys.idatrix.quality.analysis.dao.NodeRecordDao;
import com.ys.idatrix.quality.analysis.dao.NodeResultDao;
import com.ys.idatrix.quality.analysis.dto.NodeRecordDto;
import com.ys.idatrix.quality.analysis.dto.NodeResultDto;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum.AnalysisTypeFieldEnum;
import com.ys.idatrix.quality.steps.common.NodeTypeStepHandler;
import com.ys.idatrix.quality.steps.report.AnalysisReport;
import com.ys.idatrix.quality.steps.report.AnalysisReportData;
import com.ys.idatrix.quality.steps.report.AnalysisReportMeta;

public class AnalysisStepHandler extends NodeTypeStepHandler {

	Map<String ,NodeRecordDto> recordCache ; //节点对应的记录
	Map<String ,NodeResultDto> resultCache ; //域名-参考值对应的记录
	
	int fieldNameIndex;
//	int fieldValueIndex;
	int nodeNameIndex;
	int nodeTypeIndex;
	int referenceValueIndex;
	int resultIndex;
	int dictNameIndex =-1 ;
	
	public AnalysisStepHandler() {
		super();
		recordCache = new ConcurrentHashMap<>();
		resultCache = new ConcurrentHashMap<>();
	}


	@Override
	public void processRow(  NodeTypeEnum nodeType, AnalysisReport report , AnalysisReportMeta meta, AnalysisReportData data ,Object[] row ) throws Exception {

		this.data = data ;
		this.meta = meta;
		this.report =report;
		
		if( first ) {
			first = false ;
			
			fieldNameIndex = report.getInputRowMeta().indexOfValue( AnalysisTypeFieldEnum.fieldName.toString() ) ;
			if ( fieldNameIndex < 0) {
				report.handleException("inputFieldName 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + AnalysisTypeFieldEnum.fieldName.toString() ));
			}
//			fieldValueIndex = report.getInputRowMeta().indexOfValue( AnalysisTypeFieldEnum.value.toString() ) ;
//			if ( fieldValueIndex < 0) {
//				report.handleException("inputFieldValue 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + AnalysisTypeFieldEnum.value.toString() ));
//			}
			nodeNameIndex = report.getInputRowMeta().indexOfValue( AnalysisTypeFieldEnum.nodeName );
			if ( nodeNameIndex < 0) {
				report.handleException("inputNodeName 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + AnalysisTypeFieldEnum.nodeName ));
			}
			referenceValueIndex = report.getInputRowMeta().indexOfValue( AnalysisTypeFieldEnum.referenceValue.toString() );
			if ( referenceValueIndex < 0) {
				report.handleException("inputReferenceValue 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + AnalysisTypeFieldEnum.referenceValue.toString() ));
			}
			resultIndex = report.getInputRowMeta().indexOfValue( AnalysisTypeFieldEnum.result.toString()  );
			if ( resultIndex < 0) {
				report.handleException("inputResult 域名不存在.", new KettleStepException("Unable to find the specified fieldname :" + AnalysisTypeFieldEnum.result.toString() ));
			}
			
		}

		String nodeName = report.getInputRowMeta().getString(row, nodeNameIndex);
		String fieldName = report.getInputRowMeta().getString(row, fieldNameIndex);
		//String fieldValue  = report.getInputRowMeta().getString(row, fieldValueIndex);
		String referenceValue = report.getInputRowMeta().getString(row, referenceValueIndex);
		boolean result = report.getInputRowMeta().getBoolean(row , resultIndex);

		updateRecord(nodeName,nodeType.toString() , result);
		
		String optional1 = null ;
		if( NodeTypeEnum.CHARACTER.equals(nodeType)) {
			//如果是标准值  附加额外的字段 字典名称
			if( dictNameIndex == -1 ) {
				dictNameIndex = report.getInputRowMeta().indexOfValue( "dictName" );
			}
			if( dictNameIndex != -1 ) {
				optional1 = report.getInputRowMeta().getString(row , dictNameIndex);
			}
		}
		
		updateResult(nodeName, nodeType.toString() , fieldName, referenceValue,result,optional1);
		
	}

	public void updateResult(String nodeName,String nodeType,String fieldName,String referenceValue ,boolean isMatch,String optional1) throws Exception {
		
		//只有检测通过时才需要更新
		
		String key = nodeName+"-"+fieldName+"-"+referenceValue ;
		if( resultCache.containsKey(key) ) {
			NodeResultDto nresult = resultCache.get(key) ;
			nresult.increaseNumber();
			NodeResultDao.getInstance().updateNumber(nresult);
			return ;
		}
		
		NodeResultDto nresult = new NodeResultDto();
		nresult.setAnalysisName(data.taskName);
		nresult.setExecId(data.execId);
		nresult.setNodId(nodeName);
		nresult.setNodeType(nodeType);
		nresult.setFieldName(fieldName);
		nresult.setReferenceValue(referenceValue);
		nresult.setMatch(isMatch);
		nresult.setOptional1(optional1);
		nresult.increaseNumber();
	
		//数据库新增
		NodeResultDao.getInstance().insertResult(nresult);
		resultCache.put(key, nresult);
		
	}
		
	public void updateRecord(String nodeName,String nodeType ,boolean result) throws Exception {
		if( recordCache.containsKey(nodeName) ) {
			NodeRecordDto nrecord = recordCache.get(nodeName) ;
			if(result) {
				nrecord.increaseSuccessNum();
				NodeRecordDao.getInstance().updateCorrectNumber(nrecord);
			}else {
				nrecord.increaseErrorNum();
				NodeRecordDao.getInstance().updateErrorNumber(nrecord);
			}
			return ;
		}
		
		NodeRecordDto nrecord = new NodeRecordDto();
		nrecord.setAnalysisName(data.taskName);
		nrecord.setExecId(data.execId);
		nrecord.setNodId(nodeName);
		nrecord.setNodType(nodeType);
		if(result) {
			nrecord.increaseSuccessNum();
		}else {
			nrecord.increaseErrorNum();
		}
		//数据库新增
		NodeRecordDao.getInstance().insertRecord(nrecord);
		recordCache.put(nodeName, nrecord);
	}
	
	@Override
	public void dispose() {

		resultCache.clear();
		recordCache.clear();
		
	}

}

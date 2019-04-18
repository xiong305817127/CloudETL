package com.ys.idatrix.quality.steps.common;

import com.ys.idatrix.quality.steps.report.AnalysisReport;
import com.ys.idatrix.quality.steps.report.AnalysisReportData;
import com.ys.idatrix.quality.steps.report.AnalysisReportMeta;

public abstract class NodeTypeStepHandler {
	
	public AnalysisReportMeta meta ;
	public AnalysisReportData data ;
	public AnalysisReport report ;
	
	public boolean first = true ;
	
	public abstract void processRow( NodeTypeEnum nodeType, AnalysisReport report , AnalysisReportMeta meta, AnalysisReportData data ,Object[] row) throws Exception ;

	public abstract void dispose();
	
	
	
}



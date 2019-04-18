package com.ys.idatrix.quality.steps.report;

import java.util.Map;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.ys.idatrix.quality.steps.common.NodeTypeStepHandler;

public class AnalysisReportData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;

	public String taskName ;
	public String execId ;

	public Map<Class<? extends NodeTypeStepHandler>,NodeTypeStepHandler> handlers;
	
}

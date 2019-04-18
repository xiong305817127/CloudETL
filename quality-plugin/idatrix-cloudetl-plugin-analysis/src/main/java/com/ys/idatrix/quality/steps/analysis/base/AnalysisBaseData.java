package com.ys.idatrix.quality.steps.analysis.base;

import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class AnalysisBaseData  extends BaseStepData implements StepDataInterface {

	public String  nodeType ;
	public String  nodeName ;
	
	public RowMeta outputRowMeta  ;
	public int[] fieldIndexes ;
}

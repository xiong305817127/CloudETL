package com.ys.idatrix.quality.steps.analysis.custom;

import java.util.regex.Pattern;

import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;

import com.ys.idatrix.quality.steps.analysis.base.AnalysisBase;

public class CustomAnalysis extends AnalysisBase implements StepInterface {

	public CustomAnalysis(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public void validateFields(String fieldName, String valueData, Object smi, Object[] result) throws Exception {
		super.validateFields(fieldName, valueData, smi, result);
	}

	@Override
	public boolean validate(String valueData, String format, boolean isStandard) {

		Pattern p = Pattern.compile(format);
		return p.matcher(valueData).matches();

	}

	@Override
	public String guessReference(String valueData) {
		return null;
	}

}

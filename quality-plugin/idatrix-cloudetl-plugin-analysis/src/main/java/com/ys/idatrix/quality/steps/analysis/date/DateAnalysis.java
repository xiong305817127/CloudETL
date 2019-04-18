package com.ys.idatrix.quality.steps.analysis.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;

import com.ys.idatrix.quality.steps.analysis.base.AnalysisBase;


public class DateAnalysis extends AnalysisBase implements StepInterface {

	public DateAnalysis(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public  boolean validate(String valueData ,String format,boolean isStandard)  {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		try {
			// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
			simpleDateFormat.setLenient(false);
			simpleDateFormat.parse(valueData);
			return true ;
		} catch (ParseException e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return false;
		} 
	}

	@Override
	public String guessReference(String valueData) {
		return null;
	}

	
}

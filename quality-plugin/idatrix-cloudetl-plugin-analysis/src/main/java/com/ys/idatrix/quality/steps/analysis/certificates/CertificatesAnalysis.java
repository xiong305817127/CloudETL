package com.ys.idatrix.quality.steps.analysis.certificates;

import java.util.regex.Pattern;

import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;

import com.ys.idatrix.quality.steps.analysis.base.AnalysisBase;
import com.ys.idatrix.quality.steps.analysis.certificates.CertificatesAnalysisMeta.STANDARD_CODE;

public class CertificatesAnalysis extends AnalysisBase implements StepInterface {

	public CertificatesAnalysis(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean validate(String valueData, String format, boolean isStandard) {

		boolean result = false;
		if ((STANDARD_CODE.Card18.toString().equals(format) && valueData.length() == 18)
				|| (STANDARD_CODE.Card15.toString().equals(format) && valueData.length() == 15)) {
			// 长度一致
			if (IdCardVerification.VALIDITY.equals(IdCardVerification.IDCardValidate(valueData))) {
				// 有效
				result = true;
			}
		} else if (STANDARD_CODE.Passport.equals(format)) {
			Pattern p = Pattern.compile(CertificatesAnalysisMeta.PASSPORT1);
			result = p.matcher(valueData).matches();
			if (!result) {
				p = Pattern.compile(CertificatesAnalysisMeta.PASSPORT2);
				result = p.matcher(valueData).matches();
			}
		} else if (STANDARD_CODE.HKmakao.equals(format)) {
			Pattern p = Pattern.compile(CertificatesAnalysisMeta.HKMAKAO);
			result = p.matcher(valueData).matches();
		} else if (STANDARD_CODE.Taiwan.equals(format)) {
			Pattern p = Pattern.compile(CertificatesAnalysisMeta.TAIWAN1);
			result = p.matcher(valueData).matches();
			if (!result) {
				p = Pattern.compile(CertificatesAnalysisMeta.TAIWAN2);
				result = p.matcher(valueData).matches();
			}
		}

		return result;
	}

	@Override
	public String guessReference(String valueData) {
		return null;
	}

}

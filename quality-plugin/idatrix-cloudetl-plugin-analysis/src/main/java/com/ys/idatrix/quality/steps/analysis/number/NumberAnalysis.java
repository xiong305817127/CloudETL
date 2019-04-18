package com.ys.idatrix.quality.steps.analysis.number;

import java.util.regex.Pattern;

import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;

import com.ys.idatrix.quality.steps.analysis.base.AnalysisBase;

/**
 * 电话号码 分析组件
 *
 * @author XH
 * @since 2018年9月26日
 *
 */
public class NumberAnalysis extends AnalysisBase implements StepInterface {

	public NumberAnalysis(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean validate(String valueData, String format, boolean isStandard) {

		// 对正则特殊字符进行转义
		format = escapeExprSpecialWord(format);
		// 将所有 "N"替换为数字0-9
		format= format.replaceAll("N", "[0-9]");

		Pattern p = Pattern.compile(format);
		return p.matcher(valueData).matches();

	}

	@Override
	public String guessReference(String valueData) {
		if (Utils.isEmpty(valueData)) {
			return null;
		}
		StringBuilder replaceDigits = new StringBuilder();
		char c;
		for (int i = 0; i < valueData.length(); i++) {
			c = valueData.charAt(i);
			if (Character.isDigit(c)) {
				replaceDigits.append("N");
			} else {
				replaceDigits.append(c);
			}
		}
		return replaceDigits.toString();
	}

}

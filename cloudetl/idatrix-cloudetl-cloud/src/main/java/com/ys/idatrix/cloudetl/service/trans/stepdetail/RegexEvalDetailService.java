/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans.stepdetail;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.logger.CloudLogger;


/**
 * RegexEval related Detail Service
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class RegexEvalDetailService implements StepDetailService {

	@Override
	public String getStepDetailType() {
		return "RegexEval";
	}

	/**
	 * flag : getTables , getFields
	 * 
	 * @throws Exception
	 */
	@Override
	public Object dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "testRegex":
			return testRegex(param);
		default:
			return null;

		}

	}

	/**
	 * @param fileName
	 *            tableName
	 * @return Access Fields list
	 * @throws Exception
	 */
	private Map<String, Object> testRegex(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "script");

		String regexOptions = getRegexOptions(params);

		String regexScript = StringEscapeHelper.decode( params.get("script").toString() );
		Boolean canoneq = Boolean.valueOf(params.get("canoneq")!= null? params.get("canoneq").toString(): "false"  );

		String value1 = Const.NVL((String) params.get("value1"), "");
		String value2 = Const.NVL((String) params.get("value2"), "");
		String value3 = Const.NVL((String) params.get("value3"), "");
		String value4 = Const.NVL((String) params.get("value4"), "");

		Map<String, Object> result = Maps.newHashMap();
		try {

			Pattern p;
			if (canoneq) {
				p = Pattern.compile(regexOptions + regexScript, Pattern.CANON_EQ);
			} else {
				p = Pattern.compile(regexOptions + regexScript);
			}

			Matcher m = p.matcher(value1);
			boolean ismatch = m.matches();
			if (ismatch) {
				result.put("value1Result", true);
			} else {
				result.put("value1Result", false);
			}

			m = p.matcher(value2);
			ismatch = m.matches();
			if (ismatch) {
				result.put("value2Result", true);
			} else {
				result.put("value2Result", false);
			}

			m = p.matcher(value3);
			ismatch = m.matches();
			if (ismatch) {
				result.put("value3Result", true);
			} else {
				result.put("value3Result", false);
			}

			m = p.matcher(value4);
			ismatch = m.matches();
			if (ismatch) {
				result.put("value4Result", true);
			} else {
				result.put("value4Result", false);
			}

			if (!Utils.isEmpty(value4)) {
				List<String> group = Lists.newArrayList();
				int nrFields = m.groupCount();
				for (int i = 1; i <= nrFields; i++) {
					if (Utils.isEmpty(m.group(i))) {
						group.add("");
					} else {
						group.add(m.group(i));
					}
				}
				result.put("value4Group", group);
			}

			result.put("compileResult", true);			
			result.put("compileMessage", "The regular expression was successfully compiled.");
		} catch (Exception e) {
			result.put("compileResult", false);
			result.put("compileMessage", CloudLogger.getExceptionMessage(e));

		}

		return result;

	}

	public String getRegexOptions(Map<String, Object> params) {
		StringBuilder options = new StringBuilder();


		Boolean caseinsensitive = Boolean.valueOf( params.get("caseinsensitive")!= null? params.get("caseinsensitive").toString(): "false" );
		if (caseinsensitive) {
			options.append("(?i)");
		}

		Boolean comment = Boolean.valueOf(params.get("comment")!= null? params.get("comment").toString(): "false" );
		if (comment) {
			options.append("(?x)");
		}

		Boolean dotall = Boolean.valueOf( params.get("dotall")!= null? params.get("dotall").toString(): "false"  );
		if (dotall) {
			options.append("(?s)");
		}

		Boolean multiline = Boolean.valueOf(params.get("multiline")!= null? params.get("multiline").toString(): "false");
		if (multiline) {
			options.append("(?m)");
		}

		Boolean unicode = Boolean.valueOf(params.get("unicode")!= null? params.get("unicode").toString(): "false" );
		if (unicode) {
			options.append("(?u)");
		}

		Boolean unix = Boolean.valueOf(params.get("unix")!= null? params.get("unix").toString(): "false"  );
		if (unix) {
			options.append("(?d)");
		}
		return options.toString();
	}

}

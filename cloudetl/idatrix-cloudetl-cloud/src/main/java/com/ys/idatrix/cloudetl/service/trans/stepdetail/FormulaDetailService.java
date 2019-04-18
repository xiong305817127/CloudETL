/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans.stepdetail;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.libformula.editor.FormulaEvaluator;
import org.pentaho.libformula.editor.FormulaMessage;
import org.pentaho.libformula.editor.function.FunctionLib;
import org.pentaho.libformula.editor.util.PositionAndLength;
import org.pentaho.reporting.libraries.formula.lvalues.ParsePosition;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * TextInput related Detail Service
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class FormulaDetailService implements StepDetailService {

	FormulaEvaluator evaluator;

	public FormulaDetailService() throws KettleXMLException {
		super();
		FunctionLib functionLib = new FunctionLib("functions.xml");
		evaluator = new FormulaEvaluator(functionLib.getFunctionNames(), new String[] {});
	}

	@Override
	public String getStepDetailType() {
		return "Formula";
	}

	/**
	 * flag : getFields
	 * 
	 * @throws Exception
	 */
	@Override
	public Object dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "evaluator":
			return evaluator(param);
		default:
			return null;

		}

	}

	/**
	 * @param inputFiles
	 *            content
	 * @return Text Fields list
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> evaluator(Map<String, Object> params) throws Exception {

		checkDetailParam(params,  "expression");

		String expression = params.get("expression").toString();
		
		Object ifs = params.get("inputFields");
		List<String> inputFields = null ;
		if( ifs != null && ifs instanceof List) {
			inputFields = (List<String>)ifs;
		}
		if(inputFields != null) {
			// 更新执行器的 关键字域
			OsgiBundleUtils.setOsgiField(evaluator, "inputFields", inputFields.toArray(new String[] {}), true);
		}
		
		// 校验表达式
		Map<String, FormulaMessage> messages = evaluator.evaluateFormula(expression);

		StringBuilder report = new StringBuilder();
		List<Map<String, Object>> styles = Lists.newArrayList();
		int expressionLength = expression.length();
		for (FormulaMessage message : messages.values()) {

			ParsePosition position = message.getPosition();
			PositionAndLength positionAndLength = PositionAndLength.calculatePositionAndLength(expression, position);

			int pos = positionAndLength.getPosition();
			int length = positionAndLength.getLength();

			if (pos < expressionLength) {
				Map<String, Object> style = Maps.newHashMap();

				switch (message.getType()) {
				case FormulaMessage.TYPE_ERROR:
					report.append(message.toString()).append(Const.CR);

					style.put("type", "error");
					style.put("underline", true);
					style.put("pos", pos);
					style.put("length", length);
					style.put("color", "red");
					style.put("bold", true);
					styles.add(style);
					break;

				case FormulaMessage.TYPE_FUNCTION:

					style.put("type", "function");
					style.put("underline", false);
					style.put("pos", pos);
					style.put("length", length);
					style.put("color", "black");
					style.put("bold", true);
					styles.add(style);

					break;

				case FormulaMessage.TYPE_FIELD:
					// TODO : Not working for some reason.
					style.put("type", "field");
					style.put("underline", false);
					style.put("pos", pos);
					style.put("length", length);
					style.put("color", "green");
					style.put("bold", true);
					styles.add(style);

					break;

				case FormulaMessage.TYPE_STATIC_NUMBER:
				case FormulaMessage.TYPE_STATIC_STRING:
				case FormulaMessage.TYPE_STATIC_DATE:
				case FormulaMessage.TYPE_STATIC_LOGICAL:

					style.put("type", "static");
					style.put("underline", false);
					style.put("pos", pos);
					style.put("length", length);
					style.put("color", "gray");
					style.put("bold", false);
					styles.add(style);

					break;
				default:
					break;
				}
			}
		}

		Map<String, Object> result = Maps.newHashMap();
		result.put("report", report.toString());
		result.put("styles", styles);

		return result;
	}

}

/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesMetaMod;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesScript;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.JsFieldDto;
import com.ys.idatrix.quality.dto.step.parts.JsScriptDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.ext.utils.FieldValidator;
import com.ys.idatrix.quality.ext.utils.StringEscapeHelper;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Java script (modified)(JavaScript代码). 转换
 * org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesMetaMod
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Component("SPScriptValueMod")
@Scope("prototype")
public class SPScriptValueMod implements StepParameter, StepDataRelationshipParser {

	private boolean compatible;
	private String optimizationLevel;
	private List<JsScriptDto> jsScripts;
	private List<JsFieldDto> fields;

	public void setCompatible(boolean compatible) {
		this.compatible = compatible;
	}

	public boolean getCompatible() {
		return this.compatible;
	}

	public void setOptimizationLevel(String optimizationLevel) {
		this.optimizationLevel = optimizationLevel;
	}

	public String getOptimizationLevel() {
		return this.optimizationLevel;
	}

	public void setJsScripts(List<JsScriptDto> jsScripts) {
		this.jsScripts = jsScripts;
	}

	public List<JsScriptDto> getJsScripts() {
		return this.jsScripts;
	}

	public void setFields(List<JsFieldDto> fields) {
		this.fields = fields;
	}

	public List<JsFieldDto> getFields() {
		return this.fields;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("jsScripts", JsScriptDto.class);
		classMap.put("fields", JsFieldDto.class);

		return (SPScriptValueMod) JSONObject.toBean(jsonObj, SPScriptValueMod.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPScriptValueMod jsvm = new SPScriptValueMod();
		ScriptValuesMetaMod scriptValuesMetaMod = (ScriptValuesMetaMod) stepMetaInterface;

		jsvm.setCompatible(scriptValuesMetaMod.isCompatible());
		jsvm.setOptimizationLevel(scriptValuesMetaMod.getOptimizationLevel());

		List<JsFieldDto> jjfs = new ArrayList<>();
		String[] fieldName = scriptValuesMetaMod.getFieldname();
		if (fieldName != null) {
			for (int i = 0; i < fieldName.length; i++) {
				JsFieldDto jjf = new JsFieldDto();
				jjf.setLength(Integer.toString(scriptValuesMetaMod.getLength()[i]));
				jjf.setName(fieldName[i]);
				jjf.setPrecision(Integer.toString(scriptValuesMetaMod.getPrecision()[i]));
				jjf.setRename(scriptValuesMetaMod.getRename()[i]);
				jjf.setReplace(scriptValuesMetaMod.getReplace()[i]);
				jjf.setType(scriptValuesMetaMod.getType()[i]);
				jjfs.add(jjf);
			}
		}
		jsvm.setFields(jjfs);

		List<JsScriptDto> jjss = new ArrayList<>();
		ScriptValuesScript[] jsScripts = scriptValuesMetaMod.getJSScripts();
		if (jsScripts != null) {
			for (ScriptValuesScript script : jsScripts) {
				JsScriptDto jjs = new JsScriptDto();
				jjs.setName(script.getScriptName());
				jjs.setType(Integer.toString(script.getScriptType()));
				// jjs.setValue(script.getScript());
				jjs.setValue(StringEscapeHelper.encode(script.getScript()));
				jjss.add(jjs);
			}
		}
		jsvm.setJsScripts(jjss);

		return jsvm;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ScriptValuesMetaMod scriptValuesMetaMod = (ScriptValuesMetaMod) stepMetaInterface;
		SPScriptValueMod jsvm = (SPScriptValueMod) po;

		scriptValuesMetaMod.setCompatible(jsvm.getCompatible());
		scriptValuesMetaMod.setOptimizationLevel(jsvm.getOptimizationLevel());

		List<JsScriptDto> jjss = jsvm.getJsScripts();
		if (jjss != null) {
			ScriptValuesScript[] jsScripts = new ScriptValuesScript[jjss.size()];
			for (int i = 0; i < jjss.size(); i++) {
				JsScriptDto jjs = jjss.get(i);
				int iScriptType = Integer.parseInt(jjs.getType());
				String sScriptName = jjs.getName();
				String sScript = StringEscapeHelper.decode(jjs.getValue());
				jsScripts[i] = new ScriptValuesScript(iScriptType, sScriptName, sScript);
			}
			scriptValuesMetaMod.setJSScripts(jsScripts);
		}

		List<JsFieldDto> jjfs = jsvm.getFields();
		if (jjfs != null) {
			String[] fieldname = new String[jjfs.size()];
			String[] rename = new String[jjfs.size()];
			int[] type = new int[jjfs.size()];
			int[] length = new int[jjfs.size()];
			int[] precision = new int[jjfs.size()];
			boolean[] replace = new boolean[jjfs.size()];
			for (int i = 0; i < jjfs.size(); i++) {
				JsFieldDto jjf = jjfs.get(i);
				fieldname[i] = jjf.getName();
				rename[i] = jjf.getRename();
				type[i] = jjf.getType();
				length[i] = FieldValidator.fixedLength(Const.toInt(jjf.getLength(), -1));
				precision[i] = FieldValidator.fixedPrecision(Const.toInt(jjf.getPrecision(), -1));
				replace[i] = jjf.getReplace();
			}

			scriptValuesMetaMod.setFieldname(fieldname);
			scriptValuesMetaMod.setRename(rename);
			scriptValuesMetaMod.setType(type);
			scriptValuesMetaMod.setLength(length);
			scriptValuesMetaMod.setPrecision(precision);
			scriptValuesMetaMod.setReplace(replace);
		}
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ScriptValuesMetaMod scriptValuesMetaMod = (ScriptValuesMetaMod) stepMetaInterface;
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		
		//// TODO ETL script PARSE
		
		//字段重命名
		String[] outname = scriptValuesMetaMod.getFieldname();
		String[] outRename = scriptValuesMetaMod.getRename();
		if (outRename != null) {
			for (int i = 0; i < outRename.length ; i++) {
				if( !Utils.isEmpty(outRename[i]) && !outname[i].equals( outRename[i])) {
					sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, outname[i], outRename[i]) );
				}
			}
		}
	}

}

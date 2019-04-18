package com.ys.idatrix.cloudetl.dto.step.steps.script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Lists;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.regexeval.RegexEvalMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.RegexEvalfieldNameDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - RegexEval. 转换 org.pentaho.di.trans.steps.regexeval.RegexEvalMeta
 * 
 * @author XH
 * @since 2018-10-12
 */
@Component("SPRegexEval")
@Scope("prototype")
public class SPRegexEval implements StepParameter, StepDataRelationshipParser {

	String script;
	String matcher;
	String resultfieldname;
	boolean usevar;
	boolean allowcapturegroups;
	boolean replacefields;
	boolean canoneq;
	boolean caseinsensitive;
	boolean comment;
	boolean dotall;
	boolean multiline;
	boolean unicode;
	boolean unix;
	List<RegexEvalfieldNameDto> fieldNames;

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getMatcher() {
		return matcher;
	}

	public void setMatcher(String matcher) {
		this.matcher = matcher;
	}

	public String getResultfieldname() {
		return resultfieldname;
	}

	public void setResultfieldname(String resultfieldname) {
		this.resultfieldname = resultfieldname;
	}

	public boolean isUsevar() {
		return usevar;
	}

	public void setUsevar(boolean usevar) {
		this.usevar = usevar;
	}

	public boolean isAllowcapturegroups() {
		return allowcapturegroups;
	}

	public void setAllowcapturegroups(boolean allowcapturegroups) {
		this.allowcapturegroups = allowcapturegroups;
	}

	public boolean isReplacefields() {
		return replacefields;
	}

	public void setReplacefields(boolean replacefields) {
		this.replacefields = replacefields;
	}

	public boolean isCanoneq() {
		return canoneq;
	}

	public void setCanoneq(boolean canoneq) {
		this.canoneq = canoneq;
	}

	public boolean isCaseinsensitive() {
		return caseinsensitive;
	}

	public void setCaseinsensitive(boolean caseinsensitive) {
		this.caseinsensitive = caseinsensitive;
	}

	public boolean isComment() {
		return comment;
	}

	public void setComment(boolean comment) {
		this.comment = comment;
	}

	public boolean isDotall() {
		return dotall;
	}

	public void setDotall(boolean dotall) {
		this.dotall = dotall;
	}

	public boolean isMultiline() {
		return multiline;
	}

	public void setMultiline(boolean multiline) {
		this.multiline = multiline;
	}

	public boolean isUnicode() {
		return unicode;
	}

	public void setUnicode(boolean unicode) {
		this.unicode = unicode;
	}

	public boolean isUnix() {
		return unix;
	}

	public void setUnix(boolean unix) {
		this.unix = unix;
	}

	public List<RegexEvalfieldNameDto> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<RegexEvalfieldNameDto> fieldNames) {
		this.fieldNames = fieldNames;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldNames", RegexEvalfieldNameDto.class);
		return (SPRegexEval) JSONObject.toBean(jsonObj, SPRegexEval.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPRegexEval spRegexEval = new SPRegexEval();
		RegexEvalMeta regexevalmeta = (RegexEvalMeta) stepMetaInterface;

		spRegexEval.setScript(StringEscapeHelper.encode(regexevalmeta.getScript()));

		spRegexEval.setResultfieldname(regexevalmeta.getResultFieldName());
		spRegexEval.setMatcher(regexevalmeta.getMatcher());
		spRegexEval.setReplacefields(regexevalmeta.isReplacefields());

		spRegexEval.setUsevar(regexevalmeta.isUseVariableInterpolationFlagSet());
		spRegexEval.setAllowcapturegroups(regexevalmeta.isAllowCaptureGroupsFlagSet());
		spRegexEval.setCanoneq(regexevalmeta.isCanonicalEqualityFlagSet());
		spRegexEval.setCaseinsensitive(regexevalmeta.isCaseInsensitiveFlagSet());
		spRegexEval.setComment(regexevalmeta.isCommentFlagSet());
		spRegexEval.setDotall(regexevalmeta.isDotAllFlagSet());
		spRegexEval.setMultiline(regexevalmeta.isMultilineFlagSet());
		spRegexEval.setUnicode(regexevalmeta.isUnicodeFlagSet());
		spRegexEval.setUnix(regexevalmeta.isUnixLineEndingsFlagSet());

		String[] names = regexevalmeta.getFieldName();
		if (names != null && names.length > 0) {

			List<RegexEvalfieldNameDto> fs = Lists.newArrayList();
			for (int i = 0; i < names.length; i++) {
				RegexEvalfieldNameDto refnd = new RegexEvalfieldNameDto();
				refnd.setFieldName(names[i]);
				refnd.setFieldType(ValueMetaFactory.getValueMetaName((regexevalmeta.getFieldType()[i])));
				refnd.setFieldLength(regexevalmeta.getFieldLength()[i]);
				refnd.setFieldCurrency(regexevalmeta.getFieldCurrency()[i]);
				refnd.setFieldDecimal(regexevalmeta.getFieldDecimal()[i]);
				refnd.setFieldFormat(regexevalmeta.getFieldFormat()[i]);
				refnd.setFieldGroup(regexevalmeta.getFieldGroup()[i]);
				refnd.setFieldIfNull(regexevalmeta.getFieldIfNull()[i]);
				refnd.setFieldNullIf(regexevalmeta.getFieldNullIf()[i]);
				refnd.setFieldPrecision(regexevalmeta.getFieldPrecision()[i]);
				refnd.setFieldTrimType(regexevalmeta.getFieldTrimType()[i]);

				fs.add(refnd);
			}
			spRegexEval.setFieldNames(fs);
		}

		return spRegexEval;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPRegexEval spRegexEval = (SPRegexEval) po;
		RegexEvalMeta regexevalmeta = (RegexEvalMeta) stepMetaInterface;

		regexevalmeta.setResultFieldName(spRegexEval.getResultfieldname());
		regexevalmeta.setUseVariableInterpolationFlag(spRegexEval.isUsevar());
		regexevalmeta.setAllowCaptureGroupsFlag(spRegexEval.isAllowcapturegroups());
		regexevalmeta.setCaseInsensitiveFlag(spRegexEval.isCaseinsensitive());
		regexevalmeta.setUnixLineEndingsFlag(spRegexEval.isUnix());

		regexevalmeta.setScript(StringEscapeHelper.decode(spRegexEval.getScript()));

		regexevalmeta.setMatcher(spRegexEval.getMatcher());
		regexevalmeta.setReplacefields(spRegexEval.isReplacefields());
		regexevalmeta.setCommentFlag(spRegexEval.isComment());
		regexevalmeta.setDotAllFlag(spRegexEval.isDotall());
		regexevalmeta.setMultilineFlag(spRegexEval.isMultiline());
		regexevalmeta.setUnicodeFlag(spRegexEval.isUnicode());
		regexevalmeta.setCanonicalEqualityFlag(spRegexEval.isCanoneq());

		List<RegexEvalfieldNameDto> refnds = spRegexEval.getFieldNames();
		if (refnds != null && refnds.size() > 0) {

			String[] fieldName = new String[refnds.size()];
			int[] fieldType = new int[refnds.size()];
			String[] fieldFormat = new String[refnds.size()];
			String[] fieldGroup = new String[refnds.size()];
			String[] fieldDecimal = new String[refnds.size()];
			String[] fieldCurrency = new String[refnds.size()];
			int[] fieldLength = new int[refnds.size()];
			int[] fieldPrecision = new int[refnds.size()];
			String[] fieldNullIf = new String[refnds.size()];
			String[] fieldIfNull = new String[refnds.size()];
			int[] fieldTrimType = new int[refnds.size()];

			for (int i = 0; i < refnds.size(); i++) {
				RegexEvalfieldNameDto refnd = refnds.get(i);
				
				fieldName[i] = refnd.getFieldName() ;
				fieldType[i] = ValueMetaFactory.getIdForValueMeta(refnd.getFieldType());
				fieldFormat[i] = refnd.getFieldFormat();
				fieldGroup[i] = refnd.getFieldGroup();
				fieldDecimal[i] = refnd.getFieldDecimal();
				fieldCurrency[i] = refnd.getFieldCurrency();
				fieldLength[i] = refnd.getFieldLength();
				fieldPrecision[i] = refnd.getFieldPrecision();
				fieldNullIf[i] = refnd.getFieldNullIf();
				fieldIfNull[i] = refnd.getFieldIfNull();
				fieldTrimType[i] = refnd.getFieldTrimType();

			}
			regexevalmeta.setFieldName(fieldName);
			regexevalmeta.setFieldType(fieldType);
			regexevalmeta.setFieldFormat(fieldFormat);
			regexevalmeta.setFieldGroup(fieldGroup);
			regexevalmeta.setFieldDecimal(fieldDecimal);
			regexevalmeta.setFieldCurrency(fieldCurrency);
			regexevalmeta.setFieldLength(fieldLength);
			regexevalmeta.setFieldPrecision(fieldPrecision);
			regexevalmeta.setFieldNullIf(fieldNullIf);
			regexevalmeta.setFieldIfNull(fieldIfNull);
			regexevalmeta.setFieldTrimType(fieldTrimType);
		}
		
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {
		// TODO Auto-generated method stub

	}

}


package com.ys.idatrix.cloudetl.dto.step.steps.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.fuzzymatch.FuzzyMatchMeta;
import org.pentaho.pms.util.Const;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.NameFieldPairDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - FuzzyMatch. 转换 org.pentaho.di.trans.steps.fuzzymatch.FuzzyMatchMeta
 * 
 * @author FBZ
 * @since 12-11-2017
 */
@Component("SPFuzzyMatch")
@Scope("prototype")
public class SPFuzzyMatch implements StepParameter, StepDataRelationshipParser {

	/** Algorithms type, 算法 */
	private String algorithm;

	/**
	 * 算法下拉列表
	 */
	public String[] algorithmDesc = FuzzyMatchMeta.algorithmDesc;

	/** field in lookup stream with which we look up values, 匹配字段 */
	private String lookupfield;

	/** field in input stream for which we lookup values, 主要流字段 */
	private String mainstreamfield;

	/** output match fieldname, 输出字段--匹配字段 **/
	private String outputmatchfield;

	/** ouput value fieldname, 输出字段--值字段 **/
	private String outputvaluefield;

	/** case sensitive, 大小写敏感 **/
	private boolean caseSensitive;

	/** minimal value, distance for levenshtein, similarity, 最小值 **/
	private String minimalValue;

	/** maximal value, distance for levenshtein, similarity, 最大值 **/
	private String maximalValue;

	/** values separator ..., 值分割符 **/
	private String separator;

	/** get closer matching value, 获取近似值 **/
	private boolean closervalue;

	/** return these field values from lookup, 指定额外的在匹配流中的字段 -- 字段, 改名为 */
	private List<NameFieldPairDto> values;

	/**
	 * 匹配步骤
	 */
	private String mStep;

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String[] getAlgorithmDesc() {
		return algorithmDesc;
	}

	public void setAlgorithmDesc(String[] algorithmDesc) {
		this.algorithmDesc = algorithmDesc;
	}

	public String getLookupfield() {
		return lookupfield;
	}

	public void setLookupfield(String lookupfield) {
		this.lookupfield = lookupfield;
	}

	public String getMainstreamfield() {
		return mainstreamfield;
	}

	public void setMainstreamfield(String mainstreamfield) {
		this.mainstreamfield = mainstreamfield;
	}

	public String getOutputmatchfield() {
		return outputmatchfield;
	}

	public void setOutputmatchfield(String outputmatchfield) {
		this.outputmatchfield = outputmatchfield;
	}

	public String getOutputvaluefield() {
		return outputvaluefield;
	}

	public void setOutputvaluefield(String outputvaluefield) {
		this.outputvaluefield = outputvaluefield;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public String getMinimalValue() {
		return minimalValue;
	}

	public void setMinimalValue(String minimalValue) {
		this.minimalValue = minimalValue;
	}

	public String getMaximalValue() {
		return maximalValue;
	}

	public void setMaximalValue(String maximalValue) {
		this.maximalValue = maximalValue;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public boolean isCloservalue() {
		return closervalue;
	}

	public void setCloservalue(boolean closervalue) {
		this.closervalue = closervalue;
	}

	public List<NameFieldPairDto> getValues() {
		return values;
	}

	public void setValues(List<NameFieldPairDto> values) {
		this.values = values;
	}

	public String getmStep() {
		return mStep;
	}

	public void setmStep(String mStep) {
		this.mStep = mStep;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("values", NameFieldPairDto.class);

		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPFuzzyMatch) JSONObject.toBean(jsonObj, SPFuzzyMatch.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFuzzyMatch fm = new SPFuzzyMatch();
		FuzzyMatchMeta inputMeta = (FuzzyMatchMeta) stepMetaInterface;

		fm.setAlgorithm(FuzzyMatchMeta.getAlgorithmTypeDesc(inputMeta.getAlgorithmType()));

		fm.setMainstreamfield(Const.NVL(inputMeta.getMainStreamField(), ""));

		fm.setLookupfield(Const.NVL(inputMeta.getLookupField(), ""));

		fm.setCaseSensitive(inputMeta.isCaseSensitive());
		fm.setCloservalue(inputMeta.isGetCloserValue());
		fm.setMinimalValue(Const.NVL(inputMeta.getMinimalValue(), ""));

		fm.setMaximalValue(Const.NVL(inputMeta.getMaximalValue(), ""));

		fm.setOutputmatchfield(Const.NVL(inputMeta.getOutputMatchField(), ""));
		fm.setOutputvaluefield(Const.NVL(inputMeta.getOutputValueField(), ""));
		fm.setSeparator(Const.NVL(inputMeta.getSeparator(), ""));

		if (inputMeta.getValue() != null) {
			fm.setValues(new ArrayList<>(inputMeta.getValue().length));

			NameFieldPairDto dto;
			for (int i = 0; i < inputMeta.getValue().length; i++) {
				dto = new NameFieldPairDto();
				fm.getValues().add(dto);

				dto.setField(inputMeta.getValue()[i]);
				dto.setName("");
				if (inputMeta.getValueName()[i] != null
						&& !inputMeta.getValueName()[i].equals(inputMeta.getValue()[i])) {
					dto.setName(inputMeta.getValueName()[i]);
				}
			}
		}

		StreamInterface infoStream = inputMeta.getStepIOMeta().getInfoStreams().get(0);
		fm.setmStep(Const.NVL(infoStream.getStepname(), ""));

		return fm;
	}

	/*
	 * decode a step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		FuzzyMatchMeta inputMeta = (FuzzyMatchMeta) stepMetaInterface;
		SPFuzzyMatch fm = (SPFuzzyMatch) po;

		inputMeta.setMainStreamField(fm.getMainstreamfield());
		inputMeta.setLookupField(fm.getLookupfield());

		inputMeta.setAlgorithmType(FuzzyMatchMeta.getAlgorithmTypeByDesc(fm.getAlgorithm()));
		inputMeta.setCaseSensitive(fm.isCaseSensitive());
		inputMeta.setGetCloserValue(fm.isCloservalue());
		inputMeta.setMaximalValue(fm.getMaximalValue());
		inputMeta.setMinimalValue(fm.getMinimalValue());

		inputMeta.setOutputMatchField(fm.getOutputmatchfield());
		inputMeta.setOutputValueField(fm.getOutputvaluefield());
		inputMeta.setSeparator(fm.getSeparator());

		int nrvalues = null == fm.getValues() ? 0 : fm.getValues().size();
		inputMeta.allocate(nrvalues);

		NameFieldPairDto dto;
		for (int i = 0; i < nrvalues; i++) {
			dto = fm.getValues().get(i);
			inputMeta.getValue()[i] = dto.getField();
			inputMeta.getValueName()[i] = dto.getName();
			if (inputMeta.getValueName()[i] == null || inputMeta.getValueName()[i].length() == 0) {
				inputMeta.getValueName()[i] = inputMeta.getValue()[i];
			}
		}

		StreamInterface infoStream = inputMeta.getStepIOMeta().getInfoStreams().get(0);
		infoStream.setStepMeta(transMeta.findStep(fm.getmStep()));
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		FuzzyMatchMeta  fuzzyMatchMeta = (FuzzyMatchMeta ) stepMeta.getStepMetaInterface();
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		
		//输入字段
		String mainStream = fuzzyMatchMeta.getMainStreamField();
		String lookup = fuzzyMatchMeta.getLookupField();
		
		//输出字段
		String outputmatch = fuzzyMatchMeta.getOutputMatchField();
		String outputvalue = fuzzyMatchMeta.getOutputValueField();
		
		sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, mainStream, outputmatch) );
		sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, mainStream, outputvalue) );
		sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, lookup, outputmatch) );
		sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, lookup, outputvalue) );
		
		//改名
		String[] vaules = fuzzyMatchMeta.getValue(); 
		String[] vauleNames = fuzzyMatchMeta.getValueName();
		for( int i=0;i< vauleNames.length ;i++) {
			if(!StringUtils.isEmpty(vauleNames[i])) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, vaules[i], vauleNames[i]) );
			}
		}
	}
}

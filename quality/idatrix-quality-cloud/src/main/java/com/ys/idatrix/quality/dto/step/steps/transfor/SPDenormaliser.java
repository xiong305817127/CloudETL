/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.transfor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.denormaliser.DenormaliserMeta;
import org.pentaho.di.trans.steps.denormaliser.DenormaliserTargetField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.DenormaliserDenormaliserTargetFieldDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Denormaliser(列转行)
 * org.pentaho.di.trans.steps.denormaliser.DenormaliserMeta
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Component("SPDenormaliser")
@Scope("prototype")
public class SPDenormaliser implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser {

	String keyField;
	List<String> groupField;
	List<DenormaliserDenormaliserTargetFieldDto> denormaliserTargetField;

	/**
	 * @return keyField
	 */
	public String getKeyField() {
		return keyField;
	}

	/**
	 * @param keyField
	 *            要设置的 keyField
	 */
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	/**
	 * @return groupField
	 */
	public List<String> getGroupField() {
		return groupField;
	}

	/**
	 * @param groupField
	 *            要设置的 groupField
	 */
	public void setGroupField(List<String> groupField) {
		this.groupField = groupField;
	}

	/**
	 * @return denormaliserTargetField
	 */
	public List<DenormaliserDenormaliserTargetFieldDto> getDenormaliserTargetField() {
		return denormaliserTargetField;
	}

	/**
	 * @param denormaliserTargetField
	 *            要设置的 denormaliserTargetField
	 */
	public void setDenormaliserTargetField(List<DenormaliserDenormaliserTargetFieldDto> denormaliserTargetField) {
		this.denormaliserTargetField = denormaliserTargetField;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		// classMap.put("groupField", String.class);
		classMap.put("denormaliserTargetField", DenormaliserDenormaliserTargetFieldDto.class);
		return (SPDenormaliser) JSONObject.toBean(jsonObj, SPDenormaliser.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPDenormaliser spDenormaliser = new SPDenormaliser();
		DenormaliserMeta denormalisermeta = (DenormaliserMeta) stepMetaInterface;

		DenormaliserTargetField[] denormaliserTargetFieldArray = denormalisermeta.getDenormaliserTargetField();
		List<DenormaliserDenormaliserTargetFieldDto> denormaliserTargetFieldList = Arrays
				.asList(denormaliserTargetFieldArray).stream().map(temp1 -> {
					DenormaliserDenormaliserTargetFieldDto temp2 = new DenormaliserDenormaliserTargetFieldDto();
					temp2.setFieldname(temp1.getFieldName());
					temp2.setKeyvalue(temp1.getKeyValue());
					temp2.setName(temp1.getTargetName());
					temp2.setTypedesc(temp1.getTargetTypeDesc());
					temp2.setFormat(temp1.getTargetFormat());
					temp2.setLength(temp1.getTargetLength());
					temp2.setPrecision(temp1.getTargetPrecision());
					temp2.setDecimalsymbol(temp1.getTargetDecimalSymbol());
					temp2.setGroupingsymbol(temp1.getTargetGroupingSymbol());
					temp2.setCurrencysymbol(temp1.getTargetCurrencySymbol());
					temp2.setNullstring(temp1.getTargetNullString());
					temp2.setAggregationtypedesc(temp1.getTargetAggregationTypeDesc());
					return temp2;
				}).collect(Collectors.toList());
		spDenormaliser.setDenormaliserTargetField(denormaliserTargetFieldList);
		spDenormaliser.setKeyField(denormalisermeta.getKeyField());

		spDenormaliser.setGroupField(Arrays.asList(denormalisermeta.getGroupField()));

		return spDenormaliser;

	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPDenormaliser spDenormaliser = (SPDenormaliser) po;
		DenormaliserMeta denormalisermeta = (DenormaliserMeta) stepMetaInterface;

		List<DenormaliserTargetField> denormaliserTargetFieldList = spDenormaliser.getDenormaliserTargetField().stream()
				.map(temp1 -> {
					DenormaliserTargetField temp2 = new DenormaliserTargetField();
					temp2.setFieldName(temp1.getFieldname());
					temp2.setKeyValue(temp1.getKeyvalue());
					temp2.setTargetName(temp1.getName());
					temp2.setTargetType(temp1.getTypedesc());
					temp2.setTargetFormat(temp1.getFormat());
					temp2.setTargetLength(temp1.getLength());
					temp2.setTargetPrecision(temp1.getPrecision());
					temp2.setTargetDecimalSymbol(temp1.getDecimalsymbol());
					temp2.setTargetGroupingSymbol(temp1.getGroupingsymbol());
					temp2.setTargetCurrencySymbol(temp1.getCurrencysymbol());
					temp2.setTargetNullString(temp1.getNullstring());
					temp2.setTargetAggregationType(temp1.getAggregationtypedesc());
					return temp2;
				}).collect(Collectors.toList());
		denormalisermeta.setDenormaliserTargetField(denormaliserTargetFieldList
				.toArray(new DenormaliserTargetField[spDenormaliser.getDenormaliserTargetField().size()]));
		denormalisermeta.setKeyField(spDenormaliser.getKeyField());
		denormalisermeta.setGroupField(
				spDenormaliser.getGroupField().toArray(new String[spDenormaliser.getGroupField().size()]));

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		DenormaliserMeta denormalisermeta = (DenormaliserMeta) stepMetaInterface;
		//输入
		String kf = denormalisermeta.getKeyField();
		String[] groupFields = denormalisermeta.getGroupField();
		
		DenormaliserTargetField[] denormaliserTargetFieldArray = denormalisermeta.getDenormaliserTargetField();
		if(denormaliserTargetFieldArray != null) {
			Arrays.asList(denormaliserTargetFieldArray).stream().forEach(temp1 -> {
				try {
					//输出
					String name = temp1.getTargetName();
					//输入
					String fieldName = temp1.getFieldName();
					
					sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, fieldName, name) );
					sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, kf, name) );
					if(groupFields != null) {
						for(String gf : groupFields) {
							sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, gf, name) );
						}
					}
				} catch (Exception e) {
					relationshiplogger.error("",e);
				}
				
			});
		}
		
	}
	

	@Override
	public void setLinesFromCacheLines(StepLinesDto lines , StepLinesDto cacheLines) {
			//该组件会预先读取一行,比较后将有效数据输入写出,所以写出的行号对应的读入需要减1
			Map<String, Long> preMap = cacheLines.getPreEffectiveInputLines();
			if(  preMap != null && preMap.size() > 0) {
				for(String key :preMap.keySet()) {
					preMap.put(key, preMap.get(key)-1);
				}
			}
			lines.setPreEffectiveInputLines(preMap);
			lines.setLinesInput(cacheLines.getLinesInput());
			lines.setLinesOutput(cacheLines.getLinesOutput());
			lines.setLinesRead(cacheLines.getLinesRead());
			lines.setLinesWritten(cacheLines.getLinesWritten());
			lines.setLinesRejected(cacheLines.getLinesRejected());
			lines.setLinesUpdated(cacheLines.getLinesUpdated());
			lines.setLinesErrors(cacheLines.getLinesErrors());
	}

	@Override
	public int stepType() {
		return 12;
	}
}

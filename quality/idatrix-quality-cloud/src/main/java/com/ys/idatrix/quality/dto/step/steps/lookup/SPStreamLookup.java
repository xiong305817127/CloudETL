package com.ys.idatrix.quality.dto.step.steps.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.streamlookup.StreamLookupMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.StreamLookupkeystreamDto;
import com.ys.idatrix.quality.dto.step.parts.StreamLookupvalueDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - StreamLookup(流查询). 转换
 * org.pentaho.di.trans.steps.streamlookup.StreamLookupMeta
 * 
 * @author XH
 * @since 2017-09-04
 */
@Component("SPStreamLookup")
@Scope("prototype")
public class SPStreamLookup implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	String fromStep;
	boolean inputSorted;
	boolean preserveMemory;
	boolean sortedList;
	boolean integerPair;

	List<StreamLookupkeystreamDto> keys;
	List<StreamLookupvalueDto> values;

	/**
	 * @return the fromStep
	 */
	public String getFromStep() {
		return fromStep;
	}

	/**
	 * @param 设置
	 *            fromStep
	 */
	public void setFromStep(String fromStep) {
		this.fromStep = fromStep;
	}

	/**
	 * @return the inputSorted
	 */
	public boolean isInputSorted() {
		return inputSorted;
	}

	/**
	 * @param 设置
	 *            inputSorted
	 */
	public void setInputSorted(boolean inputSorted) {
		this.inputSorted = inputSorted;
	}

	/**
	 * @return the preserveMemory
	 */
	public boolean isPreserveMemory() {
		return preserveMemory;
	}

	/**
	 * @param 设置
	 *            preserveMemory
	 */
	public void setPreserveMemory(boolean preserveMemory) {
		this.preserveMemory = preserveMemory;
	}

	/**
	 * @return the sortedList
	 */
	public boolean isSortedList() {
		return sortedList;
	}

	/**
	 * @param 设置
	 *            sortedList
	 */
	public void setSortedList(boolean sortedList) {
		this.sortedList = sortedList;
	}

	/**
	 * @return the integerPair
	 */
	public boolean isIntegerPair() {
		return integerPair;
	}

	/**
	 * @param 设置
	 *            integerPair
	 */
	public void setIntegerPair(boolean integerPair) {
		this.integerPair = integerPair;
	}

	/**
	 * @return the keys
	 */
	public List<StreamLookupkeystreamDto> getKeys() {
		return keys;
	}

	/**
	 * @param 设置
	 *            keys
	 */
	public void setKeys(List<StreamLookupkeystreamDto> keys) {
		this.keys = keys;
	}

	/**
	 * @return the values
	 */
	public List<StreamLookupvalueDto> getValues() {
		return values;
	}

	/**
	 * @param 设置
	 *            values
	 */
	public void setValues(List<StreamLookupvalueDto> values) {
		this.values = values;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("keys", StreamLookupkeystreamDto.class);
		classMap.put("values", StreamLookupvalueDto.class);
		return (SPStreamLookup) JSONObject.toBean(jsonObj, SPStreamLookup.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPStreamLookup spStreamLookup = new SPStreamLookup();
		StreamLookupMeta streamlookupmeta = (StreamLookupMeta) stepMetaInterface;

		StreamInterface infoStream = streamlookupmeta.getStepIOMeta().getInfoStreams().get(0);
		spStreamLookup.setFromStep(infoStream != null ? infoStream.getStepname() : "");
		spStreamLookup.setInputSorted(streamlookupmeta.isInputSorted());
		spStreamLookup.setPreserveMemory(streamlookupmeta.isMemoryPreservationActive());
		spStreamLookup.setSortedList(streamlookupmeta.isUsingSortedList());
		spStreamLookup.setIntegerPair(streamlookupmeta.isUsingIntegerPair());

		spStreamLookup.setKeys(
				transArrayToList(streamlookupmeta.getKeystream(), new DtoTransData<StreamLookupkeystreamDto>() {
					@Override
					public StreamLookupkeystreamDto dealData(Object obj, int index) {
						StreamLookupkeystreamDto slksd = new StreamLookupkeystreamDto();
						slksd.setName((String) obj);
						slksd.setField(streamlookupmeta.getKeylookup()[index]);
						return slksd;
					}
				}));

		spStreamLookup
				.setValues(transArrayToList(streamlookupmeta.getValue(), new DtoTransData<StreamLookupvalueDto>() {
					@Override
					public StreamLookupvalueDto dealData(Object obj, int index) {
						StreamLookupvalueDto slvd = new StreamLookupvalueDto();
						slvd.setName((String) obj);
						slvd.setRename(streamlookupmeta.getValueName()[index]);
						slvd.setType(streamlookupmeta.getValueDefaultType()[index]);
						slvd.setDefaultValue(streamlookupmeta.getValueDefault()[index]);
						return slvd;
					}
				}));
		return spStreamLookup;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPStreamLookup spStreamLookup = (SPStreamLookup) po;
		StreamLookupMeta streamlookupmeta = (StreamLookupMeta) stepMetaInterface;

		StreamInterface infoStream = streamlookupmeta.getStepIOMeta().getInfoStreams().get(0);
		if(!Utils.isEmpty(spStreamLookup.getFromStep())) {
			infoStream.setSubject(spStreamLookup.getFromStep());
			infoStream.setStepMeta(transMeta.findStep(spStreamLookup.getFromStep()));
		}else {
			infoStream.setSubject(null);
			infoStream.setStepMeta(null);
		}
		 
		streamlookupmeta.setUsingSortedList(spStreamLookup.isSortedList());
		streamlookupmeta.setUsingIntegerPair(spStreamLookup.isIntegerPair());
		streamlookupmeta.setInputSorted(spStreamLookup.isInputSorted());
		streamlookupmeta.setMemoryPreservationActive(spStreamLookup.isPreserveMemory());

		if (spStreamLookup.getKeys() != null) {
			String keystream[] = new String[spStreamLookup.getKeys().size()];
			String keylookup[] = new String[spStreamLookup.getKeys().size()];
			transListToArray(spStreamLookup.getKeys(), new DtoTransData<StreamLookupkeystreamDto>() {
				@Override
				public StreamLookupkeystreamDto dealData(Object obj, int index) {
					StreamLookupkeystreamDto slksd = (StreamLookupkeystreamDto) obj;
					keystream[index] = slksd.getName();
					keylookup[index] = slksd.getField();
					return null;
				}
			});
			streamlookupmeta.setKeystream(keystream);
			streamlookupmeta.setKeylookup(keylookup);
		}

		if (spStreamLookup.getValues() != null) {
			String value[] = new String[spStreamLookup.getValues().size()];
			String valueName[] = new String[spStreamLookup.getValues().size()];
			String valueDefault[] = new String[spStreamLookup.getValues().size()];
			int valueDefaultType[] = new int[spStreamLookup.getValues().size()];
			transListToArray(spStreamLookup.getValues(), new DtoTransData<StreamLookupvalueDto>() {
				@Override
				public StreamLookupvalueDto dealData(Object obj, int index) {
					StreamLookupvalueDto slvd = (StreamLookupvalueDto) obj;
					value[index] = slvd.getName();
					valueName[index] = slvd.getRename();
					valueDefault[index] = slvd.getDefaultValue();
					valueDefaultType[index] = slvd.getType();
					return null;
				}
			});
			streamlookupmeta.setValue(value);
			streamlookupmeta.setValueName(valueName);
			streamlookupmeta.setValueDefault(valueDefault);
			streamlookupmeta.setValueDefaultType(valueDefaultType);
		}

		streamlookupmeta.searchInfoAndTargetSteps(transMeta.getSteps());
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		StreamLookupMeta streamlookupmeta = (StreamLookupMeta) stepMetaInterface;
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		//输入
		String[] outname = streamlookupmeta.getValue();
		//输出
		String[] outRename = streamlookupmeta.getValueName();
		
		
		if (outRename != null) {
			for (int i = 0; i < outRename.length ; i++) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, outname[i], outRename[i]) );
			}
		}
	}

	@Override
	public int stepType() {
		return 0;
	}

}

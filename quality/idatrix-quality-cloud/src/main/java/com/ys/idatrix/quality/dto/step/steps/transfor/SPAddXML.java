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
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.AddXMLXMLFieldDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step -add xml ,转换 org.pentaho.di.trans.steps.addxml.AddXMLMeta
 * 
 * @author XH
 * @since 2017年6月21日
 *
 */
@Component("SPAddXML")
@Scope("prototype")
public class SPAddXML implements StepParameter, StepDataRelationshipParser {

	String encoding;
	String valueName;
	String rootNode;
	boolean omitXMLheader;
	boolean omitNullValues;
	List<AddXMLXMLFieldDto> outputFields;

	/**
	 * @return encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param 设置
	 *            encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return valueName
	 */
	public String getValueName() {
		return valueName;
	}

	/**
	 * @param 设置
	 *            valueName
	 */
	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	/**
	 * @return rootNode
	 */
	public String getRootNode() {
		return rootNode;
	}

	/**
	 * @param 设置
	 *            rootNode
	 */
	public void setRootNode(String rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * @return omitXMLheader
	 */
	public boolean isOmitXMLheader() {
		return omitXMLheader;
	}

	/**
	 * @param 设置
	 *            omitXMLheader
	 */
	public void setOmitXMLheader(boolean omitXMLheader) {
		this.omitXMLheader = omitXMLheader;
	}

	/**
	 * @return omitNullValues
	 */
	public boolean isOmitNullValues() {
		return omitNullValues;
	}

	/**
	 * @param 设置
	 *            omitNullValues
	 */
	public void setOmitNullValues(boolean omitNullValues) {
		this.omitNullValues = omitNullValues;
	}

	/**
	 * @return outputFields
	 */
	public List<AddXMLXMLFieldDto> getOutputFields() {
		return outputFields;
	}

	/**
	 * @param 设置
	 *            outputFields
	 */
	public void setOutputFields(List<AddXMLXMLFieldDto> outputFields) {
		this.outputFields = outputFields;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("outputFields", AddXMLXMLFieldDto.class);
		return (SPAddXML) JSONObject.toBean(jsonObj, SPAddXML.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPAddXML spAddXML = new SPAddXML();
		// AddXMLMeta addxmlmeta = (AddXMLMeta) stepMetaInterface;

		spAddXML.setEncoding((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getEncoding")); // addxmlmeta.getEncoding()

		Object[]/* XMLField[] */ outputFieldsArray = (Object[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface,
				"getOutputFields"); // addxmlmeta.getOutputFields();
		if (outputFieldsArray != null) {
			List<AddXMLXMLFieldDto> outputFieldsList = Arrays.asList(outputFieldsArray).stream().map(temp1 -> {
				AddXMLXMLFieldDto temp2 = new AddXMLXMLFieldDto();
				temp2.setFieldname((String) OsgiBundleUtils.invokeOsgiMethod(temp1, "getFieldName"));// temp1.getFieldName()
				temp2.setElementname((String) OsgiBundleUtils.invokeOsgiMethod(temp1, "getElementName"));// temp1.getElementName());
				temp2.setType((int) OsgiBundleUtils.invokeOsgiMethod(temp1, "getType"));// temp1.getType());
				temp2.setFormat((String) OsgiBundleUtils.invokeOsgiMethod(temp1, "getFormat"));// temp1.getFormat());
				temp2.setCurrencysymbol((String) OsgiBundleUtils.invokeOsgiMethod(temp1, "getCurrencySymbol"));// temp1.getCurrencySymbol());
				temp2.setDecimalsymbol((String) OsgiBundleUtils.invokeOsgiMethod(temp1, "getDecimalSymbol"));// temp1.getDecimalSymbol());
				temp2.setGroupingsymbol((String) OsgiBundleUtils.invokeOsgiMethod(temp1, "getGroupingSymbol"));// temp1.getGroupingSymbol());
				temp2.setNullstring((String) OsgiBundleUtils.invokeOsgiMethod(temp1, "getNullString"));// temp1.getNullString());
				temp2.setLength((int) OsgiBundleUtils.invokeOsgiMethod(temp1, "getLength"));// temp1.getLength());
				temp2.setPrecision((int) OsgiBundleUtils.invokeOsgiMethod(temp1, "getPrecision"));// temp1.getPrecision());
				temp2.setAttribute((boolean) OsgiBundleUtils.invokeOsgiMethod(temp1, "isAttribute"));// temp1.isAttribute());
				temp2.setAttributeparentname(
						(String) OsgiBundleUtils.invokeOsgiMethod(temp1, "getAttributeParentName"));// temp1.getAttributeParentName());
				return temp2;
			}).collect(Collectors.toList());
			spAddXML.setOutputFields(outputFieldsList);
		}
		spAddXML.setRootNode((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getRootNode")); // addxmlmeta.getRootNode());
		spAddXML.setValueName((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getValueName")); // addxmlmeta.getValueName());
		spAddXML.setOmitXMLheader((boolean) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "isOmitXMLheader")); // addxmlmeta.isOmitXMLheader());
		spAddXML.setOmitNullValues((boolean) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "isOmitNullValues")); // addxmlmeta.isOmitNullValues());
		return spAddXML;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPAddXML spAddXML = (SPAddXML) po;
		// AddXMLMeta addxmlmeta = (AddXMLMeta) stepMetaInterface;

		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setOmitXMLheader", (boolean) spAddXML.isOmitXMLheader());// addxmlmeta.setOmitXMLheader(spAddXML.isOmitXMLheader());
		if (spAddXML.getOutputFields() != null) {
			List<Object> outputFieldsList = spAddXML.getOutputFields().stream().map(temp1 -> {
				Object temp2 = OsgiBundleUtils.newOsgiInstance(stepMetaInterface,
						"org.pentaho.di.trans.steps.addxml.XMLField");// XMLField
																		// temp2
																		// = new
																		// XMLField();
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setFieldName", temp1.getFieldname());// temp2.setFieldName(temp1.getFieldname());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setElementName", temp1.getElementname());// temp2.setElementName(temp1.getElementname());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setType", new Object[] { temp1.getType() },
						new Class[] { int.class });// temp2.setType(temp1.getType());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setFormat", temp1.getFormat());// temp2.setFormat(temp1.getFormat());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setCurrencySymbol", temp1.getCurrencysymbol());// temp2.setCurrencySymbol(temp1.getCurrencysymbol());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setDecimalSymbol", temp1.getDecimalsymbol());// temp2.setDecimalSymbol(temp1.getDecimalsymbol());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setGroupingSymbol", temp1.getGroupingsymbol());// temp2.setGroupingSymbol(temp1.getGroupingsymbol());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setNullString", temp1.getNullstring());// temp2.setNullString(temp1.getNullstring());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setLength", temp1.getLength());// temp2.setLength(temp1.getLength());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setPrecision", temp1.getPrecision());// temp2.setPrecision(temp1.getPrecision());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setAttribute", temp1.isAttribute());// temp2.setAttribute(temp1.isAttribute());
				OsgiBundleUtils.invokeOsgiMethod(temp2, "setAttributeParentName", temp1.getAttributeparentname());// temp2.setAttributeParentName(temp1.getAttributeparentname());
				return temp2;
			}).collect(Collectors.toList());
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setOutputFields",
					new Object[] { outputFieldsList.toArray() });// addxmlmeta.setOutputFields(outputFieldsList.toArray(new
																	// XMLField[spAddXML.getOutputFields().size()]));
		}
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setEncoding", spAddXML.getEncoding());// addxmlmeta.setEncoding(spAddXML.getEncoding());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setRootNode", spAddXML.getRootNode());// addxmlmeta.setRootNode(spAddXML.getRootNode());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setValueName", spAddXML.getValueName());// addxmlmeta.setValueName(spAddXML.getValueName());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setOmitNullValues", spAddXML.isOmitNullValues());// addxmlmeta.setOmitNullValues(spAddXML.isOmitNullValues());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		//输出
		String valueName = (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getValueName"); //addxmlmeta.getValueName());
		//输入
		Object[]/*XMLField[]*/ outputFieldsArray = (Object[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getOutputFields"); //addxmlmeta.getOutputFields();
		if (outputFieldsArray != null) {
			 Arrays.asList(outputFieldsArray).stream().forEach(temp1 -> {
				String fieldName = (String)OsgiBundleUtils.invokeOsgiMethod(temp1, "getFieldName");//temp1.getFieldName()
				if(!Utils.isEmpty(fieldName)) {
					try {
						sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, fieldName, valueName) );
					} catch (Exception e) {
						relationshiplogger.error("",e);
					}
				}
			});
		}
	}

}

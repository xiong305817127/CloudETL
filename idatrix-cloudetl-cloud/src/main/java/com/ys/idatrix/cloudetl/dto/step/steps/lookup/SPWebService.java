package com.ys.idatrix.cloudetl.dto.step.steps.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Lists;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.webservices.WebServiceField;
import org.pentaho.di.trans.steps.webservices.WebServiceMeta;
import org.pentaho.di.trans.steps.webservices.wsdl.Wsdl;
import org.pentaho.di.trans.steps.webservices.wsdl.WsdlOperation;
import org.pentaho.di.trans.steps.webservices.wsdl.WsdlParamContainer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ys.idatrix.cloudetl.dto.step.parts.WebServiceFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.utils.WebServiceUtils;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - WebService. 转换 org.pentaho.di.trans.steps.webservices.WebServiceMeta
 * 
 * @author FBZ
 * @since 12-1-2017
 */
@Component("SPWebServiceLookup")
@Scope("prototype")
public class SPWebService implements StepParameter, StepDataRelationshipParser {

	/** The input web service fields */
	private List<WebServiceFieldDto> fieldsIn;

	/** The output web service fields */
	private List<WebServiceFieldDto> fieldsOut;

	/** Web service URL */
	private String url;

	/** Name of the web service operation to use, 操作 */
	private String operationName;

	/**
	 * Name of the operation request name: optional, can be different from the
	 * operation name
	 */
	private String operationRequestName;

	private String proxyHost;

	private String proxyPort;

	private String httpLogin;

	private String httpPassword;

	/** Flag to allow input data to pass to the output, 将输入的数据传到输出 */
	private boolean passingInputData;

	/** The number of rows to send with each call, web service 调用步骤 */
	private String callStep;

	/** Use the 2.5/3.0 parsing logic (available for compatibility reasons) */
	private boolean compatible;

	/**
	 * The name of the repeating element name. Empty = a single row return, 重复元素名称
	 */
	private String repeatingElementName;

	/**
	 * Is this step giving back the complete reply from the service as an XML
	 * string?
	 */
	private boolean returningReplyAsString;

	public List<WebServiceFieldDto> getFieldsIn() {
		return fieldsIn;
	}

	public void setFieldsIn(List<WebServiceFieldDto> fieldsIn) {
		this.fieldsIn = fieldsIn;
	}

	public List<WebServiceFieldDto> getFieldsOut() {
		return fieldsOut;
	}

	public void setFieldsOut(List<WebServiceFieldDto> fieldsOut) {
		this.fieldsOut = fieldsOut;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationRequestName() {
		return operationRequestName;
	}

	public void setOperationRequestName(String operationRequestName) {
		this.operationRequestName = operationRequestName;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getHttpLogin() {
		return httpLogin;
	}

	public void setHttpLogin(String httpLogin) {
		this.httpLogin = httpLogin;
	}

	public String getHttpPassword() {
		return httpPassword;
	}

	public void setHttpPassword(String httpPassword) {
		this.httpPassword = httpPassword;
	}

	public boolean isPassingInputData() {
		return passingInputData;
	}

	public void setPassingInputData(boolean passingInputData) {
		this.passingInputData = passingInputData;
	}

	public String getCallStep() {
		return callStep;
	}

	public void setCallStep(String callStep) {
		this.callStep = callStep;
	}

	public boolean isCompatible() {
		return compatible;
	}

	public void setCompatible(boolean compatible) {
		this.compatible = compatible;
	}

	public String getRepeatingElementName() {
		return repeatingElementName;
	}

	public void setRepeatingElementName(String repeatingElementName) {
		this.repeatingElementName = repeatingElementName;
	}

	public boolean isReturningReplyAsString() {
		return returningReplyAsString;
	}

	public void setReturningReplyAsString(boolean returningReplyAsString) {
		this.returningReplyAsString = returningReplyAsString;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldsIn", WebServiceFieldDto.class);
		classMap.put("fieldsOut", WebServiceFieldDto.class);

		return (SPWebService) JSONObject.toBean(jsonObj, SPWebService.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPWebService ws = new SPWebService();
		WebServiceMeta meta = (WebServiceMeta) stepMetaInterface;

		ws.setUrl(Const.NVL(meta.getUrl(), ""));
		ws.setProxyHost(Const.NVL(meta.getProxyHost(), ""));
		ws.setProxyPort(Const.NVL(meta.getProxyPort(), ""));
		ws.setHttpLogin(Const.NVL(meta.getHttpLogin(), ""));
		ws.setHttpPassword(Const.NVL(meta.getHttpPassword(), ""));
		ws.setCallStep(Integer.toString(meta.getCallStep()));
		ws.setPassingInputData(meta.isPassingInputData());
		ws.setCompatible(meta.isCompatible());
		ws.setRepeatingElementName(Const.NVL(meta.getRepeatingElementName(), ""));
		ws.setReturningReplyAsString(meta.isReturningReplyAsString());

		if (StringUtils.isEmpty(meta.getUrl())) {
			ws.setOperationName(Const.NVL(meta.getOperationName(), ""));
		}
		ws.setOperationRequestName(Const.NVL(meta.getOperationRequestName(), ""));
		ws.setOperationName(meta.getOperationName());

		if (!CollectionUtils.isEmpty(meta.getFieldsIn())) {
			ws.setFieldsIn(new ArrayList<>(meta.getFieldsIn().size()));
			WebServiceFieldDto dto;
			for (WebServiceField wf : meta.getFieldsIn()) {
				dto = new WebServiceFieldDto();
				ws.getFieldsIn().add(dto);

				dto.setName(wf.getName());
				dto.setType(wf.getXsdType());
				dto.setWsName(wf.getWsName());
			}
		}

		if (!CollectionUtils.isEmpty(meta.getFieldsOut())) {
			ws.setFieldsOut(new ArrayList<>(meta.getFieldsOut().size()));
			WebServiceFieldDto dto;
			for (WebServiceField wf : meta.getFieldsOut()) {
				dto = new WebServiceFieldDto();
				ws.getFieldsOut().add(dto);

				dto.setName(wf.getName());
				dto.setType(wf.getXsdType());
				dto.setWsName(wf.getWsName());
			}
		}

		return ws;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		WebServiceMeta inputMeta = (WebServiceMeta) stepMetaInterface;
		SPWebService ws = (SPWebService) po;

		inputMeta.setUrl(ws.getUrl());
		inputMeta.setProxyHost(ws.getProxyHost());
		inputMeta.setProxyPort(ws.getProxyPort());
		inputMeta.setHttpLogin(ws.getHttpLogin());
		inputMeta.setHttpPassword(ws.getHttpPassword());
		inputMeta.setCallStep(Const.toInt(ws.getCallStep(), WebServiceMeta.DEFAULT_STEP));
		inputMeta.setPassingInputData(ws.isPassingInputData());
		inputMeta.setCompatible(ws.isCompatible());
		inputMeta.setRepeatingElementName(ws.getRepeatingElementName());
		inputMeta.setReturningReplyAsString(ws.isReturningReplyAsString());
		inputMeta.setOperationRequestName(ws.getOperationRequestName());
		inputMeta.setOperationName(ws.getOperationName());

		Wsdl wsdl = WebServiceUtils.loadWebService(transMeta, ws.getUrl(), ws.getHttpLogin(), ws.httpPassword);
		Object[] r = WebServiceUtils.loadOperation(ws.getOperationName(), wsdl);
		WsdlOperation wsdlOperation = (WsdlOperation) r[0];
		WsdlParamContainer inWsdlParamContainer = (WsdlParamContainer) r[1];
		WsdlParamContainer outWsdlParamContainer = (WsdlParamContainer) r[2];

		if (wsdlOperation != null) {
			inputMeta.setOperationName(wsdlOperation.getOperationQName().getLocalPart());
			inputMeta.setOperationNamespace(wsdlOperation.getOperationQName().getNamespaceURI());
		} else if (wsdl != null) {
			inputMeta.setOperationName(ws.getOperationName());
			inputMeta.setOperationNamespace(null);
		}

		if (inWsdlParamContainer != null) {
			inputMeta.setInFieldContainerName(inWsdlParamContainer.getContainerName());
			inputMeta.setInFieldArgumentName(inWsdlParamContainer.getItemName());
		} else if (wsdl != null) {
			inputMeta.setInFieldContainerName(null);
			inputMeta.setInFieldArgumentName(null);
		}

		if (outWsdlParamContainer != null) {
			inputMeta.setOutFieldContainerName(outWsdlParamContainer.getContainerName());
			inputMeta.setOutFieldArgumentName(outWsdlParamContainer.getItemName());
		} else if (wsdl != null) {
			inputMeta.setOutFieldContainerName(null);
			inputMeta.setOutFieldArgumentName(null);
		}

		// Input fields...
		if (!CollectionUtils.isEmpty(ws.getFieldsIn())) {
			inputMeta.setFieldsIn(new ArrayList<>(ws.getFieldsIn().size()));
			WebServiceField field;
			for (WebServiceFieldDto dto : ws.getFieldsIn()) {
				field = new WebServiceField();
				inputMeta.getFieldsIn().add(field);

				field.setXsdType(Const.NVL(dto.getType(), "String"));
				field.setName(dto.getName());
				field.setWsName(dto.getWsName());
			}
		} else {
			inputMeta.setFieldsIn(new ArrayList<>(0));
		}

		// output fields...
		if (!CollectionUtils.isEmpty(ws.getFieldsOut())) {
			inputMeta.setFieldsOut(new ArrayList<>(ws.getFieldsOut().size()));
			WebServiceField field;
			for (WebServiceFieldDto dto : ws.getFieldsOut()) {
				if (!StringUtils.isEmpty(dto.getName())) {
					field = new WebServiceField();
					inputMeta.getFieldsOut().add(field);

					field.setXsdType(Const.NVL(dto.getType(), "String"));
					field.setName(dto.getName());
					field.setWsName(dto.getWsName());
				}
			}
		} else {
			inputMeta.setFieldsOut(new ArrayList<>(0));
		}
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		WebServiceMeta webServiceMeta = (WebServiceMeta) stepMetaInterface;

		String url = webServiceMeta.getUrl();
		if (StringUtils.isEmpty(url)) {
			return;
		}

		Map<String, DataNode> itemDataNodes = DataNodeUtil.interfaceNodeParse("WebService", url, "json", stepMeta.getName(), sdr.getOutputStream().values());
		sdr.getInputDataNodes().addAll(itemDataNodes.values());

		// 增加 系统节点 和 流节点的关系
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		List<Relationship> relationships = RelationshipUtil.inputStepRelationship(itemDataNodes, null, sdr.getOutputStream(), stepMeta.getName(), from) ;
		sdr.getDataRelationship().addAll(relationships);

		// 输入字段
		List<String> inFields = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(webServiceMeta.getFieldsIn())) {
			for (WebServiceField wf : webServiceMeta.getFieldsIn()) {
				inFields.add(wf.getName());
			}
		}

		if (!CollectionUtils.isEmpty(webServiceMeta.getFieldsOut()) && inFields.size() >0) {
			for (WebServiceField wf : webServiceMeta.getFieldsOut()) {
				String out =wf.getName();
				for(String in : inFields) {
					sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, in, out) );
				}
			}
		}
	}
}

package com.ys.idatrix.quality.dto.step.steps.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.rest.RestMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.NameFieldPairDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Rest Client. 转换 org.pentaho.di.trans.steps.rest.RestMeta
 * 
 * @author FBZ
 * @since 11-1-2017
 */
@Component("SPRest")
@Scope("prototype")
public class SPRest implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	private String applicationType;

	/** URL / service to be called */
	private String url;
	private boolean urlInField;
	private String urlField;

	/** headers name */
	private List<NameFieldPairDto> headers;

	/** Query parameters name */
	private List<NameFieldPairDto> parameter;

	/** Matrix parameters name */
	private List<NameFieldPairDto> matrixParameter;

	/** function result: new value name */
	private String fieldName;
	private String resultCodeFieldName;
	private String responseTimeFieldName;
	private String responseHeaderFieldName;

	/** proxy **/
	private String proxyHost;
	private String proxyPort;
	private String httpLogin;
	private String httpPassword;
	private boolean preemptive;

	/** Body fieldname **/
	private String bodyField;

	/** HTTP Method **/
	private String method;
	private boolean dynamicMethod; //
	private String methodFieldName;

	/** Trust store **/
	private String trustStoreFile;
	private String trustStorePassword;

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isUrlInField() {
		return urlInField;
	}

	public void setUrlInField(boolean urlInField) {
		this.urlInField = urlInField;
	}

	public String getUrlField() {
		return urlField;
	}

	public void setUrlField(String urlField) {
		this.urlField = urlField;
	}

	public List<NameFieldPairDto> getHeaders() {
		return headers;
	}

	public void setHeaders(List<NameFieldPairDto> headers) {
		this.headers = headers;
	}

	public List<NameFieldPairDto> getParameter() {
		return parameter;
	}

	public void setParameter(List<NameFieldPairDto> parameter) {
		this.parameter = parameter;
	}

	public List<NameFieldPairDto> getMatrixParameter() {
		return matrixParameter;
	}

	public void setMatrixParameter(List<NameFieldPairDto> matrixParameter) {
		this.matrixParameter = matrixParameter;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getResultCodeFieldName() {
		return resultCodeFieldName;
	}

	public void setResultCodeFieldName(String resultCodeFieldName) {
		this.resultCodeFieldName = resultCodeFieldName;
	}

	public String getResponseTimeFieldName() {
		return responseTimeFieldName;
	}

	public void setResponseTimeFieldName(String responseTimeFieldName) {
		this.responseTimeFieldName = responseTimeFieldName;
	}

	public String getResponseHeaderFieldName() {
		return responseHeaderFieldName;
	}

	public void setResponseHeaderFieldName(String responseHeaderFieldName) {
		this.responseHeaderFieldName = responseHeaderFieldName;
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

	public boolean isPreemptive() {
		return preemptive;
	}

	public void setPreemptive(boolean preemptive) {
		this.preemptive = preemptive;
	}

	public String getBodyField() {
		return bodyField;
	}

	public void setBodyField(String bodyField) {
		this.bodyField = bodyField;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isDynamicMethod() {
		return dynamicMethod;
	}

	public void setDynamicMethod(boolean dynamicMethod) {
		this.dynamicMethod = dynamicMethod;
	}

	public String getMethodFieldName() {
		return methodFieldName;
	}

	public void setMethodFieldName(String methodFieldName) {
		this.methodFieldName = methodFieldName;
	}

	public String getTrustStoreFile() {
		return trustStoreFile;
	}

	public void setTrustStoreFile(String trustStoreFile) {
		this.trustStoreFile = trustStoreFile;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("headers", NameFieldPairDto.class);
		classMap.put("parameter", NameFieldPairDto.class);
		classMap.put("matrixParameter", NameFieldPairDto.class);

		return (SPRest) JSONObject.toBean(jsonObj, SPRest.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPRest rest = new SPRest();
		RestMeta inputMeta = (RestMeta) stepMetaInterface;

		int headerLen = inputMeta.getHeaderName() == null ? 0 : inputMeta.getHeaderName().length;
		rest.setHeaders(new ArrayList<>(headerLen));
		NameFieldPairDto dto;
		for (int i = 0; i < headerLen; i++) {
			dto = new NameFieldPairDto();
			rest.getHeaders().add(dto);
			dto.setField(inputMeta.getHeaderField()[i]);
			dto.setName(inputMeta.getHeaderName()[i]);
		}

		int paramLen = inputMeta.getParameterField() == null ? 0 : inputMeta.getParameterField().length;
		rest.setParameter(new ArrayList<>(paramLen));
		for (int i = 0; i < paramLen; i++) {
			dto = new NameFieldPairDto();
			rest.getParameter().add(dto);
			dto.setField(inputMeta.getParameterField()[i]);
			dto.setName(inputMeta.getParameterName()[i]);
		}

		int mLen = inputMeta.getMatrixParameterField() == null ? 0 : inputMeta.getMatrixParameterField().length;
		rest.setMatrixParameter(new ArrayList<>(mLen));
		for (int i = 0; i < mLen; i++) {
			dto = new NameFieldPairDto();
			rest.getMatrixParameter().add(dto);

			dto.setField(inputMeta.getMatrixParameterField()[i]);
			dto.setName(inputMeta.getMatrixParameterField()[i]);
		}

		rest.setMethod(Const.NVL(inputMeta.getMethod(), RestMeta.HTTP_METHOD_GET));
		rest.setDynamicMethod(inputMeta.isDynamicMethod());
		rest.setBodyField(inputMeta.getBodyField());
		rest.setMethodFieldName(inputMeta.getMethodFieldName());
		rest.setUrl(inputMeta.getUrl());
		rest.setUrlInField(inputMeta.isUrlInField());
		rest.setUrlField(inputMeta.getUrlField());
		rest.setFieldName(inputMeta.getFieldName());
		rest.setResultCodeFieldName(inputMeta.getResultCodeFieldName());
		rest.setResponseTimeFieldName(inputMeta.getResponseTimeFieldName());

		rest.setHttpLogin(inputMeta.getHttpLogin());
		rest.setHttpPassword(inputMeta.getHttpPassword());
		rest.setProxyHost(inputMeta.getProxyHost());
		rest.setProxyPort(inputMeta.getProxyPort());
		rest.setPreemptive(inputMeta.isPreemptive());
		rest.setTrustStoreFile(inputMeta.getTrustStoreFile());
		rest.setTrustStorePassword(inputMeta.getTrustStorePassword());
		rest.setResponseHeaderFieldName(inputMeta.getResponseHeaderFieldName());
		rest.setApplicationType(Const.NVL(inputMeta.getApplicationType(), ""));

		return rest;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		RestMeta inputMeta = (RestMeta) stepMetaInterface;
		SPRest rest = (SPRest) po;

		int nrheaders = null == rest.getHeaders() ? 0 : rest.getHeaders().size();
		int nrparams = null == rest.getParameter() ? 0 : rest.getParameter().size();
		int nrmatrixparams = null == rest.getMatrixParameter() ? 0 : rest.getMatrixParameter().size();
		inputMeta.allocate(nrheaders, nrparams, nrmatrixparams);

		NameFieldPairDto dto;
		for (int i = 0; i < nrheaders; i++) {
			dto = rest.getHeaders().get(i);
			inputMeta.getHeaderField()[i] = dto.getField();
			inputMeta.getHeaderName()[i] = dto.getName();
		}
		// CHECKSTYLE:Indentation:OFF
		for (int i = 0; i < nrparams; i++) {
			dto = rest.getParameter().get(i);
			inputMeta.getParameterField()[i] = dto.getField();
			inputMeta.getParameterName()[i] = dto.getName();
		}

		// CHECKSTYLE:Indentation:OFF
		for (int i = 0; i < nrmatrixparams; i++) {
			dto = rest.getMatrixParameter().get(i);
			inputMeta.getMatrixParameterField()[i] = dto.getField();
			inputMeta.getMatrixParameterName()[i] = dto.getName();
		}

		inputMeta.setDynamicMethod(rest.isDynamicMethod());
		inputMeta.setMethodFieldName(rest.getMethodFieldName());
		inputMeta.setMethod(rest.getMethod());
		inputMeta.setUrl(rest.getUrl());
		inputMeta.setUrlField(rest.getUrlField());
		inputMeta.setUrlInField(rest.isUrlInField());
		inputMeta.setBodyField(rest.getBodyField());
		inputMeta.setFieldName(rest.getFieldName());
		inputMeta.setResultCodeFieldName(rest.getResultCodeFieldName());
		inputMeta.setResponseTimeFieldName(rest.getResponseTimeFieldName());
		inputMeta.setResponseHeaderFieldName(rest.getResponseHeaderFieldName());

		inputMeta.setHttpLogin(rest.getHttpLogin());
		inputMeta.setHttpPassword(rest.getHttpPassword());
		inputMeta.setProxyHost(rest.getProxyHost());
		inputMeta.setProxyPort(rest.getProxyPort());
		inputMeta.setPreemptive(rest.isPreemptive());

		inputMeta.setTrustStoreFile(rest.getTrustStoreFile());
		inputMeta.setTrustStorePassword(rest.getTrustStorePassword());
		inputMeta.setApplicationType(rest.getApplicationType());
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		RestMeta restMeta = (RestMeta) stepMetaInterface;

		String url = restMeta.getUrl();
		if (StringUtils.isBlank(url)) {
			return;
		}

		 Map<String, DataNode> itemDataNodes = DataNodeUtil.interfaceNodeParse("Http", url, "json", stepMeta.getName(),  sdr.getOutputStream().values() ) ;
		 sdr.getInputDataNodes().addAll(itemDataNodes.values());

		// 增加 系统节点 和 流节点的关系
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		List<Relationship> relationships = RelationshipUtil.inputStepRelationship(itemDataNodes, null,  sdr.getOutputStream(), stepMeta.getName(), from); // 建立接口系统和 输出流的关系
		sdr.getDataRelationship().addAll(relationships);
		// 输出字段

		String result = restMeta.getFieldName();
		String resultcode = restMeta.getResultCodeFieldName();
		String responseTime = restMeta.getResponseTimeFieldName();
		String responseHeader = restMeta.getResponseHeaderFieldName();

		DataNode setDataNode = null ;
		if( itemDataNodes.size() > 0 ) {
			setDataNode = itemDataNodes.values().iterator().next().getParent() ;
		}
		
		// 输入字段
		for (int i = 0; i < restMeta.getParameterField().length; i++) {
			String field = restMeta.getParameterField()[i];
			//输入字段和系统的关系
			sdr.addRelationship(RelationshipUtil.buildFieldRelationship(null, setDataNode, from, field, null));
			//输入字段和输出流的关系
			if(!Utils.isEmpty(result)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, result));
			}
			if(!Utils.isEmpty(resultcode)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, resultcode));
			}
			if(!Utils.isEmpty(responseTime)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, responseTime));
			}
			if(!Utils.isEmpty(responseHeader)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, responseHeader));
			}
		}

		for (int i = 0; i < restMeta.getHeaderField().length; i++) {
			String field = restMeta.getHeaderField()[i];
			//输入字段和系统的关系
			sdr.addRelationship(RelationshipUtil.buildFieldRelationship(null, setDataNode, from, field, null));
			//输入字段和输出流的关系
			if(!Utils.isEmpty(result)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, result));
			}
			if(!Utils.isEmpty(resultcode)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, resultcode));
			}
			if(!Utils.isEmpty(responseTime)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, responseTime));
			}
			if(!Utils.isEmpty(responseHeader)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, responseHeader));
			}
		}
		
		for (int i = 0; i < restMeta.getMatrixParameterField().length; i++) {
			String field = restMeta.getMatrixParameterField()[i];

			if(!Utils.isEmpty(result)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, result));
			}
			if(!Utils.isEmpty(resultcode)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, resultcode));
			}
			if(!Utils.isEmpty(responseTime)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, responseTime));
			}
			if(!Utils.isEmpty(responseHeader)) {
				sdr.addRelationship(RelationshipUtil.buildFieldRelationship(from, field, responseHeader));
			}
		}
	}

	@Override
	public int stepType() {
		return 6;
	}

	
}

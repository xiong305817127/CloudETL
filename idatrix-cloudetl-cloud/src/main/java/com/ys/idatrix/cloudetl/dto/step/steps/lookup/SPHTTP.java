package com.ys.idatrix.cloudetl.dto.step.steps.lookup;

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
import org.pentaho.di.trans.steps.http.HTTPMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.NameFieldPairDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - HTTP. 转换 org.pentaho.di.trans.steps.http.HTTPMeta
 * 使用 Get 方法 获取Http数据
 * @author FBZ
 * @since 11-22-2017
 */
@Component("SPHTTP")
@Scope("prototype")
public class SPHTTP implements StepParameter, StepDataRelationshipParser {

	private String socketTimeout;
	private String connectionTimeout;
	private String closeIdleConnectionsTime;

	/** URL / service to be called */
	private String url;

	/** function result: new value name */
	private String fieldName;

	/** The encoding to use for retrieval of the data */
	private String encoding;

	private boolean urlInField;

	private String urlField;

	private String proxyHost;

	private String proxyPort;

	private String httpLogin;

	private String httpPassword;

	private String resultCodeFieldName;
	private String responseTimeFieldName;
	private String responseHeaderFieldName;

	/** function arguments : fieldname */
	private List<NameFieldPairDto> argument;

	private List<NameFieldPairDto> headers;

	public String getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(String socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public String getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(String connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public String getCloseIdleConnectionsTime() {
		return closeIdleConnectionsTime;
	}

	public void setCloseIdleConnectionsTime(String closeIdleConnectionsTime) {
		this.closeIdleConnectionsTime = closeIdleConnectionsTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
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

	public List<NameFieldPairDto> getArgument() {
		return argument;
	}

	public void setArgument(List<NameFieldPairDto> argument) {
		this.argument = argument;
	}

	public List<NameFieldPairDto> getHeaders() {
		return headers;
	}

	public void setHeaders(List<NameFieldPairDto> headers) {
		this.headers = headers;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("argument", NameFieldPairDto.class);
		classMap.put("headers", NameFieldPairDto.class);

		return (SPHTTP) JSONObject.toBean(jsonObj, SPHTTP.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPHTTP http = new SPHTTP();
		HTTPMeta inputMeta = (HTTPMeta) stepMetaInterface;

		int nrargs = null == inputMeta.getArgumentField() ? 0 : inputMeta.getArgumentField().length;
		int nrheaders = null == inputMeta.getHeaderField() ? 0 : inputMeta.getHeaderField().length;

		http.allocate(nrargs, nrheaders);

		NameFieldPairDto dto;
		for (int i = 0; i < nrargs; i++) {
			dto = new NameFieldPairDto();
			http.getArgument().add(dto);
			dto.setField(Const.NVL(inputMeta.getArgumentField()[i], ""));
			dto.setName(Const.NVL(inputMeta.getArgumentParameter()[i], ""));
		}

		for (int i = 0; i < nrheaders; i++) {
			dto = new NameFieldPairDto();
			http.getHeaders().add(dto);
			dto.setField(inputMeta.getHeaderField()[i]);
			dto.setName(inputMeta.getHeaderParameter()[i]);
		}

		http.setSocketTimeout(Const.NVL(inputMeta.getSocketTimeout(), ""));
		http.setConnectionTimeout(Const.NVL(inputMeta.getConnectionTimeout(), ""));
		http.setCloseIdleConnectionsTime(Const.NVL(inputMeta.getCloseIdleConnectionsTime(), ""));

		http.setUrl(Const.NVL(StringEscapeHelper.encode(inputMeta.getUrl()), ""));
		http.setUrlInField(inputMeta.isUrlInField());
		http.setUrlField(Const.NVL(inputMeta.getUrlField(), ""));
		http.setEncoding(Const.NVL(inputMeta.getEncoding(), ""));

		http.setFieldName(Const.NVL(inputMeta.getFieldName(), ""));
		http.setHttpLogin(inputMeta.getHttpLogin());
		http.setHttpPassword(inputMeta.getHttpPassword());

		http.setProxyHost(inputMeta.getProxyHost());
		http.setProxyPort(inputMeta.getProxyPort());

		http.setResultCodeFieldName(inputMeta.getResultCodeFieldName());
		http.setResponseTimeFieldName(inputMeta.getResponseTimeFieldName());
		http.setResponseHeaderFieldName(inputMeta.getResponseHeaderFieldName());

		return http;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		HTTPMeta inputMeta = (HTTPMeta) stepMetaInterface;
		SPHTTP http = (SPHTTP) po;

		int nrargs = null == http.getArgument() ? 0 : http.getArgument().size();
		int nrheaders = null == http.getHeaders() ? 0 : http.getHeaders().size();

		inputMeta.allocate(nrargs, nrheaders);

		NameFieldPairDto dto;
		for (int i = 0; i < nrargs; i++) {
			dto = http.getArgument().get(i);
			inputMeta.getArgumentField()[i] = dto.getField();
			inputMeta.getArgumentParameter()[i] = dto.getName();
		}

		for (int i = 0; i < nrheaders; i++) {
			dto = http.getHeaders().get(i);
			inputMeta.getHeaderField()[i] = dto.getField();
			inputMeta.getHeaderParameter()[i] = dto.getName();
		}

		inputMeta.setUrl(StringEscapeHelper.decode(http.getUrl()));
		inputMeta.setUrlField(http.getUrlField());
		inputMeta.setUrlInField(http.isUrlInField());

		inputMeta.setFieldName(http.getFieldName());
		inputMeta.setEncoding(http.getEncoding());
		inputMeta.setHttpLogin(http.getHttpLogin());
		inputMeta.setHttpPassword(http.getHttpPassword());

		inputMeta.setProxyHost(http.getProxyHost());
		inputMeta.setProxyPort(http.getProxyPort());

		inputMeta.setResultCodeFieldName(http.getResultCodeFieldName());
		inputMeta.setResponseTimeFieldName(http.getResponseTimeFieldName());
		inputMeta.setResponseHeaderFieldName(http.getResponseHeaderFieldName());

		inputMeta.setSocketTimeout(http.getSocketTimeout());
		inputMeta.setConnectionTimeout(http.getConnectionTimeout());
		inputMeta.setCloseIdleConnectionsTime(http.getCloseIdleConnectionsTime());
	}

	private void allocate(int nrargs, int nrqueryparams) {
		argument = new ArrayList<>(nrargs);
		headers = new ArrayList<>(nrqueryparams);
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		HTTPMeta httpMeta = (HTTPMeta) stepMetaInterface;

		String url = httpMeta.getUrl();
		if (StringUtils.isBlank(url)) {
			return;
		}

		Map<String, DataNode> itemDataNodes = DataNodeUtil.interfaceNodeParse("Http", url, "json", stepMeta.getName(), sdr.getOutputStream().values());
		sdr.getInputDataNodes().addAll(itemDataNodes.values());
		
		// 增加 系统节点 和 流节点的关系
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		List<Relationship> relationships = RelationshipUtil.inputStepRelationship(itemDataNodes, null, sdr.getOutputStream(), stepMeta.getName(), from); //建立接口系统和 输出流的关系
		sdr.getDataRelationship().addAll(relationships);
		//输出字段
		
		String result = httpMeta.getFieldName() ;
		String resultcode = httpMeta.getResultCodeFieldName();
		String responseTime = httpMeta.getResponseTimeFieldName();
		String responseHeader = httpMeta.getResponseHeaderFieldName();
		
		//输入字段
		for (int i = 0; i < httpMeta.getArgumentField().length; i++) {
			String field = httpMeta.getArgumentField()[i] ;
			
			if(!Utils.isEmpty(result)) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, field, result) );
			}
			if(!Utils.isEmpty(resultcode)) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, field, resultcode) );
			}
			if(!Utils.isEmpty(responseTime)) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, field, responseTime) );
			}
			if(!Utils.isEmpty(responseHeader)) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, field, responseHeader) );
			}
			
		}

		for (int i = 0; i < httpMeta.getHeaderField().length; i++) {
			String field = httpMeta.getHeaderField()[i];
			
			if(!Utils.isEmpty(result)) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, field, result) );
			}
			if(!Utils.isEmpty(resultcode)) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, field, resultcode) );
			}
			if(!Utils.isEmpty(responseTime)) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, field, responseTime) );
			}
			if(!Utils.isEmpty(responseHeader)) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, field, responseHeader) );
			}
		}
		
	}
}

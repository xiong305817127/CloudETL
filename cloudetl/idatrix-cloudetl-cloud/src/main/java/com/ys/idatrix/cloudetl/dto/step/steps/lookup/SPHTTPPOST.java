package com.ys.idatrix.cloudetl.dto.step.steps.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.httppost.HTTPPOSTMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.HttpBodyParamDto;
import com.ys.idatrix.cloudetl.dto.step.parts.NameFieldPairDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - HTTPPOST. 转换 org.pentaho.di.trans.steps.httppost.HTTPPOSTMeta
 *  使用 POST 方法 获取Http数据
 * @author FBZ
 * @since 11-22-2017
 */
@Component("SPHTTPPOST")
@Scope("prototype")
public class SPHTTPPOST implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	private String socketTimeout;
	private String connectionTimeout;
	private String closeIdleConnectionsTime;

	/** URL / service to be called */
	private String url;

	/** function arguments : fieldname */
	private List<HttpBodyParamDto> argument;

	/** function query field : queryField */
	private List<NameFieldPairDto> querys;

	/** function result: new value name */
	private String fieldName;
	private String resultCodeFieldName;
	private String responseHeaderFieldName;
	private boolean urlInField;

	private String urlField;

	private String requestEntity;

	private String encoding;

	private Boolean postafile;

	private String proxyHost;

	private String proxyPort;

	private String httpLogin;

	private String httpPassword;

	private String responseTimeFieldName;

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

	public Boolean getPostafile() {
		return postafile;
	}

	public void setPostafile(Boolean postafile) {
		this.postafile = postafile;
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

	public String getRequestEntity() {
		return requestEntity;
	}

	public void setRequestEntity(String requestEntity) {
		this.requestEntity = requestEntity;
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

	public List<HttpBodyParamDto> getArgument() {
		return argument;
	}

	public void setArgument(List<HttpBodyParamDto> argument) {
		this.argument = argument;
	}

	public List<NameFieldPairDto> getQuerys() {
		return querys;
	}

	public void setQuerys(List<NameFieldPairDto> querys) {
		this.querys = querys;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("querys", NameFieldPairDto.class);
		classMap.put("argument", HttpBodyParamDto.class);

		return (SPHTTPPOST) JSONObject.toBean(jsonObj, SPHTTPPOST.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPHTTPPOST http = new SPHTTPPOST();
		HTTPPOSTMeta inputMeta = (HTTPPOSTMeta) stepMetaInterface;

		int nrargs = null == inputMeta.getArgumentField() ? 0 : inputMeta.getArgumentField().length;
		http.setArgument(new ArrayList<>(nrargs));

		HttpBodyParamDto pDto;
		for (int i = 0; i < nrargs; i++) {
			pDto = new HttpBodyParamDto();
			http.getArgument().add(pDto);
			pDto.setField(inputMeta.getArgumentField()[i]);
			pDto.setName(Const.NVL(inputMeta.getArgumentParameter()[i], ""));
			pDto.setHeader(null == inputMeta.getArgumentHeader() ? Boolean.FALSE : inputMeta.getArgumentHeader()[i]);
		}

		int nrqueryparams = null == inputMeta.getQueryField() ? 0 : inputMeta.getQueryField().length;
		http.setQuerys(new ArrayList<>(nrqueryparams));

		NameFieldPairDto dto;
		for (int i = 0; i < nrqueryparams; i++) {
			dto = new NameFieldPairDto();
			http.getQuerys().add(dto);
			dto.setField(inputMeta.getQueryField()[i]);
			dto.setName(inputMeta.getQueryParameter()[i]);
		}

		http.setSocketTimeout(Const.NVL(inputMeta.getSocketTimeout(), ""));
		http.setConnectionTimeout(Const.NVL(inputMeta.getConnectionTimeout(), ""));
		http.setCloseIdleConnectionsTime(Const.NVL(inputMeta.getCloseIdleConnectionsTime(), ""));

		http.setUrl(StringEscapeHelper.encode(inputMeta.getUrl()));
		http.setUrlInField(inputMeta.isUrlInField());
		http.setUrlField(inputMeta.getUrlField());
		http.setEncoding(inputMeta.getEncoding());

		http.setRequestEntity(inputMeta.getRequestEntity());
		http.setPostafile(inputMeta.isPostAFile());

		http.setFieldName(inputMeta.getFieldName());
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

		HTTPPOSTMeta inputMeta = (HTTPPOSTMeta) stepMetaInterface;
		SPHTTPPOST http = (SPHTTPPOST) po;

		int nrargs = null == http.getArgument() ? 0 : http.getArgument().size();
		inputMeta.allocate(nrargs);
		HttpBodyParamDto pDto;
		for (int i = 0; i < nrargs; i++) {
			pDto = http.getArgument().get(i);
			inputMeta.getArgumentField()[i] = pDto.getField();
			inputMeta.getArgumentParameter()[i] = Const.NVL(pDto.getName(), "");
			inputMeta.getArgumentHeader()[i] = BooleanUtils.isTrue(pDto.getHeader());
		}

		int nrqueryparams = null == http.getQuerys() ? 0 : http.getQuerys().size();
		inputMeta.allocateQuery(nrqueryparams);
		NameFieldPairDto dto;
		for (int i = 0; i < nrqueryparams; i++) {
			dto = http.getQuerys().get(i);
			inputMeta.getQueryField()[i] = dto.getField();
			inputMeta.getQueryParameter()[i] = null == dto.getName() ? "" : dto.getName();
		}

		inputMeta.setUrl(StringEscapeHelper.decode(http.getUrl()));
		inputMeta.setUrlField(http.getUrlField());
		inputMeta.setUrlInField(http.isUrlInField());

		inputMeta.setRequestEntity(http.getRequestEntity());
		inputMeta.setPostAFile(Boolean.TRUE.equals(http.getPostafile()) ? Boolean.TRUE : Boolean.FALSE);

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

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		HTTPPOSTMeta httpPostMeta = (HTTPPOSTMeta) stepMetaInterface;

		String url = httpPostMeta.getUrl();
		if (StringUtils.isBlank(url)) {
			return;
		}
		
		Map<String, DataNode> itemDataNodes = DataNodeUtil.interfaceNodeParse("Http", url, "json", stepMeta.getName(), sdr.getOutputStream().values());
		sdr.getInputDataNodes().addAll(itemDataNodes.values());

		// 增加 系统节点 和 流节点的关系
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		List<Relationship> relationships = RelationshipUtil.inputStepRelationship(itemDataNodes, null, sdr.getOutputStream(), stepMeta.getName(), from) ;
		sdr.getDataRelationship().addAll(relationships);
		
		// 输出字段
		String result = httpPostMeta.getFieldName();
		String resultcode = httpPostMeta.getResultCodeFieldName();
		String responseTime = httpPostMeta.getResponseTimeFieldName();
		String responseHeader = httpPostMeta.getResponseHeaderFieldName();

		DataNode setDataNode = null ;
		if( itemDataNodes.size() > 0 ) {
			setDataNode = itemDataNodes.values().iterator().next().getParent() ;
		}
		// 输入字段
		for (int i = 0; i < httpPostMeta.getArgumentField().length; i++) {
			String field = httpPostMeta.getArgumentField()[i];
			
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

		for (int i = 0; i < httpPostMeta.getQueryField().length; i++) {
			String field = httpPostMeta.getQueryField()[i];
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
	}
	
	@Override
	public int stepType() {
		return 6;
	}

}

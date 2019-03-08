package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.ElasticSearchServerDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.OutputFieldsDto;

/**
 * ElasticSearch 批量插入更新
 * <br>
 * fields 为域信息配置, <br>
 * ......outputField:输出到ElasticSearch服务器上的字段名 , <br>
 * ......inputField:输入流字段名,${fieldName}方式时表示输入流值从环境变量中读取, 当为多文件批量处理时,启动params中 使用 fieldName_fileName 代表每一个文件对应的值 <br>
 *
 * @author XH
 * @since 2018年9月19日
 *
 */
public class ElasticSearchDto extends StepDto implements Serializable{

	private static final long serialVersionUID = -2141760248784099638L;
	
	public static final String type = "ElasticSearchBulk" ;
	
	private String index = "resource_catalog" ; //索引名称
	private String indexType = "resource_content" ; //类型
	
	private String idInField = "_id" ; // id输入域名
	private boolean overWriteIfSameId = true ; //相同ID时是否覆盖(是否更新/新建)
	
	private boolean stopOnError = false ; //发生错误时 是否停止

	List<OutputFieldsDto> fields ;
	
	List<ElasticSearchServerDto> servers ; //服务器信息 ,可为空,为空时读取服务器配置
	Map<String,String> settings ; // ElasticSearch 设置信息,可为空,为空时读物服务器配置
	
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getIdInField() {
		return idInField;
	}

	public void setIdInField(String idInField) {
		this.idInField = idInField;
	}

	public boolean isOverWriteIfSameId() {
		return overWriteIfSameId;
	}

	public void setOverWriteIfSameId(boolean overWriteIfSameId) {
		this.overWriteIfSameId = overWriteIfSameId;
	}

	public boolean isStopOnError() {
		return stopOnError;
	}

	public void setStopOnError(boolean stopOnError) {
		this.stopOnError = stopOnError;
	}

	public List<OutputFieldsDto> getFields() {
		return fields;
	}

	public void setFields(List<OutputFieldsDto> fields) {
		this.fields = fields;
	}
	
	public void addField(OutputFieldsDto outputField) {
		if(this.fields == null) {
			this.fields =  new ArrayList<OutputFieldsDto>();
		}
		this.fields.add(outputField);
	}

	public List<ElasticSearchServerDto> getServers() {
		return servers;
	}

	public void setServers(List<ElasticSearchServerDto> servers) {
		this.servers = servers;
	}

	public void addServer(ElasticSearchServerDto server) {
		if(this.servers == null) {
			this.servers =  new ArrayList<ElasticSearchServerDto>();
		}
		this.servers.add(server);
	}
	
	public Map<String, String> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}
	
	public void addSetting(String key ,String value) {
		if(this.settings == null) {
			this.settings =  new HashMap<String,String>();
		}
		this.settings.put(key, value);
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isJobStep() {
		return false;
	}

	@Override
	public String toString() {
		return "ElasticSearchDto [index=" + index + ", indexType=" + indexType + ", idInField=" + idInField
				+ ", overWriteIfSameId=" + overWriteIfSameId + ", stopOnError=" + stopOnError + ", fields=" + fields
				+ ", servers=" + servers + ", settings=" + settings + ", super =" + super.toString() + "]";
	}
	
	
}

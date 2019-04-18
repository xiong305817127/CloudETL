/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.engine;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for default run engine details.
 * @author JW
 * @since 2017年7月5日
 *
 */
@ApiModel("kettle引擎详细信息")
public class EngineDetailsDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("服务器")
	private String server;
	
	@ApiModelProperty("描述")
	private String description;
	
	@ApiModelProperty("是否集群方式")
	private boolean clustered;
	
	@ApiModelProperty("是否只读")
	private boolean readOnly;
	
	@ApiModelProperty("是否发送资源到远端")
	private boolean sendResources=true;
	
	@ApiModelProperty("是否远程日志本地化")
	private boolean logRemoteExecutionLocally;
	
	@ApiModelProperty("是否远程执行方式")
	private boolean remote;
	
	@ApiModelProperty("是否本地执行方式")
	private boolean local;
	
	@ApiModelProperty("是否显示转换")
	private boolean showTransformations;
	
	public String getOwner() {
    	if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}	
	
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name 要设置的 name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return server
	 */
	public String getServer() {
		return server;
	}
	/**
	 * @param server 要设置的 server
	 */
	public void setServer(String server) {
		this.server = server;
	}
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description 要设置的 description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return clustered
	 */
	public boolean isClustered() {
		return clustered;
	}
	/**
	 * @param clustered 要设置的 clustered
	 */
	public void setClustered(boolean clustered) {
		this.clustered = clustered;
	}
	/**
	 * @return readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	/**
	 * @param readOnly 要设置的 readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	/**
	 * @return sendResources
	 */
	public boolean isSendResources() {
		return sendResources;
	}
	/**
	 * @param sendResources 要设置的 sendResources
	 */
	public void setSendResources(boolean sendResources) {
		this.sendResources = sendResources;
	}
	/**
	 * @return logRemoteExecutionLocally
	 */
	public boolean isLogRemoteExecutionLocally() {
		return logRemoteExecutionLocally;
	}
	/**
	 * @param logRemoteExecutionLocally 要设置的 logRemoteExecutionLocally
	 */
	public void setLogRemoteExecutionLocally(boolean logRemoteExecutionLocally) {
		this.logRemoteExecutionLocally = logRemoteExecutionLocally;
	}
	/**
	 * @return remote
	 */
	public boolean isRemote() {
		return remote;
	}
	/**
	 * @param remote 要设置的 remote
	 */
	public void setRemote(boolean remote) {
		this.remote = remote;
	}
	/**
	 * @return local
	 */
	public boolean isLocal() {
		return local;
	}
	/**
	 * @param local 要设置的 local
	 */
	public void setLocal(boolean local) {
		this.local = local;
	}
	/**
	 * @return showTransformations
	 */
	public boolean isShowTransformations() {
		return showTransformations;
	}
	/**
	 * @param showTransformations 要设置的 showTransformations
	 */
	public void setShowTransformations(boolean showTransformations) {
		this.showTransformations = showTransformations;
	}
	
	/* 
	 * Build text.
	 */
	@Override
	public String toString() {
		return "EngineDetailsDto [name=" + name + ", server=" + server + ", description=" + description + ", clustered="
				+ clustered + ", readOnly=" + readOnly + ", sendResources=" + sendResources
				+ ", logRemoteExecutionLocally=" + logRemoteExecutionLocally + ", remote=" + remote + ", local=" + local
				+ ", showTransformations=" + showTransformations + "]";
	}
	
}

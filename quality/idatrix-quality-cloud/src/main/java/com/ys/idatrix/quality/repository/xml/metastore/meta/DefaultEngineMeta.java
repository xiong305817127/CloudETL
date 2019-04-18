/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.repository.xml.metastore.meta;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Meta data for default run configuration details.
 * @author JW
 * @since 2017年6月30日
 *
 */
public class DefaultEngineMeta {
	
	private String name;
	private String server;
	private String description;
	private boolean clustered;
	private boolean readOnly;
	private boolean sendResources;
	private boolean logRemoteExecutionLocally;
	private boolean remote;
	private boolean local;
	private boolean showTransformations;
	
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
	
	public Map<String,Object> getRunConfigurationMap(){
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("name", name);
		result.put("server", server);
		result.put("description",description);
		result.put("clustered", clustered);
		result.put("readOnly", readOnly);
		result.put("sendResources", sendResources);
		result.put("logRemoteExecutionLocally", logRemoteExecutionLocally);
		result.put("remote", remote);
		result.put("local", local);
		result.put("showTransformations", showTransformations);
		
		return  result ;
		
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultEngineMeta [name=" + name + ", server=" + server + ", description=" + description
				+ ", clustered=" + clustered + ", readOnly=" + readOnly + ", sendResources=" + sendResources
				+ ", logRemoteExecutionLocally=" + logRemoteExecutionLocally + ", remote=" + remote + ", local=" + local
				+ ", showTransformations=" + showTransformations + "]";
	}
	
}

package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;

public class SftpPutDto  extends StepDto implements Serializable{

	private static final long serialVersionUID = -5952039286954253049L;

	public static  final String type ="SFTPPUT";
	
	private String serverName;
	private String serverPort = "22";
	private String userName;
	private String password;
	
	private String sftpDirectory;
	
	private String localDirectory;
	private boolean includeSubFolders = true;
	private String fileMask;
	
	private boolean successWhenNoFile = true; //本地没有文件时 成功
	private boolean addFilenameResut = true;
	private boolean createRemoteFolder = true;
	
	private boolean usekeyfilename = false;
	private String keyfilename;
	private String keyfilepass;
	
	private String proxyType;
	private String proxyHost;
	private String proxyPort;
	private String proxyUsername;
	private String proxyPassword;
	
	private int afterFTPS = 0; // 0:什么事都不做  1:上传之后删除原文件 2:上传之后移动文件到destinationfolder
	private String destinationfolder;
	
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSftpDirectory() {
		return sftpDirectory;
	}

	public void setSftpDirectory(String sftpDirectory) {
		this.sftpDirectory = sftpDirectory;
	}

	public String getLocalDirectory() {
		return localDirectory;
	}

	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	public boolean isIncludeSubFolders() {
		return includeSubFolders;
	}

	public void setIncludeSubFolders(boolean includeSubFolders) {
		this.includeSubFolders = includeSubFolders;
	}

	public String getFileMask() {
		return fileMask;
	}

	public void setFileMask(String fileMask) {
		this.fileMask = fileMask;
	}

	public boolean isSuccessWhenNoFile() {
		return successWhenNoFile;
	}

	public void setSuccessWhenNoFile(boolean successWhenNoFile) {
		this.successWhenNoFile = successWhenNoFile;
	}

	public boolean isAddFilenameResut() {
		return addFilenameResut;
	}

	public void setAddFilenameResut(boolean addFilenameResut) {
		this.addFilenameResut = addFilenameResut;
	}

	public boolean isCreateRemoteFolder() {
		return createRemoteFolder;
	}

	public void setCreateRemoteFolder(boolean createRemoteFolder) {
		this.createRemoteFolder = createRemoteFolder;
	}

	public boolean isUsekeyfilename() {
		return usekeyfilename;
	}

	public void setUsekeyfilename(boolean usekeyfilename) {
		this.usekeyfilename = usekeyfilename;
	}

	public String getKeyfilename() {
		return keyfilename;
	}

	public void setKeyfilename(String keyfilename) {
		this.keyfilename = keyfilename;
	}

	public String getKeyfilepass() {
		return keyfilepass;
	}

	public void setKeyfilepass(String keyfilepass) {
		this.keyfilepass = keyfilepass;
	}

	public String getProxyType() {
		return proxyType;
	}

	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
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

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public int getAfterFTPS() {
		return afterFTPS;
	}

	public void setAfterFTPS(int afterFTPS) {
		this.afterFTPS = afterFTPS;
	}

	public String getDestinationfolder() {
		return destinationfolder;
	}

	public void setDestinationfolder(String destinationfolder) {
		this.destinationfolder = destinationfolder;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isJobStep() {
		return true;
	}

	@Override
	public String toString() {
		return "SftpPutDto [serverName=" + serverName + ", serverPort=" + serverPort + ", userName=" + userName
				+ ", password=" + password + ", sftpDirectory=" + sftpDirectory + ", localDirectory=" + localDirectory
				+ ", includeSubFolders=" + includeSubFolders + ", fileMask=" + fileMask + ", successWhenNoFile="
				+ successWhenNoFile + ", addFilenameResut=" + addFilenameResut + ", createRemoteFolder="
				+ createRemoteFolder + ", usekeyfilename=" + usekeyfilename + ", keyfilename=" + keyfilename
				+ ", keyfilepass=" + keyfilepass + ", proxyType=" + proxyType + ", proxyHost=" + proxyHost
				+ ", proxyPort=" + proxyPort + ", proxyUsername=" + proxyUsername + ", proxyPassword=" + proxyPassword
				+ ", afterFTPS=" + afterFTPS + ", destinationfolder=" + destinationfolder + ", super="
				+ super.toString() + "]";
	}

}

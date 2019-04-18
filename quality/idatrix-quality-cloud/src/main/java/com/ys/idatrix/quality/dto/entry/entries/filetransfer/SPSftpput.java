/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.entries.filetransfer;

import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.sftpput.JobEntrySFTPPUT;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.entry.entries.EntryParameter;
import com.ys.idatrix.quality.ext.utils.FilePathUtil;
import com.ys.idatrix.quality.ext.utils.FilePathUtil.FileType;

import net.sf.json.JSONObject;

/**
 * Entry - Sftpput 上传. 转换 org.pentaho.di.job.entries.sftpput.JobEntrySFTPPUT
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPsftpput")
@Scope("prototype")
public class SPSftpput implements EntryParameter {

	String serverName;
	String serverPort ="22";
	String userName;
	String password;
	String sftpDirectory;
	String localDirectory;
	String wildcard;
	
	boolean copyprevious = false;
	boolean copypreviousfiles = false;
	boolean addFilenameResut = false;
	boolean usekeyfilename = false;
	String keyfilename;
	String keyfilepass;
	String compression = "none";
	String proxyType;
	String proxyHost;
	String proxyPort;
	String proxyUsername;
	String proxyPassword;
	boolean createRemoteFolder;
	int afterFTPS;
	String destinationfolder;
	boolean createDestinationFolder = true;
	boolean successWhenNoFile = true;
	
	boolean includeSubFolders = true;

	
	
	/**
	 * @return serverName
	 */
	public String getServerName() {
		return serverName;
	}
	/**
	 * @param  设置 serverName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	/**
	 * @return serverPort
	 */
	public String getServerPort() {
		return serverPort;
	}
	/**
	 * @param  设置 serverPort
	 */
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	/**
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param  设置 userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param  设置 password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return sftpDirectory
	 */
	public String getSftpDirectory() {
		return sftpDirectory;
	}
	/**
	 * @param  设置 sftpDirectory
	 */
	public void setSftpDirectory(String sftpDirectory) {
		this.sftpDirectory = sftpDirectory;
	}
	/**
	 * @return localDirectory
	 */
	public String getLocalDirectory() {
		return localDirectory;
	}
	/**
	 * @param  设置 localDirectory
	 */
	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}
	/**
	 * @return wildcard
	 */
	public String getWildcard() {
		return wildcard;
	}
	/**
	 * @param  设置 wildcard
	 */
	public void setWildcard(String wildcard) {
		this.wildcard = wildcard;
	}
	/**
	 * @return copyprevious
	 */
	public boolean isCopyprevious() {
		return copyprevious;
	}
	/**
	 * @param  设置 copyprevious
	 */
	public void setCopyprevious(boolean copyprevious) {
		this.copyprevious = copyprevious;
	}
	/**
	 * @return copypreviousfiles
	 */
	public boolean isCopypreviousfiles() {
		return copypreviousfiles;
	}
	/**
	 * @param  设置 copypreviousfiles
	 */
	public void setCopypreviousfiles(boolean copypreviousfiles) {
		this.copypreviousfiles = copypreviousfiles;
	}
	/**
	 * @return addFilenameResut
	 */
	public boolean isAddFilenameResut() {
		return addFilenameResut;
	}
	/**
	 * @param  设置 addFilenameResut
	 */
	public void setAddFilenameResut(boolean addFilenameResut) {
		this.addFilenameResut = addFilenameResut;
	}
	/**
	 * @return usekeyfilename
	 */
	public boolean isUsekeyfilename() {
		return usekeyfilename;
	}
	/**
	 * @param  设置 usekeyfilename
	 */
	public void setUsekeyfilename(boolean usekeyfilename) {
		this.usekeyfilename = usekeyfilename;
	}
	/**
	 * @return keyfilename
	 */
	public String getKeyfilename() {
		return keyfilename;
	}
	/**
	 * @param  设置 keyfilename
	 */
	public void setKeyfilename(String keyfilename) {
		this.keyfilename = keyfilename;
	}
	/**
	 * @return keyfilepass
	 */
	public String getKeyfilepass() {
		return keyfilepass;
	}
	/**
	 * @param  设置 keyfilepass
	 */
	public void setKeyfilepass(String keyfilepass) {
		this.keyfilepass = keyfilepass;
	}
	/**
	 * @return compression
	 */
	public String getCompression() {
		return compression;
	}
	/**
	 * @param  设置 compression
	 */
	public void setCompression(String compression) {
		this.compression = compression;
	}
	/**
	 * @return proxyType
	 */
	public String getProxyType() {
		return proxyType;
	}
	/**
	 * @param  设置 proxyType
	 */
	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
	}
	/**
	 * @return proxyHost
	 */
	public String getProxyHost() {
		return proxyHost;
	}
	/**
	 * @param  设置 proxyHost
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	/**
	 * @return proxyPort
	 */
	public String getProxyPort() {
		return proxyPort;
	}
	/**
	 * @param  设置 proxyPort
	 */
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}
	/**
	 * @return proxyUsername
	 */
	public String getProxyUsername() {
		return proxyUsername;
	}
	/**
	 * @param  设置 proxyUsername
	 */
	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}
	/**
	 * @return proxyPassword
	 */
	public String getProxyPassword() {
		return proxyPassword;
	}
	/**
	 * @param  设置 proxyPassword
	 */
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
	/**
	 * @return createRemoteFolder
	 */
	public boolean isCreateRemoteFolder() {
		return createRemoteFolder;
	}
	/**
	 * @param  设置 createRemoteFolder
	 */
	public void setCreateRemoteFolder(boolean createRemoteFolder) {
		this.createRemoteFolder = createRemoteFolder;
	}
	/**
	 * @return afterFTPS
	 */
	public int getAfterFTPS() {
		return afterFTPS;
	}
	/**
	 * @param  设置 afterFTPS
	 */
	public void setAfterFTPS(int afterFTPS) {
		this.afterFTPS = afterFTPS;
	}
	/**
	 * @return destinationfolder
	 */
	public String getDestinationfolder() {
		return destinationfolder;
	}
	/**
	 * @param  设置 destinationfolder
	 */
	public void setDestinationfolder(String destinationfolder) {
		this.destinationfolder = destinationfolder;
	}
	/**
	 * @return createDestinationFolder
	 */
	public boolean isCreateDestinationFolder() {
		return createDestinationFolder;
	}
	/**
	 * @param  设置 createDestinationFolder
	 */
	public void setCreateDestinationFolder(boolean createDestinationFolder) {
		this.createDestinationFolder = createDestinationFolder;
	}
	/**
	 * @return successWhenNoFile
	 */
	public boolean isSuccessWhenNoFile() {
		return successWhenNoFile;
	}
	/**
	 * @param  设置 successWhenNoFile
	 */
	public void setSuccessWhenNoFile(boolean successWhenNoFile) {
		this.successWhenNoFile = successWhenNoFile;
	}
	
	/**
	 * @return the includeSubFolders
	 */
	public boolean isIncludeSubFolders() {
		return includeSubFolders;
	}
	/**
	 * @param  设置 includeSubFolders
	 */
	public void setIncludeSubFolders(boolean includeSubFolders) {
		this.includeSubFolders = includeSubFolders;
	}
	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPSftpput) JSONObject.toBean(jsonObj, SPSftpput.class);
	}
	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSftpput spSftpput= new SPSftpput();
		JobEntrySFTPPUT jobentrysftpput= (JobEntrySFTPPUT )entryMetaInterface;

		spSftpput.setDestinationfolder(FilePathUtil.getRelativeFileName(null,jobentrysftpput.getDestinationFolder(),FileType.input));
		spSftpput.setAfterFTPS(jobentrysftpput.getAfterFTPS());
		spSftpput.setPassword(Encr.decryptPasswordOptionallyEncrypted(jobentrysftpput.getPassword()));
		spSftpput.setServerName(jobentrysftpput.getServerName());
		spSftpput.setUserName(jobentrysftpput.getUserName());
		spSftpput.setWildcard(jobentrysftpput.getWildcard());
		spSftpput.setKeyfilename(jobentrysftpput.getKeyFilename());
		spSftpput.setCompression(jobentrysftpput.getCompression());
		spSftpput.setServerPort(jobentrysftpput.getServerPort());
		spSftpput.setProxyType(jobentrysftpput.getProxyType());
		spSftpput.setProxyHost(jobentrysftpput.getProxyHost());
		spSftpput.setProxyPort(jobentrysftpput.getProxyPort());
		spSftpput.setProxyUsername(jobentrysftpput.getProxyUsername());
		spSftpput.setProxyPassword(Encr.decryptPasswordOptionallyEncrypted(jobentrysftpput.getProxyPassword()));
		spSftpput.setCreateDestinationFolder(jobentrysftpput.isCreateDestinationFolder());
		spSftpput.setSuccessWhenNoFile(jobentrysftpput.isSuccessWhenNoFile());
		spSftpput.setCopyprevious(jobentrysftpput.isCopyPrevious());
		spSftpput.setCopypreviousfiles(jobentrysftpput.isCopyPreviousFiles());
		spSftpput.setAddFilenameResut(jobentrysftpput.isAddFilenameResut());
		spSftpput.setCreateRemoteFolder(jobentrysftpput.isCreateRemoteFolder());

		spSftpput.setUsekeyfilename( jobentrysftpput.isUseKeyFile()) ;
		spSftpput.setKeyfilepass( Encr.decryptPasswordOptionallyEncrypted(jobentrysftpput.getKeyPassPhrase())) ;

		spSftpput.setLocalDirectory(FilePathUtil.getRelativeFileName(null,jobentrysftpput.getLocalDirectory(), FileType.input));
		spSftpput.setSftpDirectory( jobentrysftpput.getScpDirectory()) ;
		spSftpput.setIncludeSubFolders(jobentrysftpput.isIncludeSubFolders());
		return spSftpput;
	}
	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSftpput spSftpput= (SPSftpput)po;
		JobEntrySFTPPUT  jobentrysftpput= (JobEntrySFTPPUT )entryMetaInterface;

		jobentrysftpput.setSuccessWhenNoFile(spSftpput.isSuccessWhenNoFile());
		jobentrysftpput.setAfterFTPS(spSftpput.getAfterFTPS());
		jobentrysftpput.setPassword(Encr.encryptPasswordIfNotUsingVariables(spSftpput.getPassword()));
		jobentrysftpput.setServerName(spSftpput.getServerName());
		jobentrysftpput.setUserName(spSftpput.getUserName());
		jobentrysftpput.setWildcard(spSftpput.getWildcard());
		jobentrysftpput.setCopyPrevious(spSftpput.isCopyprevious());
		jobentrysftpput.setKeyFilename(spSftpput.getKeyfilename());
		jobentrysftpput.setCompression(spSftpput.getCompression());
		jobentrysftpput.setServerPort(spSftpput.getServerPort());
		jobentrysftpput.setProxyType(spSftpput.getProxyType());
		jobentrysftpput.setProxyHost(spSftpput.getProxyHost());
		jobentrysftpput.setProxyPort(spSftpput.getProxyPort());
		jobentrysftpput.setProxyUsername(spSftpput.getProxyUsername());
		jobentrysftpput.setProxyPassword(Encr.encryptPasswordIfNotUsingVariables(spSftpput.getProxyPassword()));
		jobentrysftpput.setCopyPreviousFiles(spSftpput.isCopypreviousfiles());
		jobentrysftpput.setAddFilenameResut(spSftpput.isAddFilenameResut());
		jobentrysftpput.setCreateRemoteFolder(spSftpput.isCreateRemoteFolder());
		jobentrysftpput.setCreateDestinationFolder(spSftpput.isCreateDestinationFolder() ) ;
		jobentrysftpput.setUseKeyFile(spSftpput.isUsekeyfilename() ) ;
		jobentrysftpput.setKeyPassPhrase(Encr.encryptPasswordIfNotUsingVariables(spSftpput.getKeyfilepass()) ) ;

		jobentrysftpput.setLocalDirectory(spSftpput.getLocalDirectory());
		if(!Utils.isEmpty(spSftpput.getLocalDirectory())){
			jobentrysftpput.setLocalDirectory(FilePathUtil.getRealFileName(null,spSftpput.getLocalDirectory(),FileType.input));
		}
		jobentrysftpput.setDestinationFolder(spSftpput.getDestinationfolder());
		if(!Utils.isEmpty(spSftpput.getDestinationfolder())){
			jobentrysftpput.setDestinationFolder(FilePathUtil.getRealFileName(null,spSftpput.getDestinationfolder(),FileType.input));
		}
		jobentrysftpput.setScpDirectory(spSftpput.getSftpDirectory() ) ;
		jobentrysftpput.setIncludeSubFolders(spSftpput.isIncludeSubFolders());
	}

}

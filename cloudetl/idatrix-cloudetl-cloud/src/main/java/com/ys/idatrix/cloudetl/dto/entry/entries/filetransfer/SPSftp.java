/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry.entries.filetransfer;

import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.sftp.JobEntrySFTP;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.entry.entries.EntryParameter;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil.FileType;

import net.sf.json.JSONObject;

/**
 * Entry - Sftp 下载. 转换 org.pentaho.di.job.entries.sftp.JobEntrySFTP
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPsftp")
@Scope("prototype")
public class SPSftp implements EntryParameter {

	String serverName;
	String serverPort;
	String userName;
	String password;
	String sftpDirectory;
	String targetDirectory;
	String wildcard;
	boolean remove;
	boolean isaddresult;
	boolean createtargetfolder;
	boolean copyprevious;
	boolean usekeyfilename;
	String keyfilename;
	String keyfilepass;
	String compression;
	String proxyType;
	String proxyHost;
	String proxyPort;
	String proxyUsername;
	String proxyPassword;
	
	boolean includeSubFolders = true;

	/**
	 * @return serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param 设置
	 *            serverName
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
	 * @param 设置
	 *            serverPort
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
	 * @param 设置
	 *            userName
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
	 * @param 设置
	 *            password
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
	 * @param 设置
	 *            sftpDirectory
	 */
	public void setSftpDirectory(String sftpDirectory) {
		this.sftpDirectory = sftpDirectory;
	}

	/**
	 * @return targetDirectory
	 */
	public String getTargetDirectory() {
		return targetDirectory;
	}

	/**
	 * @param 设置
	 *            targetDirectory
	 */
	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	/**
	 * @return wildcard
	 */
	public String getWildcard() {
		return wildcard;
	}

	/**
	 * @param 设置
	 *            wildcard
	 */
	public void setWildcard(String wildcard) {
		this.wildcard = wildcard;
	}

	/**
	 * @return remove
	 */
	public boolean isRemove() {
		return remove;
	}

	/**
	 * @param 设置
	 *            remove
	 */
	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	/**
	 * @return isaddresult
	 */
	public boolean isIsaddresult() {
		return isaddresult;
	}

	/**
	 * @param 设置
	 *            isaddresult
	 */
	public void setIsaddresult(boolean isaddresult) {
		this.isaddresult = isaddresult;
	}

	/**
	 * @return createtargetfolder
	 */
	public boolean isCreatetargetfolder() {
		return createtargetfolder;
	}

	/**
	 * @param 设置
	 *            createtargetfolder
	 */
	public void setCreatetargetfolder(boolean createtargetfolder) {
		this.createtargetfolder = createtargetfolder;
	}

	/**
	 * @return copyprevious
	 */
	public boolean isCopyprevious() {
		return copyprevious;
	}

	/**
	 * @param 设置
	 *            copyprevious
	 */
	public void setCopyprevious(boolean copyprevious) {
		this.copyprevious = copyprevious;
	}

	/**
	 * @return usekeyfilename
	 */
	public boolean isUsekeyfilename() {
		return usekeyfilename;
	}

	/**
	 * @param 设置
	 *            usekeyfilename
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
	 * @param 设置
	 *            keyfilename
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
	 * @param 设置
	 *            keyfilepass
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
	 * @param 设置
	 *            compression
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
	 * @param 设置
	 *            proxyType
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
	 * @param 设置
	 *            proxyHost
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
	 * @param 设置
	 *            proxyPort
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
	 * @param 设置
	 *            proxyUsername
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
	 * @param 设置
	 *            proxyPassword
	 */
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
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
		return (SPSftp) JSONObject.toBean(jsonObj, SPSftp.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSftp spSftp = new SPSftp();
		JobEntrySFTP jobentrysftp = (JobEntrySFTP) entryMetaInterface;

		spSftp.setUserName(jobentrysftp.getUserName());
		spSftp.setWildcard(jobentrysftp.getWildcard());
		spSftp.setRemove(jobentrysftp.getRemove());
		spSftp.setServerPort(jobentrysftp.getServerPort());
		spSftp.setKeyfilename(jobentrysftp.getKeyFilename());
		spSftp.setProxyType(jobentrysftp.getProxyType());
		spSftp.setProxyHost(jobentrysftp.getProxyHost());
		spSftp.setProxyPort(jobentrysftp.getProxyPort());
		spSftp.setProxyUsername(jobentrysftp.getProxyUsername());
		spSftp.setProxyPassword(Encr.decryptPasswordOptionallyEncrypted(jobentrysftp.getProxyPassword()));
		spSftp.setPassword(Encr.decryptPasswordOptionallyEncrypted(jobentrysftp.getPassword()));
		spSftp.setCompression(jobentrysftp.getCompression());
		spSftp.setServerName(jobentrysftp.getServerName());
		spSftp.setCopyprevious(jobentrysftp.isCopyPrevious());
		spSftp.setCreatetargetfolder(jobentrysftp.isCreateTargetFolder());
		spSftp.setIsaddresult(jobentrysftp.isAddToResult());
		spSftp.setUsekeyfilename(jobentrysftp.isUseKeyFile());
		spSftp.setKeyfilepass(Encr.decryptPasswordOptionallyEncrypted(jobentrysftp.getKeyPassPhrase()));
		//local
		spSftp.setTargetDirectory(FilePathUtil.getRelativeFileName(null,jobentrysftp.getTargetDirectory(),FileType.input));
		//sftp dir
		spSftp.setSftpDirectory(jobentrysftp.getScpDirectory());
		spSftp.setIncludeSubFolders(jobentrysftp.isIncludeSubFolders());

		return spSftp;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSftp spSftp = (SPSftp) po;
		JobEntrySFTP jobentrysftp = (JobEntrySFTP) entryMetaInterface;

		jobentrysftp.setServerName(spSftp.getServerName());
		jobentrysftp.setUserName(spSftp.getUserName());
		jobentrysftp.setWildcard(spSftp.getWildcard());
		jobentrysftp.setCopyPrevious(spSftp.isCopyprevious());
		jobentrysftp.setRemove(spSftp.isRemove());
		jobentrysftp.setServerPort(spSftp.getServerPort());
		jobentrysftp.setKeyFilename(spSftp.getKeyfilename());
		jobentrysftp.setProxyType(spSftp.getProxyType());
		jobentrysftp.setProxyHost(spSftp.getProxyHost());
		jobentrysftp.setProxyPort(spSftp.getProxyPort());
		jobentrysftp.setProxyUsername(spSftp.getProxyUsername());
		jobentrysftp.setProxyPassword(Encr.encryptPasswordIfNotUsingVariables(spSftp.getProxyPassword()));
		jobentrysftp.setCreateTargetFolder(spSftp.isCreatetargetfolder());
		jobentrysftp.setPassword(Encr.encryptPasswordIfNotUsingVariables(spSftp.getPassword()));
		jobentrysftp.setCompression(spSftp.getCompression());

		jobentrysftp.setAddToResult(spSftp.isIsaddresult());
		jobentrysftp.setUseKeyFile(spSftp.isUsekeyfilename());
		jobentrysftp.setKeyPassPhrase(Encr.encryptPasswordIfNotUsingVariables(spSftp.getKeyfilepass()));
		jobentrysftp.setIncludeSubFolders(spSftp.isIncludeSubFolders());
		//local
		jobentrysftp.setTargetDirectory(spSftp.getTargetDirectory());
		if( !Utils.isEmpty(spSftp.getTargetDirectory())){
			jobentrysftp.setTargetDirectory(FilePathUtil.getRealFileName(null,spSftp.getTargetDirectory(), FileType.input));
		}
		//sftp dir
		jobentrysftp.setScpDirectory(spSftp.getSftpDirectory());


	}

}

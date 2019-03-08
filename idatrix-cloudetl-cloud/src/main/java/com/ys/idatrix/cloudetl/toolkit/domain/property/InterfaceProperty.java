/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain.property;

import java.io.Serializable;

/**
 * InterfaceProperty <br/>
 * @author JW
 * @since 2017年11月16日
 * 
 */
public class InterfaceProperty  extends BaseProperty  implements Serializable{

	private static final long serialVersionUID = 1662944705775070243L;

	private String type;
	
	private String protocol;
	
	private String action;
	
	private String format;
	
	private String uri;
	
	private String encrypt;
	
	private String compress;
	
	private String account;
	
	private String synchronization;

	public InterfaceProperty(String name) {
		super(name);
	}
	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type 要设置的 type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol 要设置的 protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action 要设置的 action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format 要设置的 format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri 要设置的 uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return encrypt
	 */
	public String getEncrypt() {
		return encrypt;
	}

	/**
	 * @param encrypt 要设置的 encrypt
	 */
	public void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}

	/**
	 * @return compress
	 */
	public String getCompress() {
		return compress;
	}

	/**
	 * @param compress 要设置的 compress
	 */
	public void setCompress(String compress) {
		this.compress = compress;
	}

	/**
	 * @return account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account 要设置的 account
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return synchronization
	 */
	public String getSynchronization() {
		return synchronization;
	}

	/**
	 * @param synchronization 要设置的 synchronization
	 */
	public void setSynchronization(String synchronization) {
		this.synchronization = synchronization;
	}

}

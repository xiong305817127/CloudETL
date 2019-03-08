/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain.property;

import java.io.Serializable;

/**
 * FileSystemProperty <br/>
 * @author JW
 * @since 2018年1月22日
 * 
 */
public class FileSystemProperty  extends BaseProperty  implements Serializable{

	private static final long serialVersionUID = -2963300111227711546L;

	private String type;
	
	private String format;
	
	private String root;
	
	private String access;

	public FileSystemProperty(String name) {
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
	 * @return root
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * @param root 要设置的 root
	 */
	public void setRoot(String root) {
		this.root = root;
	}

	/**
	 * @return access
	 */
	public String getAccess() {
		return access;
	}

	/**
	 * @param access 要设置的 access
	 */
	public void setAccess(String access) {
		this.access = access;
	}

}

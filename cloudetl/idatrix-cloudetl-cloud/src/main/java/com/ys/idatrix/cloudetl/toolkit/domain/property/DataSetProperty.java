/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain.property;

import java.io.Serializable;

/**
 * EtlProperty <br/>
 * @author JW
 * @since 2017年11月16日
 * 
 */
public class DataSetProperty  extends BaseProperty  implements Serializable{

	private static final long serialVersionUID = 5896008758944887552L;

	private String type;
	
	private String format;
	
	private String compress;
	
	private String definition;

	
	public DataSetProperty(String name) {
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
	 * @return definition
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * @param definition 要设置的 definition
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}

}

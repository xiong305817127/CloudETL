/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.parts;

/**
 *
 * @author XH
 * @since 2017年6月29日
 *
 */
public class CopyFilessourceFilefolderDto {

	String sourceConfigurationName;
	String sourceFilefolder;
	String destinationConfigurationName;
	String destinationFilefolder;
	String wildcard = "*";

	/**
	 * @return sourceFilefolder
	 */
	public String getSourceFilefolder() {
		return sourceFilefolder;
	}

	/**
	 * @param 设置
	 *            sourceFilefolder
	 */
	public void setSourceFilefolder(String sourceFilefolder) {
		this.sourceFilefolder = sourceFilefolder;
	}

	/**
	 * @return destinationFilefolder
	 */
	public String getDestinationFilefolder() {
		return destinationFilefolder;
	}

	/**
	 * @param 设置
	 *            destinationFilefolder
	 */
	public void setDestinationFilefolder(String destinationFilefolder) {
		this.destinationFilefolder = destinationFilefolder;
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
	 * @return sourceConfigurationName
	 */
	public String getSourceConfigurationName() {
		return sourceConfigurationName;
	}

	/**
	 * @param  设置 sourceConfigurationName
	 */
	public void setSourceConfigurationName(String sourceConfigurationName) {
		this.sourceConfigurationName = sourceConfigurationName;
	}

	/**
	 * @return destinationConfigurationName
	 */
	public String getDestinationConfigurationName() {
		return destinationConfigurationName;
	}

	/**
	 * @param  设置 destinationConfigurationName
	 */
	public void setDestinationConfigurationName(String destinationConfigurationName) {
		this.destinationConfigurationName = destinationConfigurationName;
	}

	
}

/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * ElasticSearchBulk的setting域,等效转换 org.pentaho.di.trans.steps.elasticsearchbulk.ElasticSearchBulkMeta.Setting
 * @author XH
 * @since 2017年6月23日
 *
 */
public class ElasticSearchBulkSettingDto {
	String setting;
	String value;
	/**
	 * @return setting
	 */
	public String getSetting() {
		return setting;
	}
	/**
	 * @param  设置 setting
	 */
	public void setSetting(String setting) {
		this.setting = setting;
	}
	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param  设置 value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}

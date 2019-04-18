/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * ElasticSearchBulk 的 Field域,等效转换 org.pentaho.di.trans.steps.elasticsearchbulk.ElasticSearchBulkMeta.Field
 * @author XH
 * @since 2017年6月23日
 *
 */
public class ElasticSearchBulkFieldDto {
	String name;
	String targetName;
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param  设置 name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return targetName
	 */
	public String getTargetName() {
		return targetName;
	}
	/**
	 * @param  设置 targetName
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	
}

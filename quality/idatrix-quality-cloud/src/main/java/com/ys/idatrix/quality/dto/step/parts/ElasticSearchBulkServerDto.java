/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 *  ElasticSearchBulk的 server域,等效转换 org.pentaho.di.trans.steps.elasticsearchbulk.ElasticSearchBulkMeta.Server
 * @author XH
 * @since 2017年6月23日
 *
 */
public class ElasticSearchBulkServerDto {
	String address;
	int port;
	/**
	 * @return address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param  设置 address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param  设置 port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
}

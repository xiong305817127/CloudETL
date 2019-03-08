/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.repository.xml.metastore;

/**
 * Enumeration for types of meta data stored for cloud application.
 * 
 * @author JW
 * @since 2017年6月26日
 *
 */
public enum CloudMetaType {
	
	DB(0, "DB Connection"),
	HADOOP(1, "Hadoop Cluster"),
	SERVER(2, "Slave Server"),
	CLUSTER(3, "Cluster Schema"),
	SPARK_ENGINE(4, "iDatrix Spark Engine"),
	SPARK_RUN_CONFIG(5, "Spark Run Configuration"),
	DEFAULT_RUN_CONFIG(6, "Default Run Configuration");
	
	
	private int index;
	private String type;

	private CloudMetaType(int index, String type) {
		this.index = index;
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public static CloudMetaType getMetaCubeTypeForType(String type) {
		for (CloudMetaType mcType : values()) {
			if (mcType.getType().equals(type)) {
				return mcType;
			}
		}
		return DB;
	}

	public static CloudMetaType getMetaCubeTypeForIndex(int index) {
		for (CloudMetaType mcType : values()) {
			if (mcType.getIndex() == index) {
				return mcType;
			}
		}
		return DB;
	}

}

/**
 * 平台图存储套件
 * iDatrxi Graph Suite
 */
package com.ys.idatrix.quality.toolkit.common;

/**
 * RelationshipType <br/>
 * @author JW
 * @since 2017年12月19日
 * 
 */
public enum RelationshipType {
	
	COULDETL_TRANS("COULDETL_TRANS"), 
	//暂时未实现
	COULDETL_JOB("COULDETL_JOB"),
	METACUBE_CUSTOM("METACUBE_CUSTOM"), METACUBE_BUSINESS("METACUBE_BUSINESS"),
	DATALAB_BATCH("DATALAB_BATCH"), DATALAB_AZKABAN("DATALAB_AZKABAN"),
	DATALAB_BUSINESS("DATALAB_BUSINESS"), GATEWAY_SFTP("GATEWAY_SFTP");
	
	private String type;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private RelationshipType(String type) {
		this.type = type;
	}

	public boolean match(RelationshipType type) {
		if (this.type.equals(type.getType())) {
			return true;
		}
		return false;
	}

	public static RelationshipType getRelationshipTypeBy(String type) {
		for (RelationshipType rt : RelationshipType.values()) {
			if (rt.getType().equals(type)) {
				return rt;
			}
		}
		return null;
	}

}

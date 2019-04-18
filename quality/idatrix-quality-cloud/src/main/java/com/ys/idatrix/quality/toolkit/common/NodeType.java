/**
 * 平台图存储套件
 * iDatrxi Graph Suite
 */
package com.ys.idatrix.quality.toolkit.common;

/**
 * LabelType <br/>
 * @author JW
 * @since 2017年12月15日
 * 
 */
public enum NodeType {

	SYSTEM("SYSTEM"), DATABASE("DATABASE"), SCHEMA("SCHEMA"), TABLE("TABLE"),
	FIELD("FIELD"), FILESYSTEM("FILESYSTEM"), FILE("FILE"), INTERFACE("INTERFACE"),
	DATASET("DATASET"), DATAITEM("DATAITEM"), DUMMY("DUMMY"), STEP_OR_ENTRY("STEP_OR_ENTRY");

	private String type;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private NodeType(String type) {
		this.type = type;
	}

	public boolean match(NodeType type) {
		if (this.type.equals(type.getType())) {
			return true;
		}
		return false;
	}

	public static NodeType getLabelTypeByType(String type) {
		for (NodeType lt : NodeType.values()) {
			if (lt.getType().equals(type)) {
				return lt;
			}
		}
		return null;
	}

}

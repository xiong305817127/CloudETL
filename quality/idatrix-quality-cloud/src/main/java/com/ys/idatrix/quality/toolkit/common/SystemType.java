/**
 * 平台图存储套件
 * iDatrxi Graph Suite
 */
package com.ys.idatrix.quality.toolkit.common;

import org.pentaho.di.core.util.Utils;

/**
 * LabelLevel <br/>
 * 	- 有效层级范围为：0~150 <br/>
 * 	- 其中，10，20，40，80 为基本层级 <br/>
 * 	- 由相邻的基本层级可以组成 30，60，120 三种有效的复合层级 <br/>
 * @author JW
 * @since 2017年12月15日
 * 
 */
public enum SystemType {
	
	DateBase("DateBase"), File("File"), Interface("Interface"), Dummy("Dummy") ;

	private String type;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private SystemType(String type) {
		this.type = type;
	}

	public boolean match(NodeType type) {
		if (this.type.equals(type.getType())) {
			return true;
		}
		return false;
	}

	public static SystemType getLabelTypeByType(String type) {
		if( Utils.isEmpty(type)) {
			return null ;
		}
		for (SystemType lt : SystemType.values()) {
			if (lt.getType().equals(type)) {
				return lt;
			}
		}
		return null;
	}

}

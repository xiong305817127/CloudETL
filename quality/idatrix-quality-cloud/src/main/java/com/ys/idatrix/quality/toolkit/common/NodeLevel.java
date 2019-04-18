/**
 * 平台图存储套件
 * iDatrxi Graph Suite
 */
package com.ys.idatrix.quality.toolkit.common;

/**
 * LabelLevel <br/>
 * 	- 有效层级范围为：0~150 <br/>
 * 	- 其中，10，20，40，80 为基本层级 <br/>
 * 	- 由相邻的基本层级可以组成 30，60，120 三种有效的复合层级 <br/>
 * @author JW
 * @since 2017年12月15日
 * 
 */
public enum NodeLevel {
	
	ALL(0),
	SYSTEM(10), // 服务器、主机、存储系统、磁盘阵列
	DATABASE(20), // 数据库、实例、用户模式、接口、文件系统
	SCHEMA(30),
	TABLE(40), // 表、结构化文件、接口文件、数据集
	DATABASE_OR_TABLE(60),
	FIELD(80), // 字段、非结构化文件、接口数据项
	TABLE_OR_FIELD(120);

	private long level;

	public long getLevel() {
		return this.level;
	}

	public void setLevel(long level) {
		this.level = level;
	}

	private NodeLevel(long level) {
		this.level = level;
	}

	public boolean match(NodeLevel level) {
		if (this.level == level.getLevel()) {
			return true;
		}
		return false;
	}

	public static NodeLevel getLabelLevelByLevel(long level) {
		for (NodeLevel ll : NodeLevel.values()) {
			if (ll.getLevel() == level) {
				return ll;
			}
		}
		return null;
	}
	
	public static boolean isLevelAvailable(long level) {
		if (getLabelLevelByLevel(level) != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * 根据给定层级值，查找符合该值的所有层级
	 * @param level
	 * @return
	 */
	public static NodeLevel[] getLabelLevels(long level) {
		if (ALL.level == level) {
			return NodeLevel.values();
		} else if (SCHEMA.level == level) {
			return new NodeLevel[] {SYSTEM, DATABASE};
		} else if (DATABASE_OR_TABLE.level == level) {
			return new NodeLevel[] {DATABASE, TABLE};
		} else if (TABLE_OR_FIELD.level == level) {
			return new NodeLevel[] {TABLE, FIELD};
		}
		
		NodeLevel ll = getLabelLevelByLevel(level);
		if (ll != null) {
			return new NodeLevel[] {ll};
		}
		
		return null; // new LabelLevel[] {};
	}

}

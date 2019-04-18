/** 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.quality.deploy;
/**
 * TransEngineCategory <br/>
 * @author JW
 * @since 2017年11月21日
 * 
 */
public enum TransEngineCategory {
	
	DEPLOYED("true"), NOT_DEPLOYED("false");
	
	private String category;
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	private TransEngineCategory(String category) {
		this.category = category;
	}
	
	public boolean match(TransEngineCategory teCategory) {
		if (category.equals(teCategory.getCategory())) {
			return true;
		}
		return false;
	}
	public static TransEngineCategory getTransEngineCategoryByCategory(String category) {
		for (TransEngineCategory teCategory : TransEngineCategory.values()) {
			if (teCategory.getCategory().equals(category)) {
				return teCategory;
			}
		}
		return null;
	}
}
/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.deploy;

/**
 * Meta Store Category.<br/>
 * 	 Local - Store meta data in XML files in local server<br/>
 *   Cache - Store meta data in remote redis cache server<br/>
 *   Database - Store meta data in database<br/>
 *   Default - Only using in developing env, to be removed.<br/> 
 * @author JW
 * @since 2017年6月28日
 *
 */
public enum MetaStoreCategory {
	
	DEFAULT("Default"), LOCAL("Local"), CACHE("Cache"), DATABASE("Database");
	
	private String category;
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	private MetaStoreCategory(String category) {
		this.category = category;
	}
	
	public boolean match(MetaStoreCategory msCategory) {
		if (category.equals(msCategory.getCategory())) {
			return true;
		}
		return false;
	}

	public static MetaStoreCategory getMetaStoreCategoryByCategory(String category) {
		for (MetaStoreCategory metaCategory : MetaStoreCategory.values()) {
			if (metaCategory.getCategory().equals(category)) {
				return metaCategory;
			}
		}
		return null;
	}
	
}

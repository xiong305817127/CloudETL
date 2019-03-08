/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.deploy;

/**
 * Meta Cube Category.<br/>
 * iDatrix - Meta data is from iDatrix MetaCube system<br/>
 * Pentaho - Meta data is from local server meta store<br/>
 * Tenant - Meta data is from tenant third-part system<br/>
 * Default - Only using in developing env, to be removed.<br/> 
 * @author JW
 * @since 2017年6月28日
 *
 */
public enum MetaCubeCategory {

	DEFAULT("Default"), IDATRIX("iDatrix"), PENTAHO("Pentaho"), TENANT("Tenant");

	private String category;

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	private MetaCubeCategory(String category) {
		this.category = category;
	}

	public boolean match(MetaCubeCategory mcCategory) {
		if (category.equals(mcCategory.getCategory())) {
			return true;
		}
		return false;
	}

	public static MetaCubeCategory getMetaCubeCategoryByCategory(String category) {
		for (MetaCubeCategory metaCategory : MetaCubeCategory.values()) {
			if (metaCategory.getCategory().equals(category)) {
				return metaCategory;
			}
		}
		return null;
	}

}

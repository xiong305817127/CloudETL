/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.reference.metacube.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for DB tables transfer from MetaCube.
 * @author JW
 * @since 2017年6月16日
 *
 */
public class MetaCubeDbTableViews {
    // 表
    private List<MetaCubeDbTableDto> tableList;
    // 视图
    private List<MetaCubeDbTableDto> viewList;
    
    
    public void addTable(MetaCubeDbTableDto table) {
    	if( table == null ) {
    		return  ;
    	}
    	table.setType("table");
    	if( this.tableList == null ) {
    		this.tableList =  new ArrayList<>();
    	}
		this.tableList.add(table);
	}
    
	public List<MetaCubeDbTableDto> getTableList() {
		return tableList;
	}
	public void setTableList(List<MetaCubeDbTableDto> tableList) {
		this.tableList = tableList;
	}
	
	public void addView(MetaCubeDbTableDto view) {
		if( view == null ) {
			return  ;
		}
		view.setType("view");
		if( this.viewList == null ) {
			this.viewList =  new ArrayList<>();
		}
		this.viewList.add(view);
	}
	  
	public List<MetaCubeDbTableDto> getViewList() {
		return viewList;
	}
	public void setViewList(List<MetaCubeDbTableDto> viewList) {
		this.viewList = viewList;
	}
	@Override
	public String toString() {
		return "MetaCubeDbTableMap [tableList=" + tableList + ", viewList=" + viewList + "]";
	}
}

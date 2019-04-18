/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPTextFileInput 的 Filter 域,等效 org.pentaho.di.trans.steps.fileinput.text.TextFileFilter
 * @author JW
 * @since 2017年5月13日
 *
 */
public class TextFileInputFilterDto {

	private String filterString;
	private int filterPosition =0;
	private boolean filterIsLastLine;
	private boolean filterIsPositive;

	public void setFilterString(String filterString){
		this.filterString = filterString;
	}
	public String getFilterString(){
		return this.filterString;
	}
	
	public void setFilterPosition(int filterPosition){
		this.filterPosition = filterPosition;
	}
	public int getFilterPosition(){
		return this.filterPosition;
	}
	
	public void setFilterIsLastLine(boolean filterIsLastLine){
		this.filterIsLastLine = filterIsLastLine;
	}
	public boolean getFilterIsLastLine(){
		return this.filterIsLastLine;
	}
	
	public void setFilterIsPositive(boolean filterIsPositive){
		this.filterIsPositive = filterIsPositive;
	}
	public boolean getFilterIsPositive(){
		return this.filterIsPositive;
	}

}

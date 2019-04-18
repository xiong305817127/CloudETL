/**
 * 云化数据集成系统 
 * iDatrix quality
 */
package com.ys.idatrix.quality.reference.metacube.dto;

/**
 * DTO for hadoop cluster overview information.
 * @author JW
 * @since 2017年6月16日
 *
 */
public class MetaCubeHadoopDto {

	private String name;
	private String type;
	private int status;

	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	
	public void setType(String type){
		this.type = type;
	}
	public String getType(){
		return this.type;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	public int getStatus(){
		return this.status;
	}

}

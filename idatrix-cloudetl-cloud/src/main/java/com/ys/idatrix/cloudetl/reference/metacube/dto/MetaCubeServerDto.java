/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.reference.metacube.dto;

/**
 * DTO for ETL slave server overview information.
 * @author JW
 * @since 2017年6月16日
 *
 */
public class MetaCubeServerDto {

	private String name;
	private boolean master;
	private int status;

	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	public void setMaster(boolean master){
		this.master = master;
	}
	public boolean getMaster(){
		return this.master;
	}
	public void setStatus(int status){
		this.status = status;
	}
	public int getStatus(){
		return this.status;
	}

}

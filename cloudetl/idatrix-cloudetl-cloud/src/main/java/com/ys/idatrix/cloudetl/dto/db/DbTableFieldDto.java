/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.db;

/**
 * 数据库表域Dto
 * @author JW
 * @since 05-12-2017
 *
 */
public class DbTableFieldDto {
	
	private String name;
	private int type;
	
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param  设置 type
	 */
	public void setType(int type) {
		this.type = type;
	}
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DbTableFieldDto [name=" + name + "]";
	}

}

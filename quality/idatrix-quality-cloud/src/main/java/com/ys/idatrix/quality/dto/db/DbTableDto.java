/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.db;

/**
 * 数据库表名 DTo,
 * @author JW
 * @since 05-12-2017
 *
 */
public class DbTableDto {
	
	private String table;
    public void setTable(String table) {
        this.table = table;
    }
    public String getTable() {
        return table;
    }
    
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DbTableDto [table=" + table + "]";
	}

}

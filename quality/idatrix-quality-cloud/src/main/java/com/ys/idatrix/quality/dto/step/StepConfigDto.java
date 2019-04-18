/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step;

/**
 * Step Config 
 * @author JW
 * @since 05-12-2017
 *
 */
public class StepConfigDto {
	
    private String clusterSchema;
    private Boolean distribute;
    
    public void setClusterSchema(String clusterSchema) {
        this.clusterSchema = clusterSchema;
    }
    public String getClusterSchema() {
        return clusterSchema;
    }
    
	/**
	 * @return distribute
	 */
	public Boolean isDistribute() {
		return distribute;
	}
	/**
	 * @param distribute 要设置的 distribute
	 */
	public void setDistribute(Boolean distribute) {
		this.distribute = distribute;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StepConfigDto [clusterSchema=" + clusterSchema + ", distribute=" + distribute + "]";
	}
	
}

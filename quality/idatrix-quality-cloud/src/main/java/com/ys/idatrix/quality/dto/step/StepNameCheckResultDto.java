/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step;

import io.swagger.annotations.ApiModel;

/**
 * step name检查结果
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换步骤是否存在")
public class StepNameCheckResultDto {
	
	private boolean existed;
    public void setExisted(boolean existed) {
        this.existed = existed;
    }
    public boolean getExisted() {
        return existed;
    }
    
	/**
	 * @param existed
	 */
	public StepNameCheckResultDto(boolean existed) {
		super();
		this.existed = existed;
	}
	
	/**
	 * 
	 */
	public StepNameCheckResultDto() {
		super();
	}
    
}

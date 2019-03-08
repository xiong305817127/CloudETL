/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO - 资源状态测试结果
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("资源测试结果信息")
public class TestResultDto {
	
	
	private Long schemaId;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("状态")
    private int status;
	
	@ApiModelProperty("信息")
    private String message;
    
	
    public Long getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name 要设置的 name
	 */
	public void setName(String name) {
		this.name = name;
	}
    
	/**
	 * @return status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status 要设置的 status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message 要设置的 message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "TestResultDto [schemaId=" + schemaId + ", name=" + name + ", status=" + status + ", message=" + message
				+ "]";
	}
	
}

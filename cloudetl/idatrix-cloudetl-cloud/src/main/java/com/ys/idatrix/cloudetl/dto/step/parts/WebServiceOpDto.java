package com.ys.idatrix.cloudetl.dto.step.parts;

import io.swagger.annotations.ApiModel;

/**
 * SPWebService 的wdls's Operation dto
 * 
 * @author FBZ
 * @since 12-11-2017
 *
 */
@ApiModel("webService 方法和参数信息")
public class WebServiceOpDto {
	// 方法名称
	private String method;
	
	// 输入参数
	private WebServiceFieldDto[] in;
	// 输出参数
	private WebServiceFieldDto[] out;
	
	/**
	 * 容器名
	 */
	private String outContainerName;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public WebServiceFieldDto[] getIn() {
		return in;
	}

	public void setIn(WebServiceFieldDto[] in) {
		this.in = in;
	}

	public WebServiceFieldDto[] getOut() {
		return out;
	}

	public void setOut(WebServiceFieldDto[] out) {
		this.out = out;
	}

	public String getOutContainerName() {
		return outContainerName;
	}

	public void setOutContainerName(String outContainerName) {
		this.outContainerName = outContainerName;
	}
}

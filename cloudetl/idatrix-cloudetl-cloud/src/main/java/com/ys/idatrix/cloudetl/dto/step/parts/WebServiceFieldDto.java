package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPWebService 的 fieldsIn域,等效
 * org.pentaho.di.trans.steps.webservices.WebServiceField
 * 
 * @author FBZ
 * @since 12-1-2017
 *
 */
public class WebServiceFieldDto {

	String type; // ws类型
	String name; // 名称
	String wsName; // ws名称

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWsName() {
		return wsName;
	}

	public void setWsName(String wsName) {
		this.wsName = wsName;
	}

}

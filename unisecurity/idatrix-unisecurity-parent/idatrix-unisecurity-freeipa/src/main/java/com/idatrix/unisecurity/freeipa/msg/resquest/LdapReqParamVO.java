package com.idatrix.unisecurity.freeipa.msg.resquest;

import java.util.List;

public class LdapReqParamVO {
	private String method;
	//params一共只有两个参数。第一个为一个List对象，第二个为一个Map对象
	private List<Object> params;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "LdapReqParamVO [method=" + method + ", params=" + params + "]";
	}

}

/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.subscribe.api.dto.parts;

import java.io.Serializable;

public class ElasticSearchServerDto implements Serializable {
	
	private static final long serialVersionUID = -3228362615193835266L;
	
	String address;
	int port;
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "ElasticSearchServerDto [address=" + address + ", port=" + port + "]";
	}
	
}

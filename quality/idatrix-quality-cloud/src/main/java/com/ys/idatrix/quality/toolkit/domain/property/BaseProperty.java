/**
 * 云化数据集成系统 
 * iDatrxi quality
 */
package com.ys.idatrix.quality.toolkit.domain.property;

import java.io.Serializable;

/**
 * BaseNode <br/>
 * 
 * @author JW
 * @since 2017年11月21日
 * 
 */
public abstract class BaseProperty implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name ;
	
	public BaseProperty(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

}

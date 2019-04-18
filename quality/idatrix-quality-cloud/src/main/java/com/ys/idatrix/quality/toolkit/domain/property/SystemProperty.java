/**
 * 云化数据集成系统 
 * iDatrxi quality
 */
package com.ys.idatrix.quality.toolkit.domain.property;

import java.io.Serializable;

import com.ys.idatrix.quality.toolkit.common.SystemType;

/**
 * SystemProperty <br/>
 * @author JW
 * @since 2017年11月16日
 * 
 */
public class SystemProperty extends BaseProperty implements Serializable{

	private static final long serialVersionUID = 8615763040619629059L;

	private SystemType type;

	private String owner;
	
	private String position;

	public SystemProperty(String name) {
		super(name);
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public SystemType getType() {
		return type;
	}

	public void setType(SystemType type) {
		this.type = type;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}

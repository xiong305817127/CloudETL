/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain;

import com.ys.idatrix.cloudetl.toolkit.common.NodeLevel;
import com.ys.idatrix.cloudetl.toolkit.common.RelationshipType;
import com.ys.idatrix.cloudetl.toolkit.domain.property.BaseProperty;

/**
 * Relationship <br/>
 * @author JW
 * @since 2018年1月15日
 * 
 */
public class Relationship implements Cloneable {
	
	private RelationshipType type;
	
	private DataNode startNode;
	
	private DataNode endNode;
	
	private NodeLevel level;
	
	private BaseProperty properties;
	
	public RelationshipType getType() {
		return type;
	}

	public void setType(RelationshipType type) {
		this.type = type;
	}

	public DataNode getStartNode() {
		return startNode;
	}

	public void setStartNode(DataNode startNode) {
		this.startNode = startNode;
	}

	public DataNode getEndNode() {
		return endNode;
	}

	public void setEndNode(DataNode endNode) {
		this.endNode = endNode;
	}

	public NodeLevel getLevel() {
		return level;
	}

	public void setLevel(NodeLevel level) {
		this.level = level;
	}

	public BaseProperty getProperties() {
		return properties;
	}

	public void setProperties(BaseProperty properties) {
		this.properties = properties;
	}
	
	public boolean equals( Relationship r ,boolean ignoreDirection) {
		if( r == null ) {
			return false;
		}
		if (r.getType() != null && r.getType().equals(getType())) {
			if( getStartNode().equals( r.getStartNode()) && getEndNode().equals( r.getEndNode())) {
				return true ;
			}
			if( ignoreDirection && getEndNode().equals( r.getStartNode()) && getStartNode().equals( r.getEndNode()) ) {
				return true ;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return type.toString()+": { " +startNode.getGuiKey()+" , "+ endNode.getGuiKey()+" } ";
	}
	
}

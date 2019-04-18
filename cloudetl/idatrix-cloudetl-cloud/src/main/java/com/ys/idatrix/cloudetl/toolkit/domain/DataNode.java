/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain;

import com.ys.idatrix.cloudetl.toolkit.common.NodeLevel;
import com.ys.idatrix.cloudetl.toolkit.common.NodeType;
import com.ys.idatrix.cloudetl.toolkit.common.SystemType;
import com.ys.idatrix.cloudetl.toolkit.domain.property.BaseProperty;
import com.ys.idatrix.cloudetl.toolkit.domain.property.SystemProperty;

//import com.ys.idatrix.cloudetl.toolkit.domain.node.BaseNode;

/**
 * NodeDto - 节点DTO <br/>
 * @author JW
 * @since 2017年12月8日
 * 
 */
public class DataNode implements Cloneable {

	private NodeType type;

	private String name ;
	
	private NodeLevel level;

	private BaseProperty properties;
	
    //	数据库guid：
	// 			system: xxx
	//			database: xxx
	//			schema: xxx
	//			table: xxx
	//			field: xxx
	//	文件guid:
	//			system: xxx
	//			fileSystem: xxx
	//			filePath: xxx
	//	接口数据guid:
	//			system: xxx
	//			dataInterface: xxx
	//			dataSet: xxx
	//			dataItem: xxx
	
	//	Dummy guid:
	//			dummyId: xxx (可以是name, key, label, etc.)
	private DataNode parent;

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public DataNode getParent() {
		return parent;
	}

	public void setParent(DataNode parent) {
		this.parent = parent;
	}
	
	public DataNode getTableLevelNode( NodeLevel level) {
		if( level == null ) {
			level = NodeLevel.TABLE ;
		}
		if(level.equals(getLevel()) ) {
			return this ;
		}else if( getParent() != null) {
			return getParent().getTableLevelNode(level) ;
		}
		return null ;
	}

	public SystemType getSystemType() {
		if( NodeType.SYSTEM.equals( getType() ) && ( getProperties() instanceof SystemProperty )) {
			return ((SystemProperty)getProperties()).getType() ;
		}else if( getParent() != null ) {
			return getParent().getSystemType() ;
		}
		return null ;
	}
	
	public DataNode getRoot() {
		if( getParent() != null ) {
			return getParent().getRoot() ;
		}
		return this ;
	}
	
	public String getGuiKey() {
		StringBuilder sb = new StringBuilder();
		if( getParent() != null ) {
			sb.append( getParent().getGuiKey());
			sb.append(".");
		}
		sb.append(getType().getType()).append("[ ").append(getName()).append(" ]");
		return sb.toString();
	}
	
	public boolean equals( DataNode dn) {
		if(dn == null ) {
			return false;
		}
		if (dn.getType() != null && dn.getType().equals(getType())) {
			if (dn.getLevel() == getLevel()) {
				if (dn.getGuiKey().equals(getGuiKey())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public String getStepOrEntryField() {
		if (NodeType.STEP_OR_ENTRY.match( getType())) {
			return getName() ;
		}
		return null;
	}
	
	public  boolean isStepOrEntryField() {
		if (NodeType.STEP_OR_ENTRY.match(getType())) {
			return true;
		}
		return false;
	}
	
	
	public static boolean isSameDataNode( DataNode dn1 , DataNode dn2) {
		if(dn1 == null && dn2 == null ) {
			return true ;
		}else if( dn1 != null && dn2 != null) {
			return dn1.equals(dn2);
		}
		return false ;
	}

	@Override
	public String toString() {
		return getGuiKey();
	}

}

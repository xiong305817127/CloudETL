/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.quality.toolkit.analyzer.trans.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.dto.step.StepFieldDto;
import com.ys.idatrix.quality.toolkit.common.NodeType;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.domain.property.FieldProperty;

/**
 * StepData <br/>
 * 	- 存储步骤数据节点和数据关系分析结果 <br/>
 * 
 * @author JW
 * @since 2018年1月25日
 * 
 */
public class StepDataRelationship {
	
	/**
	 * Input stream - 步骤输入流
	 * - 输入流中的field的name作为key
	 */
	private final Map<String, StepFieldDto> inputStream;
	
	/**
	 * Output stream - 步骤输出流
	 * - 输出流中的field的name作为key
	 */
	private final Map<String, StepFieldDto> outputStream;
	
	/**
	 * Input data nodes
	 * - 步骤实际引入的数据节点：数据库、文件、接口、步骤中引用的变量或外部参数
	 * - 每个数据节点的名字（输入输出流中的field的name，或GUID对应的字符串）作为key
	 */
	private List<DataNode> inputDataNodes;

	/**
	 * Output data nodes
	 * - 步骤实际输出的数据节点：数据库、文件、接口、步骤中设置的变量或外部参数
	 * - 每个数据节点的名字（输入输出流中的field的name，或GUID对应的字符串）作为key
	 */
	private List<DataNode> outputDataNodes;
	
	/**
	 * Relationship between input and output data node (or stream as a temporal node) 
	 * 
	 * - 步骤中产生的数据关系：
	 * - 1. 输入流（inputStream）和输出流（outputStream）之间的关系
	 * - 2. 输入流（inputStream）与输出数据节点（outputDataNodes）的关系
	 * - 3. 输入数据节点（inputDataNodes）与输出流（outputStream）的关系
	 * - 4. 输入数据节点（inputDataNodes）与输出数据节点（outputDataNodes）的关系
	 * 
	 * - 其中，输入输出流中的field作为STEP_OR_ENTRY类型的数据节点进行处理，GUID中保存流中field的名字！
	 * 
	 */
	private List<Relationship> dataRelationship;
	
	/**
	 * Constructor.
	 * 
	 * @param inputStream - 由公共的方法获取并传入
	 * @param outputStream - 由公共的方法获取并传入
	 */
	public StepDataRelationship(Map<String, StepFieldDto> inputStream, Map<String, StepFieldDto> outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		
		setInputDataNodes(new ArrayList<>());
		setOutputDataNodes(new ArrayList<>());
		setDataRelationship(new ArrayList<>());
	}


	public Map<String, StepFieldDto> getInputStream() {
		return inputStream;
	}
 
	public Map<String, StepFieldDto> getOutputStream() {
		return outputStream;
	}

	public List<DataNode> getInputDataNodes() {
		return inputDataNodes;
	}

	public void setInputDataNodes(List<DataNode> inputDataNodes) {
		this.inputDataNodes = inputDataNodes;
	}
	
	public StepDataRelationship addInputDataNode(DataNode inputDataNode) {
		if( inputDataNode != null ) {
			this.inputDataNodes .add(inputDataNode);
		}
		
		return this;
	}

	public List<DataNode> getOutputDataNodes() {
		return outputDataNodes;
	}

	public void setOutputDataNodes(List<DataNode> outputDataNodes) {
		this.outputDataNodes = outputDataNodes;
	}
	
	public StepDataRelationship addOutputDataNode(DataNode outputDataNode) {
		if( outputDataNode != null ) {
			this.outputDataNodes.add(outputDataNode);
		}
		return this;
	}

	public List<Relationship> getDataRelationship() {
		return dataRelationship;
	}

	public void setDataRelationship(List<Relationship> dataRelationship) {
		this.dataRelationship = dataRelationship;
	}
	
	public StepDataRelationship addRelationship(Relationship r) {
		if( r != null ) {
			this.dataRelationship.add(r);
		}
		return this;
	}
	
	public DataNode findInFieldDataNode(String fieldName) {
		if(Utils.isEmpty(fieldName)) {
			return null;
		}
		if(inputDataNodes != null && inputDataNodes.size() >0) {
			for( DataNode dn : inputDataNodes ) {
				if(NodeType.FIELD.equals(dn.getType())) {
					if( fieldName.equals(dn.getName())){
						return dn;
					}
					if( dn.getProperties() != null &&( dn.getProperties() instanceof FieldProperty ) ) {
						FieldProperty fieldProperty =  (FieldProperty)dn.getProperties();
						if(fieldName.equals( fieldProperty.getAliasField())) {
							return dn ;
						}
					}
				}
			}
		}
		return null;
	}
	
	public DataNode findOutFieldDataNode(String fieldName) {
		if(Utils.isEmpty(fieldName)) {
			return null;
		}
		if(outputDataNodes != null && outputDataNodes.size() >0) {
			for( DataNode dn : outputDataNodes ) {
				if(NodeType.FIELD.equals(dn.getType())) {
					if( fieldName.equals(dn.getName())){
						return dn;
					}
					if( dn.getProperties() != null &&( dn.getProperties() instanceof FieldProperty ) ) {
						FieldProperty fieldProperty =  (FieldProperty)dn.getProperties();
						if(fieldName.equals( fieldProperty.getAliasField())) {
							return dn ;
						}
					}
				}
			}
		}
		return null;
	}


}

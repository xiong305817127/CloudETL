/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.Utils;
import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.StepFieldDto;
import com.ys.idatrix.cloudetl.toolkit.common.NodeLevel;
import com.ys.idatrix.cloudetl.toolkit.common.RelationshipType;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.domain.property.RelationshipProperty;

/**
 * RelationshipUtil <br/>
 * 
 * @author JW
 * @since 2018年2月6日
 * 
 */
public class RelationshipUtil {
	
	public static final Log  logger = LogFactory.getLog("RelationshipUtil");
	
	/**
	 * 增加关系详细说明,以append的方式
	 * @param r
	 * @param detail
	 */
	public static void  addRelationshipDetail( Relationship r , String detail) {
		if( r.getProperties() instanceof RelationshipProperty) {
			RelationshipProperty property =  (RelationshipProperty)r.getProperties() ;
			property.setDetails(Utils.isEmpty(property.getDetails())?detail : (property.getDetails()+"\n"+detail) );
		}
	}
	
	/**
	 * 合并 两个关系 , 起始节点和结束节点相连
	 * @param r1
	 * @param r2
	 * @return
	 */
	public static Relationship mergeRelationship(Relationship r1, Relationship r2) {
		
		Relationship r = new Relationship();
		r.setStartNode(r1.getStartNode());
		r.setLevel( r1.getLevel() );
		r.setProperties( r1.getProperties() );
		r.setType( r1.getType());
		
		//替换 结束节点,合并2个节点的起始和结束
		//r.setEndNode(r1.getEndNode());
		r.setEndNode(r2.getEndNode());
		return r;
	}

	/**
	 * 判断是否是自己连接自己
	 * @param r1
	 * @return
	 */
	public static boolean isSelfRelationship(Relationship r1) {
		if (r1.getStartNode() != null && r1.getEndNode() != null && r1.getStartNode().equals(r1.getEndNode())) {
			return true;
		}
		return false;
	}


	/**
	 * 判断 关系是否已经包含到了 列表中
	 * @param list
	 * @param r
	 * @param ignoreDirection 是否 忽略关系的方向
	 * @return
	 */
	public static boolean isRepeatRelationship(List<Relationship> list, Relationship r, boolean ignoreDirection) {
		if (r == null || list == null || list.size() == 0) {
			return false;
		}
		int i = 0 ;
		for (Relationship l : list) {
			if (l != null && l.equals(r, ignoreDirection)) {
				i++;
			}
		}
		if(i>1) {
			return true ;
		}
		return false;
	}


	/**
	 * 构建数据关系
	 * 
	 * @param type
	 * @param level
	 * @param startnode
	 * @param endnode
	 * @param baseProperties
	 * @return
	 * @throws Exception
	 */
	public static Relationship buildRelationship(RelationshipType type, NodeLevel level, DataNode startnode,
			DataNode endnode, RelationshipProperty baseProperties) throws Exception {
		if( startnode == null || endnode == null ) {
			return null ;
		}
		Relationship relationship = new Relationship();
		relationship.setType(type);
		relationship.setLevel(level);
		relationship.setProperties(baseProperties);
		relationship.setStartNode(startnode);
		relationship.setEndNode(endnode);

		return relationship;
	}
	

	/**
	 * 构建数据关系
	 * 
	 * @param startnode
	 * @param endnode
	 * @param baseProperties
	 * @return
	 * @throws Exception
	 */
	public static Relationship buildRelationship( DataNode startnode,DataNode endnode, RelationshipProperty baseProperties) throws Exception {
		if( startnode == null || endnode == null ) {
			return null ;
		}
		Relationship relationship = new Relationship();
		relationship.setType(RelationshipType.COULDETL_TRANS);
		relationship.setLevel(NodeLevel.FIELD);
		relationship.setProperties(baseProperties);
		relationship.setStartNode(startnode);
		relationship.setEndNode(endnode);

		return relationship;
	}
	
	

	/**
	 * 构建Dummy输入数据关系
	 * @param from
	 * @param dummyId
	 * @param outFieldName
	 * @return
	 * @throws Exception
	 */
	public static Relationship buildDummyRelationship(String from, String dummyId, String outFieldName)
			throws Exception {
		RelationshipProperty baseProperties = new RelationshipProperty("从 [" + from + "],产生[ dummy(" + dummyId + ") , " + outFieldName + " ]的关系.");
		DataNode startNode = DataNodeUtil.dummyNodeParse(dummyId);
		DataNode endNode = DataNodeUtil.streamNodeParse(outFieldName);
		
		return buildRelationship(startNode,endNode, baseProperties);
	}


	/**
	 * 构建字段数据关系
	 * @param startDataNode
	 * @param endDataNode
	 * @param from
	 * @param inFieldName
	 * @param outFieldName
	 * @return
	 * @throws Exception
	 */
	public static Relationship buildFieldRelationship(DataNode startDataNode, DataNode endDataNode, String from, String inFieldName, String outFieldName) throws Exception {
		RelationshipProperty baseProperties = new RelationshipProperty("从 [" + from + "],产生[ " + inFieldName + " , " + outFieldName + " ]的关系.");
		
		if (startDataNode == null && !Utils.isEmpty(inFieldName)) {
			startDataNode = DataNodeUtil.streamNodeParse(inFieldName);
		}
		if (endDataNode == null && !Utils.isEmpty(outFieldName)) {
			endDataNode = DataNodeUtil.streamNodeParse(outFieldName);
		}
		Relationship relationship = buildRelationship(startDataNode, endDataNode, baseProperties);
		return relationship;
	}

	/**
	 *  构建流字段数据关系
	 * @param from
	 * @param inFieldName
	 * @param outFieldName
	 * @return
	 * @throws Exception
	 */
	public static Relationship buildFieldRelationship(String from, String inFieldName, String outFieldName)
			throws Exception {
		return buildFieldRelationship(null, null, from, inFieldName, outFieldName);
	}

	/**
	 * 生成 输入步骤的 关系节点( 通过输出流字段确定字段信息)
	 * @param fieldsMap 当为数据库类型/接口类型的输入时 不为空,<流域名,数据库字段节点>
	 * @param parentDataNode 当 非数据库类型/非接口类型的输入时 不为空
	 * @param outputStreamMap 步骤的输出域map
	 * @param stepName 
	 * @param from
	 * @return
	 * @throws Exception
	 */
	public static List<Relationship> inputStepRelationship(Map<String,DataNode> fieldsMap,DataNode sourceInNode,Map<String, StepFieldDto> outputStreamMap , String stepName, String from)
			throws Exception {
		List<Relationship> res = Lists.newArrayList();
		if( outputStreamMap == null || outputStreamMap.isEmpty()) {
			return res ;
		}
		for (Map.Entry<String, StepFieldDto> entery : outputStreamMap.entrySet()) {
			if (!stepName.equals(entery.getValue().getOrigin()) || Utils.isEmpty(entery.getKey())) {
				continue;
			}
			String outfieldName = entery.getKey() ;
			//当是数据库时  fieldsMap 不为空, 文件/接口类型的 为空
			if( fieldsMap != null && fieldsMap.containsKey(outfieldName)) {
				sourceInNode = fieldsMap.get(outfieldName) ;
			}
			Relationship r = buildFieldRelationship( sourceInNode , null, from, null , outfieldName);
			if( r!= null) {
				res.add(r);
			}else {
				//TODO ??
			}
		}
		return res ;
	}


	/**
	 * 生成 输出步骤的 关系节点
	 * @param fieldsMap 当为数据库类型/接口类型的输入时 不为空,<流域名,数据库字段节点>
	 * @param sourceOutNode 当 非数据库类型/非接口类型的输入时 不为空
	 * @param stepName
	 * @param from
	 * @param outFields
	 * @param inFields
	 * @return
	 * @throws Exception
	 */
	public static  List<Relationship>  outputStepRelationship(Map<String,DataNode> fieldsMap, DataNode sourceOutNode,String stepName, String from, String[] outFields, String[] inFields) throws Exception {
		List<Relationship> res = Lists.newArrayList();
		if ( !Utils.isEmpty(outFields)) {
			for (int i = 0; i < outFields.length; i++) {
				String outField = outFields[i] ;
				String inField = inFields[i] ;
				//当是数据库时  fieldsMap 不为空, 文件/接口类型的 为空
				if( fieldsMap != null && fieldsMap.containsKey(outField)) {
					sourceOutNode = fieldsMap.get(outField) ;
				}
				
				Relationship r = buildFieldRelationship(null,sourceOutNode, from, inField , null);
				if( r!= null) {
					res.add(r);
				}else {
					//TODO ??
				}
				
			}
		}
		return res;
	}

}

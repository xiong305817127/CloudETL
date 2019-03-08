/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.reference.graph;

import java.util.List;
import java.util.stream.Collectors;

import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.reference.graph.dto.WriteRelationDto;
import com.ys.idatrix.cloudetl.toolkit.common.NodeLevel;
import com.ys.idatrix.cloudetl.toolkit.common.SystemType;
import com.ys.idatrix.cloudetl.toolkit.domain.property.DatabaseProperty;
import com.ys.idatrix.cloudetl.toolkit.domain.property.FileProperty;
import com.ys.idatrix.cloudetl.toolkit.domain.property.SchemaProperty;
import com.ys.idatrix.cloudetl.toolkit.domain.property.TableProperty;
import com.ys.idatrix.graph.service.api.NodeService;
import com.ys.idatrix.graph.service.api.RelationshipService;
import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.graph.service.api.dto.edge.DataDepFieldToFieldRlatDto;
import com.ys.idatrix.graph.service.api.dto.node.EtlFolderDto;
import com.ys.idatrix.graph.service.api.dto.node.EtlTableDto;
import com.ys.idatrix.graph.service.api.dto.node.EtlTransNodeDto;
import com.ys.idatrix.graph.service.api.dto.node.EtlViewDto;

/**
 * GraphRepository <br/>
 * @author JW
 * @since 2018年1月8日
 * 
 */
@Service
public class GraphService {

	public static final String GRAPH_SYSTEM = "CloudETL";
	
	@Reference(check=false)
	private NodeService nodeService;
	
	@Reference(check=false)
	private RelationshipService relationshipService ;

	
	public static boolean isAnalyzerEnable() {
		//该功能必须开启dubbo功能
		boolean dubboDeploy =  IdatrixPropertyUtil.getBooleanProperty("dubbo.deployment") ;
		//是否开启流程分析功能
		boolean analyzerOpen =  IdatrixPropertyUtil.getBooleanProperty("idatrix.analyzer.trigger",false) ;
		return  dubboDeploy&&analyzerOpen ;
	}
	
	public boolean createEtlTransNode(String userName , String transName) throws Exception {
		if( !isAnalyzerEnable() || Utils.isEmpty( transName )) {
			return false ;
		}
		String renterId = CloudSession.getLoginRenterId() ;
		if(Utils.isEmpty(userName)) {
			userName = CloudSession.getLoginUser() ;
		}
		
		EtlTransNodeDto node = new EtlTransNodeDto();
		node.setRenterId(Long.valueOf(renterId));
		node.setUserName(userName);
		node.setTransName(transName);
		if( nodeService != null ) {
			Long res = nodeService.createEtlTransNode(node);
			if( res != null && res < 0) {
				return false ;
			}
		}
		return true;
	}
	
	
	public void deleteEtlTransNode(String userName , String transName) throws Exception {
		if( !isAnalyzerEnable() ||  Utils.isEmpty( transName )) {
			return  ;
		}
		String renterId = CloudSession.getLoginRenterId() ;
		if(Utils.isEmpty(userName)) {
			userName = CloudSession.getLoginUser() ;
		}
		
		EtlTransNodeDto node = new EtlTransNodeDto();
		node.setRenterId(Long.valueOf(renterId));
		node.setUserName(userName);
		node.setTransName(transName);
		if( nodeService != null ) {
			nodeService.deleteEtlTransNode(node);
		}
		
	}
	
	public void updateEtlTransNode(String userName , String transName,String oldTransName) throws Exception {
		if( !isAnalyzerEnable() ||  Utils.isEmpty( transName ) || Utils.isEmpty(oldTransName) || transName.equals(oldTransName)) {
			return  ;
		}
		String renterId = CloudSession.getLoginRenterId() ;
		if(Utils.isEmpty(userName)) {
			userName = CloudSession.getLoginUser() ;
		}
		EtlTransNodeDto node = new EtlTransNodeDto();
		node.setRenterId(Long.valueOf(renterId));
		node.setUserName(userName);
		node.setTransName(transName);
		//TODO
		if( nodeService != null ) {
			nodeService.renameEtlTransNode(node, oldTransName);
		}
	}
	
	
	public void saveNodeAndRelationship(String userName , String transName,List<WriteRelationDto>  wrds) throws Exception {
		if( !isAnalyzerEnable() ||  Utils.isEmpty( transName ) || wrds == null || wrds.isEmpty() ) {
			return  ;
		}
		if(Utils.isEmpty(userName)) {
			userName = CloudSession.getLoginUser() ;
		}else {
			CloudSession.setThreadLoginUser(userName);
		}
		Long renterId = Long.valueOf(  CloudSession.getLoginRenterId() );
		EtlTransNodeDto etlTrans = new EtlTransNodeDto();
		etlTrans.setRenterId(Long.valueOf(renterId));
		etlTrans.setUserName(userName);
		etlTrans.setTransName(transName);
		
		//删除该转换相关的关系数据
		relationshipService.deleteDataDepByEtlTrans(etlTrans);
		
		for( WriteRelationDto w : wrds ) {
			SystemType startType = w.getStartParent().getSystemType() ;
			SystemType endType = w.getEndParent().getSystemType() ;
			
			if( startType != null && endType != null ) {
				if( SystemType.File.equals(startType)) {
					if( SystemType.File.equals(endType) ) {
						sendRelationshipFileToFile(renterId, etlTrans,  w);
					}else if( SystemType.DateBase.equals(endType)) {
						sendRelationshipFileToDatabase(renterId, etlTrans,   w);
					}else {
						CloudLogger.getInstance(userName).warn(this, "忽略保存关系,节点["+w.getStartParent().getGuiKey()+","+w.getEndParent().getGuiKey()+"]");
					}
				}else if( SystemType.DateBase.equals(startType) ) {
					if( SystemType.File.equals(endType) ) {
						sendRelationshipDatabaseToFile(renterId, etlTrans,   w);
					}else if( SystemType.DateBase.equals(endType) ) {
						sendRelationshipDatabaseToDatabase(renterId, etlTrans,   w);
					}else {
						CloudLogger.getInstance(userName).warn(this, "忽略保存关系,节点["+w.getStartParent().getGuiKey()+","+w.getEndParent().getGuiKey()+"]");
					}
				}else {
					CloudLogger.getInstance(userName).warn(this, "忽略保存关系,节点["+w.getStartParent().getGuiKey()+","+w.getEndParent().getGuiKey()+"]");
				}
			}else {
				CloudLogger.getInstance(userName).warn(this, "保存关系,出现未获取到系统类型的节点["+w.getStartParent().getGuiKey()+","+w.getEndParent().getGuiKey()+"]");
			}
		}
		
		CloudSession.clearThreadInfo();
	}
	
	/**
	 * 目录 -> 目录
	 * @param w
	 */
	private void sendRelationshipFileToFile(Long renterId,EtlTransNodeDto etlTrans, WriteRelationDto w ) {
		
		FileProperty startfileProperty =  (FileProperty)w.getStartParent().getProperties() ;
		FileProperty endfileProperty =  (FileProperty)w.getEndParent().getProperties() ;
		
		DatabaseType startFileType = getDatabaseType(startfileProperty.getType());
		DatabaseType endFileType = getDatabaseType(endfileProperty.getType());
		
		if( startFileType != null && endFileType!= null) {
			EtlFolderDto start = new EtlFolderDto();
			start.setRenterId(Long.valueOf(renterId));
			start.setFolderPath(startfileProperty.getPath());
			start.setDatabaseType(startFileType);
			EtlFolderDto end = new EtlFolderDto() ;
			end.setRenterId(Long.valueOf(renterId));
			end.setFolderPath(endfileProperty.getPath());
			end.setDatabaseType(endFileType);
			//目录 -> 目录
			relationshipService.saveDataDepFolderToFolderRlat(start, end, etlTrans);
		}else {
			CloudLogger.getInstance(etlTrans.getUserName()).warn(this, "保存关系,文件到文件,但是文件类型不支持保存,["+startFileType+","+endFileType+"]");
		}
	}
	
	/**
	 * 目录 -> 表
	 * @param w
	 */
	private void sendRelationshipFileToDatabase(Long renterId,EtlTransNodeDto etlTrans, WriteRelationDto w ) {
		
		FileProperty  fileProperty =  (FileProperty)w.getStartParent().getProperties() ;
		TableProperty tableProperty = (TableProperty)w.getEndParent().getProperties() ;
		SchemaProperty schemaProperty = (SchemaProperty)w.getEndParent().getTableLevelNode(NodeLevel.SCHEMA).getProperties() ;
		DatabaseProperty databaseProperty = (DatabaseProperty)w.getEndParent().getTableLevelNode(NodeLevel.DATABASE).getProperties() ;
		
		DatabaseType fileType = getDatabaseType(fileProperty.getType());
		DatabaseType dbType = getDatabaseType(databaseProperty.getDbType());
		
		EtlFolderDto start = new EtlFolderDto();
		start.setRenterId(Long.valueOf(renterId));
		start.setFolderPath(fileProperty.getPath());
		start.setDatabaseType(fileType);
		
		EtlTableDto end = new EtlTableDto();
		end.setIp(databaseProperty.getHost());
		end.setDatabaseType(dbType);
		end.setSchema(schemaProperty.getName());
		end.setTable(tableProperty.getName());
		
		end.setServiceName(databaseProperty.getInstance());
		
		List<String> fields = w.getRelationships().stream().map( r -> { return r.getEndNode().getName();}).collect(Collectors.toList()); 
				
		//目录 -> 表
		relationshipService.saveDataDepFolderToTableRlat(start, end, etlTrans, fields);
	}
	
	/**
	 * 数据库 -> 目录
	 * @param w
	 */
	private void sendRelationshipDatabaseToFile(Long renterId,EtlTransNodeDto etlTrans, WriteRelationDto w ) {
		
		TableProperty tableProperty = (TableProperty)w.getStartParent().getProperties() ;
		SchemaProperty schemaProperty = (SchemaProperty)w.getStartParent().getTableLevelNode(NodeLevel.SCHEMA).getProperties() ;
		DatabaseProperty databaseProperty = (DatabaseProperty)w.getStartParent().getTableLevelNode(NodeLevel.DATABASE).getProperties() ;
		FileProperty  fileProperty =  (FileProperty)w.getEndParent().getProperties() ;
		
		DatabaseType dbType = getDatabaseType(databaseProperty.getDbType());
		DatabaseType fileType = getDatabaseType(fileProperty.getType());
		
		EtlFolderDto end = new EtlFolderDto();
		end.setRenterId(Long.valueOf(renterId));
		end.setFolderPath(fileProperty.getPath());
		end.setDatabaseType(fileType);
		
		List<String> fields = w.getRelationships().stream().map( r -> { return r.getStartNode().getName();}).collect(Collectors.toList()); 
		
		if( tableProperty.isView() ) {
			EtlViewDto start = new EtlViewDto() ;
			start.setIp(databaseProperty.getHost());
			start.setDatabaseType(dbType);
			start.setSchema(schemaProperty.getName());
			start.setView(tableProperty.getName());
			start.setServiceName(databaseProperty.getInstance());
			//视图 -> 目录
			relationshipService.saveDataDepViewToFolderRlat(start, end, etlTrans, fields);
		}else {
			EtlTableDto start = new EtlTableDto();
			start.setIp(databaseProperty.getHost());
			start.setDatabaseType(dbType);
			start.setSchema(schemaProperty.getName());
			start.setTable(tableProperty.getName());
			start.setServiceName(databaseProperty.getInstance());
			//表 -> 目录
			relationshipService.saveDataDepTableToFolderRlat(start, end, etlTrans, fields);
		}
	}
	

	/**
	 * 数据库 -> 数据库
	 * @param w
	 */
	private void sendRelationshipDatabaseToDatabase(Long renterId,EtlTransNodeDto etlTrans, WriteRelationDto w ) {
		
		TableProperty startTableProperty = (TableProperty)w.getStartParent().getProperties() ;
		SchemaProperty startSchemaProperty = (SchemaProperty)w.getStartParent().getTableLevelNode(NodeLevel.SCHEMA).getProperties() ;
		DatabaseProperty startDatabaseProperty = (DatabaseProperty)w.getStartParent().getTableLevelNode(NodeLevel.DATABASE).getProperties() ;
		
		TableProperty endTableProperty = (TableProperty)w.getEndParent().getProperties() ;
		SchemaProperty endSchemaProperty = (SchemaProperty)w.getEndParent().getTableLevelNode(NodeLevel.SCHEMA).getProperties() ;
		DatabaseProperty endDatabaseProperty = (DatabaseProperty)w.getEndParent().getTableLevelNode(NodeLevel.DATABASE).getProperties() ;

		DatabaseType startDbType = getDatabaseType(startDatabaseProperty.getDbType());
		DatabaseType endDbType = getDatabaseType(endDatabaseProperty.getDbType());
		
		List<DataDepFieldToFieldRlatDto> fields = w.getRelationships().stream().map( r -> { 
			DataDepFieldToFieldRlatDto ddfdrd = new DataDepFieldToFieldRlatDto();
			ddfdrd.setStartFieldName(r.getStartNode().getName());
			ddfdrd.setEndFieldName(r.getEndNode().getName());
			return ddfdrd;
			
		}).collect(Collectors.toList()); 
		EtlTableDto end = new EtlTableDto();
		end.setIp(endDatabaseProperty.getHost());
		end.setDatabaseType(endDbType);
		end.setSchema(endSchemaProperty.getName());
		end.setTable(endTableProperty.getName());
		end.setServiceName(endDatabaseProperty.getInstance());
		
		if( startTableProperty.isView() ) {
			EtlViewDto start = new EtlViewDto() ;
			start.setIp(startDatabaseProperty.getHost());
			start.setDatabaseType(startDbType);
			start.setSchema(startSchemaProperty.getName());
			start.setView(startTableProperty.getName());
			start.setServiceName(startDatabaseProperty.getInstance());
			//视图 -> 表
			relationshipService.saveDataDepViewToTableRlat(start, end, etlTrans, fields);
		}else {
			EtlTableDto start = new EtlTableDto();
			start.setIp(startDatabaseProperty.getHost());
			start.setDatabaseType(startDbType);
			start.setSchema(startSchemaProperty.getName());
			start.setTable(startTableProperty.getName());
			start.setServiceName(startDatabaseProperty.getInstance());
			//表 -> 表
			relationshipService.saveDataDepTableToTableRlat(start, end, etlTrans, fields);
		}
		
	}
	
	
	public  DatabaseType getDatabaseType(String type) {
		for (DatabaseType lt : DatabaseType.values()) {
			if (lt.toString().equalsIgnoreCase(type)) {
				return lt;
			}
		}
		return null;
	}
}

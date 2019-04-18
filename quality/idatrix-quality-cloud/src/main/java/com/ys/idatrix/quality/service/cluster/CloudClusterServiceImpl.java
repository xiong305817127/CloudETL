/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.cluster;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.deploy.MetaCubeCategory;
import com.ys.idatrix.quality.deploy.MetaStoreCategory;
import com.ys.idatrix.quality.dto.cluster.ClusterDetailsDto;
import com.ys.idatrix.quality.dto.cluster.ClusterDto;
import com.ys.idatrix.quality.dto.cluster.ClusterSlaveDetailsDto;
import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.PaginationDto.DealRowsInterface;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.repository.xml.CloudFileRepository;
import com.ys.idatrix.quality.repository.xml.CloudTransformation;
import com.ys.idatrix.quality.repository.xml.metastore.CloudClusterMetaStore;
import com.ys.idatrix.quality.service.CloudBaseService;
import com.ys.idatrix.quality.service.server.CloudServerService;

/**
 * Cluster schema service implementation. A cluster schema includes one master
 * and multiple slaves.
 *
 * @author JW
 * @since 05-12-2017
 *
 */
@Service
public class CloudClusterServiceImpl extends CloudBaseService implements CloudClusterService {

	@Autowired
	private CloudClusterMetaStore cloudMetaStore;

	@Autowired
	private CloudServerService cloudServerService; // Slave server

	@Autowired
	private MetaStoreCategory metaStoreCategory;
	@Autowired
	private MetaCubeCategory metaCubeCategory;

	/**
	 * 查询服务器集群列表
	 * 
	 * @return
	 * @throws KettleException 
	 */
	private Map<String,List<ClusterSchema>> getClusterSchemaList(String owner) throws Exception {

		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			// Cluster schema don't saved in MetaCube
			// break;
		case PENTAHO:
			// Get meta data from local meta store
			Map<String, List<ClusterSchema>> res = getMetaStoreList(owner, new ForeachCallback<IMetaStore,ClusterSchema> (){
				@Override
				public List<ClusterSchema> getOne(IMetaStore source) throws Exception {
					return  cloudMetaStore.getElements(source);
				}
			});
			
			String user = Const.NVL(owner, CloudSession.getLoginUser()) ;
			List<ClusterSchema> curCluster = ( res != null && res.containsKey(user) )? res.get(user) : null ; 
			if(curCluster == null || curCluster.isEmpty() ) {
				ClusterSchema defaultCluster = initMetaDefaultCluster(owner) ;
				if( defaultCluster != null ) {
					curCluster = Lists.newArrayList() ;
					curCluster.add( defaultCluster );
					res.put(user, curCluster);
				}
			}
			
			return res ;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// Get meta data from cloud transformation, only for testing!
			TransMeta transMeta = CloudTransformation.getInstance().getTransformation();
			Map<String,List<ClusterSchema>> result = Maps.newHashMap() ;
			result.put(CloudSession.getResourceUser(), transMeta.getClusterSchemas());
			return result ;
			
		}
		return Maps.newHashMap();
	}

	/**
	 * 查询指定的服务器集群详情
	 * 
	 * @param name
	 * @return
	 * @throws KettleException 
	 */
	private ClusterSchema getClusterSchema(String owner , String name) throws Exception {
		ClusterSchema meta = null;
		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			// Cluster schema don't saved in MetaCube, but slave server saved in MetaCube
			// Get meta data from local meta store
			meta = cloudMetaStore.getElement(CloudApp.getInstance().getMetaStore(owner), name);
			if (meta != null) {
				List<SlaveServer> slaves = meta.getSlaveServers();
				List<SlaveServer> slaves2 = null ;
				List<SlaveServer> slaveList = null ;
				Map<String, List<SlaveServer>> map = cloudServerService.getSlaveServerList(owner);
				String user = Const.NVL(owner, CloudSession.getLoginUser()) ;
				if( map != null && map.containsKey(user)) {
					slaveList = map.get(user) ;
					slaves2 = slaveList.stream().filter(sl -> { return slaves.contains(sl); }).collect(Collectors.toList());
				}
				meta.setSlaveServers(slaves2);
			}
			break;
		case PENTAHO:
			// Get meta data from local meta store
			meta = cloudMetaStore.getElement(CloudApp.getInstance().getMetaStore(owner), name);
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// Get meta data from cloud transformation, only for testing!
			TransMeta transMeta = null;
			transMeta = CloudTransformation.getInstance().getTransformation();

			if (transMeta != null) {
				meta = transMeta.findClusterSchema(name);
			}
		}

		return meta;
	}

	/**
	 * 保存服务器集群配置到特定的存储中
	 * 
	 * @param meta
	 * @param update
	 * @throws KettleException
	 * @throws MetaStoreException
	 */
	private void saveClusterSchemaIntoStore(String owner ,ClusterSchema meta, boolean update) throws Exception {
		switch (metaStoreCategory) {
		case LOCAL:
			if (update) {
				// Update meta into meta store
				cloudMetaStore.updateElement(CloudApp.getInstance().getMetaStore(owner), meta);
			} else {
				// Create meta into meta store
				cloudMetaStore.createElement(CloudApp.getInstance().getMetaStore(owner), meta);
			}
			break;
		case CACHE:
			// TODO.
			break;
		case DATABASE:
			// TODO.
			break;
		case DEFAULT:
			TransMeta transMeta = CloudTransformation.getInstance().getTransformation();
			transMeta.addOrReplaceClusterSchema(meta);
			 CloudFileRepository.getInstance().saveTrans(transMeta);
		}
	}

	/**
	 * 从特定的存储中删除指定的服务器集群
	 * 
	 * @param meta
	 * @throws KettleException
	 */
	private void deleteClusterSchemaFromStore(String owner , ClusterSchema meta) throws Exception {
		switch (metaStoreCategory) {
		case LOCAL:
			cloudMetaStore.deleteElement(CloudApp.getInstance().getMetaStore(owner), meta);
			break;
		case CACHE:
			// TODO.
			break;
		case DATABASE:
			// TODO.
			break;
		case DEFAULT:
			TransMeta transMeta = CloudTransformation.getInstance().getTransformation();
			ClusterSchema cluster = transMeta.findClusterSchema(meta.getName());
			if (cluster != null) {
				List<ClusterSchema> clusters = transMeta.getClusterSchemas();
				clusters.remove(cluster);
				transMeta.setClusterSchemas(clusters);
				CloudFileRepository.getInstance().saveTrans(transMeta);
			}
		}
	}

	/**
	 * 根据名字查找指定的服务器集群
	 * 
	 * @param name
	 * @return
	 * @throws KettleException 
	 */
	@Override
	public ClusterSchema findClusterSchema(String owner ,String name) throws Exception {
		return getClusterSchema(owner ,name);
	}

	/*
	 * 服务方法 - 查询服务器集群列表 Get brief cluster schema list.
	 */
	@Override
	public  Map<String,List<ClusterDto>> getCloudClusterList(String owner ) throws Exception {
		
		Map<String,List<ClusterSchema>> clusters = getClusterSchemaList(owner);
		if (clusters == null) {
			return Maps.newHashMap();
		}
		return clusters.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey() , 
				entry -> entry.getValue().stream().map(cs -> {ClusterDto jc = new ClusterDto(cs); jc.setOwner(entry.getKey());return jc ; }).collect(Collectors.toList()) ));
	}

	/*
	 * 服务方法 - 分页查询服务器集群列表 Get brief cluster schema list.
	 */
	public Map<String,PaginationDto<ClusterDto>> getCloudClusterList(String owner ,boolean isMap , int page, int pageSize,String search) throws Exception {

		Map<String, List<ClusterSchema>> clusters = getClusterSchemaList(owner);
		return getPaginationMaps(isMap, page, pageSize, search, clusters, new DealRowsInterface<ClusterDto>() {

			@Override
			public ClusterDto dealRow(Object obj, Object... params) throws Exception {
				String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
				ClusterSchema cs = (ClusterSchema) obj;
				ClusterDto jc = new ClusterDto(cs);
				jc.setOwner(eleOwner);
				return jc;
			}

			@Override
			public boolean match(Object obj,String search, Object... params) throws Exception {
				//String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
				ClusterSchema cs = (ClusterSchema) obj;
				return defaultMatch(cs.getName(),search);//cs.getName()!=null&&cs.getName().toLowerCase().contains(search.toLowerCase());
			}
		});
		
	}

	/*
	 * 服务方法 - 检查服务器集群名是否存在 Check existing of cluster schema name.
	 */
	@Override
	public CheckResultDto checkClusterName(String owner , String name) throws Exception {
		ClusterSchema schema = getClusterSchema(owner , name);
		if (schema == null) {
			return new CheckResultDto(name, false); // Not existed.
		}
		return new CheckResultDto(name, true);
	}

	/*
	 * 服务方法 - 编辑服务器集群配置信息 Edit cluster schema.
	 */
	@Override
	public ClusterDetailsDto editCluster(String owner , String name) throws Exception {
		ClusterDetailsDto details = new ClusterDetailsDto();

		ClusterSchema schema = getClusterSchema(owner,name);
		if (schema != null) {
			details.setOwner( Const.NVL(owner, CloudSession.getResourceUser()) );
			details.setName(name);
			details.setPort(schema.getBasePort());
			details.setBuffer(schema.getSocketsBufferSize());
			details.setRows(schema.getSocketsFlushInterval());
			details.setCompress(schema.isSocketsCompressed());
			details.setDynamic(schema.isDynamic());

			List<ClusterSlaveDetailsDto> servers = new ArrayList<>();
			List<SlaveServer> slaves = schema.getSlaveServers();
			if (slaves != null) {
				for (SlaveServer slave : slaves) {
					ClusterSlaveDetailsDto server = new ClusterSlaveDetailsDto();
					server.setServerName(slave.getName());
					server.setMaster(slave.isMaster());
					server.setStatus(cloudServerService.testSlaveServerStatus(slave));
					servers.add(server);
				}
			}
			details.setServers(servers);
		}

		return details;
	}

	/*
	 * 服务方法 - 保存服务器集群配置信息 Save cluster schema.
	 */
	@Override
	public ReturnCodeDto saveCluster(ClusterDetailsDto details) throws Exception {
		boolean update = true;

		ClusterSchema schema = this.getClusterSchema(details.getOwner() , details.getName());
		if (schema == null) {
			schema = new ClusterSchema();
			schema.setDescription("New cluster schema, " + details.getName());
			update = false;
		}

		schema.setBasePort(details.getPort());
		schema.setDynamic(details.getDynamic());
		schema.setName(details.getName());
		schema.setSocketsBufferSize(details.getBuffer());
		schema.setSocketsCompressed(details.getCompress());
		schema.setSocketsFlushInterval(details.getRows());

		List<SlaveServer> slaves = new ArrayList<>();
		List<ClusterSlaveDetailsDto> servers = details.getServers();
		if (servers != null) {
			for (ClusterSlaveDetailsDto server : servers) {
				SlaveServer slave = cloudServerService.findSlaveServer(details.getOwner() ,server.getServerName());
				if (slave == null) {
					continue;
				}
				slaves.add(slave);
			}
		}
		schema.setSlaveServers(slaves);

		schema.setChanged(true);
		schema.setChangedDate(new Date());

		this.saveClusterSchemaIntoStore(details.getOwner() ,schema, update);
		return new ReturnCodeDto(0, "Succeeded");

	}

	/*
	 * 服务方法 - 删除服务器集群配置 Delete cluster schema.
	 */
	@Override
	public ReturnCodeDto deleteCluster(String owner , String name) throws Exception {
		ClusterSchema cluster = getClusterSchema(owner ,name);
		if (cluster != null) {
			deleteClusterSchemaFromStore(owner ,cluster);
			return new ReturnCodeDto(0, "Succeeded");
		}
		return new ReturnCodeDto(1, "Not existed");
	}
	
	private ClusterSchema initMetaDefaultCluster( String owner ) throws Exception {
		
		 Map<String, List<SlaveServer>> slaveMap = cloudServerService.getSlaveServerList(owner) ;
		List<SlaveServer> slaveServers = slaveMap!= null ? slaveMap.get(owner) : null ;
		if( slaveServers == null || slaveServers.isEmpty() ) {
			return null ;
		}
		
		String name= "all-server-cluster";
		ClusterSchema schema = this.getClusterSchema(owner , name);
		if (schema == null) {
			schema = new ClusterSchema();
			schema.setDescription("cluster schema, incloud all server." );
			schema.setBasePort("40000");
			schema.setDynamic(false);
			schema.setName(name);
			schema.setSocketsBufferSize("2000");
			schema.setSocketsCompressed(true);
			schema.setSocketsFlushInterval("5000");
			schema.setSlaveServers(slaveServers);
			schema.setChanged(true);
			schema.setChangedDate(new Date());
	
			this.saveClusterSchemaIntoStore(owner ,schema, false);
			
			return schema ;
		}
	
		return null;
	}
	

}

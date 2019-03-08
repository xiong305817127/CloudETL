/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.www.SlaveServerStatus;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.deploy.MetaCubeCategory;
import com.ys.idatrix.cloudetl.deploy.MetaStoreCategory;
import com.ys.idatrix.cloudetl.dto.common.CheckResultDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.common.TestResultDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto.DealRowsInterface;
import com.ys.idatrix.cloudetl.dto.server.ServerBriefDto;
import com.ys.idatrix.cloudetl.dto.server.ServerDetailsDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.EncryptUtil;
import com.ys.idatrix.cloudetl.reference.metacube.MetaCubeServer;
import com.ys.idatrix.cloudetl.repository.xml.CloudFileRepository;
import com.ys.idatrix.cloudetl.repository.xml.CloudTransformation;
import com.ys.idatrix.cloudetl.repository.xml.metastore.CloudServerMetaStore;
import com.ys.idatrix.cloudetl.service.CloudBaseService;

/**
 * Server repository implementation. Master server and slave servers.
 *
 * @author JW
 * @since 05-12-2017
 *
 */
@Service("CloudServerServiceImpl")
public class CloudServerServiceImpl extends CloudBaseService  implements CloudServerService {

	@Autowired(required=false)
	private MetaCubeServer metaCubeServer;
	@Autowired
	private CloudServerMetaStore cloudMetaStore;

	@Autowired
	private MetaStoreCategory metaStoreCategory;
	@Autowired
	private MetaCubeCategory metaCubeCategory;

	@Override
	public Map<String,List<SlaveServer>> getSlaveServerList(String owner) throws Exception {

		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			//server 用户共享 ,查询当前登录用户即可
			return getUserNameList(CloudSession.getLoginUser(), new ForeachCallback<String,SlaveServer> (){
				@Override
				public List<SlaveServer> getOne(String user) throws Exception {
					return   metaCubeServer.getSlaveServerList(user);
				}
			});
		case PENTAHO:
			// Get meta data from local meta store
			return getMetaStoreList(owner, new ForeachCallback<IMetaStore,SlaveServer> (){
				@Override
				public List<SlaveServer> getOne(IMetaStore source) throws Exception {
					return  cloudMetaStore.getElements( source );
				}
			});
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// Get meta data from cloud transformation, only for testing!
			TransMeta transMeta = CloudTransformation.getInstance().getTransformation();
			Map<String,List<SlaveServer>> result = Maps.newHashMap() ;
			result.put(CloudSession.getResourceUser(), transMeta.getSlaveServers() );
			return result;
		}
		return Maps.newHashMap();
	}

	private SlaveServer getSlaveServer(String owner, String name) throws Exception {
		SlaveServer meta = null;

		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			//server 用户共享 ,查询当前登录用户即可
			meta = metaCubeServer.getSlaveServer(CloudSession.getLoginUser(),name);
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
				meta = transMeta.findSlaveServer(name);
			}
		}

		return meta;
	}

	private void saveSlaveServerIntoStore(String owner, SlaveServer meta, boolean update) throws Exception {
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
			transMeta.addOrReplaceSlaveServer(meta);
			 CloudFileRepository.getInstance().saveTrans(transMeta);
		}
	}

	private void deleteSlaveServerFromStore(String owner, SlaveServer meta) throws Exception {
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
			SlaveServer cluster = transMeta.findSlaveServer(meta.getName());
			if (cluster != null) {
				List<SlaveServer> clusters = transMeta.getSlaveServers();
				clusters.remove(cluster);
				transMeta.setSlaveServers(clusters);
				 CloudFileRepository.getInstance().saveTrans(transMeta);
			}
		}
	}

	/**
	 * Find slave server by name.
	 * 
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	@Override
	public SlaveServer findSlaveServer(String owner, String name) throws Exception {
		return getSlaveServer(owner, name);
	}

	/**
	 * Get slave server status by name.
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getSlaveServerStatus(String owner, String name) throws Exception {
		SlaveServer slave = getSlaveServer(owner, name);
		if (slave == null) {
			return "Not Found";
		}

		return testSlaveServerStatus(slave);
	}

	/**
	 * Get slave server status.
	 * 
	 * @param slave
	 * @return
	 * @throws Exception
	 */
	public String testSlaveServerStatus(SlaveServer slave) throws Exception {
		SlaveServerStatus status = null;
		try{
			status = slave.getStatus();
		}catch(Exception e){
			return "Error";
		}
		if (status == null) {
			return "Unknown";
		} else {
			return status.getStatusDescription();
		}
	}

	/*
	 * 覆盖方法：doServerTest
	 */
	@Override
	public TestResultDto doServerTest(String owner, String name) throws Exception {
		TestResultDto result = new TestResultDto();
		result.setName(name);
		String message = getSlaveServerStatus(owner, name);
		result.setStatus("Online".equals(message) ? 0 : 1);
		result.setMessage(message);
		return result;
	}

	/**
	 * Get slave server list.
	 * @throws Exception 
	 */
	@Override
	public Map<String,List<ServerBriefDto>> getCloudServerList(String owner) throws Exception {

		 Map<String, List<SlaveServer>> slaves = getSlaveServerList(owner);
		 if (slaves == null) {
			 return Maps.newHashMap();
		 }
		 return slaves.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey() , 
					entry -> entry.getValue().stream().map(cs -> {
						ServerBriefDto js = new ServerBriefDto();
						js.setOwner(entry.getKey());
						js.setName(cs.getName());
						js.setMaster(cs.isMaster());
						js.setStatus(""); // Don't test status to speed up userinteraction
						// js.setStatus(getSlaveServerStatus(slave.getName()));
						
						return js ; 
						}).collect(Collectors.toList()) ));
	}

	public Map<String,PaginationDto<ServerBriefDto>> getCloudServerList(String owner, boolean isMap ,int page, int pageSize,String search) throws Exception {
		
		Map<String, List<SlaveServer>> slaves = getSlaveServerList(owner);
		return getPaginationMaps(isMap, page, pageSize, search, slaves, new DealRowsInterface<ServerBriefDto>() {
				@Override
				public ServerBriefDto dealRow(Object obj, Object... params) throws Exception {
					String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					SlaveServer slave = (SlaveServer) obj;
					ServerBriefDto js = new ServerBriefDto();
					js.setOwner(eleOwner);
					js.setName(slave.getName());
					js.setMaster(slave.isMaster());

					js.setStatus(""); // Don't test status to speed up user
										// interaction
					return js;
				}

				@Override
				public boolean match(Object obj, String search, Object... params) throws Exception {
					//String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					SlaveServer slave = (SlaveServer) obj;
					return defaultMatch(slave.getName(),search);//slave.getName()!=null&&slave.getName().toLowerCase().contains(search.toLowerCase());
				}
			});
	}

	/*
	 * Check existing of slave server name.
	 */
	@Override
	public CheckResultDto checkServerName(String owner, String name) throws Exception {
		SlaveServer schema = getSlaveServer(owner, name);
		if (schema == null) {
			return new CheckResultDto(name, false); // Not existed.
		}
		return new CheckResultDto(name, true);
	}

	/*
	 * Edit slave server.
	 */
	@Override
	public ServerDetailsDto editServer(String owner, String name) throws Exception {
		ServerDetailsDto details = new ServerDetailsDto();

		SlaveServer slave = getSlaveServer(owner, name);
		if (slave != null) {
			details.setOwner( Const.NVL(owner, CloudSession.getResourceUser()) );
			details.setName(name);
			details.setHostname(slave.getHostname());
			details.setPort(slave.getPort());
			details.setMaster(slave.isMaster());
			details.setUsername(slave.getUsername());
			details.setPassword(EncryptUtil.getInstance().strEnc(slave.getPassword(),name,slave.getHostname(),slave.getPort()));

//			SlaveServerStatus status = null;
//			try{
//				status = slave.getStatus();
//			}catch(Exception e){}
//			if (status == null) {
				details.setStatus("unknown");
//			} else {
//				details.setStatus(status.getStatusDescription());
//			}
		}

		return details;
	}

	/*
	 * Save slave server.
	 */
	@Override
	public ReturnCodeDto saveServer(ServerDetailsDto details) throws Exception {
		boolean update = true;

		SlaveServer slave = getSlaveServer(details.getOwner() , details.getName());
		if (slave == null) {
			slave = new SlaveServer();
			slave.setDescription("New slave server, " + details.getName());
			update = false;
		}

		slave.setName(details.getName());
		slave.setHostname(details.getHostname());
		slave.setPort(details.getPort());
		slave.setUsername(details.getUsername());
		slave.setPassword(EncryptUtil.getInstance().strDec(details.getPassword(),details.getName(),details.getHostname(),details.getPort()));
		slave.setMaster(details.getMaster());

		slave.setChanged(true);

		this.saveSlaveServerIntoStore(details.getOwner() , slave, update);
		return new ReturnCodeDto(0, "Succeeded");
	}

	/*
	 * Delete slave server.
	 */
	@Override
	public ReturnCodeDto deleteServer(String owner, String name) throws Exception {
		SlaveServer cluster = getSlaveServer(owner, name);
		if (cluster != null) {
			deleteSlaveServerFromStore(owner, cluster);

			return new ReturnCodeDto(0, "Succeeded");
		}
		return new ReturnCodeDto(1, "Not existed");
	}
	
	/*
	 * Calculate total counter of slave server.
	 */
	@Override
	public double serverTotalCounter() {
		
		AtomicInteger count = new AtomicInteger( 0 );
		try {
			Map<String, List<SlaveServer>> metaList = this.getSlaveServerList(CloudSession.getResourceUser());
			metaList.entrySet().stream().forEach(entry -> { count.addAndGet(entry.getValue().size()); });
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return count.get();
	}
	
	/*
	 * Calculate error counter of slave server.
	 */
	@Override
	public double serverErrorCounter() {
		AtomicInteger count = new AtomicInteger( 0 );
		
		try {
			Map<String, List<SlaveServer>> metaList = this.getSlaveServerList(CloudSession.getResourceUser());
			metaList.entrySet().stream().forEach(entry -> { 
				entry.getValue().stream().forEach(meta -> {
					try {
						if (!"Online".equalsIgnoreCase(testSlaveServerStatus(meta))) {
							count.incrementAndGet();
						}
					} catch (Exception e) {
					}
				});
			});
		} catch (Exception e) {
		}
		return count.get();
	}

}

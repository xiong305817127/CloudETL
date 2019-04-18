/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.hadoop;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.pms.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.deploy.MetaCubeCategory;
import com.ys.idatrix.cloudetl.deploy.MetaStoreCategory;
import com.ys.idatrix.cloudetl.dto.common.CheckResultDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto.DealRowsInterface;
import com.ys.idatrix.cloudetl.dto.hadoop.HadoopBriefDto;
import com.ys.idatrix.cloudetl.dto.hadoop.HadoopDetailsDto;
import com.ys.idatrix.cloudetl.dto.hadoop.JobTrackerDto;
import com.ys.idatrix.cloudetl.dto.hadoop.ZooKeeperDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.EncryptUtil;
import com.ys.idatrix.cloudetl.reference.metacube.MetaCubeHadoop;
import com.ys.idatrix.cloudetl.repository.xml.metastore.CloudHadoopMetaStore;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.HadoopClusterMeta;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.HadoopJobTrackerMeta;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.HadoopZooKeeperMeta;
import com.ys.idatrix.cloudetl.service.CloudBaseService;

/**
 * Hadoop cluster service implementation.
 *
 * @author JW
 * @since 05-12-2017
 *
 */
@Service
public class CloudHadoopServiceImpl extends CloudBaseService  implements CloudHadoopService {

	@Autowired(required=false)
	private MetaCubeHadoop metaCubeHadoop;
	@Autowired
	private CloudHadoopMetaStore cloudMetaStore;

	@Autowired
	private MetaStoreCategory metaStoreCategory;
	@Autowired
	private MetaCubeCategory metaCubeCategory;


	private Map<String,List<HadoopClusterMeta>> getHadoopClusterList(String owner) throws Exception {

		switch(metaCubeCategory) {
		case IDATRIX :
			// Calling MetaCube RPC APIs to get meta data
			//Hadoop 用户共享 ,查询当前登录用户即可
			return getUserNameList(CloudSession.getLoginUser(), new ForeachCallback<String,HadoopClusterMeta> (){
				@Override
				public List<HadoopClusterMeta> getOne(String user) throws Exception {
					return  metaCubeHadoop.getHadoopClusterList(user);
				}
			});
		case PENTAHO :
			// Get meta data from local meta store
			return getMetaStoreList(owner, new ForeachCallback<IMetaStore,HadoopClusterMeta> (){
				@Override
				public List<HadoopClusterMeta> getOne(IMetaStore source) throws Exception {
					return  cloudMetaStore.getElements(source);
				}
			});
		case TENANT :
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT :
			// Do nothing!
		}

		return Maps.newHashMap();
	}

	private HadoopClusterMeta getHadoopCluster(String owner, String name) throws Exception {
		HadoopClusterMeta meta = null;

		switch(metaCubeCategory) {
		case IDATRIX :
			// Calling MetaCube RPC APIs to get meta data
			//Hadoop 用户共享 ,查询当前登录用户即可
			meta = metaCubeHadoop.getMetaCubeHadoop(CloudSession.getLoginUser(),name);
			break;
		case PENTAHO :
			// Get meta data from local meta store
			meta = cloudMetaStore.getElement(CloudApp.getInstance().getMetaStore(owner), name);
			if( meta != null ) {
				meta.setOwner(owner);
			}
			break;
		case TENANT :
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT :
			// Do nothing!
		}
		return meta;
	}

	private void saveHadoopClusterIntoStore(String owner , HadoopClusterMeta meta, boolean update) throws  Exception {
		switch (metaStoreCategory) {
		case LOCAL : 
			if (update) {
				// Update meta into meta store
				cloudMetaStore.updateElement(CloudApp.getInstance().getMetaStore(owner), meta);
			} else {
				// Create meta into meta store
				cloudMetaStore.createElement(CloudApp.getInstance().getMetaStore(owner), meta);
			}
			break;
		case CACHE : 
			// TODO.
			break;
		case DATABASE : 
			// TODO.
			break;
		case DEFAULT :
			// Do nothing!
		}
	}

	private void deleteHadoopClusterFromStore(String owner , HadoopClusterMeta meta) throws Exception {
		switch (metaStoreCategory) {
		case LOCAL : 
			cloudMetaStore.deleteElement(CloudApp.getInstance().getMetaStore(owner), meta);
			break;
		case CACHE : 
			// TODO.
			break;
		case DATABASE : 
			// TODO.
			break;
		case DEFAULT :
			// Do nothing!
		}
	}
	
	/* 
	 * Get cloud hadoop cluster list.
	 */
	@Override
	public Map<String,List<HadoopBriefDto>> getCloudHadoopList(String owner) throws Exception {

		Map<String, List<HadoopClusterMeta>> clusters = getHadoopClusterList( owner );
		if (clusters == null) {
			return Maps.newHashMap();
		}
		return clusters.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey() , 
				entry -> entry.getValue().stream().map(cs -> {
					HadoopBriefDto brief = new HadoopBriefDto();
					brief.setOwner(entry.getKey());
					brief.setName(cs.getName());
					brief.setStatus(Integer.toString(cs.getStatus()));
					brief.setType(cs.getStorage());
					return brief ; 
					}).collect(Collectors.toList()) ));
		
	}

	public Map<String,PaginationDto<HadoopBriefDto>> getCloudHadoopList(String owner, boolean isMap , int page,int pageSize,String search) throws Exception{
		
		Map<String, List<HadoopClusterMeta>> clusters = getHadoopClusterList(owner);
		 return getPaginationMaps(isMap, page, pageSize, search, clusters, new DealRowsInterface<HadoopBriefDto>() {
				@Override
				public HadoopBriefDto dealRow(Object obj, Object... params) throws Exception {
					String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					HadoopClusterMeta cluster=(HadoopClusterMeta)obj;
					
					HadoopBriefDto brief = new HadoopBriefDto();
					brief.setOwner(eleOwner);
					brief.setName(cluster.getName());
					brief.setStatus(Integer.toString(cluster.getStatus()));
					brief.setType(cluster.getStorage());
					return brief;
				}

				@Override
				public boolean match(Object obj, String search, Object... params) throws Exception {
					//String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					HadoopClusterMeta cluster=(HadoopClusterMeta)obj;
					return defaultMatch(cluster.getName(),search);//cluster.getName()!=null&&cluster.getName().toLowerCase().contains(search.toLowerCase());
				}
			});
	}
	
	/* 
	 * Check if hadoop cluster name is existing.
	 */
	@Override
	public CheckResultDto checkHadoopName(String owner, String name) throws Exception {
		HadoopClusterMeta meta = getHadoopCluster(owner, name);
		if (meta == null) {
			return new CheckResultDto(name, false); // Not existed.
		}
		return new CheckResultDto(name, true);
	}

	/* 
	 * Edit hadoop cluster.
	 */
	@Override
	public HadoopDetailsDto editHadoop(String owner, String name) throws Exception {
		HadoopDetailsDto details = new HadoopDetailsDto();

		HadoopClusterMeta meta = getHadoopCluster(owner, name);
		if (meta != null) {
			details.setOwner(Const.NVL(owner, CloudSession.getResourceUser()));
			details.setName(name);
			details.setHostname(meta.getHostname());
			details.setPort(meta.getPort());
			details.setUsername(meta.getUsername());
			details.setPassword(EncryptUtil.getInstance().strEnc( meta.getPassword(),name,meta.getHostname(),meta.getPort() ) );

			details.setStatus(meta.getStatus());
			details.setStorage(meta.getStorage());

			details.setUrl(meta.getUrl());

			JobTrackerDto jobTracker = new JobTrackerDto();
			HadoopJobTrackerMeta jt = meta.getJobTracker();
			if (jt != null) {
				jobTracker.setHostname(jt.getHostname());
				jobTracker.setPort(jt.getPort());
			}
			details.setJobTracker(jobTracker);

			ZooKeeperDto zooKeeper = new ZooKeeperDto();
			HadoopZooKeeperMeta zk = meta.getZooKeeper();
			if (zk != null) {
				zooKeeper.setHostname(zk.getHostname());
				zooKeeper.setPort(zk.getPort());
			}
			details.setZooKeeper(zooKeeper);
		}

		return details;
	}

	/* 
	 * Save hadoop cluster information.
	 */
	@Override
	public ReturnCodeDto saveHadoop(HadoopDetailsDto details) throws Exception {
		boolean update = true;

		HadoopClusterMeta meta = getHadoopCluster(details.getOwner() , details.getName());
		if (meta == null) {
			meta = new HadoopClusterMeta();
			update = false;
		}

		meta.setOwner(details.getOwner() );
		meta.setName(details.getName());
		meta.setHostname(details.getHostname());
		meta.setPort(details.getPort());
		meta.setUsername(details.getUsername());
		meta.setPassword(EncryptUtil.getInstance().strDec( details.getPassword() ,details.getName() ,details.getHostname() ,details.getPort() ) );

		meta.setStatus(details.getStatus());
		meta.setStorage(details.getStorage().toLowerCase());

		meta.setUrl(details.getUrl());

		JobTrackerDto jobTracker = details.getJobTracker();
		HadoopJobTrackerMeta jt = new HadoopJobTrackerMeta();
		if (jobTracker != null) {
			jt.setHostname(jobTracker.getHostname());
			jt.setPort(jobTracker.getPort());
		}
		meta.setJobTracker(jt);

		ZooKeeperDto zooKeeper = details.getZooKeeper();
		HadoopZooKeeperMeta zk = new HadoopZooKeeperMeta();
		if (zooKeeper != null) {
			zk.setHostname(zooKeeper.getHostname());
			zk.setPort(zooKeeper.getPort());
		}
		meta.setZooKeeper(zk);

		this.saveHadoopClusterIntoStore(details.getOwner() ,meta, update);
		return new ReturnCodeDto(0, "Succeeded");
	}

	/* 
	 * Delete hadoop cluster.
	 */
	@Override
	public ReturnCodeDto deleteHadoop(String owner, String name) throws Exception {
		HadoopClusterMeta meta = getHadoopCluster(owner, name);
		if (meta != null) {
			deleteHadoopClusterFromStore(owner, meta);
			return new ReturnCodeDto(0, "Succeeded");
		}
		return new ReturnCodeDto(1, "Not existed");
	}

}

/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.engine;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.deploy.MetaCubeCategory;
import com.ys.idatrix.quality.deploy.MetaStoreCategory;
import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.common.PaginationDto.DealRowsInterface;
import com.ys.idatrix.quality.dto.engine.EngineBriefDto;
import com.ys.idatrix.quality.dto.engine.EngineDetailsDto;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.reference.metacube.MetaCubeServer;
import com.ys.idatrix.quality.repository.xml.metastore.CloudDefaultEngineMetaStore;
import com.ys.idatrix.quality.repository.xml.metastore.meta.DefaultEngineMeta;
import com.ys.idatrix.quality.service.CloudBaseService;

/**
 * Service implementation for cloud default engine. (Default run configuration
 * using default engine.)
 * 
 * @author JW
 * @since 2017年7月6日
 *
 */
@Service("CloudDefaultEngineService")
public class CloudDefaultEngineServiceImpl extends CloudBaseService  implements CloudDefaultEngineService {

	private final String DefaultLocalEngineName ="Default-Local";
	
	@Autowired(required=false)
	private MetaCubeServer metaCubeServer;
	@Autowired
	private CloudDefaultEngineMetaStore cloudMetaStore;

	@Autowired
	private MetaStoreCategory metaStoreCategory;
	@Autowired
	private MetaCubeCategory metaCubeCategory;
	
	@Override
	public Map<String,List<DefaultEngineMeta>> getDefaultEngineList(String owner) throws Exception {

		Map<String, List<DefaultEngineMeta>> metaList = Maps.newHashMap();
		switch (metaCubeCategory) {
		case IDATRIX:
			// No need calling MetaCube RPC APIs to get meta data
			// Default engine don't saved in MetaCube
			//break;
		case PENTAHO:
			// Get meta data from local meta store
			metaList = getMetaStoreList(owner, new ForeachCallback<IMetaStore,DefaultEngineMeta> (){
				@Override
				public List<DefaultEngineMeta> getOne(IMetaStore source) throws Exception {
					return cloudMetaStore.getElements(source);
				}
			});
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// Do nothing!
		}
		//增加默认的Local
		String user = Const.NVL(owner, CloudSession.getLoginUser()) ;
		 List<DefaultEngineMeta> curEngines = ( metaList != null && metaList.containsKey(user) )? metaList.get(user) : null ; 
		if(curEngines == null || curEngines.isEmpty() ) {
			curEngines = Lists.newArrayList() ;
			//质量系统暂时不需要远程
			//curEngines.addAll( initMetaDefaultEngine(owner) );
			metaList.put(user, curEngines);
		}
		curEngines.add(0,getDefaultLocalConfig());
		
		return metaList;
	}

	private DefaultEngineMeta getDefaultEngine(String owner, String name) throws Exception {
		DefaultEngineMeta meta = null;

		//如果是DefaultLocalEngineName则使用默认的本地执行配置
		if(DefaultLocalEngineName.equals(name)) {
			return getDefaultLocalConfig();
		}
		switch (metaCubeCategory) {
		case IDATRIX:
			// No need calling MetaCube RPC APIs to get meta data
			// Default engine don't saved in MetaCube
			//break;
		case PENTAHO:
			// Get meta data from local meta store
			meta = cloudMetaStore.getElement(CloudApp.getInstance().getMetaStore(owner), name);
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// Do nothing!
		}

		return meta;
	}

	private void saveDefaultEngineIntoStore(String owner, DefaultEngineMeta meta, boolean update)
			throws Exception {
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
			// Do nothing!
		}
	}

	private void deleteDefaultEngineFromStore(String owner, DefaultEngineMeta meta) throws Exception {
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
			// Do nothing!
		}
	}

	/**
	 * Find default engine by name.
	 * @throws Exception 
	 */
	@Override
	public DefaultEngineMeta findDefaultEngine(String owner, String name) throws Exception {
		return getDefaultEngine(owner,name);
	}

	/**
	 * Get brief default engine list.
	 * @throws Exception 
	 */
	@Override
	public Map<String,List<EngineBriefDto>> getCloudDefaultEngineList(String owner) throws Exception {
		 Map<String, List<DefaultEngineMeta>> engines = getDefaultEngineList(owner);
			if (engines == null) {
				return Maps.newHashMap();
			}
			return engines.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey() , 
					entry -> entry.getValue().stream().map(cs -> {
						EngineBriefDto engine = new EngineBriefDto();
						engine.setOwner(entry.getKey());
						engine.setName(cs.getName());
						engine.setStatus("Testing");
						engine.setType(cs.isLocal()?"Local":(cs.isRemote()?"Remote":((cs.isClustered()?"Clustered":"Default"))));
						return engine ; 
						}).collect(Collectors.toList()) ));
	}

	public Map<String,PaginationDto<EngineBriefDto>> getCloudDefaultEngineList(String owner, boolean isMap , int page, int pageSize,String search) throws Exception {
		
		Map<String, List<DefaultEngineMeta>> engines = getDefaultEngineList(owner);
		return getPaginationMaps(isMap, page, pageSize, search, engines, new DealRowsInterface<EngineBriefDto>() {
			@Override
			public EngineBriefDto dealRow(Object obj, Object... params) throws Exception {
				String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
				DefaultEngineMeta meta = (DefaultEngineMeta) obj;
				EngineBriefDto engine = new EngineBriefDto();
				engine.setOwner(eleOwner);
				engine.setName(meta.getName());
				engine.setStatus("Testing");
				engine.setType(meta.isLocal()?"Local":(meta.isRemote()?"Remote":((meta.isClustered()?"Clustered":"Default"))));
				return engine;
			}
			
			@Override
			public boolean match(Object obj,String search, Object... params) throws Exception {
				//String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
				DefaultEngineMeta meta = (DefaultEngineMeta) obj;
				return defaultMatch(meta.getName(),search);//meta.getName()!=null&&meta.getName().toLowerCase().contains(search.toLowerCase());
			}
		});
	}

	/**
	 * Check existing of default engine name.
	 * @throws Exception 
	 */
	@Override
	public CheckResultDto checkDefaultEngineName(String owner, String name) throws Exception {
		DefaultEngineMeta meta = getDefaultEngine( owner,name);
		if (meta == null) {
			return new CheckResultDto(name, false);
		}
		return new CheckResultDto(name, true);
	}

	/**
	 * Get default engine details.
	 * @throws Exception 
	 */
	@Override
	public EngineDetailsDto editDefaultEngine(String owner, String name) throws Exception {
		EngineDetailsDto details = new EngineDetailsDto();

		DefaultEngineMeta meta = getDefaultEngine( owner,name);
		if (meta != null) {
			details.setOwner( Const.NVL(owner, CloudSession.getResourceUser()) );
			details.setName(meta.getName());
			details.setDescription(meta.getDescription());
			details.setClustered(meta.isClustered());
			details.setLocal(meta.isLocal());
			details.setLogRemoteExecutionLocally(meta.isLogRemoteExecutionLocally());
			details.setReadOnly(meta.isReadOnly());
			details.setRemote(meta.isRemote());
			details.setSendResources(meta.isSendResources());
			details.setServer(meta.getServer());
			details.setShowTransformations(meta.isShowTransformations());
		}

		return details;
	}

	/**
	 * Save default engine details.
	 * 
	 * @throws MetaStoreException
	 * @throws KettleException
	 */
	@Override
	public ReturnCodeDto saveDefaultEngine(EngineDetailsDto details) throws Exception {

		if(DefaultLocalEngineName.equals(details.getName())) {
			return new ReturnCodeDto(2,DefaultLocalEngineName+" 是默认执行配置,不能更改!");
		}
		
		boolean update = true;

		DefaultEngineMeta meta = getDefaultEngine(details.getOwner() , details.getName());
		if (meta == null) {
			meta = new DefaultEngineMeta();
			update = false;
		}

		meta.setName(details.getName());
		meta.setDescription(details.getDescription());

		meta.setLocal(details.isLocal());
		meta.setClustered(details.isClustered());
		meta.setRemote(details.isRemote());
		meta.setServer(details.getServer());

		// Adjust engine execution mode to ensure all parameters consistency.
		if (!StringUtils.hasText(details.getServer())) {
			meta.setLocal(true);
			meta.setRemote(false);
			meta.setClustered(false);
		} else if ("Clustered".equals(details.getServer())) {
			meta.setClustered(true);
			meta.setRemote(false);
			meta.setLocal(false);
		} else {
			meta.setRemote(true);
			meta.setLocal(false);
			meta.setClustered(false);
		}

		meta.setLogRemoteExecutionLocally(details.isLogRemoteExecutionLocally());
		meta.setReadOnly(details.isReadOnly());
		meta.setSendResources(details.isSendResources());
		meta.setShowTransformations(details.isShowTransformations());

		this.saveDefaultEngineIntoStore(details.getOwner() ,meta, update);
		return new ReturnCodeDto(0, "Succeeded");
	}

	/**
	 * Delete a default engine.
	 * @throws Exception 
	 */
	@Override
	public ReturnCodeDto deleteDefaultEngine(String owner, String name) throws Exception {
		
		if(DefaultLocalEngineName.equals(name)) {
			return new ReturnCodeDto(2,DefaultLocalEngineName+" 是默认执行配置,不能删除!");
		}
		
		DefaultEngineMeta meta = getDefaultEngine(owner,name);
		if (meta != null) {
			deleteDefaultEngineFromStore(owner,meta);
			return new ReturnCodeDto(0, "Succeeded");
		}
		return new ReturnCodeDto(1, "Not existed");
	}
	
	private DefaultEngineMeta getDefaultLocalConfig() {
		DefaultEngineMeta defauleLocal = new DefaultEngineMeta();
		defauleLocal.setName(DefaultLocalEngineName);
		defauleLocal.setLocal(true);
		defauleLocal.setRemote(false);
		defauleLocal.setClustered(false);
		defauleLocal.setDescription("default local engine");
		return defauleLocal;
	}
	
	@SuppressWarnings("unused")
	private List<DefaultEngineMeta> initMetaDefaultEngine( String owner ) throws Exception {
		 List<DefaultEngineMeta> result= Lists.newArrayList() ;
		 
		for ( SlaveServer server : metaCubeServer.getSlaveServerList(owner)) {
			
			String name= "remote"+(server.isMaster()?"-master-":"-slave-")+server.getName() ;
			DefaultEngineMeta details = getDefaultEngine(owner , name );
			if (details != null) {
				continue ;
			}
			details = new DefaultEngineMeta() ;
			details.setName(name);
			details.setDescription("remote server "+server.getName());
			details.setLocal(false);
			details.setRemote(true);
			details.setClustered(false);
			details.setSendResources(true);
			details.setServer(server.getName());
			this.saveDefaultEngineIntoStore(owner ,details, false);
			
			result.add(details) ;
		}
		
		String name= "cluster-engine";
		DefaultEngineMeta details = getDefaultEngine(owner , name );
		if (details == null) {
			details = new DefaultEngineMeta() ;
			details.setName(name);
			details.setDescription("cluster server ");
			details.setLocal(false);
			details.setRemote(false);
			details.setClustered(true);
			details.setSendResources(true);
			details.setServer("Clustered");
			this.saveDefaultEngineIntoStore(owner ,details, false);
			
			result.add(details) ;
		}
		
		return result ;
	}
	

}

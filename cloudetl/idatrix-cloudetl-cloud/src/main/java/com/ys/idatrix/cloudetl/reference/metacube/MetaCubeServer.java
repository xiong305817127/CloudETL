/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.reference.metacube;

import java.util.ArrayList;
import java.util.List;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.util.Utils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeServerDetailsDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeServerDto;
import com.ys.idatrix.metacube.api.beans.AmbariClusterInfoDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.service.AmbariPlatformService;

/**
 * Synchronize (slave) server data from MetaCube system
 * by calling its RPC APIs exported as dubbo services.
 * 
 * @author JW
 * @since 2017年5月24日
 *
 */
@Service
public class MetaCubeServer extends MetaCubeBase {
	
	// "CLOUDETL" : "name1:host1:9090:Y:user:passwd,name2:host2:9090:N:user:passwd"
	protected final String META_SEPARATOR = ",";
	protected final String META_SPLITTER = ":";

	@Reference(check=false )
	private AmbariPlatformService ambariPlatformService;

	/**
	 * Get ETL slave server list from MetaCube.
	 * @return
	 */
	public List<MetaCubeServerDto> getMetaCubeServerList(String owner) {
		CloudLogger.getInstance(owner).info(this,"getMetaCubeServerList...");
		
		List<MetaCubeServerDto> dtos = new ArrayList<>();
		try {
			ResultBean<AmbariClusterInfoDTO> resultBean = ambariPlatformService.getClusterInfo() ;
			if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				AmbariClusterInfoDTO result = resultBean.getData() ;
				CloudLogger.getInstance(owner).info(this,"getMetaCubeServerList result:" , result.getCloudETL());
				
				// "CLOUDETL" : "name1:host1:9090:Y:user:passwd,name2:host2:9090:N:user:passwd"
				if(!Utils.isEmpty(result.getCloudETL())) {
					for(String ce: result.getCloudETL().split( META_SEPARATOR )) {
						if( Utils.isEmpty(ce)) {
							continue ;
						}
						String[] c = ce.split( META_SPLITTER );
						MetaCubeServerDto dto = new MetaCubeServerDto();
						dto.setName(c[0].trim());
						dto.setMaster("Y".equalsIgnoreCase(c[3].trim()));
						dto.setStatus(0);
						dtos.add(dto);
					}
					
				}
			}
		} catch (Exception ex) {
			CloudLogger.getInstance(owner).error(this,"getMetaCubeServerList,获取ETL服务器列表失败",ex);
		}

		return dtos;
	}

	/**
	 * Get ETL slave server meta list from MetaCube.
	 * @return
	 */
	public List<SlaveServer> getSlaveServerList(String owner) {
		CloudLogger.getInstance(owner).info(this,"getSlaveServerList...");

		List<SlaveServer> metaList = new ArrayList<>();
		try {
			ResultBean<AmbariClusterInfoDTO> resultBean = ambariPlatformService.getClusterInfo() ;
			if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				AmbariClusterInfoDTO result = resultBean.getData() ;
				CloudLogger.getInstance(owner).info(this,"getSlaveServerList result:", result.getCloudETL());
				
				// "CLOUDETL" : "name1:host1:9090:Y:user:passwd,name2:host2:9090:N:user:passwd"
				if(!Utils.isEmpty(result.getCloudETL())) {
					for(String ce: result.getCloudETL().split( META_SEPARATOR )) {
						if( Utils.isEmpty(ce)) {
							continue ;
						}
						String[] servers = ce.split( META_SPLITTER );
						if (servers.length > 5) {
							SlaveServer serverMeta = new SlaveServer();
							serverMeta.setName(servers[0].trim());
							serverMeta.setHostname(servers[1].trim());
							serverMeta.setPort(servers[2].trim());
							serverMeta.setMaster("Y".equals(servers[3].trim()) ? true : false);
							serverMeta.setUsername(servers[4].trim());
							serverMeta.setPassword(servers[5].trim());

							serverMeta.setWebAppName("");
							serverMeta.setProxyHostname("");
							serverMeta.setProxyPort("");
							serverMeta.setNonProxyHosts("");
							metaList.add(serverMeta);
						}
					}
					
				}
			}
			
		} catch (RuntimeException ex) {
			CloudLogger.getInstance(owner).error(this ,"getSlaveServerList 获取服务器连接列表失败",ex);
		}

		return metaList;
	}

	/**
	 * Get ETL slave server configuration information from MetaCube.
	 * @param name
	 * @return
	 */
	public MetaCubeServerDetailsDto getMetaCubeServerDetails(String owner, String name) {
		CloudLogger.getInstance(owner).info(this ,"getMetaCubeServerDetails("+owner+","+name+")...");

		try {
			 ResultBean<AmbariClusterInfoDTO> resultBean = ambariPlatformService.getClusterInfo() ;
			if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				AmbariClusterInfoDTO result = resultBean.getData() ;
				CloudLogger.getInstance(owner).info(this,"getMetaCubeServerDetails result:", result.getCloudETL());
				
				// "CLOUDETL" : "name1:host1:9090:Y:user:passwd,name2:host2:9090:N:user:passwd"
				if(!Utils.isEmpty(result.getCloudETL())) {
					for(String ce: result.getCloudETL().split( META_SEPARATOR )) {
						if( Utils.isEmpty(ce)) {
							continue ;
						}
						String[] servers = ce.split( META_SPLITTER );
						if( servers[0].trim().equalsIgnoreCase(name)  ) {
							if (servers.length > 5) {
								MetaCubeServerDetailsDto dto = new MetaCubeServerDetailsDto();
								dto.setName(servers[0].trim());
								dto.setHostname(servers[1].trim());
								dto.setPort(servers[2].trim());
								dto.setStatus(0);
								dto.setUsername(servers[4].trim());
								dto.setPassword(servers[5].trim());
								dto.setMaster("Y".equals(servers[3].trim()) ? true : false);
								return dto;
							}
						}
					}
				}
			}
			
		} catch (Exception ex) {
			CloudLogger.getInstance(owner).error(this,"getMetaCubeServerDetails("+owner+","+name+")获取服务器详细信息失败",ex);
		}

		return null;
	}

	/**
	 * Get slave server meta from MetaCube.
	 * @param name
	 * @return
	 */
	public SlaveServer getSlaveServer(String owner, String name) {
		CloudLogger.getInstance(owner).info(this, "getSlaveServer(" + owner + "," + name + ")...");

		try {
			 ResultBean<AmbariClusterInfoDTO> resultBean = ambariPlatformService.getClusterInfo();
			if (resultBean != null && resultBean.isSuccess() && resultBean.getData() != null ) {
				AmbariClusterInfoDTO result = resultBean.getData() ;
				CloudLogger.getInstance(owner).info(this, "getSlaveServer result:", result.getCloudETL());

				// "CLOUDETL" : "name1:host1:9090:Y:user:passwd,name2:host2:9090:N:user:passwd"
				if (!Utils.isEmpty(result.getCloudETL())) {
					for (String ce : result.getCloudETL().split(META_SEPARATOR)) {
						if (Utils.isEmpty(ce)) {
							continue;
						}
						String[] servers = ce.split(META_SPLITTER);
						if (servers[0].trim().equalsIgnoreCase(name)) {
							if (servers.length > 5) {
								SlaveServer serverMeta = new SlaveServer();
								serverMeta.setName(servers[0].trim());
								serverMeta.setHostname(servers[1].trim());
								serverMeta.setPort(servers[2].trim());
								serverMeta.setMaster("Y".equals(servers[3].trim()) ? true : false);
								serverMeta.setUsername(servers[4].trim());
								serverMeta.setPassword(servers[5].trim());

								serverMeta.setWebAppName("");
								serverMeta.setProxyHostname("");
								serverMeta.setProxyPort("");
								serverMeta.setNonProxyHosts("");
								return serverMeta ;
							}
						}
					}
				}
			}
		} catch (RuntimeException ex) {
			CloudLogger.getInstance(owner).error(this, "getSlaveServer(" + owner + "," + name + ")获取单个服务器信息失败", ex);
		}

		return null;
	}

}

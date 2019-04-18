/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.monitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.di.www.SlaveServerStatus;
import org.pentaho.metastore.stores.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.PluginFactory;
import com.ys.idatrix.cloudetl.service.server.CloudServerService;

/**
 * CloudServerMetrics <br/>
 * 
 * @author JW
 * @since 2017年9月26日
 * 
 */
public class CloudServerMetrics {

	public static final Log  logger = LogFactory.getLog(CloudServerMetrics.class);

	/**
	 * Get the count of all servers (includes all users' meta store)
	 * 
	 * @return
	 */
	public static double serverTotalCounter() {
		// From MetaCube
		if ("iDatrix".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("metaCube.category"))) {
			try {
				Object cloudServerService = PluginFactory.getBean("CloudServerServiceImpl");
				if (cloudServerService != null) {
					return ((CloudServerService) cloudServerService).serverTotalCounter();
				} else {
					logger.warn("No server service available!");
				}
			} catch (Exception e) {
				logger.error("server total counter error.", e);
			}
		}

		// From local meta store
		try {
			return loadServerMetrics().size();
		} catch (Exception e) {
			logger.error("server total counter error.", e);
		}

		return 0;
	}

	/**
	 * Get the count of all servers in error (includes all users' meta store)
	 * 
	 * @return
	 */
	public static double serverErrorCounter() {
		// From MetaCube
		if ("iDatrix".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("metaCube.category"))) {
			try {
				Object cloudServerService = PluginFactory.getBean("CloudServerServiceImpl");
				if (cloudServerService != null) {
					return ((CloudServerService) cloudServerService).serverErrorCounter();
				} else {
					logger.warn("No server service available!");
				}
			} catch (Exception e) {
				logger.error("server error counter error.", e);
			}
		}

		// From local meta store
		double count = 0;
		try {
			List<SlaveServer> servers = loadServerMetrics();
			for (SlaveServer server : servers) {
				SlaveServerStatus status = null;
				try {
					status = server.getStatus();
				} catch (Exception e) {
				}

				if (status == null || !"Online".equalsIgnoreCase(status.getStatusDescription())) {
					count++;
				}
			}
		} catch (Exception e) {
			logger.error("server error counter error.", e);
		}

		return count;
	}

	/**
	 * Get all slave servers in all users' local repository.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static List<SlaveServer> loadServerMetrics() throws Exception {
		List<SlaveServer> metaList = new ArrayList<>();

		FileObject rootRepoDir = KettleVFS.getFileObject(CloudApp.getInstance().getRepositoryRootFolder());
		if (rootRepoDir == null || !rootRepoDir.isFolder())
			return metaList;

		FileObject[] userRepoDirs = rootRepoDir.getChildren();
		for (FileObject userRepoDir : userRepoDirs) {
			if (!userRepoDir.isFolder()) {
				userRepoDir.close();
				continue;
			}
			FileObject metaDir = null;
			FileObject metadataDir = userRepoDir.getChild(XmlUtil.META_FOLDER_NAME);
			if (metadataDir != null) {
				FileObject idatrixDir = metadataDir.getChild(MetaStoreConst.NAMESPACE_IDATRIX);
				if (idatrixDir != null) {
					metaDir = idatrixDir.getChild(MetaStoreConst.ELEMENT_TYPE_NAME_SLAVE_SERVER);
					idatrixDir.close();
				}
				metadataDir.close();
			}
			if (metaDir != null) {
				FileObject[] metaFiles = metaDir.getChildren();
				for (FileObject metaFile : metaFiles) {
					Document doc = XMLHandler.loadXMLFile(metaFile);
					if (doc == null)
						continue;

					SlaveServer meta = loadServerFromXml(doc);
					if (meta != null) {
						metaList.add(meta);
					}
				}
				metaDir.close();
			}

			userRepoDir.close();
		}
		rootRepoDir.close();

		return metaList;
	}

	/**
	 * Load the slave server with given XML document in local repository.
	 * 
	 * @param doc
	 * @return
	 */
	private static SlaveServer loadServerFromXml(Document doc) {
		// Root node:
		Node rootNode = XMLHandler.getSubNode(doc, "element");
		if (rootNode == null) {
			return null;
		}

		// Load the appropriate slave server details
		//
		SlaveServer serverMeta = new SlaveServer();
		serverMeta.setName(XMLHandler.getTagValue(rootNode, MetaStoreConst.SERVER_ATTR_ID_NAME));

		Node childrenNode = XMLHandler.getSubNode(rootNode, "children");
		int n = XMLHandler.countNodes(childrenNode, "child");
		for (int i = 0; i < n; i++) {
			Node childNode = XMLHandler.getSubNodeByNr(childrenNode, "child", i);
			String id = XMLHandler.getTagValue(childNode, "id");
			String value = XMLHandler.getTagValue(childNode, "value");

			switch (id) {
			case MetaStoreConst.SERVER_ATTR_ID_HOST_NAME:
				serverMeta.setHostname(value);
				break;
			case MetaStoreConst.SERVER_ATTR_ID_PORT:
				serverMeta.setPort(value);
				break;
			case MetaStoreConst.SERVER_ATTR_ID_USER_NAME:
				serverMeta.setUsername(value);
				break;
			case MetaStoreConst.SERVER_ATTR_ID_PASSWORD:
				serverMeta.setPassword(value);
				break;
			case MetaStoreConst.SERVER_ATTR_ID_MASTER:
				serverMeta.setMaster("Y".equals(value));
				break;
			case MetaStoreConst.SERVER_ATTR_ID_WEB_APP_NAME:
				serverMeta.setWebAppName(value);
				break;
			case MetaStoreConst.SERVER_ATTR_ID_PROXY_HOSTNAME:
				serverMeta.setProxyHostname(value);
				break;
			case MetaStoreConst.SERVER_ATTR_ID_PROXY_PORT:
				serverMeta.setProxyPort(value);
				break;
			case MetaStoreConst.SERVER_ATTR_ID_NON_PROXY_HOSTS:
				serverMeta.setNonProxyHosts(value);
				break;
			}
		}

		return serverMeta;
	}

}

/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.repository.xml.metastore.util;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.util.MetaStoreUtil;
import org.pentaho.pms.util.Const;

/**
 * Utilities for meta store operation on slave server.
 * @author JW
 * @since 2017年6月30日
 *
 */
public class ServerMetaStoreUtil extends MetaStoreUtil {

	public static List<SlaveServer> getElements(IMetaStore metaStore) throws MetaStoreException {
		List<SlaveServer> servers = new ArrayList<>();

		if(metaStore == null) {
			return servers ;
		}
		
		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SLAVE_SERVER);
		if (elementType == null) {
			return servers;
		}

		List<IMetaStoreElement> elements = metaStore.getElements(MetaStoreConst.NAMESPACE_IDATRIX, elementType);
		for (IMetaStoreElement element : elements) {
			try {
				SlaveServer server = loadMetaFromElement(metaStore, element);
				servers.add(server);
			} catch (Exception e) {
				throw new MetaStoreException("Unable to load slave server from element with name '"
						+ element.getName() + "' and type '" + elementType.getName() + "'", e);
			}
		}

		return servers;
	}

	public static SlaveServer getElement(IMetaStore metaStore, String name) throws MetaStoreException {
		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SLAVE_SERVER);
		if (elementType != null) {
			IMetaStoreElement element = null;
			try {
				element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX, elementType, name);
			} catch (MetaStoreException e) {
			}
			if (element != null) {
				try {
					return loadMetaFromElement(metaStore, element);
				} catch (KettlePluginException e) {
					throw new MetaStoreException("Unable to load slave server from element with name '"
							+ element.getName() + "' and type '" + elementType.getName() + "'", e);
				}
			}
		}

		return null;
	}

	public static void createElement(IMetaStore metaStore, SlaveServer serverMeta) throws MetaStoreException {
		// If the Pentaho namespace doesn't exist, create it!
		//
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_IDATRIX)) {
			metaStore.createNamespace(MetaStoreConst.NAMESPACE_IDATRIX);
		}

		// If the slave server element type doesn't exist, create it
		//
		populateElementType(metaStore);

		// populate an element, store it.
		//
		IMetaStoreElement serverElement = populateElement(metaStore, serverMeta);

		// Store the element physically
		//
		metaStore.createElement(MetaStoreConst.NAMESPACE_IDATRIX, serverElement.getElementType(), serverElement);
	}

	public static IMetaStoreElementType populateElementType(IMetaStore metaStore) throws MetaStoreException {
		// If the slave server element type doesn't exist, create it
		//
		IMetaStoreElementType elementType =
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SLAVE_SERVER);
		if (elementType != null) {
			return elementType;
		}

		// The new type will typically have an ID so all we need to do is give the type a name and a description.
		//
		elementType = metaStore.newElementType(MetaStoreConst.NAMESPACE_IDATRIX);

		// Name and description...
		//
		elementType.setName(MetaStoreConst.ELEMENT_TYPE_NAME_SLAVE_SERVER);
		elementType.setDescription(MetaStoreConst.ELEMENT_TYPE_DESCRIPTION_SLAVE_SERVER);
		metaStore.createElementType(MetaStoreConst.NAMESPACE_IDATRIX, elementType);
		return elementType;
	}

	public static IMetaStoreElement populateElement(IMetaStore metaStore, SlaveServer serverMeta) throws MetaStoreException {
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_IDATRIX)) {
			throw new MetaStoreException("Namespace '" + MetaStoreConst.NAMESPACE_IDATRIX + "' doesn't exist.");
		}

		if (serverMeta.getName() == null) {
			throw new MetaStoreException("Server name '" + MetaStoreConst.NAMESPACE_IDATRIX + "' can't be empty.");
		}

		// If the data type doesn't exist, error out...
		//
		/*IMetaStoreElementType elementType = 
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SLAVE_SERVER);
		if (elementType == null) {
			throw new MetaStoreException("Unable to find the slave server type");
		}*/

		IMetaStoreElementType elementType = populateElementType(metaStore);

		// generate a new slave server element and populate it with metadata
		//
		IMetaStoreElement element = metaStore.newElement(elementType, serverMeta.getName(), null);
		element.setName(serverMeta.getName());

		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_NAME, Const.NVL(serverMeta.getName(), "")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_HOST_NAME, Const.NVL(serverMeta.getHostname(), "localhost")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_PORT, Const.NVL(serverMeta.getPort(),"80")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_USER_NAME, serverMeta.getUsername()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_PASSWORD, serverMeta.getPassword()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_MASTER, serverMeta.isMaster() ? "Y" : "N"));

		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_WEB_APP_NAME, serverMeta.getWebAppName()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_PROXY_HOSTNAME, serverMeta.getProxyHostname()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_PROXY_PORT, serverMeta.getProxyPort()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SERVER_ATTR_ID_NON_PROXY_HOSTS, serverMeta.getNonProxyHosts()));

		return element;
	}

	public static SlaveServer loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) throws KettlePluginException {
		SlaveServer serverMeta = new SlaveServer();

		// Load the appropriate slave server details
		//
		serverMeta.setName(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_NAME));
		serverMeta.setHostname(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_HOST_NAME));
		serverMeta.setPort(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_PORT));
		serverMeta.setUsername(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_USER_NAME));
		serverMeta.setPassword(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_PASSWORD));
		serverMeta.setMaster("Y".equals(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_MASTER)));

		serverMeta.setWebAppName(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_WEB_APP_NAME));
		serverMeta.setProxyHostname(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_PROXY_HOSTNAME));
		serverMeta.setProxyPort(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_PROXY_PORT));
		serverMeta.setNonProxyHosts(getChildString(element, MetaStoreConst.SERVER_ATTR_ID_NON_PROXY_HOSTS));

		return serverMeta;
	}

	public static void updateElement(IMetaStore metaStore, SlaveServer serverMeta) throws MetaStoreException {
		// Populate element
		IMetaStoreElement element = populateElement(metaStore, serverMeta);

		// Find the existing element
		IMetaStoreElement de = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX, element.getElementType(), element.getName());
		if (de != null) {
			// Update the existing element
			metaStore.updateElement(MetaStoreConst.NAMESPACE_IDATRIX,
					metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_IDATRIX, element.getElementType().getName()), de.getId(), element);
		} else {
			// Create it newly
			metaStore.createElement(MetaStoreConst.NAMESPACE_IDATRIX, element.getElementType(), element);
		}
	}

	public static void deleteElement(IMetaStore metaStore, SlaveServer serverMeta) throws MetaStoreException {
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SLAVE_SERVER);
		if (elementType == null) {
			return;
		}

		// Find the existing element
		IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX, elementType, serverMeta.getName());

		if (element != null) {
			metaStore.deleteElement(MetaStoreConst.NAMESPACE_IDATRIX, elementType, element.getId());
		}
	}

}

/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.repository.xml.metastore.util;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreAttribute;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.util.MetaStoreUtil;
import org.pentaho.pms.util.Const;

/**
 * Utilities for meta store operation on cluster schema.
 * @author JW
 * @since 2017年6月30日
 *
 */
public class ClusterMetaStoreUtil extends MetaStoreUtil {

	public static List<ClusterSchema> getElements(IMetaStore metaStore) throws MetaStoreException {
		List<ClusterSchema> clusters = new ArrayList<>();

		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_CLUSTER_SCHEMA);
		if (elementType == null) {
			return clusters;
		}

		List<IMetaStoreElement> elements = metaStore.getElements(MetaStoreConst.NAMESPACE_IDATRIX, elementType);
		for (IMetaStoreElement element : elements) {
			try {
				ClusterSchema cluster = loadMetaFromElement(metaStore, element);
				clusters.add(cluster);
			} catch (Exception e) {
				throw new MetaStoreException("Unable to load cluster schema from element with name '"
						+ element.getName() + "' and type '" + elementType.getName() + "'", e);
			}
		}

		return clusters;
	}

	public static ClusterSchema getElement(IMetaStore metaStore, String name) throws MetaStoreException {
		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_CLUSTER_SCHEMA);
		if (elementType != null) {
			IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX, elementType, name);
			if (element != null) {
				try {
					return loadMetaFromElement(metaStore, element);
				} catch (KettlePluginException e) {
					throw new MetaStoreException("Unable to load cluster schema from element with name '"
							+ element.getName() + "' and type '" + elementType.getName() + "'", e);
				}
			}
		}

		return null;
	}

	public static void createElement(IMetaStore metaStore, ClusterSchema clusterMeta) throws MetaStoreException {
		// If the Pentaho namespace doesn't exist, create it!
		//
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_IDATRIX)) {
			metaStore.createNamespace(MetaStoreConst.NAMESPACE_IDATRIX);
		}

		// If the cluster schema element type doesn't exist, create it
		//
		populateElementType(metaStore);

		// populate an element, store it.
		//
		IMetaStoreElement clusterElement = populateElement(metaStore, clusterMeta);

		// Store the element physically
		//
		metaStore.createElement(MetaStoreConst.NAMESPACE_IDATRIX, clusterElement.getElementType(), clusterElement);
	}

	public static IMetaStoreElementType populateElementType(IMetaStore metaStore) throws MetaStoreException {
		// If the cluster schema element type doesn't exist, create it
		//
		IMetaStoreElementType elementType =
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_CLUSTER_SCHEMA);
		if (elementType != null) {
			return elementType;
		}

		// The new type will typically have an ID so all we need to do is give the type a name and a description.
		//
		elementType = metaStore.newElementType(MetaStoreConst.NAMESPACE_IDATRIX);

		// Name and description...
		//
		elementType.setName(MetaStoreConst.ELEMENT_TYPE_NAME_CLUSTER_SCHEMA);
		elementType.setDescription(MetaStoreConst.ELEMENT_TYPE_DESCRIPTION_CLUSTER_SCHEMA);
		metaStore.createElementType(MetaStoreConst.NAMESPACE_IDATRIX, elementType);
		return elementType;
	}

	public static IMetaStoreElement populateElement(IMetaStore metaStore, ClusterSchema clusterMeta) throws MetaStoreException {
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_IDATRIX)) {
			throw new MetaStoreException("Namespace '" + MetaStoreConst.NAMESPACE_IDATRIX + "' doesn't exist.");
		}
		
		if (clusterMeta.getName() == null) {
			throw new MetaStoreException("Cluster name '" + MetaStoreConst.NAMESPACE_IDATRIX + "' can't be empty.");
		}

		// If the data type doesn't exist, error out...
		//
		/*IMetaStoreElementType elementType = 
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_CLUSTER_SCHEMA);
		if (elementType == null) {
			throw new MetaStoreException("Unable to find the cluster schema type");
		}*/

		IMetaStoreElementType elementType = populateElementType(metaStore);

		// generate a new cluster schema element and populate it with metadata
		//
		IMetaStoreElement element = metaStore.newElement(elementType, clusterMeta.getName(), null);
		element.setName(clusterMeta.getName());

		element.addChild(metaStore.newAttribute(MetaStoreConst.CLUSTER_ATTR_ID_NAME, clusterMeta.getName()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.CLUSTER_ATTR_ID_BASE_PORT, Const.NVL(clusterMeta.getBasePort(), "40000")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.CLUSTER_ATTR_ID_BUFFER_SIZE, Const.NVL(clusterMeta.getSocketsBufferSize(), "2000")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.CLUSTER_ATTR_ID_INTERVAL, Const.NVL(clusterMeta.getSocketsFlushInterval(), "5000")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.CLUSTER_ATTR_ID_COMPRESSED, clusterMeta.isSocketsCompressed() ? "Y" : "N"));
		element.addChild(metaStore.newAttribute(MetaStoreConst.CLUSTER_ATTR_ID_DYNAMIC, clusterMeta.isDynamic() ? "Y" : "N"));

		IMetaStoreAttribute attributesChild = metaStore.newAttribute(MetaStoreConst.CLUSTER_ATTR_ID_SLAVE_SERVERS, null);
		element.addChild(attributesChild);
		List<SlaveServer> slaves = clusterMeta.getSlaveServers();
		if (slaves != null) {
			for (SlaveServer slave : slaves) {
				//attributesChild.addChild(metaStore.newAttribute(MetaStoreConst.CLUSTER_ATTR_ID_NAME, slave.getName()));
				attributesChild.addChild(metaStore.newAttribute(slave.getName(), slave.getName()));
			}
		}

		return element;
	}

	public static ClusterSchema loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) throws KettlePluginException {
		ClusterSchema clusterMeta = new ClusterSchema();

		// Load the appropriate cluster schema details
		//
		clusterMeta.setName(getChildString(element, MetaStoreConst.CLUSTER_ATTR_ID_NAME));
		clusterMeta.setBasePort(getChildString(element, MetaStoreConst.CLUSTER_ATTR_ID_BASE_PORT));
		clusterMeta.setSocketsBufferSize(getChildString(element, MetaStoreConst.CLUSTER_ATTR_ID_BUFFER_SIZE));
		clusterMeta.setSocketsFlushInterval(getChildString(element, MetaStoreConst.CLUSTER_ATTR_ID_INTERVAL));
		clusterMeta.setSocketsCompressed("Y".equals(getChildString(element, MetaStoreConst.CLUSTER_ATTR_ID_COMPRESSED)));
		clusterMeta.setDynamic("Y".equals(getChildString(element, MetaStoreConst.CLUSTER_ATTR_ID_DYNAMIC)));

		IMetaStoreAttribute attributesChild = element.getChild(MetaStoreConst.CLUSTER_ATTR_ID_SLAVE_SERVERS);
		if (attributesChild != null) {
			// Now add a list of all the slave servers...
			//
			List<SlaveServer> slaves = clusterMeta.getSlaveServers();
			//List<SlaveServer> slaves = new ArrayList<>();
			for (IMetaStoreAttribute attr : attributesChild.getChildren()) {
				SlaveServer slave = null;
				try {
					slave = ServerMetaStoreUtil.getElement(metaStore, getAttributeString(attr));
				} catch (MetaStoreException e) {
				}
				if (slave == null) {
					slave = new SlaveServer();
					slave.setName(getAttributeString(attr));
				}
				
				if (slave != null) {
					slaves.add(slave);
				}
			}
			clusterMeta.setSlaveServers(slaves);
		}
		
		return clusterMeta;
	}
	
	public static void updateElement(IMetaStore metaStore, ClusterSchema clusterMeta) throws MetaStoreException {
		// Populate element
		IMetaStoreElement element = populateElement(metaStore, clusterMeta);

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

	public static void deleteElement(IMetaStore metaStore, ClusterSchema clusterMeta) throws MetaStoreException {
		IMetaStoreElementType elementType =
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_CLUSTER_SCHEMA);
		if (elementType == null) {
			return;
		}
		
		// Find the existing element
		IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX, elementType, clusterMeta.getName());
		if (element != null) {
			metaStore.deleteElement(MetaStoreConst.NAMESPACE_IDATRIX, elementType, element.getId());
		}
	}

}

/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.repository.xml.metastore.util;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.util.MetaStoreUtil;
import org.pentaho.pms.util.Const;

import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.HadoopClusterMeta;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.HadoopJobTrackerMeta;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.HadoopZooKeeperMeta;

/**
 * Utilities for meta store operation on hadoop cluster.
 * @author JW
 * @since 2017年6月30日
 *
 */
public class HadoopMetaStoreUtil extends MetaStoreUtil {

	public static List<HadoopClusterMeta> getElements(IMetaStore metaStore) throws MetaStoreException {
		List<HadoopClusterMeta> hadoops = new ArrayList<>();

		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_NAMED_CLUSTER);
		if (elementType == null) {
			return hadoops;
		}

		List<IMetaStoreElement> elements = metaStore.getElements(MetaStoreConst.NAMESPACE_PENTAHO, elementType);
		for (IMetaStoreElement element : elements) {
			try {
				HadoopClusterMeta cluster = loadMetaFromElement(metaStore, element);
				hadoops.add(cluster);
			} catch (Exception e) {
				throw new MetaStoreException("Unable to load hadoop cluster from element with name '"
						+ element.getName() + "' and type '" + elementType.getName() + "'", e);
			}
		}

		return hadoops;
	}

	public static HadoopClusterMeta getElement(IMetaStore metaStore, String name) throws MetaStoreException {
		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_NAMED_CLUSTER);
		if (elementType != null) {
			IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_PENTAHO, elementType, name);
			if (element != null) {
				try {
					return loadMetaFromElement(metaStore, element);
				} catch (KettlePluginException e) {
					throw new MetaStoreException("Unable to load hadoop cluster from element with name '"
							+ element.getName() + "' and type '" + elementType.getName() + "'", e);
				}
			}
		}
		return null;
	}

	public static void createElement(IMetaStore metaStore, HadoopClusterMeta hadoopMeta) throws MetaStoreException {
		// If the Pentaho namespace doesn't exist, create it!
		//
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_PENTAHO)) {
			metaStore.createNamespace(MetaStoreConst.NAMESPACE_PENTAHO);
		}

		// If the hadoop cluster element type doesn't exist, create it
		//
		populateElementType(metaStore);

		// populate an element, store it.
		//
		IMetaStoreElement hadoopElement = populateElement(metaStore, hadoopMeta);

		// Store the element physically
		//
		metaStore.createElement(MetaStoreConst.NAMESPACE_PENTAHO, hadoopElement.getElementType(), hadoopElement);
	}

	public static IMetaStoreElementType populateElementType(IMetaStore metaStore) throws MetaStoreException {
		// If the hadoop cluster element type doesn't exist, create it
		//
		IMetaStoreElementType elementType =
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_NAMED_CLUSTER);
		if (elementType != null) {
			return elementType;
		}

		// The new type will typically have an ID so all we need to do is give the type a name and a description.
		//
		elementType = metaStore.newElementType(MetaStoreConst.NAMESPACE_PENTAHO);

		// Name and description...
		//
		elementType.setName(MetaStoreConst.ELEMENT_TYPE_NAME_NAMED_CLUSTER);
		elementType.setDescription(MetaStoreConst.ELEMENT_TYPE_DESCRIPTION_NAMED_CLUSTER);
		metaStore.createElementType(MetaStoreConst.NAMESPACE_PENTAHO, elementType);
		return elementType;
	}

	public static IMetaStoreElement populateElement(IMetaStore metaStore, HadoopClusterMeta hadoopMeta) throws MetaStoreException {
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_PENTAHO)) {
			throw new MetaStoreException("Namespace '" + MetaStoreConst.NAMESPACE_PENTAHO + "' doesn't exist.");
		}

		if (hadoopMeta.getName() == null) {
			throw new MetaStoreException("Hadoop cluster name '" + MetaStoreConst.NAMESPACE_IDATRIX + "' can't be empty.");
		}

		// If the data type doesn't exist, error out...
		//
		/*IMetaStoreElementType elementType = 
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_NAMED_CLUSTER);
		if (elementType == null) {
			throw new MetaStoreException("Unable to find the hadoop cluster type");
		}*/

		IMetaStoreElementType elementType = populateElementType(metaStore);

		// generate a new hadoop cluster element and populate it with metadata
		//
		IMetaStoreElement element = metaStore.newElement(elementType, hadoopMeta.getName(), null);
		element.setName(hadoopMeta.getName());

		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_NAME, hadoopMeta.getName()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_HDFS_HOST, Const.NVL(hadoopMeta.getHostname(), "")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_HDFS_PORT, Const.NVL(hadoopMeta.getPort(), "")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_HDFS_USER_NAME, Const.NVL(hadoopMeta.getUsername(), "")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_HDFS_PASSWORD, Const.NVL(hadoopMeta.getPassword(), "")));

		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_STORAGE, hadoopMeta.getStorage().toLowerCase()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_OOZIE_URL, hadoopMeta.getUrl()));

		HadoopJobTrackerMeta jt = hadoopMeta.getJobTracker();
		if (jt != null) {
			element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_JOB_TRACKER_HOST, jt.getHostname()));
			element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_JOB_TRACKER_PORT, jt.getPort()));
		}

		HadoopZooKeeperMeta zk = hadoopMeta.getZooKeeper();
		if (zk != null) {
			element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_ZOOKEEPER_HOST, zk.getHostname()));
			element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_ZOOKEEPER_PORT, zk.getPort()));
		}

		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_LAST_MOD_DATE, ""));
		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_SHIM_IDENTIFIER, ""));

		element.addChild(metaStore.newAttribute(MetaStoreConst.HADOOP_ATTR_ID_MAPR, "maprfs".equals(hadoopMeta.getStorage()) ? "Y" : "N"));

		return element;
	}

	public static HadoopClusterMeta loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) throws KettlePluginException {
		HadoopClusterMeta hadoopMeta = new HadoopClusterMeta();

		// Load the appropriate hadoop cluster details
		//
		hadoopMeta.setName(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_NAME));
		hadoopMeta.setHostname(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_HDFS_HOST));
		hadoopMeta.setPort(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_HDFS_PORT));
		hadoopMeta.setUsername(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_HDFS_USER_NAME));
		hadoopMeta.setPassword(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_HDFS_PASSWORD));

		hadoopMeta.setStorage(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_STORAGE));
		hadoopMeta.setUrl(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_OOZIE_URL));

		HadoopJobTrackerMeta jt = new HadoopJobTrackerMeta();
		jt.setHostname(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_JOB_TRACKER_HOST));
		jt.setPort(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_JOB_TRACKER_PORT));
		hadoopMeta.setJobTracker(jt);

		HadoopZooKeeperMeta zk = new HadoopZooKeeperMeta();
		zk.setHostname(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_ZOOKEEPER_HOST));
		zk.setPort(getChildString(element, MetaStoreConst.HADOOP_ATTR_ID_ZOOKEEPER_PORT));
		hadoopMeta.setZooKeeper(zk);
		return hadoopMeta;
	}

	public static void updateElement(IMetaStore metaStore, HadoopClusterMeta hadoopMeta) throws MetaStoreException {
		// Populate element
		IMetaStoreElement element = populateElement(metaStore, hadoopMeta);

		// Find the existing element
		IMetaStoreElement de = metaStore.getElementByName(MetaStoreConst.NAMESPACE_PENTAHO, element.getElementType(), element.getName());
		if (de != null) {
			// Update the existing element
			metaStore.updateElement(MetaStoreConst.NAMESPACE_PENTAHO,
					metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_PENTAHO, element.getElementType().getName()), de.getId(), element);
		} else {
			// Create it newly
			metaStore.createElement(MetaStoreConst.NAMESPACE_PENTAHO, element.getElementType(), element);
		}
	}

	public static void deleteElement(IMetaStore metaStore, HadoopClusterMeta hadoopMeta) throws MetaStoreException {
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_NAMED_CLUSTER);
		if (elementType == null) {
			return;
		}

		// Find the existing element
		IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_PENTAHO, elementType, hadoopMeta.getName());
		if (element != null) {
			// Hot-Issue: here calling element.getElementType() will throw exception, please don't do that!
			metaStore.deleteElement(MetaStoreConst.NAMESPACE_PENTAHO, elementType, element.getName());
		}
	}

}

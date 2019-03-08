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

import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.DefaultEngineMeta;

/**
 * Utilities for meta store operation on default run configuration.
 * @author JW
 * @since 2017年6月30日
 *
 */
public class DefaultEngineMetaStoreUtil extends MetaStoreUtil {
	
	public static List<DefaultEngineMeta> getElements(IMetaStore metaStore) throws MetaStoreException {
		List<DefaultEngineMeta> configs = new ArrayList<>();

		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_DEFAULT_RUN_CONFIG);
		if (elementType == null) {
			return configs;
		}

		List<IMetaStoreElement> elements = metaStore.getElements(MetaStoreConst.NAMESPACE_PENTAHO, elementType);
		for (IMetaStoreElement element : elements) {
			try {
				DefaultEngineMeta config = loadMetaFromElement(metaStore, element);
				configs.add(config);
			} catch (Exception e) {
				throw new MetaStoreException("Unable to load run configuration from element with name '"
						+ element.getName() + "' and type '" + elementType.getName() + "'", e);
			}
		}

		return configs;
	}

	public static DefaultEngineMeta getElement(IMetaStore metaStore, String name) throws MetaStoreException {
		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_DEFAULT_RUN_CONFIG);
		if (elementType != null) {
			IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_PENTAHO, elementType, name);
			if (element != null) {
				try {
					return loadMetaFromElement(metaStore, element);
				} catch (KettlePluginException e) {
					throw new MetaStoreException("Unable to load run configuration from element with name '"
							+ element.getName() + "' and type '" + elementType.getName() + "'", e);
				}
			}
		}

		return null;
	}

	public static void createElement(IMetaStore metaStore, DefaultEngineMeta defaultMeta) throws MetaStoreException {
		// If the Pentaho namespace doesn't exist, create it!
		//
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_PENTAHO)) {
			metaStore.createNamespace(MetaStoreConst.NAMESPACE_PENTAHO);
		}

		// If the run configuration element type doesn't exist, create it
		//
		populateElementType(metaStore);
		
		// populate an element, store it.
		//
		IMetaStoreElement defaultElement = populateElement(metaStore, defaultMeta);

		// Store the element physically
		//
		metaStore.createElement(MetaStoreConst.NAMESPACE_PENTAHO, defaultElement.getElementType(), defaultElement);
	}

	public static IMetaStoreElementType populateElementType(IMetaStore metaStore) throws MetaStoreException {
		// If the run configuration element type doesn't exist, create it
		//
		IMetaStoreElementType elementType =
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_DEFAULT_RUN_CONFIG);
		if (elementType != null) {
			return elementType;
		}

		// The new type will typically have an ID so all we need to do is give the type a name and a description.
		//
		elementType = metaStore.newElementType(MetaStoreConst.NAMESPACE_PENTAHO);

		// Name and description...
		//
		elementType.setName(MetaStoreConst.ELEMENT_TYPE_NAME_DEFAULT_RUN_CONFIG);
		elementType.setDescription(MetaStoreConst.ELEMENT_TYPE_DESCRIPTION_DEFAULT_RUN_CONFIG);
		metaStore.createElementType(MetaStoreConst.NAMESPACE_PENTAHO, elementType);
		return elementType;
	}

	public static IMetaStoreElement populateElement(IMetaStore metaStore, DefaultEngineMeta defaultMeta) throws MetaStoreException {
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_PENTAHO)) {
			throw new MetaStoreException("Namespace '" + MetaStoreConst.NAMESPACE_PENTAHO + "' doesn't exist.");
		}
		
		if (defaultMeta.getName() == null) {
			throw new MetaStoreException("Default engine name '" + MetaStoreConst.NAMESPACE_PENTAHO + "' can't be empty.");
		}

		// If the data type doesn't exist, error out...
		//
		/*IMetaStoreElementType elementType = 
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_DEFAULT_RUN_CONFIG);
		if (elementType == null) {
			throw new MetaStoreException("Unable to find the run configuration type");
		}*/

		IMetaStoreElementType elementType = populateElementType(metaStore);

		// generate a new run configuration element and populate it with metadata
		//
		IMetaStoreElement element = metaStore.newElement(elementType, defaultMeta.getName(), null);
		element.setName(defaultMeta.getName());

		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_NAME, defaultMeta.getName()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_CLUSTERED, defaultMeta.isClustered() ? "Y" : "N"));
		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_SERVER, Const.NVL(defaultMeta.getServer(), "")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_DESCRIPTION, defaultMeta.getDescription()));
		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_READ_ONLY, defaultMeta.isReadOnly() ? "Y" : "N"));
		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_SEND_RESOURCES, defaultMeta.isSendResources() ? "Y" : "N"));
		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_LOG_LOCALLY, defaultMeta.isLogRemoteExecutionLocally() ? "Y" : "N"));
		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_REMOTE, defaultMeta.isRemote() ? "Y" : "N"));
		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_LOCAL, defaultMeta.isLocal() ? "Y" : "N"));
		element.addChild(metaStore.newAttribute(MetaStoreConst.DEFAULT_RC_ATTR_ID_SHOW_TRANS, defaultMeta.isShowTransformations() ? "Y" : "N"));
		
		return element;
	}

	public static DefaultEngineMeta loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) throws KettlePluginException {
		DefaultEngineMeta defaultMeta = new DefaultEngineMeta();
		
		// Load the appropriate run configuration details
		//
		defaultMeta.setName(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_NAME));
		defaultMeta.setServer(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_SERVER));
		defaultMeta.setDescription(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_DESCRIPTION));
		defaultMeta.setClustered("Y".equals(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_CLUSTERED)));
		defaultMeta.setLogRemoteExecutionLocally("Y".equals(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_LOG_LOCALLY)));
		defaultMeta.setReadOnly("Y".equals(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_READ_ONLY)));
		defaultMeta.setRemote("Y".equals(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_REMOTE)));
		defaultMeta.setLocal("Y".equals(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_LOCAL)));
		defaultMeta.setSendResources("Y".equals(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_SEND_RESOURCES)));
		defaultMeta.setShowTransformations("Y".equals(getChildString(element, MetaStoreConst.DEFAULT_RC_ATTR_ID_SHOW_TRANS)));
		
		return defaultMeta;
	}
	
	public static void updateElement(IMetaStore metaStore, DefaultEngineMeta defaultMeta) throws MetaStoreException {
		// Populate element
		IMetaStoreElement element = populateElement(metaStore, defaultMeta);

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

	public static void deleteElement(IMetaStore metaStore, DefaultEngineMeta defaultMeta) throws MetaStoreException {
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_PENTAHO, MetaStoreConst.ELEMENT_TYPE_NAME_DEFAULT_RUN_CONFIG);
		if (elementType == null) {
			return;
		}
		
		// Find the existing element
		IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_PENTAHO, elementType, defaultMeta.getName());

		if (element != null) {
			metaStore.deleteElement(MetaStoreConst.NAMESPACE_PENTAHO, elementType, element.getId());
		}
	}

}

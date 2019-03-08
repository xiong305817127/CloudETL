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

import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.TransEngineMeta;

/**
 * Utilities for meta store operation on spark transformation execution engine.
 * @author JW
 * @since 2017年7月3日
 *
 */
public class TransEngineMetaStoreUtil extends MetaStoreUtil {
	
	public static List<TransEngineMeta> getElements(IMetaStore metaStore) throws MetaStoreException {
		List<TransEngineMeta> configs = new ArrayList<>();

		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SPARK_ENGINE);
		if (elementType == null) {
			return configs;
		}

		List<IMetaStoreElement> elements = metaStore.getElements(MetaStoreConst.NAMESPACE_IDATRIX, elementType);
		for (IMetaStoreElement element : elements) {
			try {
				TransEngineMeta config = loadMetaFromElement(metaStore, element);
				configs.add(config);
			} catch (Exception e) {
				throw new MetaStoreException("Unable to load run configuration from element with name '"
						+ element.getName() + "' and type '" + elementType.getName() + "'", e);
			}
		}

		return configs;
	}

	public static TransEngineMeta getElement(IMetaStore metaStore, String name) throws MetaStoreException {
		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SPARK_ENGINE);
		if (elementType != null) {
			IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX, elementType, name);
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

	public static void createElement(IMetaStore metaStore, TransEngineMeta sparkMeta) throws MetaStoreException {
		// If the Pentaho namespace doesn't exist, create it!
		//
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_IDATRIX)) {
			metaStore.createNamespace(MetaStoreConst.NAMESPACE_IDATRIX);
		}

		// If the run configuration element type doesn't exist, create it
		//
		populateElementType(metaStore);
		
		// populate an element, store it.
		//
		IMetaStoreElement sparkElement = populateElement(metaStore, sparkMeta);

		// Store the element physically
		//
		metaStore.createElement(MetaStoreConst.NAMESPACE_IDATRIX, sparkElement.getElementType(), sparkElement);
	}

	public static IMetaStoreElementType populateElementType(IMetaStore metaStore) throws MetaStoreException {
		// If the run configuration element type doesn't exist, create it
		//
		IMetaStoreElementType elementType =
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SPARK_ENGINE);
		if (elementType != null) {
			return elementType;
		}

		// The new type will typically have an ID so all we need to do is give the type a name and a description.
		//
		elementType = metaStore.newElementType(MetaStoreConst.NAMESPACE_IDATRIX);

		// Name and description...
		//
		elementType.setName(MetaStoreConst.ELEMENT_TYPE_NAME_SPARK_ENGINE);
		elementType.setDescription(MetaStoreConst.ELEMENT_TYPE_DESCRIPTION_SPARK_ENGINE);
		metaStore.createElementType(MetaStoreConst.NAMESPACE_IDATRIX, elementType);
		return elementType;
	}

	public static IMetaStoreElement populateElement(IMetaStore metaStore, TransEngineMeta sparkMeta) throws MetaStoreException {
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_IDATRIX)) {
			throw new MetaStoreException("Namespace '" + MetaStoreConst.NAMESPACE_IDATRIX + "' doesn't exist.");
		}
		
		if (sparkMeta.getName() == null) {
			throw new MetaStoreException("Trans engine name '" + MetaStoreConst.NAMESPACE_IDATRIX + "' can't be empty.");
		}

		// If the data type doesn't exist, error out...
		//
		/*IMetaStoreElementType elementType = 
				metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SPARK_ENGINE);
		if (elementType == null) {
			throw new MetaStoreException("Unable to find the run configuration type");
		}*/

		IMetaStoreElementType elementType = populateElementType(metaStore);

		// generate a new run configuration element and populate it with metadata
		//
		IMetaStoreElement element = metaStore.newElement(elementType, sparkMeta.getName(), null);
		element.setName(sparkMeta.getName());

		element.addChild(metaStore.newAttribute(MetaStoreConst.SPARK_RC_ATTR_ID_NAME, Const.NVL(sparkMeta.getName(), "")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SPARK_RC_ATTR_ID_URL, Const.NVL(sparkMeta.getUrl(), "")));
		element.addChild(metaStore.newAttribute(MetaStoreConst.SPARK_RC_ATTR_ID_DESCRIPTION, Const.NVL(sparkMeta.getDescription(), "")));
		
		return element;
	}

	public static TransEngineMeta loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) throws KettlePluginException {
		TransEngineMeta sparkMeta = new TransEngineMeta();
		
		// Load the appropriate run configuration details
		//
		sparkMeta.setName(getChildString(element, MetaStoreConst.SPARK_RC_ATTR_ID_NAME));
		sparkMeta.setUrl(getChildString(element, MetaStoreConst.SPARK_RC_ATTR_ID_URL));
		sparkMeta.setDescription(getChildString(element, MetaStoreConst.SPARK_RC_ATTR_ID_DESCRIPTION));
		
		return sparkMeta;
	}
	
	public static void updateElement(IMetaStore metaStore, TransEngineMeta sparkMeta) throws MetaStoreException {
		// Populate element
		IMetaStoreElement element = populateElement(metaStore, sparkMeta);

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

	public static void deleteElement(IMetaStore metaStore, TransEngineMeta sparkMeta) throws MetaStoreException {
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_SPARK_ENGINE);
		if (elementType == null) {
			return;
		}
		
		// Find the existing element
		IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX, elementType, sparkMeta.getName());

		if (element != null) {
			metaStore.deleteElement(MetaStoreConst.NAMESPACE_IDATRIX, elementType, element.getId());
		}
	}

}

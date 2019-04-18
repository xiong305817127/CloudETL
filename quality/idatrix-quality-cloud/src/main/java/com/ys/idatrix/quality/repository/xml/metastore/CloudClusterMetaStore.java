/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.repository.xml.metastore;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.springframework.stereotype.Service;

import com.ys.idatrix.quality.repository.xml.metastore.util.ClusterMetaStoreUtil;

/**
 * Cloud cluster schema meta store operation implementations.
 * @author JW
 * @since 2017年6月26日
 *
 */
@Service
public class CloudClusterMetaStore {
	
	public static final Log  logger = LogFactory.getLog("CloudClusterMetaStore");
	
	/**
	 * Populate cluster schema element type in given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public IMetaStoreElementType populateElementType(IMetaStore metaStore) {
		try {
			return ClusterMetaStoreUtil.populateElementType(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Populate cluster schema elements including sub-elements that can be stored in given meta store.
	 * 
	 * @param metaStore
	 * @param clusterMeta
	 * @return
	 */
	public IMetaStoreElement populateElement(IMetaStore metaStore, ClusterSchema clusterMeta) {
		try {
			return ClusterMetaStoreUtil.populateElement(metaStore, clusterMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Get all cluster schema elements from given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public List<ClusterSchema> getElements(IMetaStore metaStore) {
		try {
			return ClusterMetaStoreUtil.getElements(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return new ArrayList<>();
	}
	
	/**
	 * Get cluster schema element from given meta store.
	 * @param metaStore
	 * @param name
	 * @return
	 */
	public ClusterSchema getElement(IMetaStore metaStore, String name) {
		try {
			return ClusterMetaStoreUtil.getElement(metaStore, name);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Create a new cluster schema element in given meta store.
	 * 
	 * @param metaStore
	 * @param clusterMeta
	 * @throws MetaStoreException 
	 */
	public void createElement(IMetaStore metaStore, ClusterSchema clusterMeta) throws MetaStoreException {
		try {
			ClusterMetaStoreUtil.createElement(metaStore, clusterMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
			throw e;
		}
	}

	/**
	 * Load cluster schema meta from given element and meta store.
	 * 
	 * @param metaStore
	 * @param element
	 * @return
	 */
	public ClusterSchema loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) {
		try {
			return ClusterMetaStoreUtil.loadMetaFromElement(metaStore, element);
		} catch (KettlePluginException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Update cluster schema meta in meta store,
	 * if not existing then create it in the meta store.
	 * 
	 * @param metaStore
	 * @param clusterMeta
	 */
	public void updateElement(IMetaStore metaStore, ClusterSchema clusterMeta) {
		try {
			ClusterMetaStoreUtil.updateElement(metaStore, clusterMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
	}

	/**
	 * Delete cluster schema meta from given meta store.
	 * 
	 * @param metaStore
	 * @param clusterMeta
	 */
	public void deleteElement(IMetaStore metaStore, ClusterSchema clusterMeta) {
		try {
			ClusterMetaStoreUtil.deleteElement(metaStore, clusterMeta);
		} catch (MetaStoreException e1) {
			logger.error("",e1);
		}
	}

}

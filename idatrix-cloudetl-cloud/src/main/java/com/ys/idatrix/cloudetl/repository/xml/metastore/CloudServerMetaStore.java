/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.repository.xml.metastore;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.springframework.stereotype.Service;

import com.ys.idatrix.cloudetl.repository.xml.metastore.util.ServerMetaStoreUtil;

/**
 * Cloud slave server meta store operation implementations.
 * @author JW
 * @since 2017年6月26日
 *
 */
@Service
public class CloudServerMetaStore {
	
	public static final Log  logger = LogFactory.getLog("CloudServerMetaStore");
	
	/**
	 * Populate slave server element type in given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public IMetaStoreElementType populateElementType(IMetaStore metaStore) {
		try {
			return ServerMetaStoreUtil.populateElementType(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Populate slave server elements including sub-elements that can be stored in given meta store.
	 * 
	 * @param metaStore
	 * @param serverMeta
	 * @return
	 */
	public IMetaStoreElement populateElement(IMetaStore metaStore, SlaveServer serverMeta) {
		try {
			return ServerMetaStoreUtil.populateElement(metaStore, serverMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Get all slave server elements from given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public List<SlaveServer> getElements(IMetaStore metaStore) {
		try {
			return ServerMetaStoreUtil.getElements(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return new ArrayList<>();
	}
	
	/**
	 * Get slave server element from given meta store.
	 * @param metaStore
	 * @param name
	 * @return
	 */
	public SlaveServer getElement(IMetaStore metaStore, String name) {
		try {
			return ServerMetaStoreUtil.getElement(metaStore, name);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Create a new slave server element in given meta store.
	 * 
	 * @param metaStore
	 * @param serverMeta
	 * @throws MetaStoreException 
	 */
	public void createElement(IMetaStore metaStore, SlaveServer serverMeta) throws MetaStoreException {
		try {
			ServerMetaStoreUtil.createElement(metaStore, serverMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
			throw e;
		}
	}

	/**
	 * Load slave server meta from given element and meta store.
	 * 
	 * @param metaStore
	 * @param element
	 * @return
	 */
	public SlaveServer loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) {
		try {
			return ServerMetaStoreUtil.loadMetaFromElement(metaStore, element);
		} catch (KettlePluginException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Update slave server meta in meta store,
	 * if not existing then create it in the meta store.
	 * 
	 * @param metaStore
	 * @param serverMeta
	 */
	public void updateElement(IMetaStore metaStore, SlaveServer serverMeta) {
		try {
			ServerMetaStoreUtil.updateElement(metaStore, serverMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
	}

	/**
	 * Delete slave server meta from given meta store.
	 * 
	 * @param metaStore
	 * @param serverMeta
	 */
	public void deleteElement(IMetaStore metaStore, SlaveServer serverMeta) {
		try {
			ServerMetaStoreUtil.deleteElement(metaStore, serverMeta);
		} catch (MetaStoreException e1) {
			logger.error("",e1);
		}
	}

}

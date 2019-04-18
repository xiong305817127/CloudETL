/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.repository.xml.metastore;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.springframework.stereotype.Service;

import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.DefaultEngineMeta;
import com.ys.idatrix.cloudetl.repository.xml.metastore.util.DefaultEngineMetaStoreUtil;

/**
 * Cloud default run configuration meta store operation implementations.
 * @author JW
 * @since 2017年7月3日
 *
 */
@Service
public class CloudDefaultEngineMetaStore {
	
	public static final Log  logger = LogFactory.getLog("CloudDefaultEngineMetaStore");
	/**
	 * Populate default run configuration element type in given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public IMetaStoreElementType populateElementType(IMetaStore metaStore) {
		try {
			return DefaultEngineMetaStoreUtil.populateElementType(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Populate default run configuration elements including sub-elements that can be stored in given meta store.
	 * 
	 * @param metaStore
	 * @param defaultMeta
	 * @return
	 */
	public IMetaStoreElement populateElement(IMetaStore metaStore, DefaultEngineMeta defaultMeta) {
		try {
			return DefaultEngineMetaStoreUtil.populateElement(metaStore, defaultMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Get all default run configuration elements from given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public List<DefaultEngineMeta> getElements(IMetaStore metaStore) {
		try {
			return DefaultEngineMetaStoreUtil.getElements(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return new ArrayList<>();
	}
	
	/**
	 * Get default run configuration element from given meta store.
	 * @param metaStore
	 * @param name
	 * @return
	 */
	public DefaultEngineMeta getElement(IMetaStore metaStore, String name) {
		try {
			return DefaultEngineMetaStoreUtil.getElement(metaStore, name);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Create a new default run configuration element in given meta store.
	 * 
	 * @param metaStore
	 * @param defaultMeta
	 * @throws MetaStoreException 
	 */
	public void createElement(IMetaStore metaStore, DefaultEngineMeta defaultMeta) throws MetaStoreException {
		try {
			DefaultEngineMetaStoreUtil.createElement(metaStore, defaultMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
			throw e;
		}
	}

	/**
	 * Load default run configuration meta from given element and meta store.
	 * 
	 * @param metaStore
	 * @param element
	 * @return
	 */
	public DefaultEngineMeta loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) {
		try {
			return DefaultEngineMetaStoreUtil.loadMetaFromElement(metaStore, element);
		} catch (KettlePluginException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Update default run configuration meta in meta store,
	 * if not existing then create it in the meta store.
	 * 
	 * @param metaStore
	 * @param defaultMeta
	 */
	public void updateElement(IMetaStore metaStore, DefaultEngineMeta defaultMeta) {
		try {
			DefaultEngineMetaStoreUtil.updateElement(metaStore, defaultMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
	}

	/**
	 * Delete default run configuration meta from given meta store.
	 * 
	 * @param metaStore
	 * @param defaultMeta
	 */
	public void deleteElement(IMetaStore metaStore, DefaultEngineMeta defaultMeta) {
		try {
			DefaultEngineMetaStoreUtil.deleteElement(metaStore, defaultMeta);
		} catch (MetaStoreException e1) {
			logger.error("",e1);
		}
	}

}

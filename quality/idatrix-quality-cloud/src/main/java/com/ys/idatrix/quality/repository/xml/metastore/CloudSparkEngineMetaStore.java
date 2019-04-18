/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.repository.xml.metastore;

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

import com.ys.idatrix.quality.repository.xml.metastore.meta.SparkEngineMeta;
import com.ys.idatrix.quality.repository.xml.metastore.util.SparkEngineMetaStoreUtil;

/**
 * Cloud spark run configuration meta store operation implementations.
 * @author JW
 * @since 2017年7月3日
 *
 */
@Service
public class CloudSparkEngineMetaStore {
	
	public static final Log  logger = LogFactory.getLog("CloudSparkEngineMetaStore");

	
	/**
	 * Populate spark run configuration element type in given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public IMetaStoreElementType populateElementType(IMetaStore metaStore) {
		try {
			return SparkEngineMetaStoreUtil.populateElementType(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Populate spark run configuration elements including sub-elements that can be stored in given meta store.
	 * 
	 * @param metaStore
	 * @param sparkMeta
	 * @return
	 */
	public IMetaStoreElement populateElement(IMetaStore metaStore, SparkEngineMeta sparkMeta) {
		try {
			return SparkEngineMetaStoreUtil.populateElement(metaStore, sparkMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Get all spark run configuration elements from given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public List<SparkEngineMeta> getElements(IMetaStore metaStore) {
		try {
			return SparkEngineMetaStoreUtil.getElements(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return new ArrayList<>();
	}
	
	/**
	 * Get spark run configuration element from given meta store.
	 * @param metaStore
	 * @param name
	 * @return
	 */
	public SparkEngineMeta getElement(IMetaStore metaStore, String name) {
		try {
			return SparkEngineMetaStoreUtil.getElement(metaStore, name);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Create a new spark run configuration element in given meta store.
	 * 
	 * @param metaStore
	 * @param sparkMeta
	 * @throws MetaStoreException 
	 */
	public void createElement(IMetaStore metaStore, SparkEngineMeta sparkMeta) throws MetaStoreException {
		try {
			SparkEngineMetaStoreUtil.createElement(metaStore, sparkMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
			throw e;
		}
	}

	/**
	 * Load spark run configuration meta from given element and meta store.
	 * 
	 * @param metaStore
	 * @param element
	 * @return
	 */
	public SparkEngineMeta loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) {
		try {
			return SparkEngineMetaStoreUtil.loadMetaFromElement(metaStore, element);
		} catch (KettlePluginException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Update spark run configuration meta in meta store,
	 * if not existing then create it in the meta store.
	 * 
	 * @param metaStore
	 * @param sparkMeta
	 */
	public void updateElement(IMetaStore metaStore, SparkEngineMeta sparkMeta) {
		try {
			SparkEngineMetaStoreUtil.updateElement(metaStore, sparkMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
	}

	/**
	 * Delete spark run configuration meta from given meta store.
	 * 
	 * @param metaStore
	 * @param sparkMeta
	 */
	public void deleteElement(IMetaStore metaStore, SparkEngineMeta sparkMeta) {
		try {
			SparkEngineMetaStoreUtil.deleteElement(metaStore, sparkMeta);
		} catch (MetaStoreException e1) {
			logger.error("",e1);
		}
	}

}

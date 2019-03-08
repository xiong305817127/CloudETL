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

import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.HadoopClusterMeta;
import com.ys.idatrix.cloudetl.repository.xml.metastore.util.HadoopMetaStoreUtil;

/**
 * Cloud hadoop cluster meta store operation implementations.
 * @author JW
 * @since 2017年6月26日
 *
 */
@Service
public class CloudHadoopMetaStore {
	
	public static final Log  logger = LogFactory.getLog("CloudHadoopMetaStore");
	
	/**
	 * Populate hadoop cluster element type in given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public IMetaStoreElementType populateElementType(IMetaStore metaStore) {
		try {
			return HadoopMetaStoreUtil.populateElementType(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Populate hadoop cluster elements including sub-elements that can be stored in given meta store.
	 * 
	 * @param metaStore
	 * @param hadoopMeta
	 * @return
	 */
	public IMetaStoreElement populateElement(IMetaStore metaStore, HadoopClusterMeta hadoopMeta) {
		try {
			return HadoopMetaStoreUtil.populateElement(metaStore, hadoopMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Get all hadoop cluster elements from given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public List<HadoopClusterMeta> getElements(IMetaStore metaStore) {
		try {
			return HadoopMetaStoreUtil.getElements(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return new ArrayList<>();
	}
	
	/**
	 * Get hadoop cluster element from given meta store.
	 * @param metaStore
	 * @param name
	 * @return
	 */
	public HadoopClusterMeta getElement(IMetaStore metaStore, String name) {
		try {
			return HadoopMetaStoreUtil.getElement(metaStore, name);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Create a new hadoop cluster element in given meta store.
	 * 
	 * @param metaStore
	 * @param hadoopMeta
	 * @throws MetaStoreException 
	 */
	public void createElement(IMetaStore metaStore, HadoopClusterMeta hadoopMeta) throws MetaStoreException {
		try {
			HadoopMetaStoreUtil.createElement(metaStore, hadoopMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
			throw e;
		}
	}

	/**
	 * Load hadoop cluster meta from given element and meta store.
	 * 
	 * @param metaStore
	 * @param element
	 * @return
	 */
	public HadoopClusterMeta loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) {
		try {
			return HadoopMetaStoreUtil.loadMetaFromElement(metaStore, element);
		} catch (KettlePluginException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Update hadoop cluster meta in meta store,
	 * if not existing then create it in the meta store.
	 * 
	 * @param metaStore
	 * @param hadoopMeta
	 */
	public void updateElement(IMetaStore metaStore, HadoopClusterMeta hadoopMeta) {
		try {
			HadoopMetaStoreUtil.updateElement(metaStore, hadoopMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
	}

	/**
	 * Delete hadoop cluster meta from given meta store.
	 * 
	 * @param metaStore
	 * @param hadoopMeta
	 */
	public void deleteElement(IMetaStore metaStore, HadoopClusterMeta hadoopMeta) {
		try {
			HadoopMetaStoreUtil.deleteElement(metaStore, hadoopMeta);
		} catch (MetaStoreException e1) {
			logger.error("",e1);
		}
	}

}

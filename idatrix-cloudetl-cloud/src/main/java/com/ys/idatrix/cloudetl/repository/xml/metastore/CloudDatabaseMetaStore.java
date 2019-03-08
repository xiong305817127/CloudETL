/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.repository.xml.metastore;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.metastore.DatabaseMetaStoreUtil;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.springframework.stereotype.Service;

/**
 * Cloud database connection meta store operation implementations.
 * @author JW
 * @since 2017年6月22日
 *
 */
@Service
public class CloudDatabaseMetaStore {

	public static final Log  logger = LogFactory.getLog("CloudDatabaseMetaStore");
	
	/**
	 * Populate database connection element type in given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public IMetaStoreElementType populateElementType(IMetaStore metaStore) {
		try {
			return DatabaseMetaStoreUtil.populateDatabaseElementType(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Populate database connection elements including sub-elements that can be stored in given meta store.
	 * 
	 * @param metaStore
	 * @param databaseMeta
	 * @return
	 */
	public IMetaStoreElement populateElement(IMetaStore metaStore, DatabaseMeta databaseMeta) {
		try {
			return DatabaseMetaStoreUtil.populateDatabaseElement(metaStore, databaseMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Get all database connection elements from given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public List<DatabaseMeta> getElements(IMetaStore metaStore) {
		try {
			return DatabaseMetaStoreUtil.getDatabaseElements(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return new ArrayList<>();
	}
	
	/**
	 * Get database connection element from given meta store.
	 * @param metaStore
	 * @param name
	 * @return
	 */
	public DatabaseMeta getElement(IMetaStore metaStore, String name) {
		try {
			return DatabaseMetaStoreUtil.getDatabaseElement(metaStore, name);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Create a new database connection element in given meta store.
	 * 
	 * @param metaStore
	 * @param databaseMeta
	 * @throws MetaStoreException 
	 */
	public void createElement(IMetaStore metaStore, DatabaseMeta databaseMeta) throws MetaStoreException {
		try {
			DatabaseMetaStoreUtil.createDatabaseElement(metaStore, databaseMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
			throw e;
		}
	}

	/**
	 * Load database connection meta from given element and meta store.
	 * 
	 * @param metaStore
	 * @param element
	 * @return
	 */
	public DatabaseMeta loadMetaFromElement(IMetaStore metaStore, IMetaStoreElement element) {
		try {
			return DatabaseMetaStoreUtil.loadDatabaseMetaFromDatabaseElement(metaStore, element);
		} catch (KettlePluginException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Update database connection meta in meta store,
	 * if not existing then create it in the meta store.
	 * 
	 * @param metaStore
	 * @param databaseMeta
	 */
	public void updateElement(IMetaStore metaStore, DatabaseMeta databaseMeta) {
		try {
			DatabaseMetaStoreUtil.updateElement(metaStore, databaseMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
	}

	/**
	 * Delete database connection meta from given meta store.
	 * 
	 * @param metaStore
	 * @param databaseMeta
	 */
	public void deleteElement(IMetaStore metaStore, DatabaseMeta databaseMeta) {
		try {
			DatabaseMetaStoreUtil.deleteElement(metaStore, databaseMeta);
		} catch (MetaStoreException e1) {
			logger.error("",e1);
		}
	}

}

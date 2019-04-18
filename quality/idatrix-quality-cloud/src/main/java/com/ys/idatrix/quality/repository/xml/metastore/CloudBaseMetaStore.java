/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.repository.xml.metastore;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.functions.T;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.metastore.DatabaseMetaStoreUtil;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreException;

import com.ys.idatrix.quality.repository.xml.metastore.meta.DefaultEngineMeta;
import com.ys.idatrix.quality.repository.xml.metastore.meta.HadoopClusterMeta;
import com.ys.idatrix.quality.repository.xml.metastore.meta.SparkEngineMeta;
import com.ys.idatrix.quality.repository.xml.metastore.util.ClusterMetaStoreUtil;
import com.ys.idatrix.quality.repository.xml.metastore.util.DefaultEngineMetaStoreUtil;
import com.ys.idatrix.quality.repository.xml.metastore.util.HadoopMetaStoreUtil;
import com.ys.idatrix.quality.repository.xml.metastore.util.ServerMetaStoreUtil;
import com.ys.idatrix.quality.repository.xml.metastore.util.SparkEngineMetaStoreUtil;

/**
 * Base procedure for meta store.
 * 
 * @author JW
 * @since 2017年6月22日
 *
 */
public class CloudBaseMetaStore {
	
	public static final Log  logger = LogFactory.getLog("CloudBaseMetaStore");

	private static CloudMetaType CLOUD_META_TYPE = CloudMetaType.DB;

	public static void syncMetaStore() {

	}

	/**
	 * Populate <CloudMetaType> element type in given meta store.
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
	 * Populate <CloudMetaType> elements including sub-elements that can be stored in given meta store.
	 * 
	 * @param metaStore
	 * @param databaseMeta
	 * @return
	 */
	@SuppressWarnings("hiding")
	public <T> IMetaStoreElement populateElement(IMetaStore metaStore, T meta) {
		try {
			switch (CLOUD_META_TYPE) {
				case DB : 
					return DatabaseMetaStoreUtil.populateDatabaseElement(metaStore, (DatabaseMeta)meta);
				case CLUSTER : 
					return ClusterMetaStoreUtil.populateElement(metaStore, (ClusterSchema)meta);
				case SERVER : 
					return ServerMetaStoreUtil.populateElement(metaStore, (SlaveServer)meta);
				case HADOOP : 
					return HadoopMetaStoreUtil.populateElement(metaStore, (HadoopClusterMeta)meta);
				case SPARK_ENGINE : 
					// TODO.
				case DEFAULT_RUN_CONFIG : 
					return DefaultEngineMetaStoreUtil.populateElement(metaStore, (DefaultEngineMeta)meta);
				case SPARK_RUN_CONFIG : 
					return SparkEngineMetaStoreUtil.populateElement(metaStore, (SparkEngineMeta)meta);
			}
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * Get all <CloudMetaType> elements from given meta store.
	 * 
	 * @param metaStore
	 * @return
	 */
	public List<DatabaseMeta> getElements(IMetaStore metaStore) {
		try {
			switch (CLOUD_META_TYPE) {
			case DB : 
				return DatabaseMetaStoreUtil.getDatabaseElements(metaStore);
			case CLUSTER : 
				
			case SERVER : 
				
			case HADOOP : 
				
			case SPARK_ENGINE : 
				// TODO.
			case DEFAULT_RUN_CONFIG : 
				
			case SPARK_RUN_CONFIG : 
				
		}
			
			return DatabaseMetaStoreUtil.getDatabaseElements(metaStore);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
		return new ArrayList<>();
	}

	/**
	 * Get <CloudMetaType> element from given meta store.
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
	 * Create a new <CloudMetaType> element in given meta store.
	 * 
	 * @param metaStore
	 * @param databaseMeta
	 */
	public void createElement(IMetaStore metaStore, DatabaseMeta databaseMeta) {
		try {
			DatabaseMetaStoreUtil.createDatabaseElement(metaStore, databaseMeta);
		} catch (MetaStoreException e) {
			logger.error("",e);
		}
	}

	/**
	 * Load <CloudMetaType> meta from given element and meta store.
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
	 * Update <CloudMetaType> meta in meta store,
	 * if not existing then create it in the meta store.
	 * 
	 * @param metaStore
	 * @param databaseMeta
	 */
	public void updateElement(IMetaStore metaStore, DatabaseMeta databaseMeta) {
		// Populate element
		IMetaStoreElement databaseElement = populateElement(metaStore, databaseMeta);

		try {
			// Find the existing element
			IMetaStoreElement de = metaStore.getElementByName(MetaStoreConst.NAMESPACE_PENTAHO,
					databaseElement.getElementType(), databaseElement.getName());
			if (de != null) {
				// Update the existing element
				metaStore.updateElement(MetaStoreConst.NAMESPACE_PENTAHO,
						metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_PENTAHO, databaseElement.getElementType().getName()),
						databaseElement.getId(), databaseElement);
			} else {
				// Create it newly
				metaStore.createElement(MetaStoreConst.NAMESPACE_PENTAHO, databaseElement.getElementType(), databaseElement);
			}
		} catch (MetaStoreException e1) {
			logger.error("",e1);
		}

		// Populate an element, store it.
		/*IMetaStoreElement databaseElement2 = populateElement(metaStore, databaseMeta);
		try {
			metaStore.updateElement(MetaStoreConst.NAMESPACE_PENTAHO,
					databaseElement1.getElementType(), databaseElement1.getId(), databaseElement2);
		} catch (MetaStoreException e) {
			logger.error(e);
		}*/
	}

	/**
	 * Delete <CloudMetaType> meta from given meta store.
	 * 
	 * @param metaStore
	 * @param databaseMeta
	 */
	public void deleteElement(IMetaStore metaStore, DatabaseMeta databaseMeta) {
		try {
			// Find the existing element
			IMetaStoreElement databaseElement = metaStore.getElementByName(MetaStoreConst.NAMESPACE_PENTAHO,
					populateElementType(metaStore), databaseMeta.getName());

			if (databaseElement != null) {
				metaStore.deleteElement(MetaStoreConst.NAMESPACE_PENTAHO, databaseElement.getElementType(), databaseElement.getId());
			}
		} catch (MetaStoreException e1) {
			logger.error("",e1);
		}
	}

}

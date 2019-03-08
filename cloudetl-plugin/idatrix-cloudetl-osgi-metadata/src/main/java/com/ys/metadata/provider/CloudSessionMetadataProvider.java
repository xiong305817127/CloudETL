package com.ys.metadata.provider;

import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.osgi.metastore.locator.api.MetastoreProvider;

/**
 * org.pentaho.osgi.metastore.locator.impl.repository.RepositoryMetastoreProvider
 *
 * @author XH
 * @since 2017年8月2日
 *
 */
public class CloudSessionMetadataProvider implements MetastoreProvider {

	@Override
	public IMetaStore getMetastore() {
		
		try {
			return (IMetaStore)OsgiBundleUtils.invokeOsgiMethod(Utils.getCloudSession(), "getMetaStore");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
//		return CloudSession.getMetaStore();
	}

	
	 
}

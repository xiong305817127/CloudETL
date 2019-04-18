/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.repository.xml;

import java.util.Date;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;

import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;

/**
 * We use a common transformation to store the general ETL resources,
 * such as database connection, slave servers, clusters, and spark engines.
 * TODO. should be deprecated if cloud store implemented !
 * 
 * @author JW
 * @since 05-12-2017
 * 
 */
public class CloudTransformation {
	
	//private static final String CLOUD_REPOSITORY_PATH = "/cloud/";
	private static final String CLOUD_TRANSFORMATION_NAME = "cloud_transformation";
	
	private static CloudTransformation cloudTransformation;
	private static TransMeta transformation;
	//private static ThreadLocal<TransMeta> tl = new ThreadLocal<>();
	
	/**
	 * 
	 * @throws KettleException
	 */
	private CloudTransformation() throws Exception {
		//tl.set(initCloudTransformation());
		transformation = initCloudTransformation();
	}
	
	/**
	 * 
	 * @return
	 * @throws KettleException
	 */
	private TransMeta initCloudTransformation() throws Exception {
		Repository repository = CloudApp.getInstance().getRepository();
		RepositoryDirectoryInterface path = repository.findDirectory(new StringObjectId(CloudSession.getCloudRepositoryPath()));
		
		if(repository.exists(CLOUD_TRANSFORMATION_NAME, path, RepositoryObjectType.TRANSFORMATION)) {
			return loadCloudTransformation();
		}
		
		TransMeta transMeta = new TransMeta();
		transMeta.setRepository(CloudApp.getInstance().getRepository());
		transMeta.setMetaStore(CloudSession.getMetaStore());
		transMeta.setName(CLOUD_TRANSFORMATION_NAME);
		transMeta.setRepositoryDirectory(path);
		
		repository.save(transMeta, "add: " + new Date(), null);
		return transMeta;
	}
	
	/**
	 * 
	 * @return
	 */
	public TransMeta getTransformation() {
		//return tl.get();
		return transformation;
	}
	
	/**
	 * 
	 * @return
	 * @throws KettleException
	 */
	private TransMeta loadCloudTransformation() throws KettleException {
		Repository repository = CloudApp.getInstance().getRepository();
		RepositoryDirectoryInterface path = repository.findDirectory(new StringObjectId(CloudSession.getCloudRepositoryPath()));
		ObjectId id = repository.getTransformationID(CLOUD_TRANSFORMATION_NAME, path);
		
		TransMeta transMeta = CloudApp.getInstance().getRepository().loadTransformation(id, null);
		RepositoryObject repositoryObject = CloudApp.getInstance().getRepository().getObjectInformation(id, RepositoryObjectType.TRANSFORMATION);
		transMeta.setRepositoryDirectory(repositoryObject.getRepositoryDirectory());
		
		return transMeta;
	}
	
	/**
	 * 
	 * @return
	 * @throws KettleException
	 */
	public static CloudTransformation getInstance() throws  Exception {
		if (cloudTransformation == null) {
			cloudTransformation = new CloudTransformation();
		}
		return cloudTransformation;
	}
	
}

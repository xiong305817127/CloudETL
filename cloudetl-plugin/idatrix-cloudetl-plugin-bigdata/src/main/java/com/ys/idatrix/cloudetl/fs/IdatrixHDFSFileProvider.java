/*******************************************************************************
 * Pentaho Big Data
 * <p>
 * Copyright (C) 2002-2017 by Pentaho : http://www.pentaho.com
 * <p>
 * ******************************************************************************
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package com.ys.idatrix.cloudetl.fs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.commons.vfs2.provider.VfsComponentContext;
import org.pentaho.di.core.annotations.KettleLifecyclePlugin;
import org.pentaho.di.core.lifecycle.KettleLifecycleListener;
import org.pentaho.di.core.lifecycle.LifecycleException;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.UUIDUtil;
import org.pentaho.di.core.vfs.KettleVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ys.idatrix.cloudetl.fs.hadoop.HDFSFileSystem;
import com.ys.idatrix.cloudetl.fs.hadoop.HadoopFileSystemImpl;
import com.ys.idatrix.cloudetl.fs.hadoop.interfaces.HadoopFileSystemCallable;
import com.ys.idatrix.cloudetl.security.HadoopSecurityManagerException;
import com.ys.idatrix.cloudetl.security.IdatrixSecurityManager;
import com.ys.idatrix.cloudetl.util.EnvUtils;

/**
 * 基于Hadoop数据安全实现的HDFS文件系统提供者 <br/>
 * IdatrixHDFSFileProvider <br/>
 * @author XH
 * @since 2017年10月25日
 *
 */
@KettleLifecyclePlugin(id = "idatrixhdfsprovider")
public class IdatrixHDFSFileProvider extends AbstractOriginatingFileProvider implements KettleLifecycleListener {
	
	private Logger logger = LoggerFactory.getLogger(IdatrixHDFSFileProvider.class);

	public static final String PLUGIN_ID = "idatrixhdfsprovider";
	/**
	 * The scheme this provider was designed to support
	 */
	public static final String SCHEME = "hdfs";

	private AbstractOriginatingFileProvider bigdataHdfsProvider ;

	/**
	 * The provider's capabilities.
	 */
	public static final Collection<Capability> capabilities = Collections.unmodifiableCollection(Arrays
			.asList(new Capability[] { Capability.CREATE, Capability.DELETE, Capability.RENAME, Capability.GET_TYPE,
					Capability.LIST_CHILDREN, Capability.READ_CONTENT, Capability.URI, Capability.WRITE_CONTENT,
					Capability.GET_LAST_MODIFIED, Capability.SET_LAST_MODIFIED_FILE, Capability.RANDOM_ACCESS_READ }));

	@Override
	public Collection<Capability> getCapabilities() {
		return capabilities;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider#findFile(org.apache.commons.vfs2.FileObject, java.lang.String, org.apache.commons.vfs2.FileSystemOptions)
	 */
	@Override
	public FileObject findFile(FileObject baseFile, String uri, FileSystemOptions fileSystemOptions)
			throws FileSystemException {
		uri = uri.replace(IdatrixHDFSFileProvider.SCHEME, "hdfs");
		return super.findFile(baseFile, uri, fileSystemOptions);
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.vfs2.provider.AbstractFileProvider#parseUri(org.apache.commons.vfs2.FileName, java.lang.String)
	 */
	@Override
	public FileName parseUri(FileName base, String uri) throws FileSystemException {
		uri = uri.replace(IdatrixHDFSFileProvider.SCHEME, "hdfs");
		return super.parseUri(base, uri);
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.vfs2.provider.AbstractFileProvider#addFileSystem(java.lang.Comparable, org.apache.commons.vfs2.FileSystem)
	 */
	@Override
	protected void addFileSystem(Comparable<?> key, FileSystem fs) throws FileSystemException {
		/*String userId = EnvUtils.getUserId();
		if(userId!= null && userId.length() >0 && key instanceof AbstractFileName ){
			AbstractFileName oldKey = (AbstractFileName)key;
			String keyStr=oldKey.getURI()+userId;
			OsgiBundleUtils.setOsgiField(oldKey, "key", keyStr, true);
		}*/
		super.addFileSystem(key, fs);
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.vfs2.provider.AbstractFileProvider#findFileSystem(java.lang.Comparable, org.apache.commons.vfs2.FileSystemOptions)
	 */
	@Override
	protected FileSystem findFileSystem(Comparable<?> key, FileSystemOptions fileSystemProps) {
		/*String userId = EnvUtils.getUserId();
		if(userId!= null && userId.length() >0 && key instanceof AbstractFileName ){
			AbstractFileName oldKey = (AbstractFileName)key;
			String keyStr=oldKey.getURI()+userId;
			OsgiBundleUtils.setOsgiField(oldKey, "key", keyStr, true);
		}*/
		return super.findFileSystem(key, fileSystemProps);
	}

	@Override
	protected FileSystem doCreateFileSystem(FileName rootName, FileSystemOptions fileSystemOptions) throws FileSystemException {
		try {
			org.apache.hadoop.fs.FileSystem fs = IdatrixSecurityManager.getInstance().getFs( EnvUtils.getUserId(), rootName,fileSystemOptions);
			if (fs != null) {
				return new HDFSFileSystem(rootName, fileSystemOptions,
						new HadoopFileSystemImpl(new HadoopFileSystemCallable() {
							@Override
							public org.apache.hadoop.fs.FileSystem getFileSystem() {
								return fs;
							}
						}));
			}else{
				// 将schema恢复为 hdfs,走默认文件系统
				//DefaultFileSystemManager defaultFileSystemManager = ((DefaultFileSystemManager) KettleVFS.getInstance().getFileSystemManager());
				//Map<String, FileProvider> providers = (Map<String, FileProvider>) OsgiBundleUtils.getOsgiField(defaultFileSystemManager, "providers", true);
				//AbstractOriginatingFileProvider provider = (AbstractOriginatingFileProvider) providers.get("hdfs");// 787
				if (bigdataHdfsProvider != null ) {
					String url = rootName.getURI().replace(IdatrixHDFSFileProvider.SCHEME, "hdfs");
					VfsComponentContext vcc = (VfsComponentContext) OsgiBundleUtils.invokeOsgiMethod(bigdataHdfsProvider,"getContext");
					rootName = vcc.getFileSystemManager().resolveName(bigdataHdfsProvider.parseUri(null, url), "/");
					return (FileSystem) OsgiBundleUtils.invokeOsgiMethod(bigdataHdfsProvider, "doCreateFileSystem", rootName,fileSystemOptions);
				}
				throw new FileSystemException("使用默认的hdfs文件失败!");
			}
		} catch (HadoopSecurityManagerException e) {
			throw new FileSystemException(e);
		}
	}

	@Override
	public void onEnvironmentInit() throws LifecycleException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					registerHdfsProvider();
				} catch (LifecycleException e) {
				}
			}
		}, UUIDUtil.getUUIDAsString()+EnvUtils.getUserId()).start();
	}

	@SuppressWarnings("unchecked")
	private void registerHdfsProvider() throws LifecycleException{
		try {
			DefaultFileSystemManager defaultFileSystemManager = ((DefaultFileSystemManager) KettleVFS.getInstance().getFileSystemManager());
			Map<String, FileProvider> providers = (Map<String, FileProvider>) OsgiBundleUtils.getOsgiField(defaultFileSystemManager, "providers", true);
			String retryNum = System.getProperty("idatrix.hadoop.fs.retry.time","1200");
			int num=Integer.parseInt(retryNum);
			while(!providers.containsKey(SCHEME) && num > 0){
				try {
					Thread.sleep(1000);
					num--;
				} catch (InterruptedException e) {
				}
			}
			if(providers.containsKey(SCHEME)){
				bigdataHdfsProvider=  (AbstractOriginatingFileProvider) providers.get(SCHEME);
				providers.remove(SCHEME);
				logger.info(">>> registerHdfsProvider() -> "+ (Integer.parseInt(retryNum)-num) + "s 备份默认HDFS provider成功!");
			}else{
				logger.warn(">>> registerHdfsProvider() -> "+ (Integer.parseInt(retryNum)-num) + "s 备份原生HDFSprovider失败,原生HDFS不可用!");
			}

			this.setFileNameParser(IdatrixHDFSFileNameParser.getInstance());
			((DefaultFileSystemManager) KettleVFS.getInstance().getFileSystemManager()).addProvider(new String[] { SCHEME }, this);
		} catch (FileSystemException e) {
			throw new LifecycleException(e, false);
		}
	}

	@Override
	public void onEnvironmentShutdown() {

	}

}

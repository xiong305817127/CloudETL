/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.utils;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;


/**
 * UnixPathUtil.java
 * 
 * @author JW
 * @since 2017年8月3日
 *
 */
public class FilePathUtil {

	/**
	 * 支持的所有文件类型
	 */
	public static enum FileType {
		data, excel, access, txt, csv, json, output, upload, input,hdfs,sftp,ktr,kjb ;
		
		public static FileType getFileType(String name) {
			try {
				return FileType.valueOf(name);
			}catch( Exception e) {
				return null ;
			}
		}
	}

	public static final List<FileType> TEMPLETE=Lists.newArrayList(FileType.excel, FileType.access, FileType.txt, FileType.csv, FileType.json );
	
	/**
	 * 允许上传的文件类型
	 */
	private static final List<FileType> allowsUploadType = Lists.newArrayList(FileType.data, FileType.excel, FileType.access, FileType.txt, FileType.csv, FileType.json,  FileType.upload,FileType.ktr,FileType.kjb);

	/**
	 * 文件类型对应的文件扩展名设置
	 */
	private static final Map<FileType,List<String>> extensions = Maps.newHashMap();
	static {
		extensions.put(FileType.excel, Lists.newArrayList("xls" ,"xlsx","xlsm","xltx","xltm","xlsb","xlam","et","ett"));
		extensions.put(FileType.access, Lists.newArrayList("mdb" ,"accdb"));
		//extensions.put(FileType.txt, Lists.newArrayList("txt" ,"rtf","md","html","xml","ini","properties","bat","sh","shell","bak"));
		extensions.put(FileType.csv, Lists.newArrayList("csv" ));
		extensions.put(FileType.json, Lists.newArrayList("json","js" ));
		extensions.put(FileType.ktr, Lists.newArrayList("ktr" ));
		extensions.put(FileType.kjb, Lists.newArrayList("kjb" ));
	}

	
	/**
	 * 类型是否支持上传
	 * @param type
	 * @return
	 */
	public static boolean isUploadType(FileType type){
		if(type == null){
			return false;
		}
		if(allowsUploadType.contains(type)){
			return true ;
		}
		return false ;	
	}

	/**
	 * 上传文件 是否和类型扩展名相匹配
	 * @param type
	 * @param file
	 * @return
	 */
	public static boolean fileExtensionIfSupport(FileType type,FileObject file ){
		try {
			if(type == null){
				return true;
			}
			if(file.isFolder()){
				return true;
			}
			List<String> extension = extensions.get(type);
			String fileExtension = file.getName().getExtension();
			if( extension != null && extension.size() >0){
				return extension.contains(fileExtension.toLowerCase());
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 *创建文件/文件夹, 如果不存在
	 * @param path
	 * @param isFolder ,是否是创建文件夹
	 * @return
	 */
	public static boolean createFileIfNotExist(String absolutePath,boolean isFolder) {
		FileObject fileObj = null;
		try {
			fileObj = KettleVFS.getFileObject(absolutePath);
			if(!fileObj.exists()){
				if(isFolder){
					fileObj.createFolder();
				}else{
					fileObj.createFile();
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (fileObj != null) {
				try {
					fileObj.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 文件/文件夹是否存在
	 * @param path
	 * @param isFolder 是否是文件夹,为空则只判断是否存在
	 * @return
	 */
	public static boolean fileIsExist(String absolutePath,Boolean isFolder){
		FileObject fileObj=null;
		try {
			fileObj = KettleVFS.getFileObject(absolutePath);
			if(fileObj.exists()){
				if(isFolder == null){
					return true ;
				}
				if(fileObj.isFolder()&&isFolder){
					return true ;
				}
				if(fileObj.isFile()&&!isFolder){
					return true ;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		} finally {
			if (fileObj != null) {
				try {
					fileObj.close();
				} catch (Exception e) {
				}
			}
		}
	}
	/**
	 * 获取路径的URI对象
	 * @param allPath : 所有格式的路径 ( 绝对路径 , 相对路径(格式 type::path))
	 * @return
	 * @throws Exception
	 */
	public static URI getRealFileURI(String allPath) throws Exception {
		
		try {
			//不报异常则是相对路径
			allPath = getRealFileName(null,allPath) ;
		}catch( KettleException ke) { 
			//为绝对路径
		}
		try( FileObject fileObject = KettleVFS.getFileObject(allPath);){
			return fileObject.getURL().toURI();
		}

	}
	
	/**
	 * 获取路径对应文件类型的相对路径<br>
	 * eg.<br>
	 * 		absolutePath: /root/output/test/aaa.txt ,<br>
	 *		type:output(对应的根路径是/root/output), <br> 
	 *		返回  test/aaa.txt
	 * @param absolutePath 文件绝对路径
	 * @param type 文件类型
	 * @return 文件相对路径
	 * @throws Exception
	 */
	public static String getRelativeFileName(String owner ,String absolutePath, FileType type) throws Exception {
		String root = getRealFileName(owner ,"", type);
		if (absolutePath != null && root!= null && !"/".equals(root) && absolutePath.length() >= root.length() && absolutePath.startsWith(root)) {
			return absolutePath.substring(root.length());
		}
		return absolutePath;
	}

	/**
	 * 获取固定格式的路径字符串对应的完整路径<br>
	 * eg.<br>
	 * 		relativePathStr: output::test/aaa.txt ,<br>
	 *		返回  /root/output/test/aaa.txt
	 * @param relativePathStr 固定格式的路径字符串 , 固定格式为: type::path  (类型::相对路径)
	 * @return 文件绝对路径
	 * @throws Exception
	 */
	public static String getRealFileName(String owner ,String relativePathStr) throws Exception {
		String path = relativePathStr ;
		FileType type = FileType.input ;
		if ( !Utils.isEmpty(relativePathStr) && relativePathStr.contains("::")) {
			String[] paths = StringUtils.split(relativePathStr, "::", 2);
			if (paths != null && paths.length == 2 && FileType.getFileType(paths[0]) != null ) {
				path = paths[1] ;
				type = FileType.getFileType(paths[0]) ;
			}
		}
		return getRealFileName(owner ,path, type );
	}

	/**
	 * 根据类型和相对路径  获取文件绝对整路径<br>
	 * eg.<br>
	 * 		relativePath: test/aaa.txt ,<br>
	 *		type:output(对应的根路径是/root/output), <br> 
	 *		返回   /root/output/test/aaa.txt
	 * @param relativePath 相对路径
	 * @param type 文件类型
	 * @return 文件绝对路径
	 * @throws Exception
	 */
	public static String getRealFileName(String owner , String relativePath, FileType type) throws Exception {
		relativePath = ( Utils.isEmpty(relativePath) || ".".equals(relativePath) ) ? "/" : relativePath;
		if ( type == null ) {
			return UnixPathUtil.unixPath2(relativePath);
		}

		FileSystemManager e = KettleVFS.getInstance().getFileSystemManager();
		String[] schemes = e.getSchemes();
		for (int i = 0; i < schemes.length ; ++i) {
			if (relativePath.startsWith(schemes[i] + ":")) {
				return  UnixPathUtil.unixPath2(relativePath);
			}
		}

		switch (type) {
		case ktr :
			return getGoodPath(owner , CloudApp.getInstance().getRepositoryRootFolder()+CloudApp.getInstance().getUserTransRepositoryPath(owner,null) , relativePath );
		case kjb:
			return getGoodPath(owner , CloudApp.getInstance().getRepositoryRootFolder()+CloudApp.getInstance().getUserJobsRepositoryPath(owner,null) , relativePath ) ;
		case data:
			return getGoodPath(owner , getDataStoreRootPath(owner) , relativePath );
		case input:
			return getGoodPath(owner ,null,relativePath);
		case output:
			return getGoodPath(owner , getOutputRootPath(owner) , relativePath );
		case upload:
			return getGoodPath(owner , getTempleteRootPath(owner) , relativePath );
		case sftp:
		case hdfs:
			return  getGoodPath(owner ,null,relativePath);
		case excel:
		case access:
		case txt:
		case csv:
		case json:
			if (StringUtils.isEmpty(relativePath) || !relativePath.startsWith(type+"/") ) {
				relativePath = type + "/" + relativePath;
			}
			return getGoodPath(owner , getTempleteRootPath(owner) , relativePath );
		default:
			new KettleException("类型 [" + type + "] 不合法!");
		}

		return  UnixPathUtil.unixPath2(relativePath);
	}
	
	private static String getGoodPath(String owner , String parentPath,String path) {
		if(Utils.isEmpty(parentPath) && Utils.isEmpty(path)) {
			//都为空 返回 根路径
			return "/" ;
		}
		if( ( !Utils.isEmpty( parentPath)) && Utils.isEmpty( path) ) {
			//父路径不为空,子路径为空,直接返回父路径
			 return UnixPathUtil.unixPath2(parentPath) ;
		}
		if( Utils.isEmpty( parentPath ) || path.startsWith(parentPath) ) {
			// 父路径为空 或者 子路径已经是以父路径开头 , 直接返回子路径
			 return UnixPathUtil.unixPath2(path) ;
		}
		
		if( path.startsWith(getDataStoreRootPath(owner)) || path.startsWith(getTempleteRootPath(owner))  || path.startsWith(getOutputRootPath(owner))  ) {
			//是 配置路径开头
			 return UnixPathUtil.unixPath2(path) ;
		}
		
		return UnixPathUtil.unixPath2(parentPath+"/"+path) ;
	}


	public static final String defaultFilePathKey = "idatrix.metadata.reposity.root";
	
	private static String getTempleteRootPath(String owner) {
		String path = IdatrixPropertyUtil.getPropertyByFormatUser("templete.upload.file.root.path", owner) ;
		if(Utils.isEmpty(path)) {
			String root = IdatrixPropertyUtil.getProperty(defaultFilePathKey,"/data/ETL/reposity/") ;
			Boolean renterPrivilege = IdatrixPropertyUtil.getBooleanProperty("idatrix.renter.super.privilege.enable", false);
			if(renterPrivilege || ( !renterPrivilege && IdatrixPropertyUtil.getBooleanProperty("idatrix.use.renter.enable", false)) ) {
				//带租户Id
				path =  root +"/"+CloudSession.getLoginRenterId()+"/"+ Const.NVL(owner, CloudSession.getResourceUser() )+"/templete/";
			}else {
				//只要user
				path =  root +"/"+ Const.NVL(owner, CloudSession.getResourceUser() )+"/templete/";
			}
		}
		path = UnixPathUtil.unixPath(path);
		createFileIfNotExist( path,true);
		return path ;
	}
	
	public static String getDataStoreRootPath(String owner) {
		String path = IdatrixPropertyUtil.getPropertyByFormatUser("dataStore.upload.file.root.path", owner) ;
		if(Utils.isEmpty(path)) {
			String root = IdatrixPropertyUtil.getProperty(defaultFilePathKey,"/data/ETL/reposity/") ;
			Boolean renterPrivilege = IdatrixPropertyUtil.getBooleanProperty("idatrix.renter.super.privilege.enable", false);
			if(renterPrivilege || ( !renterPrivilege && IdatrixPropertyUtil.getBooleanProperty("idatrix.use.renter.enable", false)) ) {
				//带租户Id
				path =  root +"/"+CloudSession.getLoginRenterId()+"/"+ Const.NVL(owner, CloudSession.getResourceUser() )+"/dataStore/";
			}else {
				//只要user
				path =  root +"/"+ Const.NVL(owner, CloudSession.getResourceUser() )+"/dataStore/";
			}
		}
		path = UnixPathUtil.unixPath(path);
		createFileIfNotExist( path,true);
		return path ;
	}
	
	public static String getOutputRootPath(String owner) {
		String path = IdatrixPropertyUtil.getPropertyByFormatUser("output.server.file.root.path", owner) ;
		if(Utils.isEmpty(path)) {
			String root = IdatrixPropertyUtil.getProperty(defaultFilePathKey,"/data/ETL/reposity/") ;
			Boolean renterPrivilege = IdatrixPropertyUtil.getBooleanProperty("idatrix.renter.super.privilege.enable", false);
			if(renterPrivilege || ( !renterPrivilege && IdatrixPropertyUtil.getBooleanProperty("idatrix.use.renter.enable", false)) ) {
				//带租户Id
				path =  root +"/"+CloudSession.getLoginRenterId()+"/"+ Const.NVL(owner, CloudSession.getResourceUser() )+"/output/";
			}else {
				//只要user
				path =  root +"/"+ Const.NVL(owner, CloudSession.getResourceUser() )+"/output/";
			}
		}
		path = UnixPathUtil.unixPath(path);
		createFileIfNotExist( path,true);
		return path ;
	}

	
}

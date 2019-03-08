/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.job.entries.sftp.SFTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.ys.idatrix.cloudetl.deploy.MetaCubeCategory;
import com.ys.idatrix.cloudetl.dto.common.FileListDto;
import com.ys.idatrix.cloudetl.dto.common.FileListRequestDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto.DealRowsInterface;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil.FileType;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.reference.metacube.MetaCubeHadoop;
import com.ys.idatrix.cloudetl.repository.database.SystemSettingDao;
import com.ys.idatrix.cloudetl.repository.database.dto.SystemSettingsDto;
import com.ys.idatrix.cloudetl.service.CloudBaseService;
import com.ys.idatrix.cloudetl.service.job.CloudJobService;
import com.ys.idatrix.cloudetl.service.trans.CloudTransService;
import com.ys.idatrix.cloudetl.service.trans.stepdetail.HadoopFileInputDetailService;


/**
 * Cloud basic service implementation.
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Service
public class FileServiceImpl extends CloudBaseService implements FileService {

	@Autowired
	private MetaCubeCategory metaCubeCategory;

	@Autowired
	HadoopFileInputDetailService hadoopFileInputDetailService;
	@Autowired
	CloudJobService cloudJobService ;
	@Autowired
	CloudTransService cloudTransService ;
	
	@Autowired(required = false)
	MetaCubeHadoop metaCubeHadoop;
	
	public ReturnCodeDto saveSystemSetting(SystemSettingsDto setting) throws Exception{
		if(Utils.isEmpty(setting.getKey())) {
			throw new KettleException("系统设置的key不能为空!");
		}
		
		SystemSettingsDto old = SystemSettingDao.getInstance().getSetting(setting.getKey());
		if( old == null  ) {
			SystemSettingDao.getInstance().addSetting(setting);
			return new ReturnCodeDto(0,"saved");
		}else {
			SystemSettingDao.getInstance().updateSetting(setting);
			return new ReturnCodeDto(0,"updated");
		}
	}
	
	public  List<SystemSettingsDto> getSystemSetting() throws Exception{
		return SystemSettingDao.getInstance().getSettings();
	}
	
	
	public ReturnCodeDto getVersion() throws Exception{

		String versionFileRoot =".";
		String filePrefix = "version";

		FileObject rootFolder = null;
		try{
			rootFolder = KettleVFS.getFileObject(versionFileRoot);
			if(rootFolder.exists() && rootFolder.isFolder()){
				for (FileObject childFolder : rootFolder.getChildren()) {
					if(childFolder.isFile() && childFolder.getName().getBaseName().startsWith(filePrefix)){
						ReturnCodeDto rcd = new ReturnCodeDto(0, childFolder.getName().getBaseName());
						rcd.setData(StringEscapeHelper.encode(KettleVFS.getTextFileContent(childFolder.getURL().toString(), "UTF-8")));
						childFolder.close();
						return rcd ;
					}
					childFolder.close();
				}
			}
			throw new KettleException("获取版本信息异常!");
		}finally{
			if(rootFolder != null){
				rootFolder.close();
			}
		}

	}

	public FileObject downloadFile(FileListRequestDto fileListRequestDto) throws Exception {

		String owner = fileListRequestDto == null  ? CloudSession.getResourceUser() : fileListRequestDto.getOwner();
		FileType type = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getType()) ? FileType.input : FileType.getFileType(fileListRequestDto.getType());
		String path = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getPath()) ? "" : fileListRequestDto.getPath();
		
		if (!FilePathUtil.isUploadType(type) && !FileType.output.equals(type)) {
			throw new KettleException("type " + type + " is not support!");
		}

		path = FilePathUtil.getRealFileName(owner ,path, type);

		if (!FilePathUtil.fileIsExist(path, false)) {
			throw new KettleException("file " + path + " is not exist!");
		}

		return KettleVFS.getFileObject(path);
	}

	public ReturnCodeDto deleteFile(FileListRequestDto fileListRequestDto) throws Exception {
		
		String owner = fileListRequestDto == null  ? CloudSession.getResourceUser() : fileListRequestDto.getOwner();
		FileType type = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getType()) ? FileType.input : FileType.getFileType(fileListRequestDto.getType());
		String path = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getPath())  ? "" : fileListRequestDto.getPath();
		if (!FilePathUtil.isUploadType(type) && !FileType.output.equals(type) ) {
			throw new KettleException("type " + type + " is not support!");
		}

		path = FilePathUtil.getRealFileName(owner,path, type);

		if (!FilePathUtil.fileIsExist(path, false)) {
			throw new KettleException("file " + path + " is not exist!");
		}
		try (FileObject fileObject = KettleVFS.getFileObject(path)) {
			return new ReturnCodeDto(0, fileObject.delete() ? "success" : "error");
		}

	}

	@Override
	public ReturnCodeDto fileisExist(FileListRequestDto fileListRequestDto) throws Exception {

		String owner = fileListRequestDto == null  ? CloudSession.getResourceUser() : fileListRequestDto.getOwner();
		FileType type = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getType()) ? FileType.input : FileType.getFileType(fileListRequestDto.getType());
		String path = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getPath())  ? "" : fileListRequestDto.getPath();
		
		if (FileType.hdfs.equals(type)) {
			String[] values = path.split("::");
			if (values == null || values.length != 2) {
				throw new KettleException("hadoop path is invalid!");
			}
			List<String> userRootList = metaCubeHadoop.getHadoopUserRoots(owner, null);
			if( userRootList!= null && userRootList.size() >0 ) {
				String curRoot = userRootList.stream().filter(r -> values[1].startsWith(r)).findAny().orElse(null) ;
				if( Utils.isEmpty(curRoot) ) {
					throw new KettleException("path " + path + " root is not match!");
				}
			}else {
				throw new KettleException("path " + path + " root is not found!");
			}

			path = hadoopFileInputDetailService.getConnectPath(owner, values[0], values[1]);
		} else if (FileType.sftp.equals(type)) {
			String[] values = path.split("::");
			if (values == null || (values.length != 2 && values.length != 5)) {
				throw new KettleException("sftp path  is invalid!");
			}
			FileListDto res = getSftpFileList(owner, values);
			if (res != null) {
				return new ReturnCodeDto(0, "exist");
			} else {
				throw new KettleException("get sftp filelist fail!");
			}
		}else {
			path = FilePathUtil.getRealFileName(owner,path, type);
		}
		
		if (!FilePathUtil.fileIsExist(path, null)) {
			throw new KettleException("path " + path + " is not exist!");
		}
		return new ReturnCodeDto(0, "exist");

	}

	/**
	 * type : data| excel|access|txt|csv|json|ktr|kjb
	 */
	@Override
	public FileListDto uploadFile(MultipartFile file,String owner , String typeStr, String filterTypeStr, boolean isCover) throws Exception {

		owner = Const.NVL(owner,  CloudSession.getResourceUser() );
		FileType type = FileType.getFileType(  Const.NVL(typeStr, "input" ) ); 
		FileType filterType = FileType.getFileType(  Const.NVL(filterTypeStr, type.name() ) ); 

		String root = FilePathUtil.getRealFileName(owner ,"", type);
		String filePath = file.getOriginalFilename();
		if ( FilePathUtil.TEMPLETE.contains(type) ) {
			filePath = type + "/" + file.getOriginalFilename();
		}

		String path = root + file.getOriginalFilename();
		FileObject uploadfile = KettleVFS.getFileObject(path);
		if (uploadfile.exists() && !isCover) {
			uploadfile.close();
			throw new KettleException("file :" + file.getOriginalFilename() + " is exist!");
		}
		if (!FilePathUtil.fileExtensionIfSupport(filterType, uploadfile)) {
			uploadfile.close();
			throw new KettleException("file :" + file.getOriginalFilename() + " extension is not support!");
		}

		FileObject parent = uploadfile.getParent();
		if (parent != null) {
			if (!parent.exists()) {
				parent.createFolder();
			}
		}
		parent.close();

		try (InputStream inStream = file.getInputStream();
				OutputStream outStream = KettleVFS.getOutputStream(uploadfile, false);) {
			int b;
			while ((b = inStream.read()) != -1) {
				outStream.write(b);
			}
		}
		
		//假如是 数据库仓库,ktr/kjb需要写入数据库
		if(CloudApp.getInstance().isDatabaseRepository()){
			switch (type) {
			case ktr:
				cloudTransService.addDBTrans(path);break;
			case kjb:
				cloudJobService.addDbJob(path);break;
			default:
				break;
			}
		}

		
		FileListDto result = new FileListDto();
		result.setFileName(file.getOriginalFilename());
		result.setPath(filePath);
		result.setFolder(result.isFolder());
		result.setLastModified(Double.doubleToLongBits(uploadfile.getFileSystem().getLastModTimeAccuracy()));
		result.setRead(uploadfile.isReadable());
		result.setWrite(uploadfile.isWriteable());

		uploadfile.close();
		return result;
	}

	public Map<String,PaginationDto<FileListDto>> getFileList(FileListRequestDto fileListRequestDto,String owner, boolean isMap ,int page, int pageSize, String search) throws Exception {

		FileType type = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getType()) ? FileType.input : FileType.getFileType(fileListRequestDto.getType());
		String path = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getPath())  ? "" : fileListRequestDto.getPath();
		FileType filterType = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getFilterType()) ? type : FileType.getFileType(fileListRequestDto.getFilterType()); 

		if (!FilePathUtil.isUploadType(type) && !FileType.output.equals(type)) {
			throw new KettleException("type " + type + " is not support!");
		}
		
		String sourcePath = path;
		Map<String, List<FileListDto>> userDirMap = getUserNameList(owner, new ForeachCallback<String,FileListDto>() {
			@Override
			public List<FileListDto> getOne(String user) throws Exception {
				List<FileListDto> childrenList = Lists.newArrayList();
				String userpath = FilePathUtil.getRealFileName(user ,path, type);
				try (FileObject root = KettleVFS.getFileObject(userpath);) {
					if (root != null && root.exists() && root.isFolder()) {
						getChildFileList(filterType, childrenList, sourcePath, root);
						childrenList.sort((FileListDto o1, FileListDto o2) -> {
							return (int) (o1.getLastModified() - o2.getLastModified());
						});
					}
				}
				return childrenList ;
			}
			
		});
		return getPaginationMaps(isMap, page, pageSize, search, userDirMap, new DealRowsInterface<FileListDto>() {
			@Override
			public FileListDto dealRow(Object obj, Object... params) throws Exception {
				String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
				FileListDto res = (FileListDto) obj;
				res.setOwner(eleOwner);
				return res ;
			}

			@Override
			public boolean match(Object obj, String search, Object... params) throws Exception {
				//String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
				FileListDto fd = (FileListDto) obj;
				return defaultMatch(fd.getPath(), search);// fd.getPath()!=null && fd.getPath().toLowerCase().contains(search.toLowerCase());
			}
		});
	}

	private void getChildFileList(FileType filterType, List<FileListDto> result, String sourcePath, FileObject parent)
			throws Exception {

		if (parent.isFolder()) {
			for (FileObject childFolder : parent.getChildren()) {
				String parentPath = sourcePath + (childFolder.isFolder() ? (childFolder.getName().getBaseName() + "/") : "");
				getChildFileList(filterType, result, parentPath, childFolder);
			}
		} else {
			if (FilePathUtil.fileExtensionIfSupport(filterType, parent)) {
				FileListDto childDto = new FileListDto();
				childDto.setFileName(parent.getName().getBaseName());
				childDto.setPath(sourcePath + parent.getName().getBaseName());
				childDto.setFolder(false);
				childDto.setLastModified(Double.doubleToLongBits(parent.getFileSystem().getLastModTimeAccuracy()));
				childDto.setRead(parent.isReadable());
				childDto.setWrite(parent.isWriteable());
				result.add(childDto);
			}
		}
		parent.close();
	}

	@Override
	public String getDataStorePath(String owner , String type) throws Exception {
		return FilePathUtil.getRealFileName(owner ,"", FileType.getFileType(type) );
	}

	/**
	 * 
	 * type : data|input|output|upload|excel|access|txt|csv|hdfs|sftp path :
	 * root-path | sourceConfigurationName::fileName |
	 * sourceConfigurationName::path/serverName::serverPort::userName::password:
	 * :path depth: -1 | number
	 */
	@Override
	public FileListDto getFileList(FileListRequestDto fileListRequestDto) throws Exception {
		
		String owner = fileListRequestDto == null  ? CloudSession.getResourceUser() : fileListRequestDto.getOwner();
		FileType type = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getType()) ? FileType.input : FileType.getFileType(fileListRequestDto.getType());
		String path = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getPath())  ? "" : fileListRequestDto.getPath();
		FileType filterType = fileListRequestDto == null||Utils.isEmpty(fileListRequestDto.getFilterType()) ? type : FileType.getFileType(fileListRequestDto.getFilterType()); 
		int depth = fileListRequestDto == null ? -1 : fileListRequestDto.getDepth();
		
		String sourcePath = path;
		String rootFileName = path;

		if ( FileType.hdfs.equals(type)) {
			String[] values = path.split("::");
			if (values == null || values.length != 2) {
				throw new KettleException("hadoop path is invalid!");
			}
			List<String> userRootList = metaCubeHadoop.getHadoopUserRoots(owner, null);
			if( userRootList!= null && userRootList.size() >0 ) {
				String curRoot = userRootList.stream().filter(r -> values[1].startsWith(r)).findAny().orElse(null) ;
				if( Utils.isEmpty(curRoot) ) {
					throw new KettleException("path " + path + " root is not match!");
				}
			}else {
				throw new KettleException("path " + path + " root is not found!");
			}
			sourcePath = values[1];
			path = hadoopFileInputDetailService.getConnectPath(owner , values[0], values[1]);
		} else if (FileType.sftp.equals(type)) {
			String[] values = path.split("::");
			if (values == null || (values.length != 2 && values.length != 5)) {
				throw new KettleException("sftp path  is invalid!");
			}
			FileListDto res = getSftpFileList(owner , values);
			if (res != null) {
				return res;
			} else {
				throw new KettleException("get sftp filelist fail!");
			}
		} else {
			path = FilePathUtil.getRealFileName(owner , path , type);
			rootFileName = path;
		}

		FileObject root = KettleVFS.getFileObject(path);
		FileListDto result = new FileListDto();
		result.setFileName(rootFileName);
		result.setPath(sourcePath);
		result.setFolder(root.isFolder());
		result.setLastModified(Double.doubleToLongBits(root.getFileSystem().getLastModTimeAccuracy()));
		result.setRead(root.isReadable());
		result.setWrite(root.isWriteable());

		if (root != null && root.exists()) {
			getChildFile(filterType, result, root, depth);
		}

		root.close();
		return result;
	}

	private void getChildFile(FileType filterType, FileListDto result, FileObject parent, int depth) throws Exception {
		if (depth == 0) {
			return;
		}
		if (parent.isFolder()) {
			FileObject[] children = parent.getChildren();
			Arrays.sort(children, (FileObject o1, FileObject o2) -> {
				return o1.getName().compareTo(o2.getName());
			});
			for (FileObject childFolder : children) {

				if (FilePathUtil.fileExtensionIfSupport(filterType, childFolder)) {
					FileListDto childDto = new FileListDto();
					childDto.setFileName(childFolder.getName().getBaseName());
					childDto.setPath(result.getPath() + childFolder.getName().getBaseName() + (childFolder.isFolder() ? "/" : ""));
					childDto.setFolder(childFolder.isFolder());
					childDto.setLastModified(Double.doubleToLongBits(childFolder.getFileSystem().getLastModTimeAccuracy()));
					childDto.setRead(childFolder.isReadable());
					childDto.setWrite(childFolder.isWriteable());

					getChildFile(filterType, childDto, childFolder, depth - 1);
					result.addChild(childDto);
					childFolder.close();
				}
			}
		}
	}
	
	@Override
	public String getParentPath(String path) throws Exception {
		path = Utils.isEmpty(path) ? "/" : path;
		FileObject root = KettleVFS.getFileObject(path);
		if (root == null || root.getParent() == null || root.getParent().getName() == null) {
			return "/"; // No parent!
		}
		
		// Windows: file:///D:yyy, Unix: file:///yyy
		FileObject parentobj = root.getParent() ;
		String parent = parentobj.getURL().getPath();
		if (parent.startsWith("file:///")) {
			parent = parent.substring("file:///".length());
		}
		
		if (parent.indexOf(":") != 1 && !parent.startsWith("/")) {
			parent = "/" + parent;
		}
		
		if (!parent.endsWith("/")) {
			parent += "/";
		}
		
		if (Utils.isEmpty(parent) || path.equals(parent)) {
			return ""; // No parent!
		}
		
		return parent;
	}
	
	
	@Override
	public  List<String> getHdfsRootPath(String owner, Boolean isRead) throws Exception{
		return metaCubeHadoop.getHadoopUserRoots(owner, isRead) ;
	}

	private FileListDto getSftpFileList(String owner, String[] values) throws Exception {
		switch (metaCubeCategory) {
		case IDATRIX :
			//metaCube 没通,暂时走正常流程
			
//			// Calling MetaCube RPC APIs to get meta data
//			if (values.length != 2) {
//				throw new KettleException("sftp path  is invalid!");
//			}
//
//			MetaCubeFtpDirectoriesDto dirs = cloudFtpMetaCube.metaCubeFtpDirectories(values[0], values[1]);
//			if (dirs != null) {
//				FileListDto result = new FileListDto();
//				result.setFileName(dirs.getName());
//				result.setPath(values[1]);
//				result.setFolder(true);
//				if (dirs.getDirectories() != null) {
//					dirs.getDirectories().stream().forEach(dir -> {
//						boolean isFolder = "dir".equals(dir.getType()) ? true : false;
//						FileListDto childDto = new FileListDto();
//						childDto.setFileName(dir.getDirName());
//						childDto.setPath(result.getPath() + dir.getDirName() + (isFolder ? "/" : ""));
//						childDto.setFolder(isFolder);
//						childDto.setRead("read".equals(dir.getAccess()) ? true : false);
//						childDto.setWrite("write".equals(dir.getType()) ? true : false);
//						result.addChild(childDto);
//					});
//				}
//				return result;
//			} else {
//				throw new KettleException("Failed to retrieve file list!");
//			}

		case PENTAHO:
			// Get meta data from local meta store
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// Do nothing!
		}

		if (values.length != 5) {
			throw new KettleException("sftp path  is invalid!");
		}
		String serverName = values[0];
		String serverPort = values[1];
		String userName = values[2];
		String password = values[3];
		String path = values[4];
		SFTPClient sftpclient = new SFTPClient(InetAddress.getByName(serverName), Const.toInt(serverPort, 22), userName,
				null, null);

		// Set proxy?
		// if ( !Utils.isEmpty( proxyHost ) ) {
		// // Set proxy
		// sftpclient.setProxy(
		// proxyHost,proxyPort,proxyUsername,proxyPassword,proxyType );
		// }
		// login to ftp host ...
		sftpclient.login(password);

		FileListDto result = new FileListDto();
		result.setFileName(path);
		result.setPath(path);
		result.setFolder(true);
		
		 ChannelSftp c =  (ChannelSftp) OsgiBundleUtils.getOsgiField(sftpclient, "c", true);
		if(c != null) {
			Vector<?> v = c.ls(path);
			if ( v != null ) {
			      for ( int i = 0; i < v.size(); i++ ) {
			          Object obj = v.elementAt( i );
			          if ( obj != null && obj instanceof LsEntry ) {
			            LsEntry lse = (LsEntry) obj;
			            if(".".equals(lse.getFilename()) || "..".equals(lse.getFilename()) || Utils.isEmpty(lse.getFilename())) {
			            	continue ;
			            }
			            FileListDto childDto = new FileListDto();
						childDto.setFileName(lse.getFilename());
			            if ( !lse.getAttrs().isDir() ) {
			            	childDto.setPath(result.getPath() + lse.getFilename());
							childDto.setFolder(false);
			            }else {
			            	childDto.setPath(result.getPath() + lse.getFilename() + "/");
							childDto.setFolder(true);
			            }
			            result.addChild(childDto);
			          }
			      }
			 }
		}

		return result;
	}


}

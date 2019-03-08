/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.file;

import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;
import org.springframework.web.multipart.MultipartFile;

import com.ys.idatrix.cloudetl.dto.common.FileListDto;
import com.ys.idatrix.cloudetl.dto.common.FileListRequestDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.repository.database.dto.SystemSettingsDto;


/**
 * Cloud basic service.
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface FileService {
	
	/**
	 * 
	 * type : data|input|output|upload|excel|access|txt|csv|hdfs|json
	 * path : root-path | sourceConfigurationName::fileName 
	 * depth: -1 | number
	 */
	FileListDto getFileList(FileListRequestDto fileListRequestDto) throws Exception;
	
	List<String> getHdfsRootPath(String owner, Boolean isRead) throws Exception;
	
	String getParentPath(String path) throws Exception;
	
	Map<String,PaginationDto<FileListDto>> getFileList(FileListRequestDto fileListRequestDto,String owner, boolean isMap ,int page,int pageSize,String search) throws  Exception;
	
	/** 
	 * type : data| excel|access|txt|csv|json
	 */
	FileListDto uploadFile(MultipartFile file,String owner , String type,String filterType,boolean isCover) throws Exception;
	
	String getDataStorePath(String owner , String type) throws Exception;
	
	ReturnCodeDto fileisExist(FileListRequestDto fileListRequestDto) throws Exception;
	
	ReturnCodeDto deleteFile(FileListRequestDto fileListRequestDto) throws Exception;
	
	FileObject downloadFile(FileListRequestDto fileListRequestDto) throws Exception;
	
	ReturnCodeDto getVersion() throws Exception;
	
	ReturnCodeDto saveSystemSetting(SystemSettingsDto setting) throws Exception;
	
	 List<SystemSettingsDto>  getSystemSetting( ) throws Exception;
	
}

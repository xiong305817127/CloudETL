/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.entries.filemanagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.copyfiles.JobEntryCopyFiles;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.dto.entry.entries.EntryParameter;
import com.ys.idatrix.quality.dto.entry.parts.CopyFilessourceFilefolderDto;
import com.ys.idatrix.quality.ext.utils.FilePathUtil;
import com.ys.idatrix.quality.ext.utils.FilePathUtil.FileType;

import net.sf.json.JSONObject;

/**
 * Entry - CopyFiles. 转换 org.pentaho.di.job.entries.copyfiles.JobEntryCopyFiles
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPcopy_files")
@Scope("prototype")
public class SPCopyFiles implements EntryParameter {
	
	boolean copyEmptyFolders = true;
	boolean argFromPrevious = false;
	boolean overwriteFiles = false;
	boolean includeSubfolders = true;
	boolean removeSourceFiles = false;
	boolean addResultFilesname = false;
	boolean destinationIsAfile = false;
	boolean createDestinationFolder = false;
	List<CopyFilessourceFilefolderDto> filefolder;

	/**
	 * @return copyEmptyFolders
	 */
	public boolean isCopyEmptyFolders() {
		return copyEmptyFolders;
	}

	/**
	 * @param 设置
	 *            copyEmptyFolders
	 */
	public void setCopyEmptyFolders(boolean copyEmptyFolders) {
		this.copyEmptyFolders = copyEmptyFolders;
	}

	/**
	 * @return argFromPrevious
	 */
	public boolean isArgFromPrevious() {
		return argFromPrevious;
	}

	/**
	 * @param 设置
	 *            argFromPrevious
	 */
	public void setArgFromPrevious(boolean argFromPrevious) {
		this.argFromPrevious = argFromPrevious;
	}

	/**
	 * @return overwriteFiles
	 */
	public boolean isOverwriteFiles() {
		return overwriteFiles;
	}

	/**
	 * @param 设置
	 *            overwriteFiles
	 */
	public void setOverwriteFiles(boolean overwriteFiles) {
		this.overwriteFiles = overwriteFiles;
	}

	/**
	 * @return includeSubfolders
	 */
	public boolean isIncludeSubfolders() {
		return includeSubfolders;
	}

	/**
	 * @param 设置
	 *            includeSubfolders
	 */
	public void setIncludeSubfolders(boolean includeSubfolders) {
		this.includeSubfolders = includeSubfolders;
	}

	/**
	 * @return removeSourceFiles
	 */
	public boolean isRemoveSourceFiles() {
		return removeSourceFiles;
	}

	/**
	 * @param 设置
	 *            removeSourceFiles
	 */
	public void setRemoveSourceFiles(boolean removeSourceFiles) {
		this.removeSourceFiles = removeSourceFiles;
	}

	/**
	 * @return addResultFilesname
	 */
	public boolean isAddResultFilesname() {
		return addResultFilesname;
	}

	/**
	 * @param 设置
	 *            addResultFilesname
	 */
	public void setAddResultFilesname(boolean addResultFilesname) {
		this.addResultFilesname = addResultFilesname;
	}

	/**
	 * @return destinationIsAfile
	 */
	public boolean isDestinationIsAfile() {
		return destinationIsAfile;
	}

	/**
	 * @param 设置
	 *            destinationIsAfile
	 */
	public void setDestinationIsAfile(boolean destinationIsAfile) {
		this.destinationIsAfile = destinationIsAfile;
	}

	/**
	 * @return createDestinationFolder
	 */
	public boolean isCreateDestinationFolder() {
		return createDestinationFolder;
	}

	/**
	 * @param 设置
	 *            createDestinationFolder
	 */
	public void setCreateDestinationFolder(boolean createDestinationFolder) {
		this.createDestinationFolder = createDestinationFolder;
	}


	/**
	 * @return filefolder
	 */
	public List<CopyFilessourceFilefolderDto> getFilefolder() {
		return filefolder;
	}

	/**
	 * @param  设置 filefolder
	 */
	public void setFilefolder(List<CopyFilessourceFilefolderDto> filefolder) {
		this.filefolder = filefolder;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("filefolder", CopyFilessourceFilefolderDto.class);
		return (SPCopyFiles) JSONObject.toBean(jsonObj, SPCopyFiles.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPCopyFiles spCopyFiles = new SPCopyFiles();
		JobEntryCopyFiles jobentrycopyfiles = (JobEntryCopyFiles) entryMetaInterface;

		spCopyFiles.setCopyEmptyFolders(jobentrycopyfiles.isCopyEmptyFolders());
		spCopyFiles.setIncludeSubfolders(jobentrycopyfiles.isIncludeSubfolders());
		spCopyFiles.setAddResultFilesname(jobentrycopyfiles.isAddresultfilesname());
		spCopyFiles.setArgFromPrevious(jobentrycopyfiles.isArgFromPrevious());
		spCopyFiles.setRemoveSourceFiles(jobentrycopyfiles.isRemoveSourceFiles());
		spCopyFiles.setDestinationIsAfile(jobentrycopyfiles.isDestinationIsAFile());
		spCopyFiles.setCreateDestinationFolder(jobentrycopyfiles.isCreateDestinationFolder());
		spCopyFiles.setOverwriteFiles(jobentrycopyfiles.isoverwrite_files());

		if (jobentrycopyfiles.source_filefolder != null) {

			List<CopyFilessourceFilefolderDto> parametersList = Lists.newArrayList();
			String[] source_filefolder = jobentrycopyfiles.source_filefolder;
			String[] destination_filefolder = jobentrycopyfiles.destination_filefolder;
			String[] wildcard = jobentrycopyfiles.wildcard;
			for (int i = 0; i < source_filefolder.length; i++) {
				CopyFilessourceFilefolderDto parametersDto = new CopyFilessourceFilefolderDto();
				
				parametersDto.setWildcard(wildcard[i]);
				boolean isSourceLocal = ( jobentrycopyfiles.getConfigurationBy(source_filefolder[i])!=null && jobentrycopyfiles.getConfigurationBy(source_filefolder[i]).startsWith(JobEntryCopyFiles.LOCAL_SOURCE_FILE));	
				if(isSourceLocal){
					parametersDto.setSourceConfigurationName("local");
					parametersDto.setSourceFilefolder(FilePathUtil.getRelativeFileName(null,source_filefolder[i],FileType.input));
				}else{
					loadDataMap(parametersDto, jobentrycopyfiles.getConfigurationBy(source_filefolder[i]), source_filefolder[i], true);
				}
				
				boolean isDestLocal = ( jobentrycopyfiles.getConfigurationBy(destination_filefolder[i])!=null && jobentrycopyfiles.getConfigurationBy(destination_filefolder[i]).startsWith(JobEntryCopyFiles.LOCAL_DEST_FILE) );
				if(isDestLocal){
					parametersDto.setDestinationConfigurationName("local");
					parametersDto.setDestinationFilefolder(FilePathUtil.getRelativeFileName(null,destination_filefolder[i],FileType.input ));
				
				}else{
					loadDataMap(parametersDto,jobentrycopyfiles.getConfigurationBy(destination_filefolder[i]), destination_filefolder[i], false);
				}
				
				parametersList.add(parametersDto);
			}
			spCopyFiles.setFilefolder(parametersList);
		}

		return spCopyFiles;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPCopyFiles spCopyFiles = (SPCopyFiles) po;
		JobEntryCopyFiles jobentrycopyfiles = (JobEntryCopyFiles) entryMetaInterface;

		jobentrycopyfiles.setCopyEmptyFolders(spCopyFiles.isCopyEmptyFolders());
		jobentrycopyfiles.setIncludeSubfolders(spCopyFiles.isIncludeSubfolders());
		jobentrycopyfiles.setAddresultfilesname(spCopyFiles.isAddResultFilesname());
		jobentrycopyfiles.setArgFromPrevious(spCopyFiles.isArgFromPrevious());
		jobentrycopyfiles.setRemoveSourceFiles(spCopyFiles.isRemoveSourceFiles());
		jobentrycopyfiles.setDestinationIsAFile(spCopyFiles.isDestinationIsAfile());
		jobentrycopyfiles.setCreateDestinationFolder(spCopyFiles.isCreateDestinationFolder());
		jobentrycopyfiles.setoverwrite_files(spCopyFiles.isOverwriteFiles());

		if (spCopyFiles.getFilefolder() != null && spCopyFiles.getFilefolder().size() > 0) {
			String source_filefolder[] = new String[spCopyFiles.getFilefolder().size()];
			String destination_filefolder[] = new String[spCopyFiles.getFilefolder().size()];
			String wildcard[] = new String[spCopyFiles.getFilefolder().size()];
			HashMap<String, String> mappings = Maps.newHashMap();
			for (int i = 0; i < spCopyFiles.getFilefolder().size(); i++) {
				CopyFilessourceFilefolderDto jpd = spCopyFiles.getFilefolder().get(i);
				
				wildcard[i] = jpd.getWildcard();
				boolean isSourceLocal = ( jpd.getSourceConfigurationName()!=null &&jpd.getSourceConfigurationName().equals("local"));	
				if(isSourceLocal){
					source_filefolder[i] = FilePathUtil.getRealFileName(null,jpd.getSourceFilefolder(),FileType.input );//dataFilePathRoot+jpd.getSourceFilefolder();
					mappings.put(source_filefolder[i] ,JobEntryCopyFiles.LOCAL_SOURCE_FILE +i );
				}else{
					source_filefolder[i] = SaveDataMap(jpd.getSourceConfigurationName(), jpd.getSourceFilefolder(), mappings, i);
				}
				
				boolean isDestLocal = ( jpd.getDestinationConfigurationName()!=null &&jpd.getDestinationConfigurationName().equals("local") );
				if(isDestLocal){
					destination_filefolder[i] = FilePathUtil.getRealFileName(null,jpd.getDestinationFilefolder(),FileType.input);//dataFilePathRoot+jpd.getDestinationFilefolder();
					mappings.put(destination_filefolder[i] , JobEntryCopyFiles.LOCAL_DEST_FILE + i );
				}else{
					destination_filefolder[i] = SaveDataMap(jpd.getDestinationConfigurationName(), jpd.getDestinationFilefolder(),mappings,i);
				}
				
			}
			jobentrycopyfiles.setConfigurationMappings(mappings);
			
			jobentrycopyfiles.source_filefolder = source_filefolder;
			jobentrycopyfiles.destination_filefolder = destination_filefolder;
			jobentrycopyfiles.wildcard = wildcard;
		}

	}

	/**
	 * @param jpd
	 * @param mappings
	 * @param i
	 * @return
	 * @throws Exception 
	 */
	public String SaveDataMap(String configName,String path, HashMap<String, String> mappings, int i) throws Exception {
		mappings.put(path, JobEntryCopyFiles.STATIC_DEST_FILE + i );
		return  path;
	}

	/**
	 * 处理
	 * @param mappings
	 * @return
	 */
	public void loadDataMap(CopyFilessourceFilefolderDto parametersDto,String configName,String path,boolean isSource)  throws Exception {
		if(isSource){
			parametersDto.setSourceConfigurationName("static");
			parametersDto.setSourceFilefolder(path);
		}else{
			parametersDto.setDestinationFilefolder(path);
			parametersDto.setDestinationConfigurationName("static");
		}
		
	}

}

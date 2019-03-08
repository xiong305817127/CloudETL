package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.FileTransmitDto;

public class FileCopyDto  extends StepDto implements Serializable{

	private static final long serialVersionUID = -2378839650624473787L;

	public static  final String type ="HadoopCopyFilesPlugin";
	
	private List<FileTransmitDto> files ;
	
	private boolean copyEmptyFolders = true;//	复制空文件夹
	private boolean overwriteFiles	 = false;//文件存在则覆盖
	private boolean includeSubfolders = false;//	复制子文件夹
	private boolean removeSourceFiles = false;//	复制完成后删除源文件
	private boolean destinationIsAfile = true;//	目标是一个文件

	public void addFile(String sourceFile ,String destinationFile) {
		if( files ==  null ) {
			files = new ArrayList<FileTransmitDto>();
		}
		
		files.add(new FileTransmitDto(sourceFile, destinationFile));
	}
	
	public List<FileTransmitDto> getFiles() {
		return files;
	}

	public void setFiles(List<FileTransmitDto> files) {
		this.files = files;
	}

	public boolean isCopyEmptyFolders() {
		return copyEmptyFolders;
	}

	public void setCopyEmptyFolders(boolean copyEmptyFolders) {
		this.copyEmptyFolders = copyEmptyFolders;
	}

	public boolean isOverwriteFiles() {
		return overwriteFiles;
	}

	public void setOverwriteFiles(boolean overwriteFiles) {
		this.overwriteFiles = overwriteFiles;
	}

	public boolean isIncludeSubfolders() {
		return includeSubfolders;
	}

	public void setIncludeSubfolders(boolean includeSubfolders) {
		this.includeSubfolders = includeSubfolders;
	}

	public boolean isRemoveSourceFiles() {
		return removeSourceFiles;
	}

	public void setRemoveSourceFiles(boolean removeSourceFiles) {
		this.removeSourceFiles = removeSourceFiles;
	}

	public boolean isDestinationIsAfile() {
		return destinationIsAfile;
	}

	public void setDestinationIsAfile(boolean destinationIsAfile) {
		this.destinationIsAfile = destinationIsAfile;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isJobStep() {
		return true;
	}

	@Override
	public String toString() {
		return "FileCopyDto [files=" + files + ", copyEmptyFolders=" + copyEmptyFolders + ", overwriteFiles="
				+ overwriteFiles + ", includeSubfolders=" + includeSubfolders + ", removeSourceFiles="
				+ removeSourceFiles + ", destinationIsAfile=" + destinationIsAfile + ", super =" + super.toString()
				+ "]";
	}

}

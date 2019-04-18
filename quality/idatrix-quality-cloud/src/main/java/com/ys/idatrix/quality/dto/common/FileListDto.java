/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.common;

import java.util.List;

import org.pentaho.di.core.util.Utils;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件树Dto
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("文件节点信息")
public class FileListDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("文件名")
	private String fileName;
	
	@ApiModelProperty("文件路径")
	private String path;
	
	@ApiModelProperty("是否是文件夹")
	private boolean isFolder;
	
	@ApiModelProperty("是否可读")
	private boolean isRead;
	
	@ApiModelProperty("是否可写")
	private boolean isWrite;
	
	@ApiModelProperty("文件最后修改时间")
	private long lastModified;
	
    private List<FileListDto> children;
	
	public String getOwner() {
    	if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}	
    
	/**
	 * @return fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param  设置 fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * @return path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param  设置 path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return isFolder
	 */
	public boolean isFolder() {
		return isFolder;
	}

	/**
	 * @param  设置 isFolder
	 */
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	/**
	 * @return isRead
	 */
	public boolean isRead() {
		return isRead;
	}

	/**
	 * @param  设置 isRead
	 */
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	/**
	 * @return isWrite
	 */
	public boolean isWrite() {
		return isWrite;
	}

	/**
	 * @param  设置 isWrite
	 */
	public void setWrite(boolean isWrite) {
		this.isWrite = isWrite;
	}

	/**
	 * @return lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param  设置 lastModified
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return children
	 */
	public List<FileListDto> getChildren() {
		return children;
	}

	/**
	 * @param  设置 children
	 */
	public void setChildren(List<FileListDto> children) {
		this.children = children;
	}

	
	/**
	 * @param  设置 children
	 */
	public void addChild(FileListDto child) {
		if(children == null){
			children=Lists.newArrayList();
		}
		this.children.add(child);
	}

    
    
}

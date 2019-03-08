/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.subscribe.api.dto.parts;

import java.io.Serializable;

/**
 *
 * @author XH
 * @since 2017年6月29日
 *
 */
public class FileTransmitDto implements Serializable {

	private static final long serialVersionUID = 4584001444776466843L;
	
	private String sourceName = "hdfs"	; //源文件来源类型,可选 local，hdfs
	private String sourceFile; // 源文件/源文件夹
	private String fileMask = "*"; // 文件名正则匹配
	private String destinationName = "hdfs"; //	目标来源类型,可选 local，hdfs
	private String destinationFile	; //目标文件/目标文件夹
	
	
	public FileTransmitDto() {
		super();
	}
	
	public FileTransmitDto(String sourceFile, String destinationFile) {
		super();
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
	}
	public FileTransmitDto(String sourceName, String sourceFile, String fileMask, String destinationName,
			String destinationFile) {
		super();
		this.sourceName = sourceName;
		this.sourceFile = sourceFile;
		this.fileMask = fileMask;
		this.destinationName = destinationName;
		this.destinationFile = destinationFile;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getSourceFile() {
		return sourceFile;
	}
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	public String getFileMask() {
		return fileMask;
	}
	public void setFileMask(String fileMask) {
		this.fileMask = fileMask;
	}
	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public String getDestinationFile() {
		return destinationFile;
	}
	public void setDestinationFile(String destinationFile) {
		this.destinationFile = destinationFile;
	}

	@Override
	public String toString() {
		return "FileTransmitDto [sourceName=" + sourceName + ", sourceFile=" + sourceFile + ", fileMask=" + fileMask
				+ ", destinationName=" + destinationName + ", destinationFile=" + destinationFile + "]";
	}

}

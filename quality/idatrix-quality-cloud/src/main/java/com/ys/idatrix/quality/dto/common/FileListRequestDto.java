/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.common;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件树Dto
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("文件获取请求信息")
public class FileListRequestDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("文件类型")
	private String type;
	
	@ApiModelProperty("文件相对路径")
	private String path;
	
	@ApiModelProperty("文件夹获取深度")
	private int  depth;
	
	@ApiModelProperty("文件过滤类型")
	private String filterType ;
	
	/**
	 * 
	 */
	public FileListRequestDto() {
		super();
	}
	/**
	 * @param path
	 * @param depth
	 * @param type
	 */
	public FileListRequestDto(String owner , String path, int depth, String type) {
		super();
		this.owner = owner;
		this.path = path;
		this.depth = depth;
		this.type = type;
	}
	
	@org.codehaus.jackson.annotate.JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	public String getRealOwner() {
		return owner;
	}
	
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
	 * @return the path
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
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}
	/**
	 * @param  设置 depth
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param  设置 type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the filterType
	 */
	public String getFilterType() {
		return filterType;
	}
	/**
	 * @param  设置 filterType
	 */
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}
    
}

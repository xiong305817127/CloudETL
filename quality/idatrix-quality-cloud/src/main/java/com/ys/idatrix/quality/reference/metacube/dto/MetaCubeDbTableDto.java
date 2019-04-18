/**
 * 云化数据集成系统 
 * iDatrix quality
 */
package com.ys.idatrix.quality.reference.metacube.dto;

/**
 * DTO for DB tables transfer from MetaCube.
 * @author JW
 * @since 2017年6月16日
 *
 */
public class MetaCubeDbTableDto {

	private Long id;
	private String name;
	private String type;  //table or view
	
	public MetaCubeDbTableDto() {
		super();
	}

	public MetaCubeDbTableDto(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public MetaCubeDbTableDto(Long id, String name, String type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "MetaCubeDbTableDto [id=" + id + ", name=" + name + ", type=" + type + "]";
	}

}

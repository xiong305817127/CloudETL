package com.ys.idatrix.quality.analysis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 字典实体类
 */
@Table(catalog="idatrix.analysis.node.dict.tableName",name="tbl_nodeDict")
public class NodeDictDto {
	
    // 当前列的唯一标识
	@Id
    private String id;

    // 字典名称
    private String dictName;

    // 字典描述
    private String dictDesc;

    // 字典新增时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;

    // 最后的修改时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    // 当前字典是否生效
    private Long status ;//0：不生效，1：生效，2：待更新

    // 最后的生效时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date activeTime;
    
    //是否共享
    private Boolean  share ;
    
    //所属租户ID
    private String renterId;

    // 创建人
    private String creator;

    // 修改人
    private String modifier;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Date activeTime) {
        this.activeTime = activeTime;
    }

	public Boolean isShare() {
		return share;
	}

	public void setShare(Boolean share) {
		this.share = share;
	}

	public String getRenterId() {
		return renterId;
	}

	public void setRenterId(String renterId) {
		this.renterId = renterId;
	}

	public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getDictDesc() {
        return dictDesc;
    }

    public void setDictDesc(String dictDesc) {
        this.dictDesc = dictDesc;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
    
}
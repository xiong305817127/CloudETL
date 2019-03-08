package com.idatrix.resource.datareport.vo;

/**
 * Created by Robin Wing on 2018-6-19.
 */
public class ResourceFileVO {

    /*主键*/
    private Long id;

    /*资源ID*/
    private Long resourceId;

    /*发布出来的文件名*/
    private String pubFileName;

    /*数据批次，格式为yyyy-MM-dd*/
    private String dataBatch;

    private String updateTime;

    /*TODO：文件大小从哪里获取？*/
    private String fileSize;

    private String fileType;

    private String fileDescription;

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getPubFileName() {
        return pubFileName;
    }

    public void setPubFileName(String pubFileName) {
        this.pubFileName = pubFileName;
    }

    public String getDataBatch() {
        return dataBatch;
    }

    public void setDataBatch(String dataBatch) {
        this.dataBatch = dataBatch;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}

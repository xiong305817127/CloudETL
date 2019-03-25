package com.idatrix.resource.basedata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件VO
 *
 * @author wzl
 */
@ApiModel
public class FileVO {

    /**
     * 主键
     */
    @ApiModelProperty("文件id")
    private Long id;
    /**
     * 原始文件名
     */
    @ApiModelProperty("原始文件名")
    private String originFileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    public FileVO() {
    }

    public FileVO(Long id, String originFileName) {
        this.id = id;
        this.originFileName = originFileName;
    }
}

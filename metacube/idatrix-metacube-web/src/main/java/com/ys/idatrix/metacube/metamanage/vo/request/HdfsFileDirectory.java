package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: HdfsFileDirectory
 * @Description:
 * @author: zhoujian
 * @date: 2018/9/14
 */
@Data
@ApiModel(value = "HdfsFileDirectory", description = "HDFS查询路径")
public class HdfsFileDirectory {

    /**
     * 编号
     */
    @ApiModelProperty("路径metadId编号")
    private Integer id;

    /**
     * 目录名称，用来搜索过滤
     */
    @ApiModelProperty("目录名称，用来搜索过滤")
    private String dirSearchKey;


    /**
     * 完整路径名称
     */
    @ApiModelProperty("完整路径名称")
    private String allPathname;


    /**
     * 租户id
     */
    @ApiModelProperty("租户id")
    private String renterId;


}
package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 元数据定义HDFS定义
 * @author robin
 *
 */
@Data
@ApiModel("元数据定义配置参数")
public class MetaDefHDFSVO extends MetadataBaseVO {


    /*名称：HDFS-目录名称*/
    @ApiModelProperty("名称：HDFS-根目录名称")
    private String rootPath;

//    /*名称：HDFS-目录名称*/
//    @ApiModelProperty("名称：HDFS-名称")
//    private String name;
//
//    /*内容描述：HDFS-存储路径*/
//    @ApiModelProperty("内容描述：HDFS-子目录路径")
//    private String  identification;
}

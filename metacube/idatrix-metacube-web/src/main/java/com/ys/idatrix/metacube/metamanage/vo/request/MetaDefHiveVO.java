package com.ys.idatrix.metacube.metamanage.vo.request;

import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 元数据定义-HIVE
 * @author robin
 */
@Data
@ApiModel("元数据定义-HIVE配置")
public class MetaDefHiveVO extends MetadataBaseVO {

    /*HIVE数据库名称*/
    @ApiModelProperty("HIVE数据库名称")
    private String databaseName;

//    /*名称：HDFS-目录名称*/
//    @ApiModelProperty("名称：Hive表名")
//    private String name;
//
//    /*内容描述：HDFS-存储路径*/
//    @ApiModelProperty("内容描述：Hive表中文名")
//    private String  identification;

    /**
     * 是否外表
     */
    @ApiModelProperty("是否外表")
    private Boolean isExternalTable;

    /**
     * hdfs路径
     */
    @ApiModelProperty("hdfs路径")
    private String location;

    /**
     * 每列之间的分隔符
     */
    @ApiModelProperty("每列之间的分隔符")
    private String fieldsTerminated;

    /**
     * 每行之间的分隔符
     */
    @ApiModelProperty("每行之间的分隔符")
    private String linesTerminated;

    /**
     * 空值处理
     */
    @ApiModelProperty("空值处理")
    private String nullDefined;

    /**
     * 存储格式，TEXTFILE,SEQUENCEFILE,PARQUET,AVRO
     */
    @ApiModelProperty("存储格式，TEXTFILE,SEQUENCEFILE,PARQUET,AVRO")
    private String storeFormat;


    @ApiModelProperty("字段和分区字段都定义在该位置，" +
            "分区时需要：isPartition、indexPartition分别表示是否分区字段、分区序号")
    private List<TableColumn> columnList;

}

package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName MetadataSearchVo
 * @Description 元数据vo
 * @Author ouyang
 * @Date 2019/1/16
 */
@ApiModel(value = "MetadataSearchVo", description = "元数据搜索实体类")
@Data
public class MetadataSearchVo extends SearchVO {

    @ApiModelProperty("模式id")
    private Long schemaId;

    @ApiModelProperty("数据库类型,1.mysql,2.oracle,3.dm,4.postgreSQL,5.hive,6.base,7.hdfs,8.ElasticSearch")
    private Integer databaseType;

    @ApiModelProperty("不同数据库下分辨不同资源 如：db 1:表 2:视图 3:存储过程")
    private Integer resourceType;

    @ApiModelProperty("元数据状态，0,草稿，1,有效，2,删除")
    private Integer status;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("所属组织代码")
    private String regCode;

}

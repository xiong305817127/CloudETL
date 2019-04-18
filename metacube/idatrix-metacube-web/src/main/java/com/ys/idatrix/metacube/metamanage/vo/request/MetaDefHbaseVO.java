package com.ys.idatrix.metacube.metamanage.vo.request;

import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 元数据定义-Hbase
 * @author robin
 *
 */

@Data
@ApiModel("元数据定义-Hbase配置")
public class MetaDefHbaseVO extends MetadataBaseVO  {


    /**
     * 数据库名称（oracle/dm 数据库实例名）
     */
    @ApiModelProperty("HBase数据库实际名称")
    private String databaseName;

   @ApiModelProperty("Hbase有column family概念，存储在字段description里面")
   private List<TableColumn> columnList;
}

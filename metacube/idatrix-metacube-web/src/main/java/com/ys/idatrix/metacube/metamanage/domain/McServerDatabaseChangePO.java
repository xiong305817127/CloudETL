package com.ys.idatrix.metacube.metamanage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 服务器、数据库变更历史
 *
 * @author wzl
 */
@Data
@Accessors(chain = true)
@ApiModel
public class McServerDatabaseChangePO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 变更类型 1 服务器 2 数据库 ...
     */
    @ApiModelProperty("变更类型 1 服务器 2 数据库")
    private Integer type;
    /**
     * 逻辑外键 服务器id、数据库id ...
     */
    @ApiModelProperty("服务器id/数据库id")
    private Long fkId;
    /**
     * 变更时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("变更时间")
    private Date createTime;
    /**
     * 操作人
     */
    @ApiModelProperty("操作人")
    private String operator;
    /**
     * 变更内容
     */
    @ApiModelProperty("变更内容")
    private String content;
}

package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 变更记录查询VO
 *
 * @author wzl
 */
@Data
@ApiModel
public class ChangeSearchVO extends SearchVO {

    /**
     * 变更类型 1 服务器 2 数据库 ...
     */
    @ApiModelProperty(hidden = true)
    private Integer type;
    /**
     * 逻辑外键 服务器id、数据库id ...
     */
    @ApiModelProperty(hidden = true)
    private Long fkId;
}

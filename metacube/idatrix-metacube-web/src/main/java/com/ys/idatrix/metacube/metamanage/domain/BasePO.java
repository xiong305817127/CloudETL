package com.ys.idatrix.metacube.metamanage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 基础PO 封装了一些通用字段
 *
 * @author wzl
 */
@Data
@Accessors(chain = true)
@ApiModel
public class BasePO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("创建人")
    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("修改时间")
    private Date modifyTime;

    @ApiModelProperty(value = "逻辑删除字段 0未删除 1已删除", hidden = true)
    @JsonIgnore
    private Integer isDeleted;

    @ApiModelProperty(value = "租户id", hidden = true)
    @JsonIgnore
    private Long renterId;

    /**
     * 创建信息
     */
    public <T extends BasePO> T fillCreateInfo(T basePO, String username) {
        basePO.setCreator(username);
        basePO.setCreateTime(new Date());
        return fillModifyInfo(basePO, username);
    }

    /**
     * 修改信息
     */
    public <T extends BasePO> T fillModifyInfo(T basePO, String username) {
        basePO.setModifier(username);
        basePO.setModifyTime(new Date());
        return basePO;
    }
}

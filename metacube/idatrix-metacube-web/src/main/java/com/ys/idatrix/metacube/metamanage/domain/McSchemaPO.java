package com.ys.idatrix.metacube.metamanage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Schema实体（数据库实例）
 *
 * @author wzl
 */
@Data
@Accessors(chain = true)
@ApiModel
public class McSchemaPO extends BasePO {

    @ApiModelProperty("数据库id")
    private Long dbId;

    @ApiModelProperty(value = "数据库类型", hidden = true)
    @JsonIgnore
    private transient Integer dbType;

    @ApiModelProperty("服务名称 oracle实例名")
    private String serviceName;

    @ApiModelProperty("模式名称、es索引名称、hdfs目录")
    private String name;

    @ApiModelProperty("模式中文名称、hdfs目录中文名")
    private String nameCn;

    @ApiModelProperty("数据库账号")
    private String username;

    @ApiModelProperty("数据库密码")
    private String password;

    @ApiModelProperty("组织编码 多个以英文逗号分隔")
    private String orgCode;

    @ApiModelProperty("组织名称 多个以英文逗号分隔")
    private String orgName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("类型 1新建 2注册")
    private Integer type;

    @ApiModelProperty("状态 0正常 1禁用")
    private Integer status;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        McSchemaPO schemaPO = (McSchemaPO) o;
        return Objects.equals(dbId, schemaPO.dbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dbId);
    }
}

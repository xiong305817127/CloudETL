package com.ys.idatrix.metacube.metamanage.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "Metadata", description = "元数据实体类，也是主表")
@Data
public class Metadata {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("hdf目录 or es索引名称 or db实体表名")
    private String name;

    @ApiModelProperty("hdf子目录 or es描述 or db中文表名")
    private String identification;

    @ApiModelProperty("公开状态：0:不公开 1:授权访问")
    private Integer publicStatus;

    @ApiModelProperty("主题id")
    private Long themeId;

    @ApiModelProperty("标签，可能多个，以，隔开")
    private String tags;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("当前版本号，递增")
    private Integer version;

    @ApiModelProperty("租户id")
    private Long renterId;

    @ApiModelProperty("模型id")
    private Long schemaId;

    @ApiModelProperty("数据库类型:1.mysql,2.oracle,3.dm,4.postgreSQL,5.hive,6.base,7.hdfs,8.ElasticSearch")
    private Integer databaseType;

    @ApiModelProperty("不同数据库下分辨不同资源 如：db 1:表 2:视图")
    private Integer resourceType;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("修改时间")
    private Date modifyTime;

    @ApiModelProperty("是否为采集数据")
    private Boolean isGather;

    @ApiModelProperty("当前状态：0草稿 1生效 2删除")
    private Integer status;

    @ApiModelProperty("部门编码，从schema中获取")
    private String deptCodes;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Metadata other = (Metadata) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (themeId == null) {
            if (other.themeId != null) {
                return false;
            }
        } else if (!themeId.equals(other.themeId)) {
            return false;
        }
        if (tags == null) {
            if (other.tags != null) {
                return false;
            }
        } else if (!tags.equals(other.tags)) {
            return false;
        }
        if (remark == null) {
            if (other.remark != null) {
                return false;
            }
        } else if (!remark.equals(other.remark)) {
            return false;
        }
        if (publicStatus == null) {
            if (other.publicStatus != null) {
                return false;
            }
        } else if (!publicStatus.equals(other.publicStatus)) {
            return false;
        }
        return true;
    }

}
package com.ys.idatrix.metacube.metamanage.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName ApprovalProcessVO
 * @Description
 * @Author ouyang
 * @Date
 */
@ApiModel(value = "ApprovalProcessVO", description = "权限申请VO")
@Data
public class ApprovalProcessVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("申请人")
    private String creator;

    @ApiModelProperty("租户ID")
    private Long renterId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("申请时间")
    private Date createTime;

    // 查询时使用（当前申请人所属组织编码）
    @JsonIgnore
    private String deptCode;

    @ApiModelProperty("申请的资源id")
    private Long resourceId;

    @ApiModelProperty("申请人所属组织名")
    private String deptName;

    @ApiModelProperty("申请原因")
    private String cause;

    @ApiModelProperty("申请状态 1-申请中 2-通过 3-不通过 4-已回收 5-已撤回 6-删除")
    private Integer status;

    @ApiModelProperty("审批人")
    private String approver;

    @ApiModelProperty("审批意见")
    private String opinion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("最后修改时间")
    private Date modifyTime;


    @ApiModelProperty("服务器ID")
    private Long serverId;

    @ApiModelProperty("服务器名")
    private String serverName;

    @ApiModelProperty("服务器IP")
    private String ip;

    @ApiModelProperty("主机名称")
    private String hostname;


    @ApiModelProperty("数据库ID")
    private Long databaseId;

    @ApiModelProperty("数据库名")
    private String databaseName;

    @ApiModelProperty("数据库中文名")
    private String databaseNameCn;

    @ApiModelProperty("数据库归属 1:ODS-操作数据存储 2:DW-数据仓库 3:DM-数据集市")
    private Integer belong;

    @ApiModelProperty("数据库类型 1:MYSQL 2:ORACLE 3:DM 4:POSTGRESQL 5:HIVE 6:HBASE 7:HDFS 8:ELASTICSEARCH ")
    private Integer databaseType;


    @ApiModelProperty("模式ID")
    private Long schemaId;

    // 查询时使用（当前资源所属组织编码，可能有多个）
    @JsonIgnore
    private String schemaDeptCodes;

    @ApiModelProperty("当前模式所属组织名")
    private String schemaDeptNames;

    @ApiModelProperty("资源类型 1:表 2:视图 只有数据库类型为 mysql 或 oracle 时才会使用")
    private Integer resourceType;

    @ApiModelProperty("资源名")
    private String resourceName;

    @ApiModelProperty("中文资源名")
    private String resourceNameCn;

    @ApiModelProperty("当前申请的资源权限")
    private List<ResourceAuthVO> resourceAuthVOList;
}
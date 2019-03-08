package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(value = "ApprovalProcess", description = "元数据权限申请实体类")
@Data
public class ApprovalProcess {

    @NotNull(message = "ID不能为空", groups = Update.class)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("申请人")
    private String creator;

    @ApiModelProperty("租户ID")
    private Long renterId;

    @ApiModelProperty("申请时间")
    private Date createTime;

    @ApiModelProperty("申请组织id")
    private String deptCode;

    @NotBlank(message = "信息不能为空", groups = Save.class)
    @ApiModelProperty("申请原因")
    private String cause;

    @NotNull(message = "申请的资源id不能为空", groups = Save.class)
    @ApiModelProperty("申请的资源id")
    private Long resourceId;

    @NotNull(message = "资源类型不能为空", groups = Save.class)
    @ApiModelProperty("资源类型")
    private Integer resourceType;

    @NotNull(message = "当前申请权限值不能为空", groups = Save.class)
    @ApiModelProperty("当前申请的权限，二进制表示")
    private Integer authValue;

    @ApiModelProperty("申请状态 1-申请中 2-通过 3-不通过 4-已回收 5-已撤回 6-删除")
    private Integer status;

    @ApiModelProperty("审批人")
    private String approver;

    @ApiModelProperty("审批意见")
    private String opinion;

    @ApiModelProperty("修改时间")
    private Date modifyTime;
}
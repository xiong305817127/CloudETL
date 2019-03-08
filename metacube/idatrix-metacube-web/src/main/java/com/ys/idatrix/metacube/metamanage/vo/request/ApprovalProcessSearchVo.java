package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName ApprovalProcessSearchVo
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class ApprovalProcessSearchVo extends SearchVO {

    @ApiModelProperty("申请状态 1-申请中 2-通过 3-不通过 4-已回收 5-已撤回 6-删除")
    private List<Integer> status;

    @ApiModelProperty("申请人")
    private String creator;

    @ApiModelProperty("租户ID")
    private Long renterId;

    @ApiModelProperty("申请时间")
    private Date createTime;

    @ApiModelProperty("用户归属组织编码")
    private String ascriptionDeptCode;

    @ApiModelProperty("资源所属组织编码，可能有多个")
    private List<String> resourceOrganisationCodes;

    @ApiModelProperty("资源数据库类型")
    private Integer databaseType;
}
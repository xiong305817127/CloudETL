package com.ys.idatrix.metacube.metamanage.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 组织类
 *
 * @author wzl
 */
@ApiModel
@Data
public class MetaDataOrganization {

    @ApiModelProperty("组织id")
    private Long id;

    @ApiModelProperty("租户id")
    private Long renterId;

    @ApiModelProperty("租户名称")
    private String renterName;

    @ApiModelProperty("所属组织id")
    private Long ascriptionDeptId;

    @ApiModelProperty("组织编码")
    private String deptCode;

    @ApiModelProperty("组织名称")
    private String deptName;

    @ApiModelProperty("统一信用代码")
    private String unifiedCreditCode;

    public MetaDataOrganization() {
    }

    public MetaDataOrganization(Long id, Long renterId, String renterName,
            Long ascriptionDeptId, String deptCode, String deptName, String unifiedCreditCode) {
        this.id = id;
        this.renterId = renterId;
        this.renterName = renterName;
        this.ascriptionDeptId = ascriptionDeptId;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.unifiedCreditCode = unifiedCreditCode;
    }
}

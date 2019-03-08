package com.ys.idatrix.metacube.metamanage.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
@ApiModel
public class ServerVO {

    @ApiModelProperty("服务器id")
    private Long id;

    @ApiModelProperty("服务器名称")
    private String name;

    @ApiModelProperty("服务器用途：1前置库 2平台库 3平台库-Hadoop")
    private Integer use;

    @ApiModelProperty("服务器ip")
    private String ip;

    @ApiModelProperty("服务器主机名")
    private String hostname;

    @ApiModelProperty("所属组织 组织编码")
    private String orgCode;

    @ApiModelProperty("位置信息")
    private String location;

    @ApiModelProperty("联系人")
    private String contact;

    @ApiModelProperty("联系人电话")
    private String contactNumber;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    @ApiModelProperty("租户id")
    private Long renterId;

    @ApiModelProperty("数据库列表")
    private List<DatabaseVO> databaseList;
}

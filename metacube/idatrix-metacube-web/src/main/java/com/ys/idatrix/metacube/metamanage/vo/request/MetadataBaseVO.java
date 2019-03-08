package com.ys.idatrix.metacube.metamanage.vo.request;

import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName MetadataBaseVO
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class MetadataBaseVO {

    @NotNull(message = "id不能为空", groups = Update.class)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("内容描述：Hive、HBase表中文名/HDFS-子目录路径")
    private String identification;

    @NotNull(message = "公开状态不能为空", groups = Save.class)
    @ApiModelProperty("公开状态：0:不公开 1:授权访问")
    private Integer publicStatus = 0;

    @NotNull(message = "主题不能为空")
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

    @NotNull(message = "模式不能为空", groups = Save.class)
    @ApiModelProperty("模式id")
    private Long schemaId;

    @ApiModelProperty("数据库类型")
    private Integer databaseType;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("修改时间")
    private Date modifyTime;

    @ApiModelProperty("状态：0草稿 1生效 2删除")
    private Integer status;

    @ApiModelProperty("是否为采集数据")
    private Boolean isGather;

    @ApiModelProperty("部门编码，可能是多个")
    private String deptCodes;

    //========================= 冗余页面显示 字段 =========================//

    @ApiModelProperty("模式名称。ES索引名称、HDFS根目录")
    private String schemaName;

    @ApiModelProperty("所属组织名称，可能多个")
    private String deptNames;

    @ApiModelProperty("主题名称")
    private String themeName;

}

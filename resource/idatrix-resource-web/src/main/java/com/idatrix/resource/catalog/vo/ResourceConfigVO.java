package com.idatrix.resource.catalog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by Robin Wing on 2018-5-23.
 */
@Data
@ApiModel("资源信息配置参数")
public class ResourceConfigVO {

    /*资源ID（自动增加）*/
    @ApiModelProperty("资源ID，新增时候不需要配置，修改需要有ID")
    private Long id;

    /*资源树形结构保存 整个树形分支*/
    @ApiModelProperty("资源树形结构保存 整个树形分支")
    private Long[] catalogIdArray;

    /*资源分类名称*/
    @ApiModelProperty("资源分类名称")
    private String catalogName;

    /*信息资源分类编码(来自信息资源分类表)*/
    @ApiModelProperty("信息资源分类编码(来自信息资源分类表)")
    private String catalogCode;

    /*资源顺序号*/
    @ApiModelProperty("资源顺序号")
    private String seqNum;

    /*资源编码，由catalog_code +”/”+ seq_num，自动生成*/
    @ApiModelProperty("资源编码，由catalog_code +”/”+ seq_num，自动生成")
    private String code;

    /*资源名称*/
    @ApiModelProperty("资源名称")
    private String name;

    /*资源摘要*/
    @ApiModelProperty("资源摘要")
    private String remark;

    /*信息资源提供方名称*/
    @ApiModelProperty("信息资源提供方名称")
    private String deptName;

    /*信息资源提供方名称id列表*/
    @ApiModelProperty("信息资源提供方名称id列表")
    private String deptNameIdArray;

    /*信息资源提供方代码*/
    @ApiModelProperty("信息资源提供方代码")
    private String deptCode;

    /*资源摘要*/
    @ApiModelProperty("资源摘要")
    private String resourceAbstract;

    /*关键字,将来扩展功能*/
    @ApiModelProperty("关键字,将来扩展功能")
    private String keyword;

    /*资源格式大类:1电子文件，2电子表格，3数据库，4图形图像，5流媒体，6自描述格式，7服务接口*/
    @ApiModelProperty("资源格式大类:1电子文件，2电子表格，3数据库，4图形图像，5流媒体，6自描述格式，7服务接口")
    private int formatType;

    /*资源格式信息（mysql,oracle,db2,sqlserver，文件，pdf,xls等）*/
    @ApiModelProperty("资源格式信息（mysql,oracle,db2,sqlserver，文件，pdf,xls等）")
    private String formatInfo;

    /*格式补充说明*/
    @ApiModelProperty("格式补充说明")
    private String formatInfoExtend;

    /*共享类型（无条件共享、有条件共享、不予共享三类。值域范围对应共享类型排序分别为1、2、3。）*/
    @ApiModelProperty("共享类型（无条件共享、有条件共享、不予共享三类。值域范围对应共享类型排序分别为1、2、3。）")
    private int shareType;

    /*共享条件，当有条件共享时填写*/
    @ApiModelProperty("共享条件，当有条件共享时填写")
    private String shareCondition;

    /*共享方式：1数据库，2文件下载，3webservice服务*/
    @ApiModelProperty("共享方式：1数据库，2文件下载，3webservice服务")
    private int shareMethod;

    /*共享部门：将对于数据信息存储在 rc_dept_limited 数据表中*/
    @ApiModelProperty("将对于数据信息存储在 rc_dept_limited 数据表中")
    private Long[] shareDeptArray;

    /*是否向社会开放，1是，0否*/
    @ApiModelProperty("是否向社会开放，1是，0否")
    private int openType;

    /*对向社会开放资源的条件描述。当“是向社会开放”取值为 1 时，描述开放条件*/
    @ApiModelProperty("对向社会开放资源的条件描述。当“是向社会开放”取值为 1 时，描述开放条件")
    private String openCondition;

    /*更新周期（信息资源更新的频度。分为实时1、每日2、每周3、每月4、每季度5、每年6等。）*/
    @ApiModelProperty("更新周期（信息资源更新的频度。分为实时1、每日2、每周3、每月4、每季度5、每年6等。）")
    private int refreshCycle;

    /*政务信息资源提供方发布共享、开放政务信息资源的日期CCYY-MM-DD*/
    @ApiModelProperty("政务信息资源提供方发布共享、开放政务信息资源的日期yyyy-MM-dd")
    private Date pubDate;

    /*关联资源代码*/
    @ApiModelProperty("关联资源代码")
    private String relationCode;

    /*绑定table的唯一标识id，仅当资源类型为数据库时填写*/
    @ApiModelProperty("绑定table的唯一标识id，仅当资源类型为数据库时填写")
    private Long bindTableId;

    /*涉及数据类型为数据库时候，需要保存库名ID和表名ID*/
    @ApiModelProperty("涉及数据类型为数据库时候，需要保存库名ID和表名ID")
    private String libTableId;

    @ApiModelProperty("资源信息细项")
    private List<ResourceColumnVO> resourceColumnVOList;

    /*绑定服务id，rc_service表主键，仅当资源类型为服务时填写*/
    @ApiModelProperty("定服务id，rc_service表主键，仅当资源类型为服务时填写")
    private Long bindServiceId;

    /*状态(0.草稿 1.退回修改 2.注册审核拒绝 3.待注册审核 4.已注册 5.待发布审核 6.发布审核拒绝 7.取消发布 8.发布) */
    @ApiModelProperty("状态(0.草稿 1.退回修改 2.注册审核拒绝 3.待注册审核 4.已注册 5.待发布审核 6.发布审核拒绝 7.取消发布 8.发布)")
    private String status;

    /*资源录入者*/
    @ApiModelProperty(value = "资源录入者", hidden = true)
    private String creator;


    /*订阅权限标志：0表示没有订阅权限，1表示可以订阅，2，表示已经订阅*/
    @ApiModelProperty("订阅权限标志：0表示没有订阅权限，1表示可以订阅，2，表示已经订阅")
    private int subscribeFlag;


}

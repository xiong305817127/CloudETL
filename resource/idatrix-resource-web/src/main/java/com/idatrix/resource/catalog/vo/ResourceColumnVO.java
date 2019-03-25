package com.idatrix.resource.catalog.vo;

import com.idatrix.resource.catalog.po.ResourceColumnPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Robin Wing on 2018-5-23.
 */

@Data
@ApiModel("订阅信息详情")
public class ResourceColumnVO {

    /*主键*/
    @ApiModelProperty("rc_subscribe_dbio主键")
    private Long id;

    /*rc_resource_column主键*/
    @ApiModelProperty("rc_resource_column主键")
    private Long columnId;

    /*引用资源标识符id*/
    @ApiModelProperty("引用资源标识符id")
    private Long resourceId;

    /*列名称*/
    @ApiModelProperty("列名称")
    private String colName;

    /*数据类型：字符型 C、数值型 N、货币型 Y、日期型 D、日期时间型 T、
    逻辑型 L、备注型 M、通用型 G、双精度型 B、整型 I、浮点型 F*/
    private String colType;

    /*细项顺序码：001-999*/
    private String colSeqNum;

    /*绑定数据库表的列名*/
    private String tableColCode;

    /*绑定数据库表的列类型*/
    @ApiModelProperty("绑定数据库表的列类型")
    private String tableColType;

    /*交换时是否必选：0否，1是*/
    @ApiModelProperty("交换时是否必选：0否，1是")
    private Boolean requiredFlag;

    /*是否主键: 0否，1是*/
    @ApiModelProperty("是否主键: 0否，1是")
    private Boolean uniqueFlag;

    /*日期类型格式*/
    @ApiModelProperty("日期类型格式")
    private String dateFormat;

    /*是否能够进行脱敏处理*/
    @ApiModelProperty("是否能够进行脱敏处理标志，大于0表示能够进行脱敏处理")
    private int dataMaskingFlag;

    /*数据脱敏方式:(mask/cut)掩码/截取(唯一标识符不能够脱敏、
    只有字符串数据才能脱敏只有param_type是input类型才有效)*/
    @ApiModelProperty("数据脱敏方式:(mask/truncate)掩码/截取(唯一标识符不能够脱敏、\n" +
            "    只有字符串数据才能脱敏只有param_type是input类型才有效)")
    private String dataMaskingType;

    @ApiModelProperty("数据脱敏开始处理位，默认为0")
    private int dataStartIndex;

    @ApiModelProperty("数据脱敏处理长度，默认为1")
    private int dataLength;

    public ResourceColumnVO(){super();}


    public ResourceColumnVO(ResourceColumnPO rcPO){
        this.id = rcPO.getId();
        this.colSeqNum = rcPO.getColSeqNum();
        this.colName = rcPO.getColName();
        this.colType = rcPO.getColType();
        this.tableColCode = rcPO.getTableColCode();
        this.tableColType = rcPO.getTableColType();
        this.requiredFlag = rcPO.getRequiredFlag();
        this.uniqueFlag = rcPO.getUniqueFlag();
        this.dateFormat = rcPO.getDateFormat();
    }


}

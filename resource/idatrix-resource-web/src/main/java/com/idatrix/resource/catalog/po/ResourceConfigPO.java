package com.idatrix.resource.catalog.po;

import lombok.Data;

import java.util.Date;

/**
 * 政务信息资源表
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
@Data
public class ResourceConfigPO
{
    /*资源ID（自动增加）*/
    private Long id;

    /*记录目录树里面，最后一个目录节点ID，方便目录树删除节点处理*/
/*    private Long lastCatalogId;*/
    private String catalogFullName;

    /*信息资源分类(来自信息资源分类表)*/
    private String catalogCode;

    /*资源顺序号*/
    private String seqNum;

    /*资源编码，由catalog_code +”/”+ seq_num，自动生成*/
    private String code;

    /*资源名称*/
    private String name;

    /*资源摘要*/
    private String remark;

    /*信息资源提供方名称*/
    private String deptName;

    /*信息资源提供方代码*/
    private String deptCode;

    /*资源提供方ID 字符串*/
    private String deptNameIds;

    /*资源摘要*/
    private String resourceAbstract;

    /*关键字,将来扩展功能*/
    private String keyword;

    /*资源格式大类:1电子文件，2电子表格，3数据库，4图形图像，5流媒体，6自描述格式，7服务接口*/
    private int formatType;

    /*资源格式信息（mysql,oracle,db2,sqlserver，文件，pdf,xls等）*/
    private String formatInfo;

    /*格式补充说明*/
    private String formatInfoExtend;

    /*共享类型（无条件共享、有条件共享、不予共享三类。值域范围对应共享类型排序分别为1、2、3。）*/
    private int shareType;

    /*共享条件，当有条件共享时填写*/
    private String shareCondition;

    /*共享方式：1数据库，2文件下载，3webservice服务*/
    private int shareMethod;

    /*是否向社会开放，1是，0否*/
    private int openType;

    /*对向社会开放资源的条件描述。当“是否向社会开放”取值为 1 时，描述开放条件*/
    private String openCondition;

    /*更新周期（信息资源更新的频度。分为实时1、每日2、每周3、每月4、每季度5、每半年6，每年7等。）*/
    private int refreshCycle;

    /*政务信息资源提供方发布共享、开放政务信息资源的日期CCYY-MM-DD*/
    private Date pubDate;

    /*关联资源代码*/
    private String relationCode;

    /*绑定table的唯一标识id，仅当资源类型为数据库时填写*/
    private Long bindTableId;

    /*绑定服务id，rc_service表主键，仅当资源类型为服务时填写*/
    private Long bindServiceId;

    /*涉及数据类型为数据库时候，需要保存库名ID和表名ID*/
    private String libTableId;

    /*状态(0.草稿 1.退回修改2.注册审核拒绝10.待注册审核19.已注册20.待发布审核22.发布审核拒绝23.取消发布29.发布) */
    private String status;

    /*租户ID，用户租户隔离*/
    private Long rentId;

    private Date createTime;

    private String creator;

    private Date updateTime;

    private String updater;


}

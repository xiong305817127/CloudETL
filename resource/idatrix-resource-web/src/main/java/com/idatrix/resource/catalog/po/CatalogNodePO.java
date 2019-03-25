package com.idatrix.resource.catalog.po;

import lombok.Data;

import java.util.Date;

/**
 * 政务信息资源分类表
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
@Data
public class CatalogNodePO {

    /*目录ID使用 32位*/
    private Long id;

    /*父节点 id*/
    private Long parentId;

    /*父节点ID 全称*/
    private String parentFullCode;

    /*节点资源名称*/
    private String resourceName;

    /*节点资源编码，为不同长度数据，类、项目、目、细目：长度数字分别长度为：1位、2位、3位、不定长度*/
    private String resourceEncode;

    /*节点所在层级深度，分为类，项目，目，细目*/
    private int dept;

    /*租户ID，实现组合隔离*/
    private Long rentId;

    /*目录分类节点资源个数统计*/
    private Long resourceCount;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public CatalogNodePO(){
        super();
    }

    public CatalogNodePO(String name, String code, Long rentId, String user){
        this.parentId = 0L;
        this.parentFullCode = "0";
        this.resourceName = name;
        this.resourceEncode = code;
        this.dept = 1;
        this.rentId = rentId;
        this.creator = user;
        this.modifier = user;
        this.createTime = new Date();
        this.modifyTime = new Date();
    }
}

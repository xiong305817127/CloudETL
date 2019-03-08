package com.ys.idatrix.metacube.metamanage.domain;

import lombok.Data;

import java.util.Date;

/**
 * mc_md_hive_field
 * @author 
 */
@Data
public class McMdHiveFieldPO {
    /**
     * 主键，同mc_metadata主键
     */
    private Long id;

    /**
     * 是否外表
     */
    private Boolean isExternalTable;

    /**
     * hdfs路径
     */
    private String location;

    /**
     * 每列之间的分隔符
     */
    private String fieldsTerminated;

    /**
     * 每行之间的分隔符
     */
    private String linesTerminated;

    /**
     * 空值处理
     */
    private String nullDefined;

    /**
     * 存储格式，TEXTFILE,SEQUENCEFILE,PARQUET,AVRO
     */
    private String storeFormat;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifyTime;

    public McMdHiveFieldPO(){
        super();
    }

    public McMdHiveFieldPO(String user){
        super();
        this.creator = user;
        this.modifier = user;
        this.createTime = new Date();
        this.modifyTime = new Date();
    }

}
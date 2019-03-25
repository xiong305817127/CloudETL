package com.ys.idatrix.metacube.api.beans.dataswap;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetadataField
 * @Description:元数据列信息
 * @Author: ZhouJian
 * @Date: 2018/8/7
 */
@Data
public class MetadataField implements Serializable {

    private static final long serialVersionUID = 5711507193010255711L;

    /**
     * 列名称
     */
    private String colName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 长度
     */
    private String length;

    /**
     * 精度
     */
    private String precision;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否主键（"0"/"1"）
     */
    private String IsPk;

    /**
     * 是否为空（"0"/"1"）
     */
    private String IsNull;

    /**
     * 外键。元数据表主键标识ID
     * <p>
     * 不用调用方传入。程序自动设置。
     */
    private int metaid;

    //=================== 后面如下属性现在元数据操作页面没有，可以不用传 ===============================//

    /**
     * 版本
     */
    private int versionid = 0;

    /**
     * 状态
     */
    private int status = 1;

    /**
     * 是否维度（"0"/"1"）
     */
    private String IsDemension = "1";

    /**
     * 是否度量（"0"/"1"）
     */
    private String IsMetric = "1";

    /**
     * 引用的数据标准
     */
    private int standard = 0;

    /**
     * 元数据表列族
     */
    private String colFamily;

    /**
     * 列代码
     */
    private String colCode;

    /**
     * 安全属性
     */
    private String secAttribute;

    /**
     * 属性排序索引，从0开始
     * 索引方法
     */
    private String indexId;

    /**
     * 索引类型
     */
    private String indexType;

    /**
     * 序列
     */
    private String sequence;

    /**
     * 分桶
     */
    private String bucketing;

    public MetadataField(){
        super();
    }

    public MetadataField(String colName, String type){
        this.colName = colName;
        this.dataType = type;
    }

}

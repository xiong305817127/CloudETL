package com.ys.idatrix.metacube.api.beans;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: MetaHdfsDTO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/29
 */
@Data
public class MetaHdfsDTO implements Serializable {

    private static final long serialVersionUID = 4467552736132840643L;

    /**
     * 元数据标识编号
     */
    private Integer id;

    /**
     * 父路径编号
     */
    private Integer parentId;

    /**
     * hdfs完整路径
     */
    private String path;

    /**
     * hdfs路径描述
     */
    private String desc;

    /**
     * 部门
     */
    private String department;

    /**
     * 组织机构
     */
    private String organization;

    /**
     *创建时间 
     */
    private Date createtime;

}

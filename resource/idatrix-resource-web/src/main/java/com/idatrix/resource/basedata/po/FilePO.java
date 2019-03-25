package com.idatrix.resource.basedata.po;

import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件实体对象
 *
 * @author wzl
 */
@Data
@Accessors(chain = true)
public class FilePO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 附件来源 暂定 1服务说明 2数据上报
     */
    private Integer source;
    /**
     * source=1时，为服务id
     */
    private Long parentId;
    /**
     * 实际存储的文件名
     */
    private String storageFileName;
    /**
     * 原始文件名
     */
    private String originFileName;
    /**
     * 文件大小
     */
    private Long fileSize;
    /**
     * 文件扩展名
     */
    private String fileExtension;
    /**
     * 文件描述
     */
    private String fileDescription;
    /**
     * 创建人
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
    /**
     * 逻辑删除字段 1 表示删除， 0 表示未删除
     */
    private Integer isDeleted;
}

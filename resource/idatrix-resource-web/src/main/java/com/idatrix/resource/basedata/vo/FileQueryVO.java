package com.idatrix.resource.basedata.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件查询VO
 *
 * @author wzl
 */
@Data
@Accessors(chain = true)
public class FileQueryVO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 文件来源 暂定 1服务说明 2数据上报
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
     * 创建人
     */
    private String creator;
}

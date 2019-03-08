package com.ys.idatrix.db.api.hdfs.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: FileFetchDto
 * @Description: hdfs文件提取（下载）数据对象
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Data
@Accessors(chain = true)
public class FileFetchDto implements Serializable {


    private static final long serialVersionUID = 4009693598031724164L;
    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 總的文件大小
     */
    private long fileSize;

    /**
     * 块數據
     */
    private byte[] blockBytes;

    /**
     * 最後分片
     */
    private boolean bEnd;

}

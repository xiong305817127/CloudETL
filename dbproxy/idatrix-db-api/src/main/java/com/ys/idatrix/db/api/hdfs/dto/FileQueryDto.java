package com.ys.idatrix.db.api.hdfs.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: FileQueryDto
 * @Description: hdfs文件元数据信息
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class FileQueryDto implements Serializable{

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 是否是文件
     */
    private boolean isFile;

    /**
     * 文件长度
     */
    private long fileLen;

    /**
     * 文件修改日期
     */
    private String modifiedTime;

    /**
     * 文件上次访问日期
     */
    private String accessTime;

    /**
     * 文件备份数
     */
    private int replicates;

    /**
     * 文件所有者
     */
    private String owner;

    /**
     * 文件所在的分组
     */
    private String groupName;

    /**
     * 文件的权限
     */
    private String permissions;

    @Override
    public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName());
            sb.append("{");
            sb.append("fileName=" + fileName);
            sb.append("; filePath=" + filePath);
            if(!isFile){
                sb.append("; fileLen=" + fileLen);
                sb.append("; replication=" + replicates);
            }
            sb.append("; modification_time=" + modifiedTime);
            sb.append("; access_time=" + accessTime);
            sb.append("; owner=" + owner);
            sb.append("; group=" + groupName);
            sb.append("; permission=" + permissions);
            sb.append("}");
            return sb.toString();
    }
}

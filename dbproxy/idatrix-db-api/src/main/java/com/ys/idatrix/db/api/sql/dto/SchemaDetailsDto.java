package com.ys.idatrix.db.api.sql.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: SchemaDetailsDto
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/12
 */
@Data
@Accessors(chain = true)
public class SchemaDetailsDto implements Serializable {

    /**
     * 存储系统类型
     */
    private String type;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 端口
     */
    private String port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 模式名称
     */
    private String schemaName;

}

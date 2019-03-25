package com.ys.idatrix.db.api.sql.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @ClassName: SqlExecReqDto
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Data
@Accessors(chain = true)
public class SqlExecReqDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库链接方式：schema_id,detail
     */
    private SchemaModeEnum schemaModeEnum;

    /**
     * 模式链接详细。与 schemaId 二选一
     *
     * schemaModeEnum=detail 获取数据库链接
     */
    private SchemaDetailsDto schemaDetails;

    /**
     * 模式id。 与 dbLinkDto 二选一
     *
     * schemaModeEnum=id 获取数据库链接
     */
    private Long schemaId;

    /**
     * 是否需要验证权限-command中的表
     * 需要验证权限的操作 schemaId 必须不为空
     */
    private boolean needPermission = false;

    /**
     * sql语句
     */
    private String command;

}

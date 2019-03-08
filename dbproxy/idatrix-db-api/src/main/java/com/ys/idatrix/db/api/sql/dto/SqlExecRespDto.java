package com.ys.idatrix.db.api.sql.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: SqlExecRespDto
 * @Description: sql执行结果
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SqlExecRespDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行sql
     */
    private String sql;

    /**
     * datalab的sql执行保存id
     */
    private int executeId;

    /**
     * DML 非select操作的影响行数
     */
    private int effectRow;

    public SqlExecRespDto(String sql) {
        this.sql = sql;
    }

    public SqlExecRespDto(String sql, int effectRow) {
        this.sql = sql;
        this.effectRow = effectRow;
    }

    public SqlExecRespDto(int executeId, String sql) {
        this.executeId = executeId;
        this.sql = sql;
    }


}

package com.ys.idatrix.db.api.sql.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * @ClassName: SqlQueryRespDto
 * @Description: sql执行结果
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SqlQueryRespDto implements Serializable {

    private static final long serialVersionUID = 5453599263675599989L;

    private String sql;

    private List<Map<String, Object>> data;

    private List<String> columns;

    public SqlQueryRespDto(String sql) {
        super();
        this.sql = sql;
    }

    public SqlQueryRespDto(String sql, List<Map<String, Object>> data) {
        this.sql = sql;
        this.data = data;
    }

    public SqlQueryRespDto(String sql, List<Map<String, Object>> data, List<String> columns) {
        this.sql = sql;
        this.data = data;
        this.columns = columns;
    }

}

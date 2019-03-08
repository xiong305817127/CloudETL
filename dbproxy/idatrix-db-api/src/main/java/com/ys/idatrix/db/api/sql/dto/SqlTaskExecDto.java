package com.ys.idatrix.db.api.sql.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @ClassName: SqlTaskExecDto
 * @Description: Sql 任务执行详细
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Data
@Accessors(chain = true)
public class SqlTaskExecDto implements Serializable {

    private static final long serialVersionUID = -4366819153840187345L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 执行类型：select update
     */
    private String type;

    /**
     * 执行结果
     */
    private List<SqlTaskExecResultDto> results;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 修改时间（完成时间）
     */
    private Timestamp modifyTime;

    /**
     * 消耗时间
     **/
    private String expendTime;

}

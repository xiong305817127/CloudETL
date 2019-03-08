package com.ys.idatrix.db.dao.mapper;

import com.ys.idatrix.db.annotation.TargetDataSource;
import com.ys.idatrix.db.domain.DbSqlExecution;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @ClassName: DbSqlResult
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@TargetDataSource(name = "dbproxy")
public interface DbSqlExecutionMapper {

    /**
     * 根据主键查询sql执行
     *
     * @param id
     * @return
     */
    DbSqlExecution getById(@Param("id") int id);

    /**
     * 添加sql执行
     *
     * @param record
     * @return
     */
    int insert(DbSqlExecution record);

    /**
     * 修改执行数
     *
     * @param record
     * @return
     */
    int updateExecutingCount(DbSqlExecution record);


    /**
     * 查找最新的执行记录
     *
     * @param creator
     * @param system
     * @param rows
     * @return
     */
    List<DbSqlExecution> findLatestExecutions(@Param("creator") String creator, @Param("system") String system,
                                              @Param("rows") int rows);

}
package com.idatrix.unisecurity.core.mybatis.page;

public interface Dialect {

    String RS_COLUMN = "totalCount";

    boolean supportsLimit();

    /**
     * 以传入SQL为基础组装分页查询的SQL语句，传递给myBatis调用
     *
     * @param sql    原始SQL
     * @param offset 分页查询的记录的偏移量
     * @param limit  每页限定记录数
     * @return 拼装好的SQL
     */
    String getLimitSqlString(String sql, int offset, int limit);

    /**
     * 以传入SQL为基础组装总记录数查询的SQL语句
     *
     * @param sql 原始SQL
     * @return 拼装好的SQL
     */
    String getCountSqlString(String sql);
}


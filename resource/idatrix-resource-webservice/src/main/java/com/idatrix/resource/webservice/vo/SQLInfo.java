package com.idatrix.resource.webservice.vo;

import com.ys.idatrix.db.proxy.api.sql.SqlCommand;

/**
 * 数据SQL操作
 */
public class SQLInfo {

    /*用户操作用户名*/
    private String userName;

    /*执行命令数据*/
    private SqlCommand sqlCommand;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public SqlCommand getSqlCommand() {
        return sqlCommand;
    }

    public void setSqlCommand(SqlCommand sqlCommand) {
        this.sqlCommand = sqlCommand;
    }

    @Override
    public String toString() {
        return "SQLInfo{" +
                "userName='" + userName + '\'' +
                ", sqlCommand=" + sqlCommand +
                '}';
    }
}

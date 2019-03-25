package com.idatrix.resource.webservice.vo;

import com.ys.idatrix.db.api.sql.dto.SqlExecReqDto;

/**
 * 数据SQL操作
 */
public class SQLInfo {

    /*用户操作用户名*/
    private String userName;

    /*执行命令数据*/
    private SqlExecReqDto sqlCommand;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public SqlExecReqDto getSqlCommand() {
        return sqlCommand;
    }

    public void setSqlCommand(SqlExecReqDto sqlCommand) {
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

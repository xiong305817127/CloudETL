package com.idatrix.resource.common.vo;


import com.ys.idatrix.db.api.sql.dto.SqlExecReqDto;
import lombok.Data;

import java.util.Date;

/**
 * 数据SQL操作
 */
@Data
public class SQLInfo {

    /*用户操作用户名*/
    private String userName;

    /*执行命令数据*/
    private SqlExecReqDto sqlCommand;

    /*查询开始时间*/
    private Date queryTime;

    public SQLInfo(){super();}

    public SQLInfo(String user, SqlExecReqDto command, Date queryTime){
        this.userName = user;
        this.sqlCommand = command;
        this.queryTime = queryTime;
    }


}


package com.ys.idatrix.db.test;

import com.alibaba.fastjson.JSON;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.sql.dto.*;
import com.ys.idatrix.db.api.sql.service.SqlExecService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName: SqlExecServiceTest
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/6
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlExecServiceTest {

    @Autowired
    private SqlExecService sqlExecService;


    @Test(timeout = 1200000)
    public void testQueryMysql(){
        String sql = "select * from sb_app";
        SqlExecReqDto reqDto = new SqlExecReqDto();
        reqDto.setSchemaDetails(new SchemaDetailsDto());
        reqDto.setCommand(sql);
        reqDto.getSchemaDetails().setType("MYSQL");
        reqDto.getSchemaDetails().setIp("10.0.0.85");
        reqDto.getSchemaDetails().setPort("3306");
        reqDto.getSchemaDetails().setUsername("root");
        reqDto.getSchemaDetails().setPassword("admin");
        reqDto.getSchemaDetails().setSchemaName("servicebase");

        RespResult<SqlQueryRespDto> result = sqlExecService.executeQuery("zhoujian", reqDto);
        printSqlQueryResult(result);
    }


    @Test(timeout = 1200000)
    public void testAsyncExecuteMysql(){
        String sql = "select * from sb_app";
        SqlExecReqDto reqDto = new SqlExecReqDto();
        reqDto.setSchemaDetails(new SchemaDetailsDto());
        reqDto.setCommand(sql);
        reqDto.getSchemaDetails().setType("MYSQL");
        reqDto.getSchemaDetails().setIp("10.0.0.85");
        reqDto.getSchemaDetails().setPort("3306");
        reqDto.getSchemaDetails().setUsername("root");
        reqDto.getSchemaDetails().setPassword("admin");
        reqDto.getSchemaDetails().setSchemaName("servicebase");

        RespResult<SqlExecRespDto> result = sqlExecService.asyncExecute("zhoujian", reqDto);
        printSqlAsyncResult(result);
    }


    @Test(timeout = 1200000)
    public void testAsyncExecuteMysql2(){
        String sql = "select * from zhj_manual_tb1";
        SqlExecReqDto reqDto = new SqlExecReqDto();
        reqDto.setSchemaDetails(new SchemaDetailsDto());
        reqDto.setCommand(sql);
        reqDto.getSchemaDetails().setType("MYSQL");
        reqDto.getSchemaDetails().setIp("10.0.0.85");
        reqDto.getSchemaDetails().setPort("3306");
        reqDto.getSchemaDetails().setUsername("root");
        reqDto.getSchemaDetails().setPassword("admin");
        reqDto.getSchemaDetails().setSchemaName("test_mysql_create1");
        reqDto.setSchemaId(33L);
        reqDto.setNeedPermission(true);

        RespResult<SqlExecRespDto> result = sqlExecService.asyncExecute("wzl", reqDto);
        printSqlAsyncResult(result);
    }


    private void printSqlQueryResult(RespResult<SqlQueryRespDto> result){
        System.out.println("result:" + JSON.toJSONString(result,true));

        if(result.isSuccess()){

            SqlQueryRespDto respDto = result.getData();
            System.out.println("respDto:" + JSON.toJSONString(respDto,true));

        }else{
            System.out.println("执行失败:" + result.getMsg());
        }
    }


    private void printSqlAsyncResult(RespResult<SqlExecRespDto> result){
        System.out.println("result:" + JSON.toJSONString(result,true));

        if(result.isSuccess()){

            SqlExecRespDto respDto = result.getData();
            System.out.println("respDto:" + JSON.toJSONString(respDto,true));

            RespResult<Boolean> relt = sqlExecService.querySqlTaskIsCompleted(respDto.getExecuteId());

            if(relt.isSuccess()){
                boolean isCompleted = relt.getData();
                while (isCompleted) {
                    isCompleted = sqlExecService.querySqlTaskIsCompleted(respDto.getExecuteId()).getData();
                }
                RespResult<SqlTaskExecDto> sqlExecution = sqlExecService.getSqlTaskDetail(respDto.getExecuteId());
                System.out.println("**************" + JSON.toJSONString(sqlExecution));
            }

        }else{
            System.out.println("执行失败:" + result.getMsg());
        }
    }

}

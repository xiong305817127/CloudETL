package com.ys.idatrix.db.core.hbase;

import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.core.base.BaseExecService;
import com.ys.idatrix.db.core.security.HadoopSecurityManager;
import com.ys.idatrix.db.exception.HadoopSecurityManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;

/**
 * @ClassName: PhoenixExecService
 * @Description: HBase Sql 执行
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Service
public class PhoenixExecService extends BaseExecService {

    @Autowired(required = false)
    private HadoopSecurityManager hadoopSecurityManager;


    /**
     * 执行select查询语句
     *
     * @param select
     * @return
     * @throws Exception
     */
    public SqlQueryRespDto executeQuery(String select) throws Exception {
        return query(select, null);
    }


    /**
     * 批量执行更新语句
     *
     * @param commands ddl语句列表
     * @return
     */
    public List<SqlExecRespDto> batchExecuteUpdate(String... commands) throws Exception {
        return batchUpdate(null, false, true, commands);
    }


    @Override
    protected Connection getConnection(Object database) throws HadoopSecurityManagerException {
        // jdbc:phoneix[:zk_quorum][:zk_port][:zk_hbase_path][:headless_keytab_file:principal]
        return hadoopSecurityManager.getPhoenixJdbcConnection();
    }


}

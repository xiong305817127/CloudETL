package com.ys.idatrix.metacube.dubbo.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ys.idatrix.db.api.hbase.service.HBaseService;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import com.ys.idatrix.db.api.hive.service.HiveService;
import com.ys.idatrix.db.api.rdb.service.MysqlService;
import com.ys.idatrix.db.api.rdb.service.OracleService;
import com.ys.idatrix.db.api.rdb.service.RdbService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * db proxy服务消费
 *
 * @author wzl
 */
@EnableAutoConfiguration
@Component
public class DbProxyConsumer {

    @Reference
    private RdbService rdbService;

    @Reference
    private HdfsUnrestrictedService hdfsService;

    @Reference
    private HBaseService hbaseService;

    @Reference
    private HiveService hiveService;

    @Reference
    private MysqlService mysqlService;

    @Reference
    private OracleService oracleService;

    @Bean
    public RdbService getRdbService() {
        return rdbService;
    }

    @Bean
    public HdfsUnrestrictedService getHdfsService() {
        return hdfsService;
    }

    @Bean
    public HBaseService getHbaseService() {
        return hbaseService;
    }

    @Bean
    public HiveService getHiveService() {
        return hiveService;
    }

    @Bean
    public MysqlService mysqlService() {
        return mysqlService;
    }

    @Bean
    public OracleService oracleService() {
        return oracleService;
    }
}

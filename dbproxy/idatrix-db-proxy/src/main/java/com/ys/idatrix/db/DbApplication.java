package com.ys.idatrix.db;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.ys.idatrix.db.datasource.DynamicDataSourceRegister;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @ClassName: DbApplication
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Import(DynamicDataSourceRegister.class)
@MapperScan("com.ys.idatrix.db.dao.mapper")
@EnableDubboConfiguration
@EnableAspectJAutoProxy
@SpringBootApplication
public class DbApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DbApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }

}

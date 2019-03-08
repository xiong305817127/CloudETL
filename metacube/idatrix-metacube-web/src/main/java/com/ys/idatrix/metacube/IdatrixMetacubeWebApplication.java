package com.ys.idatrix.metacube;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableWebMvc
@MapperScan({"com.ys.idatrix.metacube.metamanage.mapper"})
@EnableDubboConfiguration
@SpringBootApplication
@EnableSwagger2
public class IdatrixMetacubeWebApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IdatrixMetacubeWebApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }

}


package com.idatrix.resource.common.config;


import com.idatrix.resource.common.utils.DateTools;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;


@EnableSwagger2
@Configuration
public class SwaggerConfig {

	 @Bean
	 public Docket createRestApi() {

         //添加head参数start
         ParameterBuilder tokenPar = new ParameterBuilder();
         List<Parameter> pars = new ArrayList<Parameter>();
         tokenPar.name("VT")
                 .description("用户VT信息,测试接口时使用，前端调用接口不需要传递该参数")
                 .modelRef(new ModelRef("string"))
                 .parameterType("header")
                 .required(true)
                 .build();
         pars.add(tokenPar.build());
         //添加head参数end

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.idatrix.resource")) // 注意修改此处的包名
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
	    }

	 private ApiInfo apiInfo() {
//		 ApiInfo apiInfo = new ApiInfo("CLoudETL API", "CLoudETL API 列表", "C01V01", null, null, null, null);
		 
	        return new ApiInfoBuilder()
	                .title("Catalog Resource API") // 任意，请稍微规范点
	                .description("资源共享交换平台接口 API 列表") // 任意，请稍微规范点
	                .version("v0.1 "+ DateTools.getDateTime())
	                .build();
	    }
}

package com.ys.idatrix.cloudetl.web.config;

import java.util.ArrayList;
import java.util.List;

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


@EnableSwagger2
@Configuration
public class SwaggerConfig {
	
	 @Bean
	 public Docket createRestApi() {
		 
			ParameterBuilder vt = new ParameterBuilder();
	        List<Parameter> pars = new ArrayList<Parameter>();  
	        vt.name("VT").description("VT认证输入串,测试使用,非API参数,前端可忽略.")
	    	.modelRef(new ModelRef("string")).parameterType("header") 
	    	.required(true).build(); //header中的ticket参数非必填，传空也可以
	    	pars.add(vt.build());    //根据每个方法名也知道当前方法在设置什么参数
		 
	        return new Docket(DocumentationType.SWAGGER_2)
	                .apiInfo(apiInfo())
	                .select()
	                .apis(RequestHandlerSelectors.basePackage("com.ys.idatrix.cloudetl.web.controller")) // 注意修改此处的包名
	                .paths(PathSelectors.any())
	                .build()
	                .globalOperationParameters(pars)  ;
	    }

	 private ApiInfo apiInfo() {
//		 ApiInfo apiInfo = new ApiInfo("CLoudETL API", "CLoudETL API 列表", "C01V01", null, null, null, null);
		 
	        return new ApiInfoBuilder()
	                .title("CLoudETL API") // 任意，请稍微规范点
	                .description("CLoudETL API 列表") // 任意，请稍微规范点
	                .version("C01V01")
	                .build();
	    }
}

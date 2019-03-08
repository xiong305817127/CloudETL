package com.idatrix.unisecurity.config;

import joptsimple.internal.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Restful API 访问路径:
 * http://IP:port/{context-path}/swagger-ui.html
 * eg:http://localhost:8080/config-web/swagger-ui.html
 * @ClassName: SwaggerConfig
 * @Description Swagger的配置类
 * @Author ouyang
 * @Date 2018/9/20 20:59
 * @Version 1.0
 **/
@SuppressWarnings("all")
@WebAppConfiguration
@EnableSwagger2
@EnableWebMvc
@Configuration
public class SwaggerConfig {

    @Value("${contextPath}")
    private String contextPath;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).useDefaultResponseMessages(false).pathProvider(new CustRelativePathProvider()).select()
                .apis(RequestHandlerSelectors.basePackage("com.idatrix.unisecurity"))
                .paths(PathSelectors.any()).build();
    }

    public class CustRelativePathProvider extends AbstractPathProvider {
        public static final String ROOT = "/";

        @Override
        public String getOperationPath(String operationPath) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
            String uri = Paths.removeAdjacentForwardSlashes(uriComponentsBuilder.path(operationPath).build().toString());
            return uri + ".shtml";
        }

        @Override
        protected String applicationPath() {
            return Strings.isNullOrEmpty(contextPath) ? ROOT : contextPath;
        }

        @Override
        protected String getDocumentationPath() {
            return ROOT;
        }
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("SECURITY API").description("安全系统 API 列表")
                // .termsOfServiceUrl("http://www.gdbigdata.com").contact("chl&lk")
                .version("1.0").build();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}

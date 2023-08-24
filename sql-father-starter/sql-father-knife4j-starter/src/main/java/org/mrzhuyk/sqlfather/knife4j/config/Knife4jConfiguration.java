package org.mrzhuyk.sqlfather.knife4j.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {
    @Bean
    public Docket groupRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(groupApiInfo())
            .select()
            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
            //.apis(RequestHandlerSelectors.basePackage("org.mrzhuyk.sqlfather.**.controller"))
            .paths(PathSelectors.any())
            .build();
    }
    
    private ApiInfo groupApiInfo(){
        return new ApiInfoBuilder()
            .title("sql-fatherAPI文档")
            .description("sql-fatherAPI文档")
            .termsOfServiceUrl("http://www.****.com/")
            .contact(new Contact("mrzhu", "https://****.com", "*****@qq.com"))
            .version("1.0")
            .build();
    }
}

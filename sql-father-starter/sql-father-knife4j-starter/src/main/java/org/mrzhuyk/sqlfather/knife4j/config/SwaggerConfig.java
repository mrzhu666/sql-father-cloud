package org.mrzhuyk.sqlfather.knife4j.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("sql-fatherAPI文档")
                .version("1.0")
                .description( "sql-fatherAPI文档")
                .termsOfService("http://www.****.com/")
                .license(new License().name("Apache 2.0")
                    .url("http://doc.xiaominfo.com"))
            ).addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
            .components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION,new SecurityScheme()
                .name(HttpHeaders.AUTHORIZATION).type(SecurityScheme.Type.HTTP).scheme("bearer")));
    }
    
    
    //@Bean
    //public Docket groupRestApi() {
    //    return new Docket(DocumentationType.SWAGGER_2)
    //        .apiInfo(groupApiInfo())
    //        .select()
    //        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
    //        //.apis(RequestHandlerSelectors.basePackage("org.mrzhuyk.sqlfather.**.controller"))
    //        .paths(PathSelectors.any())
    //        .build();
    //}
    //
    //private ApiInfo groupApiInfo(){
    //    return new ApiInfoBuilder()
    //        .title("sql-fatherAPI文档")
    //        .description("sql-fatherAPI文档")
    //        .termsOfServiceUrl("http://www.****.com/")
    //        .contact(new Contact("mrzhu", "https://****.com", "*****@qq.com"))
    //        .version("1.0")
    //        .build();
    //}
}

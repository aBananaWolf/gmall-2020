package com.atguigu.gmall.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author wyl
 * @create 2020-06-11 12:24
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket myDocket(Environment environment) {
        // 必须是开发环境
        Profiles profiles = Profiles.of("dev", "test");
        boolean flag = environment.acceptsProfiles(profiles);
        // 选择文档
        return new Docket(DocumentationType.SWAGGER_2)
                // 分组，可以有多个docket 进行分组
                .groupName("我的文档")
                .enable(flag)
                // 文档主页描述
                .apiInfo(apiInfo())
                // 选择扫描-开始，可以有注解扫描
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.atguigu.gmall.pms"))
                .build();
                // 选择扫描-结束
    }
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("营销管理API文档")
                .description("普通的文档")
                .build();
    }
}

package cn.yctech.facelogin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * springdoc配置文件
 *
 * @author yanqing
 * @date 2022/1/24 9:56
 */
@Configuration
public class SpringdocConfig {
    @Bean
    public OpenAPI springSysOpenApi() {
        // 名字和创建的SecuritySchemes一致
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");
        List<SecurityRequirement> securityRequirementArrayList = new ArrayList<>();
        securityRequirementArrayList.add(securityRequirement);

        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes("Authorization",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.APIKEY)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .name("Authorization")
                                                .description("Token令牌")
                                                .in(SecurityScheme.In.HEADER)
                                )
                )
                .security(securityRequirementArrayList)
                .info(new Info().title("用户接口文档")
                        .description("用户接口文档")
                        .version("v0.0.1")
                        .license(new License().name("name").url("http://ai4wms.cn")))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }
}

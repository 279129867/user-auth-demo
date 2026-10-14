package com.demo.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger 配置：
 * 声明 JWT Bearer 认证方案，让 Swagger 界面显示「Authorize」登录入口
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "用户管理与登录鉴权 API",
                version = "1.0.0",
                description = "注册 / 登录 / JWT 鉴权 / Redis 缓存 Demo 接口文档"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}

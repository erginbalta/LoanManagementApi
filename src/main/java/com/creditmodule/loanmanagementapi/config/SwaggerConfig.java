package com.creditmodule.loanmanagementapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Credit Module API")
                        .version("1.0.0")
                        .description("API documentation for Credit Module Challenge")
                        .contact(new Contact()
                                .name("Ergin Balta")
                                .email("erginbalta96@gmail.com")
                                .url("https://github.com/erginbalta")));
    }
}

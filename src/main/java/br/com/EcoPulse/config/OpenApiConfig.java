package br.com.EcoPulse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecoPulseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EcoPulse / SoulUp REST API")
                        .version("1.0.0")
                        .description("API REST consolidada (T_SOUL_) — 4º Sprint Domain Driven Design Using Java")
                        .contact(new Contact().name("Equipe EcoPulse / SoulUp")));
    }
}

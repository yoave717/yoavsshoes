package com.shoestore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class SwaggerConfig {

   @Value("${server.port:8080}")
   private String serverPort;

   @Bean
   public OpenAPI customOpenAPI() {
       return new OpenAPI()
               .servers(List.of(
                       new Server().url("http://localhost:" + serverPort).description("Local Development Server")
               ))
               .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
               .components(new Components()
                       .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
   }


   /**
    * Security scheme for JWT authentication
    */
   private SecurityScheme createAPIKeyScheme() {
       return new SecurityScheme()
               .type(SecurityScheme.Type.HTTP)
               .bearerFormat("JWT")
               .scheme("bearer")
               .description("Enter JWT Bearer token");
   }
}
package com.srsoft.modorder25.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

	@Value("${server.port}")
	private String serverPort;

	@Value("${domain.public.railway}")
	private String publicDomain;

	@Bean
	public OpenAPI customOpenAPI() {
		// Definizione dello schema di sicurezza JWT
		SecurityScheme securityScheme = new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.in(SecurityScheme.In.HEADER)
				.name("Authorization");

		// Server di sviluppo
		Server devServer = new Server()
				.url(publicDomain)
				.description("Server di Sviluppo");

		return new OpenAPI()
				.info(new Info()
						.title("SR SOFT FRAMEWORK API")
						.version("1.0")
						.description("API Framework Module Authenticate e Commesse")
						.contact(new Contact()
								.name("SR Soft")
								.email("ingineering@srsoft.it")
								.url("https://www.srsoft.it"))
						.license(new License()
								.name("Apache 2.0")
								.url("http://www.apache.org/licenses/LICENSE-2.0.html")))
				.servers(List.of(devServer/*, prodServer*/))
				.components(new Components()
						.addSecuritySchemes("bearer-jwt", securityScheme))
				.addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));

	}
}
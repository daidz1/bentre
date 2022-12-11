package ws.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfig {
	@Bean
	public OpenAPI springShopOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("TDNV - API")
						.description("Document for TDNV Api")
						.version("v0.0.1")
						.license(new License().name("Apache 2.0").url("http://springdoc.org")))
				.externalDocs(new ExternalDocumentation()
						.description("Author: Khuetech")
						.url("https://khuetech.net"))
				.addSecurityItem(new SecurityRequirement().addList("Token")).components(new Components().addSecuritySchemes("Token", new SecurityScheme().name("Token").type(SecurityScheme.Type.HTTP).scheme("Bearer").bearerFormat("JWT")));
	}
}
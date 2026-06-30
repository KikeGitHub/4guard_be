package com.fourguard.wms.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SpringDoc OpenAPI configuration.
 *
 * <p>Configures the Swagger UI with:
 * <ul>
 *   <li>Bearer JWT authentication scheme</li>
 *   <li>API metadata (title, version, contact, license)</li>
 *   <li>Server URL based on the configured context path</li>
 * </ul>
 *
 * <p>Swagger UI is available at:
 * {@code http://localhost:8080/api/v1/swagger-ui.html}</p>
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${server.servlet.context-path:/api/v1}") String contextPath) {

        return new OpenAPI()
                .info(buildApiInfo())
                .servers(List.of(
                        new Server()
                                .url(contextPath)
                                .description("4GUARD WMS API Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, buildSecurityScheme()));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Info buildApiInfo() {
        return new Info()
                .title("4GUARD WMS — REST API")
                .version("1.0.0")
                .description("""
                        Backend REST API del sistema de gestión de almacenes **4GUARD WMS**.
                        
                        ## Autenticación
                        Todos los endpoints (excepto `/auth/**`) requieren un Bearer JWT.
                        1. Llama a `POST /auth/login` con tus credenciales.
                        2. Copia el `accessToken` de la respuesta.
                        3. Haz clic en **Authorize** e introduce: `Bearer {token}`.
                        """)
                .contact(new Contact()
                        .name("4GUARD Engineering Team")
                        .email("dev@4guard.local")
                        .url("https://4guard.local"))
                .license(new License()
                        .name("Proprietary — 4GUARD")
                        .url("https://4guard.local/license"));
    }

    private SecurityScheme buildSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Introduce el JWT obtenido en `/auth/login`. Formato: `Bearer {token}`");
    }
}

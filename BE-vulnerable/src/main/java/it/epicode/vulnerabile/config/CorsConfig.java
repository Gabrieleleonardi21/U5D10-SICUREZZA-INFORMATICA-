package it.epicode.vulnerabile.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS PERMISSIVO: qualsiasi origine puo' chiamare questo backend.
 * E' gia' una debolezza (fa parte della versione vulnerabile): il backend
 * non distingue tra il frontend legittimo e una pagina di un attaccante.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOriginPatterns("*") // qualunque origine
				.allowedMethods("*")
				.allowedHeaders("*"); // accetta anche Content-Type: application/json cross-origin
	}
}

package it.epicode.sicuro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS RISTRETTO: solo il frontend legittimo (localhost:5173) puo' chiamare via browser.
 * Una pagina di un'altra origine viene bloccata dal browser gia' a questo livello.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private final String frontendOrigin;

	public CorsConfig(@Value("${app.frontend-origin}") String frontendOrigin) {
		this.frontendOrigin = frontendOrigin;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(frontendOrigin) // solo questa origine
				.allowedMethods("GET", "POST");
	}
}

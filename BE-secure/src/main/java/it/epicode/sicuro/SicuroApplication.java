package it.epicode.sicuro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Avvio del backend SICURO (porta 8082). */
@SpringBootApplication
public class SicuroApplication {

	public static void main(String[] args) {
		SpringApplication.run(SicuroApplication.class, args);
	}
}

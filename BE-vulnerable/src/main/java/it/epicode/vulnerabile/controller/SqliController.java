package it.epicode.vulnerabile.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.epicode.vulnerabile.payload.LoginRequest;
import it.epicode.vulnerabile.payload.SqliResponse;

/**
 * DEMO SQL INJECTION - versione VULNERABILE.
 * La query viene costruita concatenando direttamente i valori inviati dall'utente:
 * un input come  admin' OR '1'='1' --  cambia la logica della query e fa passare il login.
 */
@RestController
@RequestMapping("/api/sqli")
public class SqliController {

	private static final Logger log = LoggerFactory.getLogger(SqliController.class);
	private final JdbcTemplate jdbc;

	public SqliController(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@PostMapping("/login")
	public SqliResponse login(@RequestBody LoginRequest body) {
		// PUNTO DEBOLE: username e password entrano nella stringa SQL senza alcun controllo.
		String query = "SELECT * FROM utenti WHERE username='" + body.username()
				+ "' AND password='" + body.password() + "'";
		log.info("[VULNERABILE] eseguo query: {}", query);

		// queryForList(String) usa uno Statement semplice: la stringa viene eseguita cosi' com'e'.
		List<Map<String, Object>> righe = jdbc.queryForList(query);

		boolean ok = !righe.isEmpty();
		String utente = ok ? String.valueOf(righe.get(0).get("USERNAME")) : null;
		String dettaglio = ok
				? "Login riuscito: la query ha restituito almeno una riga (anche senza credenziali valide)."
				: "Login fallito: nessuna riga trovata.";

		return new SqliResponse(ok, query, null, righe.size(), ok ? 200 : 401, utente, dettaglio);
	}
}

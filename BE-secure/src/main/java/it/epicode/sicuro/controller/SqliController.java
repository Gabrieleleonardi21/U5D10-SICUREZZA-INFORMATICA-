package it.epicode.sicuro.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.epicode.sicuro.payload.LoginRequest;
import it.epicode.sicuro.payload.SqliResponse;

/**
 * DEMO SQL INJECTION - versione SICURA.
 * La query e' fissa con i segnaposto ?: i valori dell'utente viaggiano come parametri,
 * non come pezzi di SQL. L'input  admin' OR '1'='1' --  viene cercato alla lettera e non trova nulla.
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
		// DIFESA: query parametrizzata. I ? restano ? e i valori sono legati a parte.
		String query = "SELECT * FROM utenti WHERE username=? AND password=?";
		log.info("[SICURO] eseguo query parametrizzata: {} | parametri: [{}, {}]",
				query, body.username(), body.password());

		// queryForList(sql, args...) usa un PreparedStatement: i parametri non alterano la query.
		List<Map<String, Object>> righe = jdbc.queryForList(query, body.username(), body.password());

		boolean ok = !righe.isEmpty();
		String utente = ok ? String.valueOf(righe.get(0).get("USERNAME")) : null;
		String dettaglio = ok
				? "Login riuscito con credenziali corrette."
				: "Login rifiutato: l'input viene trattato come dato, non come SQL.";

		return new SqliResponse(ok, query, List.of(safe(body.username()), safe(body.password())),
				righe.size(), ok ? 200 : 401, utente, dettaglio);
	}

	private String safe(String s) {
		return s == null ? "" : s;
	}
}

package it.epicode.sicuro.controller;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.epicode.sicuro.payload.BonificoRequest;
import it.epicode.sicuro.payload.CsrfResponse;

/**
 * DEMO CSRF - versione SICURA.
 * Il bonifico parte solo se la richiesta porta un token anti-CSRF valido (rilasciato da /token)
 * e arriva dall'origine del frontend legittimo. Una pagina esterna non ha il token: viene respinta.
 */
@RestController
@RequestMapping("/api/csrf")
public class CsrfController {

	private static final Logger log = LoggerFactory.getLogger(CsrfController.class);
	private static final int SALDO_INIZIALE = 1000;
	private final AtomicInteger saldo = new AtomicInteger(SALDO_INIZIALE);
	// Token validi rilasciati al frontend legittimo.
	private final Set<String> tokenValidi = ConcurrentHashMap.newKeySet();
	private final String frontendOrigin;

	public CsrfController(@Value("${app.frontend-origin}") String frontendOrigin) {
		this.frontendOrigin = frontendOrigin;
	}

	/** Rilascia un token anti-CSRF: solo chi passa da qui (il frontend) puo' ottenerlo. */
	@GetMapping("/token")
	public String token() {
		String token = UUID.randomUUID().toString();
		tokenValidi.add(token);
		return token;
	}

	@PostMapping("/bonifico")
	public ResponseEntity<CsrfResponse> bonifico(
			@RequestBody BonificoRequest body,
			@RequestHeader(value = "X-CSRF-Token", required = false) String csrfToken,
			@RequestHeader(value = "Origin", required = false) String origin) {

		// DIFESA 1: il token deve essere presente e valido.
		if (csrfToken == null || !tokenValidi.contains(csrfToken)) {
			String riga = "BLOCCATO: token CSRF mancante o non valido (Origin: " + origin + ")";
			log.warn("[SICURO] {}", riga);
			return ResponseEntity.status(403).body(new CsrfResponse(false, 403, riga, saldo.get(),
					"Manca il token anti-CSRF: una pagina esterna non puo' conoscerlo, quindi non passa."));
		}

		// DIFESA 2: l'origine deve essere quella del frontend legittimo.
		if (origin != null && !origin.equals(frontendOrigin)) {
			String riga = "BLOCCATO: origine non consentita (" + origin + ")";
			log.warn("[SICURO] {}", riga);
			return ResponseEntity.status(403).body(new CsrfResponse(false, 403, riga, saldo.get(),
					"L'header Origin non corrisponde al frontend autorizzato."));
		}

		// Token valido e origine corretta: si esegue (e si consuma il token, uso singolo).
		tokenValidi.remove(csrfToken);
		int importo = body.importo() == null ? 0 : body.importo();
		int nuovoSaldo = saldo.addAndGet(-importo);
		String riga = "BONIFICO ESEGUITO con token valido: " + importo + "€ -> " + body.destinatario();
		log.info("[SICURO] {}", riga);
		return ResponseEntity.ok(new CsrfResponse(true, 200, riga, nuovoSaldo,
				"Richiesta legittima: token valido e origine corretta."));
	}

	@GetMapping("/saldo")
	public int saldo() {
		return saldo.get();
	}

	@PostMapping("/reset")
	public int reset() {
		return saldo.updateAndGet(v -> SALDO_INIZIALE);
	}
}

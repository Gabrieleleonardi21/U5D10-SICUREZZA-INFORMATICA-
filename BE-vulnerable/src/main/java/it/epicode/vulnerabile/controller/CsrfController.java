package it.epicode.vulnerabile.controller;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.epicode.vulnerabile.payload.BonificoRequest;
import it.epicode.vulnerabile.payload.CsrfResponse;

/**
 * DEMO CSRF - versione VULNERABILE.
 * Il bonifico viene eseguito per chiunque invii la richiesta: nessun token anti-CSRF,
 * nessun controllo sull'origine. Una pagina esterna puo' farlo partire di nascosto.
 */
@RestController
@RequestMapping("/api/csrf")
public class CsrfController {

	private static final Logger log = LoggerFactory.getLogger(CsrfController.class);
	private static final int SALDO_INIZIALE = 1000;
	// Saldo del conto della "vittima", tenuto in memoria per la demo.
	private final AtomicInteger saldo = new AtomicInteger(SALDO_INIZIALE);

	@PostMapping("/bonifico")
	public CsrfResponse bonifico(@RequestBody BonificoRequest body,
			@RequestHeader(value = "Origin", required = false) String origin) {
		int importo = body.importo() == null ? 0 : body.importo();
		// PUNTO DEBOLE: si esegue e basta, senza verificare chi ha inviato la richiesta.
		int nuovoSaldo = saldo.addAndGet(-importo);
		String riga = "BONIFICO ESEGUITO: " + importo + "€ -> " + body.destinatario()
				+ " (Origin: " + origin + ")";
		log.warn("[VULNERABILE] {}", riga);

		return new CsrfResponse(true, 200, riga, nuovoSaldo,
				"Nessun token e nessun controllo di origine: la richiesta viene accettata sempre.");
	}

	/** Saldo corrente: il frontend lo mostra per far vedere l'effetto dell'attacco. */
	@GetMapping("/saldo")
	public int saldo() {
		return saldo.get();
	}

	/** Ripristina il saldo iniziale per rifare la demo. */
	@PostMapping("/reset")
	public int reset() {
		return saldo.updateAndGet(v -> SALDO_INIZIALE);
	}
}

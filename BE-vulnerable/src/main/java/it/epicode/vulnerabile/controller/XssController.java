package it.epicode.vulnerabile.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.epicode.vulnerabile.payload.CommentoRequest;
import it.epicode.vulnerabile.payload.XssResponse;

/**
 * DEMO XSS - versione VULNERABILE.
 * Il commento viene salvato e restituito COSI' COM'E': se contiene HTML/JavaScript,
 * il frontend che lo inserisce con innerHTML lo esegue.
 */
@RestController
@RequestMapping("/api/xss")
public class XssController {

	private static final Logger log = LoggerFactory.getLogger(XssController.class);

	@PostMapping("/commento")
	public XssResponse commento(@RequestBody CommentoRequest body) {
		String testo = body.testo() == null ? "" : body.testo();
		// PUNTO DEBOLE: nessuna sanificazione, il contenuto grezzo torna al client.
		log.info("[VULNERABILE] commento salvato senza sanificazione: {}", testo);

		boolean contieneHtml = testo.contains("<") || testo.contains(">");
		String dettaglio = contieneHtml
				? "Il contenuto grezzo viene reso con innerHTML: eventuali <script>/onerror vengono eseguiti."
				: "Nessun tag nel testo: nessun effetto visibile, ma la porta resta aperta.";

		return new XssResponse(contieneHtml, testo, testo, "innerHTML (grezzo)", dettaglio);
	}
}

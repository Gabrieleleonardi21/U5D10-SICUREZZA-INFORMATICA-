package it.epicode.sicuro.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.epicode.sicuro.payload.CommentoRequest;
import it.epicode.sicuro.payload.XssResponse;

/**
 * DEMO XSS - versione SICURA.
 * Il commento viene "neutralizzato" trasformando i caratteri speciali in entita' HTML:
 * &lt; &gt; &amp; ... Cosi' <script> diventa testo visibile e non codice eseguibile.
 */
@RestController
@RequestMapping("/api/xss")
public class XssController {

	private static final Logger log = LoggerFactory.getLogger(XssController.class);

	@PostMapping("/commento")
	public XssResponse commento(@RequestBody CommentoRequest body) {
		String testo = body.testo() == null ? "" : body.testo();
		// DIFESA: escaping dei caratteri usati per aprire tag e attributi.
		String escapato = escapeHtml(testo);
		log.info("[SICURO] commento sanificato: {}", escapato);

		return new XssResponse(false, testo, escapato, "textContent + escaping lato server",
				"I caratteri speciali diventano entita' HTML: il browser li mostra come testo, non li esegue.");
	}

	/** Converte i caratteri pericolosi in entita' HTML. */
	private String escapeHtml(String s) {
		return s.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}

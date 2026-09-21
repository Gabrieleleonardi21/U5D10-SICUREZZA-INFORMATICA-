package it.epicode.vulnerabile.payload;

/**
 * Esito del bonifico.
 * @param statusCode codice di stato della risposta (il dettaglio che spiega la differenza)
 * @param log        riga scritta nel registro del server
 */
public record CsrfResponse(
		boolean eseguito,
		int statusCode,
		String log,
		int saldo,
		String dettaglio) {}

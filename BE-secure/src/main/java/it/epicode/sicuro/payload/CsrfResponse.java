package it.epicode.sicuro.payload;

/** Esito del bonifico (stessa firma del backend vulnerabile). */
public record CsrfResponse(
		boolean eseguito,
		int statusCode,
		String log,
		int saldo,
		String dettaglio) {}

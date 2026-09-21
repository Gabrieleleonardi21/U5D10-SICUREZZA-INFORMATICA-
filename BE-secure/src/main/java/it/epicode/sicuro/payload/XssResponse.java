package it.epicode.sicuro.payload;

/** Esito dell'invio commento (stessa firma del backend vulnerabile). */
public record XssResponse(
		boolean attaccoRiuscito,
		String testoSalvato,
		String htmlReso,
		String modalitaRender,
		String dettaglio) {}

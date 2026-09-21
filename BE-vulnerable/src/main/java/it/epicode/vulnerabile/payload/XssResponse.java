package it.epicode.vulnerabile.payload;

/**
 * Esito dell'invio commento.
 * @param htmlReso      contenuto che finisce nella pagina (il dettaglio che spiega la differenza)
 * @param modalitaRender come il frontend renderizzerebbe quel contenuto
 */
public record XssResponse(
		boolean attaccoRiuscito,
		String testoSalvato,
		String htmlReso,
		String modalitaRender,
		String dettaglio) {}

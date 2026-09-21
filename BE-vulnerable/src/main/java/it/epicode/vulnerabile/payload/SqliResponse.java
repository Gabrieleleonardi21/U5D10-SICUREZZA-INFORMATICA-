package it.epicode.vulnerabile.payload;

import java.util.List;

/**
 * Esito del login mostrato affiancato nel tutorial.
 * @param query      la query realmente inviata al database (il dettaglio che spiega la differenza)
 * @param parametri  parametri legati (solo lato sicuro con query parametrizzata; qui null)
 */
public record SqliResponse(
		boolean attaccoRiuscito,
		String query,
		List<String> parametri,
		int righeTrovate,
		int statusLogin,
		String utente,
		String dettaglio) {}

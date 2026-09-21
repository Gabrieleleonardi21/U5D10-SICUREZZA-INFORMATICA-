package it.epicode.sicuro.payload;

import java.util.List;

/** Esito del login mostrato affiancato nel tutorial (stessa firma del backend vulnerabile). */
public record SqliResponse(
		boolean attaccoRiuscito,
		String query,
		List<String> parametri,
		int righeTrovate,
		int statusLogin,
		String utente,
		String dettaglio) {}

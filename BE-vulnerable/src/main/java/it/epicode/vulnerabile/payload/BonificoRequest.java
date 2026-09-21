package it.epicode.vulnerabile.payload;

/** Corpo del bonifico per la demo CSRF. */
public record BonificoRequest(Integer importo, String destinatario) {}

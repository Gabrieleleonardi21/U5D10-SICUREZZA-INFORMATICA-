// Indirizzi dei due backend che espongono gli STESSI endpoint con la stessa firma.
export const BACKENDS = {
  vulnerabile: "http://localhost:8081",
  sicuro: "http://localhost:8082",
};

// Definizione dei tre attacchi: campi editabili, endpoint e testo esplicativo.
// Ogni attacco ha un "dettaglio" diverso che rende leggibile la differenza (vedi consegna):
//  - SQLi -> la query inviata al database
//  - XSS  -> il contenuto reso nella pagina
//  - CSRF -> il codice di stato e la riga scritta nel registro
export const ATTACCHI = [
  {
    id: "sqli",
    nome: "SQL Injection",
    endpoint: "/api/sqli/login",
    sommario: "Un input nel login cambia la query e fa entrare senza credenziali valide.",
    // I valori di default sono gia' il payload d'attacco, ma il campo resta modificabile.
    campi: [
      { chiave: "username", label: "Username", valore: "admin' OR '1'='1' --" },
      { chiave: "password", label: "Password", valore: "qualsiasi" },
    ],
  },
  {
    id: "xss",
    nome: "XSS",
    endpoint: "/api/xss/commento",
    sommario: "Un commento con HTML/JavaScript viene eseguito da chi apre la pagina.",
    campi: [
      {
        chiave: "testo",
        label: "Commento",
        multiline: true,
        // Payload classico: <img> con onerror. Quando il src fallisce, parte il codice.
        valore:
          "<img src=x onerror=\"document.body.style.background='crimson';document.body.style.color='#fff';document.body.textContent='XSS ESEGUITO: il codice iniettato e partito'\">",
      },
    ],
  },
  {
    id: "csrf",
    nome: "CSRF",
    endpoint: "/api/csrf/bonifico",
    sommario: "Una richiesta di bonifico parte senza che la vittima l'abbia voluta.",
    campi: [
      { chiave: "importo", label: "Importo (€)", valore: "500", numero: true },
      { chiave: "destinatario", label: "Destinatario", valore: "attaccante" },
    ],
  },
];

# Traccia per la presentazione a lezione

Come spiegare il progetto dal vivo. Schema fisso per ogni attacco:
**concetto → demo → codice → limite**. Prima si fa partire l'attacco, poi si spiega perché.

> Prima di iniziare: avvia i 4 servizi (vedi `README.md`) e, nella scheda CSRF, premi
> **Reset saldi**. Tieni aperta `DOCUMENTAZIONE.md` come rete di sicurezza.

---

## Apertura (30 secondi)

> "Ho costruito lo stesso identico servizio in due versioni: una scritta male (`:8081`) e
> una difesa (`:8082`). Il frontend manda lo **stesso valore** a entrambe e mostra le due
> risposte affiancate. Così l'attacco e la sua soluzione si vedono uno accanto all'altra."

Spiega **perché due backend**: la firma degli endpoint è identica (`login`, `commento`,
`bonifico`), cambia solo *come* è scritto dentro il metodo. È questo il cuore della lezione.

---

## 1. SQL Injection — metodo `login()`

- **Concetto:** "Il login costruisce una query SQL incollando dentro quello che scrivi. Se
  scrivo SQL al posto del nome, cambio la domanda che fa il database."
- **Demo:** scheda *SQL Injection* → valore già pronto `admin' OR '1'='1' --` → **Esegui**.
  A sinistra: 3 righe, login come `admin`. A destra: 0 righe, 401.
- **Codice (la riga chiave):**
  - Vulnerabile: `"... username='" + username + "'"` → concatenazione, il testo diventa SQL.
  - Protetto: `"... username=?"` + parametri → il `?` resta `?`, il valore viaggia a parte.
  - **Da dire:** "Con il `?` la query è già decisa prima: l'apice che ho scritto viene
    cercato come fosse un nome utente, non trovato, login rifiutato."
- **Limite:** "Protegge dall'injection, non dal resto: qui le password sono in chiaro, in
  produzione andrebbero cifrate (bcrypt)."

## 2. XSS — metodo `commento()`

- **Concetto:** "Un commento con dentro del codice, se la pagina lo mostra così com'è, viene
  **eseguito** da chi apre la pagina."
- **Demo:** scheda *XSS* → **Esegui**. A sinistra il riquadro diventa rosso ("XSS ESEGUITO"):
  il codice è partito davvero. A destra compare il testo `&lt;img...&gt;`, innocuo.
- **Codice (la riga chiave):**
  - Vulnerabile: il testo torna **grezzo** e viene messo con `innerHTML`.
  - Protetto: `escapeHtml()` trasforma `<` `>` in `&lt;` `&gt;`.
  - **Da dire:** "Senza il `<`, nessun tag si apre: `<script>` diventa testo da leggere, non
    codice da eseguire."
- **Limite:** "L'escaping giusto dipende dal punto in cui metti il dato; la difesa completa
  aggiunge una Content-Security-Policy."

## 3. CSRF — metodo `bonifico()`

- **Concetto:** "Un altro sito può far partire dal tuo browser una richiesta che tu non hai
  chiesto — un bonifico — se il backend non controlla **chi** l'ha mandata."
- **Demo (la più d'effetto):** scheda *CSRF* → apri la **pagina dell'attaccante (:9000)**:
  al solo caricamento il saldo del conto vulnerabile **cala da solo**; quello protetto no.
- **Codice (la riga chiave):**
  - Vulnerabile: esegue e basta.
  - Protetto: richiede un **token anti-CSRF** valido + controlla l'`Origin` (+ CORS ristretto).
  - **Da dire:** "L'attaccante può far *inviare* la richiesta, ma non può conoscere il token:
    senza token, 403."
- **Limite:** "Se il sito ha anche un XSS, l'attaccante può rubare il token: XSS e CSRF vanno
  chiusi insieme."

---

## Chiusura (il messaggio che resta)

> "Ogni difesa chiude **un** punto preciso, nessuna le chiude tutte. La sicurezza è la somma:
> query parametrizzate + escaping + token + cifratura + CSP."

## Promemoria pratici

- Avvia i 4 servizi *prima* di iniziare, così durante la demo non ti fermi.
- Resetta i saldi CSRF prima di mostrare la pagina dell'attaccante.
- Se qualcuno chiede "e i cookie?": il CSRF classico usa il cookie di sessione allegato in
  automatico dal browser; qui è semplificato con token + Origin (spiegato nei limiti della
  `DOCUMENTAZIONE.md`).

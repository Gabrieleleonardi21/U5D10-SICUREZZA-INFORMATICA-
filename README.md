# Attacco & Difesa — tutorial interattivo

Tutorial che spiega tre attacchi web (**SQL Injection**, **XSS**, **CSRF**) facendoli
partire davvero. Lo stesso valore viene inviato a due backend con gli **stessi endpoint**:
uno scritto in modo **vulnerabile**, uno con la **difesa applicata**. Il frontend mostra
le due risposte affiancate e il dettaglio che rende leggibile la differenza.

## Componenti e porte

| Componente        | Cartella        | Porta | Cosa fa                                                        |
|-------------------|-----------------|-------|----------------------------------------------------------------|
| Backend vulnerabile | `BE-vulnerable` | 8081  | Espone login/commento/bonifico **senza** difese                |
| Backend protetto  | `BE-secure`     | 8082  | Stessi endpoint **con** query parametrizzate, escaping e token |
| Frontend tutorial | `FE`            | 5173  | Selettore attacco, campo editabile, confronto affiancato       |
| Pagina attaccante | `attacker`      | 9000  | Sito esterno che lancia il CSRF cross-origin (drive-by)        |

I due backend usano un database **H2 in memoria** popolato all'avvio da `schema.sql` +
`data.sql`: nessun database da installare o configurare.

## Come avviare tutto

Servono **Java 25** e **Maven**, **Node.js** e **Python 3** (solo per servire la pagina
attaccante). Aprire quattro terminali:

```bash
# 1) Backend vulnerabile (porta 8081)
cd BE-vulnerable
mvn spring-boot:run

# 2) Backend protetto (porta 8082)
cd BE-secure
mvn spring-boot:run

# 3) Frontend del tutorial (porta 5173)
cd FE
npm install      # solo la prima volta
npm run dev

# 4) Pagina dell'attaccante per la demo CSRF (porta 9000)
cd attacker
python3 -m http.server 9000
```

Poi aprire il browser su **http://localhost:5173**.

## Come si usa il tutorial

1. Scegli l'attacco con le tre schede in alto (SQL Injection / XSS / CSRF).
2. Il campo con il valore dell'attacco è **modificabile**: puoi cambiarlo e rilanciare.
3. Premi **"Esegui su entrambi"**: la stessa richiesta parte verso i due backend.
4. Confronta i due riquadri:
   - a **sinistra** il vulnerabile con l'**attacco riuscito**;
   - a **destra** il protetto con il **rifiuto**.
   Ogni lato mostra il dettaglio che spiega la differenza:
   - **SQL Injection** → la query inviata al database;
   - **XSS** → il contenuto reso nella pagina (a sinistra viene eseguito davvero, in un
     iframe isolato);
   - **CSRF** → il codice di stato della risposta e la riga scritta nel registro.

### Demo CSRF dal sito esterno

Nella scheda CSRF apri la **pagina dell'attaccante (:9000)**: al solo caricamento invia
un bonifico ai due backend, senza che tu l'abbia chiesto. Tornando al tutorial e premendo
"Reset saldi"/ricaricando vedrai che il **saldo del conto vulnerabile è calato da solo**,
mentre quello protetto è rimasto intatto (richiesta bloccata da CORS + token mancante).

## Valori di esempio (già precompilati)

| Attacco       | Campo         | Valore di esempio                     |
|---------------|---------------|----------------------------------------|
| SQL Injection | username      | `admin' OR '1'='1' --`                 |
| SQL Injection | password      | `qualsiasi`                            |
| XSS           | commento      | `<img src=x onerror="...">` (esegue codice) |
| CSRF          | importo       | `500`                                  |
| CSRF          | destinatario  | `attaccante`                           |

## Documentazione

Spiegazione degli attacchi, valore di esempio e confronto fra codice vulnerabile e
protetto (con i limiti di ogni difesa): vedi **[docs/DOCUMENTAZIONE.md](docs/DOCUMENTAZIONE.md)**.

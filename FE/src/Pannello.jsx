import RenderXss from "./RenderXss";

// Riga etichetta/valore riutilizzabile, per non ripetere lo stesso markup.
function Riga({ etichetta, children }) {
  return (
    <div className="riga">
      <span className="riga-label">{etichetta}</span>
      <div className="riga-val">{children}</div>
    </div>
  );
}

// Riquadro di codice/monospace (query, log, HTML...). Usa textContent tramite JSX: niente innerHTML.
function Codice({ children }) {
  return <pre className="codice">{children}</pre>;
}

// Stabilisce se l'attacco e' riuscito su questo lato, leggendo il campo giusto per ogni attacco.
function attaccoRiuscito(attaccoId, dati) {
  if (!dati) return false;
  if (attaccoId === "csrf") return dati.eseguito === true;
  return dati.attaccoRiuscito === true;
}

export default function Pannello({ lato, attaccoId, risposta }) {
  const vulnerabile = lato === "vulnerabile";
  const dati = risposta?.dati;
  const riuscito = attaccoRiuscito(attaccoId, dati);

  return (
    <section className={"pannello " + (vulnerabile ? "vulnerabile" : "sicuro")}>
      <header className="pannello-head">
        <h3>{vulnerabile ? "Backend vulnerabile" : "Backend protetto"}</h3>
        <span className="porta">{vulnerabile ? ":8081" : ":8082"}</span>
      </header>

      {/* Esito in evidenza (solo dopo l'esecuzione): rosso se passa, verde se viene fermato. */}
      {risposta && (
        <div className={"esito " + (riuscito ? "ko" : "ok")}>
          {riuscito ? "ATTACCO RIUSCITO" : "ATTACCO BLOCCATO"}
        </div>
      )}

      {!risposta && <p className="attesa">In attesa dell'esecuzione…</p>}

      {/* Caso richiesta non arrivata al controller: es. blocco CORS del backend protetto. */}
      {risposta && !dati && (
        <Riga etichetta="Richiesta bloccata dal browser">
          <Codice>{risposta.errore || "Il backend ha rifiutato la richiesta (CORS / rete)."}</Codice>
        </Riga>
      )}

      {/* --- Dettaglio SQL Injection: la query inviata al database --- */}
      {dati && attaccoId === "sqli" && (
        <>
          <Riga etichetta="Query inviata al database">
            <Codice>{dati.query}</Codice>
          </Riga>
          {dati.parametri && (
            <Riga etichetta="Parametri legati (separati dalla query)">
              <Codice>{JSON.stringify(dati.parametri)}</Codice>
            </Riga>
          )}
          <Riga etichetta="Righe trovate">{dati.righeTrovate}</Riga>
          <Riga etichetta="Codice di stato del login">{dati.statusLogin}</Riga>
          <Riga etichetta="Utente autenticato">{dati.utente || "—"}</Riga>
        </>
      )}

      {/* --- Dettaglio XSS: il contenuto reso nella pagina (guardia su htmlReso) --- */}
      {dati && attaccoId === "xss" && dati.htmlReso !== undefined && (
        <>
          <Riga etichetta="Come viene reso">{dati.modalitaRender}</Riga>
          <Riga etichetta="Contenuto reso nella pagina">
            {vulnerabile ? (
              // Lato vulnerabile: il payload viene eseguito davvero (dentro una sandbox isolata).
              <RenderXss html={dati.htmlReso} />
            ) : (
              // Lato protetto: il testo escapato compare come semplice testo, non come codice.
              <Codice>{dati.htmlReso}</Codice>
            )}
          </Riga>
        </>
      )}

      {/* --- Dettaglio CSRF: codice di stato e riga scritta nel registro --- */}
      {dati && attaccoId === "csrf" && (
        <>
          <Riga etichetta="Codice di stato della risposta">{dati.statusCode}</Riga>
          <Riga etichetta="Riga scritta nel registro">
            <Codice>{dati.log}</Codice>
          </Riga>
          <Riga etichetta="Saldo dopo l'operazione">{dati.saldo} &euro;</Riga>
        </>
      )}

      {dati?.dettaglio && <p className="spiega">{dati.dettaglio}</p>}
    </section>
  );
}

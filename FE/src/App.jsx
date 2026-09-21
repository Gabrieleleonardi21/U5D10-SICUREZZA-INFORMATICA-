import { useEffect, useMemo, useState } from "react";
import { ATTACCHI, BACKENDS } from "./attacchi";
import { inviaAEntrambi, leggiSaldi, resetSaldi } from "./api";
import Pannello from "./Pannello";
import "./App.css";

// Costruisce lo stato iniziale dei campi (chiave -> valore di default) per un attacco.
function valoriIniziali(attacco) {
  const out = {};
  for (const campo of attacco.campi) out[campo.chiave] = campo.valore;
  return out;
}

export default function App() {
  const [attaccoId, setAttaccoId] = useState(ATTACCHI[0].id);
  const attacco = useMemo(() => ATTACCHI.find((a) => a.id === attaccoId), [attaccoId]);

  const [valori, setValori] = useState(() => valoriIniziali(ATTACCHI[0]));
  const [risultato, setRisultato] = useState(null);
  const [caricamento, setCaricamento] = useState(false);
  const [saldi, setSaldi] = useState(null);

  // Cambio attacco in modo atomico: campi e risultato si aggiornano insieme, cosi' non
  // esiste un render intermedio in cui il risultato del vecchio attacco resta appeso.
  function cambiaAttacco(id) {
    setAttaccoId(id);
    setValori(valoriIniziali(ATTACCHI.find((a) => a.id === id)));
    setRisultato(null);
  }

  // Per la demo CSRF tengo aggiornato il saldo dei due conti.
  useEffect(() => {
    if (attaccoId !== "csrf") return;
    aggiornaSaldi();
  }, [attaccoId]);

  async function aggiornaSaldi() {
    setSaldi(await leggiSaldi());
  }

  function cambiaCampo(chiave, valore) {
    setValori((v) => ({ ...v, [chiave]: valore }));
  }

  // Comando unico: costruisce il corpo dai campi e lo invia a entrambi i backend.
  async function esegui() {
    setCaricamento(true);
    const corpo = {};
    for (const campo of attacco.campi) {
      const grezzo = valori[campo.chiave];
      corpo[campo.chiave] = campo.numero ? Number(grezzo) : grezzo;
    }
    const esito = await inviaAEntrambi(attacco.endpoint, corpo);
    setRisultato(esito);
    if (attaccoId === "csrf") await aggiornaSaldi();
    setCaricamento(false);
  }

  async function reset() {
    await resetSaldi();
    await aggiornaSaldi();
    setRisultato(null);
  }

  return (
    <div className="app">
      <header className="testata">
        <h1>Attacco &amp; Difesa — tutorial interattivo</h1>
        <p>
          Lo stesso valore viene inviato a due backend: a sinistra quello vulnerabile, a destra quello
          protetto. Il confronto mostra dove finisce la differenza.
        </p>
      </header>

      {/* Selettore dei tre attacchi */}
      <nav className="tab">
        {ATTACCHI.map((a) => (
          <button
            key={a.id}
            className={a.id === attaccoId ? "attivo" : ""}
            onClick={() => cambiaAttacco(a.id)}
          >
            {a.nome}
          </button>
        ))}
      </nav>

      <p className="sommario">{attacco.sommario}</p>

      {/* Campi editabili + comando unico */}
      <div className="form">
        {attacco.campi.map((campo) => (
          <label key={campo.chiave} className="campo">
            <span>{campo.label}</span>
            {campo.multiline ? (
              <textarea
                rows={3}
                value={valori[campo.chiave]}
                onChange={(e) => cambiaCampo(campo.chiave, e.target.value)}
              />
            ) : (
              <input
                value={valori[campo.chiave]}
                onChange={(e) => cambiaCampo(campo.chiave, e.target.value)}
              />
            )}
          </label>
        ))}
        <button className="esegui" onClick={esegui} disabled={caricamento}>
          {caricamento ? "Invio…" : "Esegui su entrambi"}
        </button>
      </div>

      {/* Box specifico CSRF: saldo dei conti e pagina esterna dell'attaccante */}
      {attaccoId === "csrf" && (
        <div className="csrf-extra">
          <div className="saldi">
            <span>
              Saldo conto vulnerabile: <b>{saldi?.vulnerabile ?? "…"} &euro;</b>
            </span>
            <span>
              Saldo conto protetto: <b>{saldi?.sicuro ?? "…"} &euro;</b>
            </span>
            <button onClick={reset}>Reset saldi</button>
          </div>
          <p className="nota">
            Il CSRF vero parte da un sito esterno. Apri la{" "}
            <a href={"http://localhost:9000"} target="_blank" rel="noreferrer">
              pagina dell'attaccante (:9000)
            </a>{" "}
            e poi torna qui: il saldo del conto vulnerabile sara' calato da solo, quello protetto no.
          </p>
        </div>
      )}

      {/* Confronto affiancato */}
      <div className="confronto">
        <Pannello lato="vulnerabile" attaccoId={attaccoId} risposta={risultato?.vulnerabile} />
        <Pannello lato="sicuro" attaccoId={attaccoId} risposta={risultato?.sicuro} />
      </div>

      <footer className="pie">
        Backend: <code>{BACKENDS.vulnerabile}</code> (vulnerabile) ·{" "}
        <code>{BACKENDS.sicuro}</code> (protetto)
      </footer>
    </div>
  );
}

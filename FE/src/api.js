import { BACKENDS } from "./attacchi";

// Invia lo stesso identico corpo ai due backend e restituisce le due risposte affiancate.
// E' il "comando unico" della consegna: una sola azione, due risultati da confrontare.
export async function inviaAEntrambi(endpoint, corpo) {
  const [vulnerabile, sicuro] = await Promise.all([
    chiamata(BACKENDS.vulnerabile + endpoint, corpo),
    chiamata(BACKENDS.sicuro + endpoint, corpo),
  ]);
  return { vulnerabile, sicuro };
}

// Una singola POST JSON. Legge il body anche in caso di errore (es. 403 del CSRF sicuro).
async function chiamata(url, corpo) {
  try {
    const res = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(corpo),
    });
    const dati = await res.json().catch(() => null);
    return { ok: res.ok, status: res.status, dati };
  } catch (e) {
    // Errore di rete o richiesta bloccata dal browser (es. CORS del backend sicuro).
    return { ok: false, status: 0, dati: null, errore: String(e) };
  }
}

// Legge il saldo del conto dai due backend (serve alla demo CSRF).
export async function leggiSaldi() {
  const leggi = (base) =>
    fetch(base + "/api/csrf/saldo")
      .then((r) => r.json())
      .catch(() => null);
  const [vulnerabile, sicuro] = await Promise.all([
    leggi(BACKENDS.vulnerabile),
    leggi(BACKENDS.sicuro),
  ]);
  return { vulnerabile, sicuro };
}

// Riporta i saldi al valore iniziale per rifare la demo.
export async function resetSaldi() {
  const reset = (base) =>
    fetch(base + "/api/csrf/reset", { method: "POST" }).catch(() => null);
  await Promise.all([reset(BACKENDS.vulnerabile), reset(BACKENDS.sicuro)]);
}

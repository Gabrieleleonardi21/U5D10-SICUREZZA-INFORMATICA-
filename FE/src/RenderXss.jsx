import { useMemo } from "react";

// Costruisce il documento della sandbox in cui il commento viene inserito con innerHTML
// (esattamente il "sink" vulnerabile). Il payload entra come stringa JS con i < neutralizzati,
// cosi' non puo' chiudere il tag <script> del documento.
function costruisciDoc(html) {
  const payloadJs = JSON.stringify(html ?? "").replace(/</g, "\\u003c");
  return `<!doctype html>
<html><head><meta charset="utf-8"><style>
  body{font-family:system-ui,sans-serif;margin:0;padding:12px;font-size:14px}
</style></head>
<body>
  <div id="out"></div>
  <script>
    // Punto debole riprodotto: il contenuto non fidato finisce in innerHTML.
    document.getElementById("out").innerHTML = ${payloadJs};
  <\/script>
</body></html>`;
}

// Rende il contenuto dentro un iframe isolato: sandbox="allow-scripts" SENZA allow-same-origin,
// cosi' l'eventuale codice iniettato gira ma resta chiuso qui e non tocca mai l'app reale.
export default function RenderXss({ html }) {
  const doc = useMemo(() => costruisciDoc(html), [html]);
  return (
    <iframe
      title="render-vulnerabile"
      sandbox="allow-scripts"
      srcDoc={doc}
      style={{ width: "100%", height: 90, border: "1px solid #d1495b", borderRadius: 6, background: "#fff" }}
    />
  );
}

async function api(metodo, caminho, dados) {
    const opcoes = { method: metodo };
    if (dados) {
        opcoes.headers = { "Content-Type": "application/x-www-form-urlencoded" };
        opcoes.body = new URLSearchParams(dados).toString();
    }
    let resposta;
    try {
        resposta = await fetch("/api" + caminho, opcoes);
    } catch (e) {
        throw new Error("Servidor fora do ar. Execute o run.bat e abra http://localhost:8080");
    }
    const json = await resposta.json();
    if (!resposta.ok) throw new Error(json.erro || "Erro inesperado.");
    return json;
}

function esc(texto) {
    const mapa = { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" };
    return String(texto).replace(/[&<>"']/g, (c) => mapa[c]);
}

function mostrar(id, texto, tipo) {
    const el = document.getElementById(id);
    el.textContent = texto;
    el.className = "msg " + (tipo || "ok");
    el.hidden = !texto;
}

function dataBr(iso) {
    const d = new Date(iso.slice(0, 19));
    return d.toLocaleDateString("pt-BR") + " " + d.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" });
}

function tabela(linhas, colunas) {
    if (!linhas.length) return '<p class="vazio">Sem dados ainda.</p>';
    const cab = colunas.map((c) => "<th>" + esc(c[1]) + "</th>").join("");
    const corpo = linhas
        .map((l) => "<tr>" + colunas.map((c) => '<td data-rotulo="' + esc(c[1]) + '">' + esc(l[c[0]]) + "</td>").join("") + "</tr>")
        .join("");
    return '<table class="cartoes"><thead><tr>' + cab + "</tr></thead><tbody>" + corpo + "</tbody></table>";
}

document.addEventListener("DOMContentLoaded", () => {
    const atual = location.pathname.split("/").pop() || "index.html";
    document.querySelectorAll(".topo nav a").forEach((a) => {
        if (a.getAttribute("href") === atual) a.setAttribute("aria-current", "page");
    });
});

const form = document.getElementById("form");
let ocorrencias = [];
let abrigos = [];
let cidadaos = [];

function validar(d) {
    if (!d.idCidadao) return "Cadastre e selecione um cidadao.";
    if (!d.descricao.trim()) return "Informe a descricao.";
    if (d.descricao.length > 300) return "A descricao passa de 300 caracteres.";
    const sev = Number(d.severidade);
    if (!Number.isInteger(sev) || sev < 1 || sev > 5) return "A severidade deve ser de 1 a 5.";
    return "";
}

function limpar() {
    form.reset();
    form.elements.id.value = "";
    document.getElementById("titulo-form").textContent = "Nova ocorrencia";
    document.getElementById("cancelar").hidden = true;
}

function desenhar() {
    const lista = document.getElementById("lista");
    if (!ocorrencias.length) {
        lista.innerHTML = '<p class="vazio">Nenhuma ocorrencia registrada.</p>';
        return;
    }
    lista.innerHTML = ocorrencias
        .map((o) => {
            const grave = o.severidade >= 4 ? " grave" : "";
            const ativos = o.abrigos.map((a) => a.id);
            const opcoes = abrigos
                .filter((a) => !ativos.includes(a.id) && a.vagas > 0)
                .map((a) => '<option value="' + a.id + '">' + esc(a.nome) + " (" + a.vagas + " vagas)</option>")
                .join("");
            const fichas = o.abrigos.length
                ? o.abrigos
                      .map(
                          (a) =>
                              '<li class="ficha">' + esc(a.nome) + '<button type="button" title="Desativar abrigo" aria-label="Desativar ' +
                              esc(a.nome) + '" data-desativar="' + o.id + ":" + a.id + '">x</button></li>'
                      )
                      .join("")
                : '<li class="vazio">Nenhum abrigo ativado.</li>';
            return (
                '<article class="ocorrencia' + grave + '">' +
                "<header><strong>" + esc(o.tipo) + " #" + o.id + '</strong><span class="selo' + grave + '">Severidade ' + o.severidade + "</span></header>" +
                "<p>" + esc(o.descricao) + "</p>" +
                '<p class="rotulo">Registrada por ' + esc(o.cidadao.nome) + " (" + esc(o.cidadao.bairro) + ") em " + dataBr(o.dataHora) + "</p>" +
                '<div class="alerta">' + esc(o.alerta) + "</div>" +
                "<strong>Abrigos ativados</strong>" +
                '<ul class="fichas">' + fichas + "</ul>" +
                '<div class="linha-ativar">' +
                '<select aria-label="Abrigo a ativar" data-sel="' + o.id + '"><option value="">Escolha um abrigo</option>' + opcoes + "</select>" +
                '<button class="pequeno" data-ativar="' + o.id + '">Ativar abrigo</button>' +
                '<button class="leve pequeno" data-editar="' + o.id + '">Editar</button>' +
                '<button class="perigo pequeno" data-excluir="' + o.id + '">Excluir</button>' +
                "</div></article>"
            );
        })
        .join("");
}

async function carregar() {
    try {
        [ocorrencias, abrigos, cidadaos] = await Promise.all([api("GET", "/ocorrencias"), api("GET", "/abrigos"), api("GET", "/cidadaos")]);
        const atual = document.getElementById("sel-cidadao").value;
        document.getElementById("sel-cidadao").innerHTML = cidadaos
            .map((c) => '<option value="' + c.id + '">' + esc(c.nome) + " - " + esc(c.bairro) + "</option>")
            .join("");
        if (atual) document.getElementById("sel-cidadao").value = atual;
        desenhar();
    } catch (e) {
        mostrar("msg", e.message, "erro");
    }
}

form.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    const dados = Object.fromEntries(new FormData(form));
    const erro = validar(dados);
    if (erro) return mostrar("msg", erro, "erro");
    const id = dados.id;
    delete dados.id;
    try {
        if (id) await api("PUT", "/ocorrencias/" + id, dados);
        else await api("POST", "/ocorrencias", dados);
        mostrar("msg", id ? "Ocorrencia atualizada." : "Ocorrencia registrada.");
        limpar();
        await carregar();
    } catch (e) {
        mostrar("msg", e.message, "erro");
    }
});

document.getElementById("cancelar").addEventListener("click", () => {
    limpar();
    mostrar("msg", "");
});

document.getElementById("lista").addEventListener("click", async (ev) => {
    const d = ev.target.dataset;
    try {
        if (d.editar) {
            const o = ocorrencias.find((x) => x.id == d.editar);
            form.elements.tipo.value = o.tipo;
            form.elements.severidade.value = o.severidade;
            form.elements.idCidadao.value = o.cidadao.id;
            form.elements.descricao.value = o.descricao;
            form.elements.id.value = o.id;
            document.getElementById("titulo-form").textContent = "Editando ocorrencia " + o.id;
            document.getElementById("cancelar").hidden = false;
            form.scrollIntoView({ behavior: "smooth" });
        } else if (d.ativar) {
            const idAbrigo = document.querySelector('[data-sel="' + d.ativar + '"]').value;
            if (!idAbrigo) return mostrar("msg", "Escolha um abrigo para ativar.", "erro");
            await api("POST", "/ocorrencias/" + d.ativar + "/abrigos", { idAbrigo });
            mostrar("msg", "Abrigo ativado.");
            await carregar();
        } else if (d.desativar) {
            const [idOcorrencia, idAbrigo] = d.desativar.split(":");
            await api("DELETE", "/ocorrencias/" + idOcorrencia + "/abrigos/" + idAbrigo);
            mostrar("msg", "Abrigo desativado.");
            await carregar();
        } else if (d.excluir && confirm("Excluir esta ocorrencia?")) {
            await api("DELETE", "/ocorrencias/" + d.excluir);
            mostrar("msg", "Ocorrencia excluida.");
            await carregar();
        }
    } catch (e) {
        mostrar("msg", e.message, "erro");
    }
});

carregar();

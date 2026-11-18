const form = document.getElementById("form");
let lista = [];

function validar(d) {
    const capacidade = Number(d.capacidade);
    const vagas = Number(d.vagas);
    if (!d.nome.trim()) return "Informe o nome.";
    if (!Number.isInteger(capacidade) || capacidade <= 0) return "A capacidade deve ser um numero maior que zero.";
    if (!Number.isInteger(vagas) || vagas < 0) return "As vagas devem ser um numero igual ou maior que zero.";
    if (vagas > capacidade) return "As vagas nao podem passar da capacidade.";
    if (!d.logradouro.trim()) return "Informe o logradouro.";
    if (!d.bairro.trim()) return "Informe o bairro.";
    return "";
}

function limpar() {
    form.reset();
    form.elements.id.value = "";
    document.getElementById("titulo-form").textContent = "Novo abrigo";
    document.getElementById("cancelar").hidden = true;
}

function desenhar() {
    const corpo = document.getElementById("corpo");
    if (!lista.length) {
        corpo.innerHTML = '<tr><td colspan="5" class="vazio">Nenhum abrigo cadastrado.</td></tr>';
        return;
    }
    corpo.innerHTML = lista
        .map(
            (a) =>
                "<tr>" +
                '<td data-rotulo="Nome">' + esc(a.nome) + "</td>" +
                '<td data-rotulo="Bairro">' + esc(a.bairro) + "</td>" +
                '<td data-rotulo="Logradouro">' + esc(a.logradouro) + "</td>" +
                '<td data-rotulo="Vagas">' + a.vagas + " de " + a.capacidade + "</td>" +
                '<td class="acoes">' +
                '<input type="number" min="1" value="1" aria-label="Quantidade de vagas" data-qtd="' + a.id + '">' +
                '<button class="leve pequeno" data-ocupar="' + a.id + '">Ocupar</button>' +
                '<button class="leve pequeno" data-liberar="' + a.id + '">Liberar</button>' +
                '<button class="leve pequeno" data-editar="' + a.id + '">Editar</button>' +
                '<button class="perigo pequeno" data-excluir="' + a.id + '">Excluir</button></td>' +
                "</tr>"
        )
        .join("");
}

async function carregar() {
    try {
        lista = await api("GET", "/abrigos");
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
        if (id) await api("PUT", "/abrigos/" + id, dados);
        else await api("POST", "/abrigos", dados);
        mostrar("msg", id ? "Abrigo atualizado." : "Abrigo cadastrado.");
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

document.getElementById("corpo").addEventListener("click", async (ev) => {
    const d = ev.target.dataset;
    try {
        if (d.editar) {
            const a = lista.find((x) => x.id == d.editar);
            for (const campo of ["nome", "capacidade", "vagas", "logradouro", "bairro"]) form.elements[campo].value = a[campo];
            form.elements.id.value = a.id;
            document.getElementById("titulo-form").textContent = "Editando abrigo " + a.id;
            document.getElementById("cancelar").hidden = false;
            form.scrollIntoView({ behavior: "smooth" });
        } else if (d.ocupar || d.liberar) {
            const id = d.ocupar || d.liberar;
            const quantidade = document.querySelector('[data-qtd="' + id + '"]').value;
            await api("POST", "/abrigos/" + id + (d.ocupar ? "/ocupar" : "/liberar"), { quantidade });
            mostrar("msg", d.ocupar ? "Vagas ocupadas." : "Vagas liberadas.");
            await carregar();
        } else if (d.excluir && confirm("Excluir este abrigo?")) {
            await api("DELETE", "/abrigos/" + d.excluir);
            mostrar("msg", "Abrigo excluido.");
            await carregar();
        }
    } catch (e) {
        mostrar("msg", e.message, "erro");
    }
});

carregar();

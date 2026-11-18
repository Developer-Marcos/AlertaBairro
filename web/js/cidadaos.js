const form = document.getElementById("form");
let lista = [];

function validar(d) {
    if (!d.nome.trim()) return "Informe o nome.";
    if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(d.email.trim())) return "Informe um e-mail valido.";
    if (d.telefone.replace(/\D/g, "").length < 10) return "O telefone deve ter pelo menos 10 digitos.";
    if (!d.endereco.trim()) return "Informe o endereco.";
    if (!d.bairro.trim()) return "Informe o bairro.";
    return "";
}

function limpar() {
    form.reset();
    form.elements.id.value = "";
    document.getElementById("titulo-form").textContent = "Novo cidadao";
    document.getElementById("cancelar").hidden = true;
}

function desenhar() {
    const corpo = document.getElementById("corpo");
    if (!lista.length) {
        corpo.innerHTML = '<tr><td colspan="6" class="vazio">Nenhum cidadao cadastrado.</td></tr>';
        return;
    }
    corpo.innerHTML = lista
        .map(
            (c) =>
                "<tr>" +
                '<td data-rotulo="Nome">' + esc(c.nome) + "</td>" +
                '<td data-rotulo="E-mail">' + esc(c.email) + "</td>" +
                '<td data-rotulo="Telefone">' + esc(c.telefone) + "</td>" +
                '<td data-rotulo="Endereco">' + esc(c.endereco) + "</td>" +
                '<td data-rotulo="Bairro">' + esc(c.bairro) + "</td>" +
                '<td class="acoes"><button class="leve pequeno" data-editar="' + c.id + '">Editar</button>' +
                '<button class="perigo pequeno" data-excluir="' + c.id + '">Excluir</button></td>' +
                "</tr>"
        )
        .join("");
}

async function carregar() {
    try {
        lista = await api("GET", "/cidadaos");
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
        if (id) await api("PUT", "/cidadaos/" + id, dados);
        else await api("POST", "/cidadaos", dados);
        mostrar("msg", id ? "Cidadao atualizado." : "Cidadao cadastrado.");
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
    const editar = ev.target.dataset.editar;
    const excluir = ev.target.dataset.excluir;
    if (editar) {
        const c = lista.find((x) => x.id == editar);
        for (const campo of ["nome", "email", "telefone", "endereco", "bairro"]) form.elements[campo].value = c[campo];
        form.elements.id.value = c.id;
        document.getElementById("titulo-form").textContent = "Editando cidadao " + c.id;
        document.getElementById("cancelar").hidden = false;
        form.scrollIntoView({ behavior: "smooth" });
    }
    if (excluir && confirm("Excluir este cidadao?")) {
        try {
            await api("DELETE", "/cidadaos/" + excluir);
            mostrar("msg", "Cidadao excluido.");
            await carregar();
        } catch (e) {
            mostrar("msg", e.message, "erro");
        }
    }
});

carregar();

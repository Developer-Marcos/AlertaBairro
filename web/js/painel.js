async function carregarPainel() {
    try {
        const [cidadaos, ocorrencias, abrigos, bairros, tipos, rankAbrigos] = await Promise.all([
            api("GET", "/cidadaos"),
            api("GET", "/ocorrencias"),
            api("GET", "/abrigos"),
            api("GET", "/relatorios/bairros"),
            api("GET", "/relatorios/tipos"),
            api("GET", "/relatorios/abrigos"),
        ]);

        document.getElementById("n-cidadaos").textContent = cidadaos.length;
        document.getElementById("n-ocorrencias").textContent = ocorrencias.length;
        document.getElementById("n-abrigos").textContent = abrigos.length;
        document.getElementById("n-vagas").textContent = abrigos.reduce((soma, a) => soma + a.vagas, 0);

        const graves = ocorrencias.filter((o) => o.severidade >= 4);
        document.getElementById("graves").innerHTML = graves.length
            ? graves
                  .map(
                      (o) =>
                          '<div class="ocorrencia grave"><header><strong>' + esc(o.tipo) + " em " + esc(o.cidadao.bairro) +
                          '</strong><span class="selo grave">Severidade ' + o.severidade + "</span></header><p>" + esc(o.descricao) +
                          '</p><div class="alerta">' + esc(o.alerta) + "</div></div>"
                  )
                  .join("")
            : '<p class="vazio">Nenhuma ocorrencia grave no momento.</p>';

        document.getElementById("rel-bairros").innerHTML = tabela(bairros, [
            ["bairro", "Bairro"],
            ["ocorrencias", "Ocorrencias"],
            ["severidade_media", "Severidade media"],
        ]);
        document.getElementById("rel-tipos").innerHTML = tabela(tipos, [
            ["tipo", "Tipo"],
            ["total", "Total"],
            ["maior_severidade", "Maior severidade"],
        ]);
        document.getElementById("rel-abrigos").innerHTML = tabela(rankAbrigos, [
            ["abrigo", "Abrigo"],
            ["bairro", "Bairro"],
            ["acionamentos", "Acionamentos"],
            ["vagas", "Vagas livres"],
        ]);
    } catch (e) {
        mostrar("msg", e.message, "erro");
    }
}

carregarPainel();

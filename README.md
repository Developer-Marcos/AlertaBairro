# AlertaBairro

Sistema de monitoramento e suporte comunitario para areas sujeitas a enchentes e deslizamentos.

Projeto da Global Solution 2026/2 (CitizenTech) - FIAP, Engenharia de Software, turma 2ESOA.

Video pitch: ____

## Problema e cidadao beneficiado

Moradores de areas urbanas de risco nao tem alertas rapidos nem um canal organizado para avisar a defesa civil e os vizinhos quando ocorre um alagamento ou deslizamento. A defesa civil, por sua vez, nao tem uma visao unica de onde estao as ocorrencias e de quais abrigos ainda tem vagas.

## Solucao

- Cidadaos cadastrados registram ocorrencias (alagamento ou deslizamento) com severidade de 1 a 5.
- Cada ocorrencia gera um texto de alerta diferente conforme o tipo e a gravidade.
- A defesa civil ativa abrigos para cada ocorrencia, respeitando as vagas disponiveis.
- O painel mostra ocorrencias graves e relatorios por bairro, por tipo e por abrigo.

## ODS da ONU

- ODS 11 (Cidades e comunidades sustentaveis): reduz o risco de moradores em areas vulneraveis e organiza a resposta da comunidade.
- ODS 13 (Acao contra a mudanca global do clima): apoia a adaptacao a eventos climaticos extremos com alerta e resposta rapida.

## Como executar

Requisito: JDK 17 ou superior (testado com JDK 21). Nao precisa instalar banco de dados nem baixar bibliotecas: o driver e o banco H2 ja estao em `lib/h2.jar`.

Windows, na pasta do projeto:

```
.\run.bat
```

Linux ou macOS, na pasta do projeto:

```
mkdir -p out
javac -encoding UTF-8 -cp lib/h2.jar -sourcepath src -d out src/ui/Main.java
java -cp out:lib/h2.jar ui.Main
```

Depois abra http://localhost:8080 no navegador. Para encerrar, feche a janela ou use Ctrl+C.

Na primeira execucao o programa cria o arquivo de banco `alertabairro.mv.db` e executa o `database/script.sql`, que cria as tabelas e a carga inicial. Para voltar aos dados iniciais, encerre o programa e apague os arquivos `alertabairro.*`.

## Banco de dados

- Banco relacional H2 (modo arquivo), acessado pelo Java via JDBC.
- Script completo em `database/script.sql`: 4 tabelas, chaves primarias e estrangeiras, NOT NULL, UNIQUE (email), CHECK (tipo, severidade, capacidade e vagas) e INSERTs de carga inicial. O script pode ser executado mais de uma vez.
- Tabelas: `TB_CIDADAO`, `TB_OCORRENCIA`, `TB_ABRIGO` e a associativa `TB_OCORRENCIA_ABRIGO` (relacionamento N:N entre ocorrencias e abrigos).

## Diagramas

- Diagrama entidade-relacionamento: `docs/diagrama_er.png`
- Diagrama de classes (UML): `docs/diagrama_classes.png`

## Estrutura do codigo

```
src/
  model/    Cidadao, Abrigo, Ocorrencia (abstrata), Alagamento, Deslizamento, Alertavel (interface)
  dao/      CidadaoDAO, AbrigoDAO, OcorrenciaDAO, RelatorioDAO (JDBC, CRUD completo)
  service/  CidadaoService, AbrigoService, OcorrenciaService (regras de negocio)
  ui/       Main, Servidor (HTTP), Json (interface com a web)
  util/     Conexao, ValidacaoException, DaoException
web/        index.html, ocorrencias.html, abrigos.html, cidadaos.html, css/, js/
database/   script.sql
docs/       diagramas
```

Orientacao a objetos: `Alagamento` e `Deslizamento` herdam de `Ocorrencia` e implementam `gerarAlerta()` do seu jeito (polimorfismo). `Ocorrencia` implementa a interface `Alertavel`. Os atributos sao privados e a validacao fica nos construtores e setters.

Tratamento de erros: dados invalidos lancam `ValidacaoException` e erros de banco (e-mail duplicado, exclusao de registro em uso, valor fora das regras) sao traduzidos para mensagens claras. A tela mostra a mensagem e o sistema continua funcionando.

## Interface web

Quatro paginas com o mesmo menu: Painel, Ocorrencias, Abrigos e Cidadaos. HTML semantico, validacao em JavaScript, mensagens na propria pagina e layout responsivo (celular e desktop). A web le e grava os dados reais do banco por meio de uma API HTTP feita com o `HttpServer` que ja vem no JDK, sem frameworks.

API (formulario `application/x-www-form-urlencoded`, resposta em JSON):

| Rota | Metodos |
|---|---|
| `/api/cidadaos` e `/api/cidadaos/{id}` | GET, POST, PUT, DELETE |
| `/api/abrigos` e `/api/abrigos/{id}` | GET, POST, PUT, DELETE |
| `/api/abrigos/{id}/ocupar` e `/liberar` | POST (quantidade) |
| `/api/ocorrencias` e `/api/ocorrencias/{id}` | GET, POST, PUT, DELETE |
| `/api/ocorrencias/{id}/abrigos` | POST (idAbrigo) |
| `/api/ocorrencias/{id}/abrigos/{idAbrigo}` | DELETE |
| `/api/relatorios/bairros`, `/abrigos`, `/tipos` | GET |

## Relatorios com SQL

Tres consultas com JOIN e agregacao em `RelatorioDAO`, exibidas no painel: ocorrencias por bairro (COUNT e AVG), abrigos mais acionados (LEFT JOIN e COUNT) e ocorrencias por tipo (COUNT e MAX).

#### IA Generativa foi usada apenas para revisao de sintaxe

## Integrantes

Veja o arquivo `entrega_global_solution.txt`.

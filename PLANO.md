# AlertaBairro - Global Solution 2026/2 (CitizenTech)

Entrega: **19/11/2026 ate 23h55**, plataforma, 1 entrega por grupo. Sem prorrogacao.

## Ideia
Sistema de alertas e suporte comunitario para areas de risco de enchente/alagamento.
Cidadao registra ocorrencias, defesa civil ve o que esta acontecendo e quais abrigos ativar.
ODS 11 (Cidades Sustentaveis) e ODS 13 (Acao Climatica).

## Banco (4 tabelas, 1 N:N)
- TB_CIDADAO (ID, Nome, Email, Telefone, Endereco, Bairro)
- TB_OCORRENCIA (ID, Tipo [Alagamento/Deslizamento], Descricao, Data_Hora, Severidade, ID_Cidadao)
- TB_ABRIGO (ID, Nome, Capacidade, Vagas_Disponiveis, Logradouro, Bairro)
- TB_OCORRENCIA_ABRIGO (N:N: abrigos ativados por ocorrencia)

## Entregaveis (checklist)
- [ ] Codigo Java + JDBC com README de execucao (e secao declarando uso de IA)
- [ ] Script .sql (CREATE com PK, FK, NOT NULL, 1+ UNIQUE/CHECK, INSERTs; roda sem erro)
- [ ] Diagrama de classes (UML) em PDF/imagem
- [ ] Diagrama ER em PDF/imagem
- [ ] Web: 3+ paginas, menu igual, HTML semantico, validacao JS, responsivo
- [ ] Video pitch ate 3 min, link publico (testar em aba anonima), mostrando o site funcionando
- [ ] .txt: nome do grupo, RM e nome de cada integrante, link do pitch
- [ ] .zip final enviado na plataforma

## Pontuacao (20)
Java 7 (UML 1, POO 1, CRUD JDBC 3, camadas 1, erros 1) | Banco 4 (ER 2, SQL 2) | Web 4 (nav 1, CRUD 2, responsivo 1) | Pitch 5

## Regras que tiram ponto
Tecnologia fora do conteudo ate a fase 7 nao pontua. Codigo que nao compila / nao conecta / .sql com erro nao pontua. Entrega fora do padrao: -1.

## Diferenciais (nao pontuam, desempatam)
Web ligada ao backend, repo GitHub com commits de todos, relatorios SQL (JOIN + agregacao), validacao com gente real.

## Divisao sugerida (ate 5 pessoas)
1. Banco: .sql + diagrama ER
2. Java: model + DAO (JDBC) + UML
3. Java: service + interface + tratamento de erros
4. Web: paginas, CSS responsivo, JS
5. Pitch + README + .txt + integracao final

## Cronograma (hoje 16/11)
- 16/11: grupo aprova ideia, divide tarefas, cria repo GitHub
- 17/11: banco + model/DAO prontos, web esqueleto
- 18/11: CRUD completo, web ligada, diagramas, README
- 19/11 (manha/tarde): gravar pitch, testar zip do zero, enviar com folga (nao deixar pra 23h)

package ui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Abrigo;
import model.Cidadao;
import model.Ocorrencia;
import service.AbrigoService;
import service.CidadaoService;
import service.OcorrenciaService;
import util.DaoException;
import util.ValidacaoException;

public class Servidor {
    private final CidadaoService cidadaos = new CidadaoService();
    private final AbrigoService abrigos = new AbrigoService();
    private final OcorrenciaService ocorrencias = new OcorrenciaService();
    private final Path pastaWeb = Path.of("web").toAbsolutePath().normalize();

    public void iniciar(int porta) throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        servidor.createContext("/api", this::api);
        servidor.createContext("/", this::estatico);
        servidor.start();
    }

    private void api(HttpExchange ex) throws IOException {
        int status = 200;
        String corpo;
        try {
            String caminho = ex.getRequestURI().getPath();
            String[] p = (caminho.length() > 5 ? caminho.substring(5) : "").split("/");
            Map<String, String> f = parametros(ex);
            String metodo = ex.getRequestMethod();
            String resposta = switch (p[0]) {
                case "cidadaos" -> rotaCidadaos(metodo, p, f);
                case "abrigos" -> rotaAbrigos(metodo, p, f);
                case "ocorrencias" -> rotaOcorrencias(metodo, p, f);
                case "relatorios" -> rotaRelatorios(metodo, p);
                default -> null;
            };
            if (resposta == null) {
                status = 404;
                corpo = Json.erro("Rota nao encontrada.");
            } else {
                corpo = resposta;
            }
        } catch (ValidacaoException e) {
            status = 400;
            corpo = Json.erro(e.getMessage());
        } catch (DaoException e) {
            status = 500;
            corpo = Json.erro(e.getMessage());
        } catch (RuntimeException e) {
            status = 500;
            corpo = Json.erro("Erro interno: " + e.getMessage());
        }
        enviar(ex, status, "application/json; charset=utf-8", corpo.getBytes(StandardCharsets.UTF_8));
    }

    private String rotaCidadaos(String metodo, String[] p, Map<String, String> f) {
        if (p.length == 1) {
            if (metodo.equals("GET")) return lista(cidadaos.listar().stream().map(Json::cidadao).toList());
            if (metodo.equals("POST")) {
                return Json.cidadao(cidadaos.cadastrar(f.get("nome"), f.get("email"), f.get("telefone"), f.get("endereco"), f.get("bairro")));
            }
        } else if (p.length == 2) {
            int id = inteiro(p[1], "id");
            if (metodo.equals("GET")) return Json.cidadao(cidadaos.buscar(id));
            if (metodo.equals("PUT")) {
                return Json.cidadao(cidadaos.atualizar(id, f.get("nome"), f.get("email"), f.get("telefone"), f.get("endereco"), f.get("bairro")));
            }
            if (metodo.equals("DELETE")) {
                cidadaos.excluir(id);
                return "{\"ok\":true}";
            }
        }
        return null;
    }

    private String rotaAbrigos(String metodo, String[] p, Map<String, String> f) {
        if (p.length == 1) {
            if (metodo.equals("GET")) return lista(abrigos.listar().stream().map(Json::abrigo).toList());
            if (metodo.equals("POST")) {
                return Json.abrigo(abrigos.cadastrar(f.get("nome"), inteiro(f.get("capacidade"), "capacidade"),
                        inteiro(f.get("vagas"), "vagas"), f.get("logradouro"), f.get("bairro")));
            }
        } else if (p.length == 2) {
            int id = inteiro(p[1], "id");
            if (metodo.equals("GET")) return Json.abrigo(abrigos.buscar(id));
            if (metodo.equals("PUT")) {
                return Json.abrigo(abrigos.atualizar(id, f.get("nome"), inteiro(f.get("capacidade"), "capacidade"),
                        inteiro(f.get("vagas"), "vagas"), f.get("logradouro"), f.get("bairro")));
            }
            if (metodo.equals("DELETE")) {
                abrigos.excluir(id);
                return "{\"ok\":true}";
            }
        } else if (p.length == 3 && metodo.equals("POST")) {
            int id = inteiro(p[1], "id");
            int qtd = inteiro(f.get("quantidade"), "quantidade");
            if (p[2].equals("ocupar")) return Json.abrigo(abrigos.ocuparVagas(id, qtd));
            if (p[2].equals("liberar")) return Json.abrigo(abrigos.liberarVagas(id, qtd));
        }
        return null;
    }

    private String rotaOcorrencias(String metodo, String[] p, Map<String, String> f) {
        if (p.length == 1) {
            if (metodo.equals("GET")) return lista(ocorrencias.listar().stream().map(Json::ocorrencia).toList());
            if (metodo.equals("POST")) {
                Ocorrencia o = ocorrencias.cadastrar(f.get("tipo"), f.get("descricao"),
                        inteiro(f.get("severidade"), "severidade"), inteiro(f.get("idCidadao"), "idCidadao"));
                return Json.ocorrencia(o);
            }
        } else if (p.length == 2) {
            int id = inteiro(p[1], "id");
            if (metodo.equals("GET")) return Json.ocorrencia(ocorrencias.buscar(id));
            if (metodo.equals("PUT")) {
                return Json.ocorrencia(ocorrencias.atualizar(id, f.get("tipo"), f.get("descricao"),
                        inteiro(f.get("severidade"), "severidade"), inteiro(f.get("idCidadao"), "idCidadao")));
            }
            if (metodo.equals("DELETE")) {
                ocorrencias.excluir(id);
                return "{\"ok\":true}";
            }
        } else if (p.length >= 3 && p[2].equals("abrigos")) {
            int id = inteiro(p[1], "id");
            if (p.length == 3 && metodo.equals("POST")) {
                return Json.ocorrencia(ocorrencias.ativarAbrigo(id, inteiro(f.get("idAbrigo"), "idAbrigo")));
            }
            if (p.length == 4 && metodo.equals("DELETE")) {
                return Json.ocorrencia(ocorrencias.desativarAbrigo(id, inteiro(p[3], "idAbrigo")));
            }
        }
        return null;
    }

    private String rotaRelatorios(String metodo, String[] p) {
        if (!metodo.equals("GET") || p.length != 2) return null;
        return switch (p[1]) {
            case "bairros" -> Json.linhas(ocorrencias.relatorioPorBairro());
            case "abrigos" -> Json.linhas(ocorrencias.relatorioAbrigos());
            case "tipos" -> Json.linhas(ocorrencias.relatorioPorTipo());
            default -> null;
        };
    }

    private String lista(List<String> itens) {
        return Json.lista(new ArrayList<>(itens));
    }

    private int inteiro(String valor, String campo) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidacaoException("O campo " + campo + " deve ser um numero inteiro.");
        }
    }

    private Map<String, String> parametros(HttpExchange ex) throws IOException {
        Map<String, String> mapa = new HashMap<>();
        String consulta = ex.getRequestURI().getRawQuery();
        String corpo = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        for (String texto : new String[] {consulta, corpo}) {
            if (texto == null || texto.isBlank()) continue;
            for (String par : texto.split("&")) {
                String[] kv = par.split("=", 2);
                mapa.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
            }
        }
        return mapa;
    }

    private void estatico(HttpExchange ex) throws IOException {
        String caminho = ex.getRequestURI().getPath();
        if (caminho.equals("/")) caminho = "/index.html";
        Path arquivo = pastaWeb.resolve(caminho.substring(1)).normalize();
        if (!arquivo.startsWith(pastaWeb) || !Files.isRegularFile(arquivo)) {
            enviar(ex, 404, "text/plain; charset=utf-8", "Pagina nao encontrada".getBytes(StandardCharsets.UTF_8));
            return;
        }
        enviar(ex, 200, tipo(arquivo.getFileName().toString()), Files.readAllBytes(arquivo));
    }

    private String tipo(String nome) {
        if (nome.endsWith(".html")) return "text/html; charset=utf-8";
        if (nome.endsWith(".css")) return "text/css; charset=utf-8";
        if (nome.endsWith(".js")) return "text/javascript; charset=utf-8";
        if (nome.endsWith(".svg")) return "image/svg+xml";
        if (nome.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }

    private void enviar(HttpExchange ex, int status, String tipo, byte[] dados) throws IOException {
        ex.getResponseHeaders().set("Content-Type", tipo);
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(status, dados.length);
        ex.getResponseBody().write(dados);
        ex.close();
    }
}

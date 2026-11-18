package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Abrigo;
import model.Cidadao;
import model.Ocorrencia;

public class Json {

    public static String texto(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.append('"').toString();
    }

    public static String cidadao(Cidadao c) {
        return "{\"id\":" + c.getId() + ",\"nome\":" + texto(c.getNome()) + ",\"email\":" + texto(c.getEmail())
                + ",\"telefone\":" + texto(c.getTelefone()) + ",\"endereco\":" + texto(c.getEndereco())
                + ",\"bairro\":" + texto(c.getBairro()) + "}";
    }

    public static String abrigo(Abrigo a) {
        return "{\"id\":" + a.getId() + ",\"nome\":" + texto(a.getNome()) + ",\"capacidade\":" + a.getCapacidade()
                + ",\"vagas\":" + a.getVagasDisponiveis() + ",\"logradouro\":" + texto(a.getLogradouro())
                + ",\"bairro\":" + texto(a.getBairro()) + "}";
    }

    public static String ocorrencia(Ocorrencia o) {
        List<String> abrigos = new ArrayList<>();
        for (Abrigo a : o.getAbrigos()) abrigos.add(abrigo(a));
        return "{\"id\":" + o.getId() + ",\"tipo\":" + texto(o.getTipo()) + ",\"descricao\":" + texto(o.getDescricao())
                + ",\"dataHora\":" + texto(o.getDataHora().toString()) + ",\"severidade\":" + o.getSeveridade()
                + ",\"alerta\":" + texto(o.gerarAlerta()) + ",\"cidadao\":" + cidadao(o.getCidadao())
                + ",\"abrigos\":" + lista(abrigos) + "}";
    }

    public static String linhas(List<Map<String, Object>> linhas) {
        List<String> itens = new ArrayList<>();
        for (Map<String, Object> linha : linhas) {
            List<String> campos = new ArrayList<>();
            for (Map.Entry<String, Object> e : linha.entrySet()) {
                Object v = e.getValue();
                String valor = v instanceof Number ? v.toString() : texto(v == null ? null : v.toString());
                campos.add(texto(e.getKey().toLowerCase()) + ":" + valor);
            }
            itens.add("{" + String.join(",", campos) + "}");
        }
        return lista(itens);
    }

    public static String lista(List<String> itens) {
        return "[" + String.join(",", itens) + "]";
    }

    public static String erro(String mensagem) {
        return "{\"erro\":" + texto(mensagem) + "}";
    }
}

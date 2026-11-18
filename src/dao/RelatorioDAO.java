package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import util.Conexao;

public class RelatorioDAO {

    public List<Map<String, Object>> ocorrenciasPorBairro() {
        return consultar("SELECT c.BAIRRO AS BAIRRO, COUNT(o.ID) AS OCORRENCIAS, ROUND(AVG(o.SEVERIDADE), 1) AS SEVERIDADE_MEDIA "
                + "FROM TB_OCORRENCIA o JOIN TB_CIDADAO c ON c.ID = o.ID_CIDADAO "
                + "GROUP BY c.BAIRRO ORDER BY OCORRENCIAS DESC, c.BAIRRO");
    }

    public List<Map<String, Object>> abrigosMaisAcionados() {
        return consultar("SELECT a.NOME AS ABRIGO, a.BAIRRO AS BAIRRO, COUNT(oa.ID_OCORRENCIA) AS ACIONAMENTOS, a.VAGAS_DISPONIVEIS AS VAGAS "
                + "FROM TB_ABRIGO a LEFT JOIN TB_OCORRENCIA_ABRIGO oa ON oa.ID_ABRIGO = a.ID "
                + "GROUP BY a.ID, a.NOME, a.BAIRRO, a.VAGAS_DISPONIVEIS ORDER BY ACIONAMENTOS DESC, a.NOME");
    }

    public List<Map<String, Object>> ocorrenciasPorTipo() {
        return consultar("SELECT TIPO, COUNT(*) AS TOTAL, MAX(SEVERIDADE) AS MAIOR_SEVERIDADE "
                + "FROM TB_OCORRENCIA GROUP BY TIPO ORDER BY TOTAL DESC");
    }

    private List<Map<String, Object>> consultar(String sql) {
        List<Map<String, Object>> linhas = new ArrayList<>();
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> linha = new LinkedHashMap<>();
                for (int i = 1; i <= md.getColumnCount(); i++) linha.put(md.getColumnLabel(i), rs.getObject(i));
                linhas.add(linha);
            }
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
        return linhas;
    }
}

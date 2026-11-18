package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Abrigo;
import model.Cidadao;
import model.Ocorrencia;
import util.Conexao;

public class OcorrenciaDAO {
    private static final String SELECT =
            "SELECT o.ID AS O_ID, o.TIPO, o.DESCRICAO, o.DATA_HORA, o.SEVERIDADE, "
            + "c.ID AS C_ID, c.NOME, c.EMAIL, c.TELEFONE, c.ENDERECO, c.BAIRRO "
            + "FROM TB_OCORRENCIA o JOIN TB_CIDADAO c ON c.ID = o.ID_CIDADAO ";

    public void inserir(Ocorrencia o) {
        String sql = "INSERT INTO TB_OCORRENCIA (TIPO, DESCRICAO, DATA_HORA, SEVERIDADE, ID_CIDADAO) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getTipo());
            ps.setString(2, o.getDescricao());
            ps.setTimestamp(3, Timestamp.valueOf(o.getDataHora()));
            ps.setInt(4, o.getSeveridade());
            ps.setInt(5, o.getCidadao().getId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) o.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public List<Ocorrencia> listar() {
        List<Ocorrencia> lista = new ArrayList<>();
        try (Connection con = Conexao.abrir()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT + "ORDER BY o.DATA_HORA DESC, o.ID DESC");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
            for (Ocorrencia o : lista) carregarAbrigos(con, o);
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
        return lista;
    }

    public Ocorrencia buscarPorId(int id) {
        try (Connection con = Conexao.abrir()) {
            Ocorrencia o = null;
            try (PreparedStatement ps = con.prepareStatement(SELECT + "WHERE o.ID = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) o = montar(rs);
                }
            }
            if (o != null) carregarAbrigos(con, o);
            return o;
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public boolean atualizar(Ocorrencia o) {
        String sql = "UPDATE TB_OCORRENCIA SET TIPO = ?, DESCRICAO = ?, SEVERIDADE = ?, ID_CIDADAO = ? WHERE ID = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getTipo());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getSeveridade());
            ps.setInt(4, o.getCidadao().getId());
            ps.setInt(5, o.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public boolean excluir(int id) {
        try (Connection con = Conexao.abrir()) {
            con.setAutoCommit(false);
            try (PreparedStatement vinculos = con.prepareStatement("DELETE FROM TB_OCORRENCIA_ABRIGO WHERE ID_OCORRENCIA = ?");
                 PreparedStatement ocorrencia = con.prepareStatement("DELETE FROM TB_OCORRENCIA WHERE ID = ?")) {
                vinculos.setInt(1, id);
                vinculos.executeUpdate();
                ocorrencia.setInt(1, id);
                boolean apagou = ocorrencia.executeUpdate() > 0;
                con.commit();
                return apagou;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public void ativarAbrigo(int idOcorrencia, int idAbrigo) {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("INSERT INTO TB_OCORRENCIA_ABRIGO (ID_OCORRENCIA, ID_ABRIGO) VALUES (?, ?)")) {
            ps.setInt(1, idOcorrencia);
            ps.setInt(2, idAbrigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public boolean desativarAbrigo(int idOcorrencia, int idAbrigo) {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TB_OCORRENCIA_ABRIGO WHERE ID_OCORRENCIA = ? AND ID_ABRIGO = ?")) {
            ps.setInt(1, idOcorrencia);
            ps.setInt(2, idAbrigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    private void carregarAbrigos(Connection con, Ocorrencia o) throws SQLException {
        String sql = "SELECT a.* FROM TB_ABRIGO a JOIN TB_OCORRENCIA_ABRIGO oa ON oa.ID_ABRIGO = a.ID "
                + "WHERE oa.ID_OCORRENCIA = ? ORDER BY a.NOME";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Abrigo a = AbrigoDAO.montar(rs);
                    o.ativarAbrigo(a);
                }
            }
        }
    }

    private Ocorrencia montar(ResultSet rs) throws SQLException {
        Cidadao c = new Cidadao(rs.getInt("C_ID"), rs.getString("NOME"), rs.getString("EMAIL"),
                rs.getString("TELEFONE"), rs.getString("ENDERECO"), rs.getString("BAIRRO"));
        return Ocorrencia.nova(rs.getString("TIPO"), rs.getInt("O_ID"), rs.getString("DESCRICAO"),
                rs.getTimestamp("DATA_HORA").toLocalDateTime(), rs.getInt("SEVERIDADE"), c);
    }
}

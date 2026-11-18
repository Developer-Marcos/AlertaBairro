package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Abrigo;
import util.Conexao;

public class AbrigoDAO {

    public void inserir(Abrigo a) {
        String sql = "INSERT INTO TB_ABRIGO (NOME, CAPACIDADE, VAGAS_DISPONIVEIS, LOGRADOURO, BAIRRO) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNome());
            ps.setInt(2, a.getCapacidade());
            ps.setInt(3, a.getVagasDisponiveis());
            ps.setString(4, a.getLogradouro());
            ps.setString(5, a.getBairro());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public List<Abrigo> listar() {
        List<Abrigo> lista = new ArrayList<>();
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_ABRIGO ORDER BY NOME");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
        return lista;
    }

    public Abrigo buscarPorId(int id) {
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_ABRIGO WHERE ID = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public boolean atualizar(Abrigo a) {
        String sql = "UPDATE TB_ABRIGO SET NOME = ?, CAPACIDADE = ?, VAGAS_DISPONIVEIS = ?, LOGRADOURO = ?, BAIRRO = ? WHERE ID = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNome());
            ps.setInt(2, a.getCapacidade());
            ps.setInt(3, a.getVagasDisponiveis());
            ps.setString(4, a.getLogradouro());
            ps.setString(5, a.getBairro());
            ps.setInt(6, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public boolean excluir(int id) {
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement("DELETE FROM TB_ABRIGO WHERE ID = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    static Abrigo montar(ResultSet rs) throws SQLException {
        return new Abrigo(rs.getInt("ID"), rs.getString("NOME"), rs.getInt("CAPACIDADE"),
                rs.getInt("VAGAS_DISPONIVEIS"), rs.getString("LOGRADOURO"), rs.getString("BAIRRO"));
    }
}

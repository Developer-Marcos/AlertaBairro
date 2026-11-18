package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Cidadao;
import util.Conexao;

public class CidadaoDAO {

    public void inserir(Cidadao c) {
        String sql = "INSERT INTO TB_CIDADAO (NOME, EMAIL, TELEFONE, ENDERECO, BAIRRO) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getTelefone());
            ps.setString(4, c.getEndereco());
            ps.setString(5, c.getBairro());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public List<Cidadao> listar() {
        List<Cidadao> lista = new ArrayList<>();
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_CIDADAO ORDER BY NOME");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
        return lista;
    }

    public Cidadao buscarPorId(int id) {
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_CIDADAO WHERE ID = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public boolean atualizar(Cidadao c) {
        String sql = "UPDATE TB_CIDADAO SET NOME = ?, EMAIL = ?, TELEFONE = ?, ENDERECO = ?, BAIRRO = ? WHERE ID = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getTelefone());
            ps.setString(4, c.getEndereco());
            ps.setString(5, c.getBairro());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    public boolean excluir(int id) {
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement("DELETE FROM TB_CIDADAO WHERE ID = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw Conexao.traduzir(e);
        }
    }

    static Cidadao montar(ResultSet rs) throws SQLException {
        return new Cidadao(rs.getInt("ID"), rs.getString("NOME"), rs.getString("EMAIL"),
                rs.getString("TELEFONE"), rs.getString("ENDERECO"), rs.getString("BAIRRO"));
    }
}

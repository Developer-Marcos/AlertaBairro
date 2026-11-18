package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {
    private static final String URL = "jdbc:h2:./alertabairro";

    public static Connection abrir() {
        try {
            return DriverManager.getConnection(URL, "sa", "");
        } catch (SQLException e) {
            throw new DaoException("Nao foi possivel conectar ao banco: " + e.getMessage(), e);
        }
    }

    public static void inicializar() {
        try (Connection c = abrir(); Statement s = c.createStatement()) {
            ResultSet r = s.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'TB_CIDADAO'");
            r.next();
            if (r.getInt(1) == 0) {
                s.execute(Files.readString(Path.of("database", "script.sql")));
            }
        } catch (SQLException | IOException e) {
            throw new DaoException("Falha ao preparar o banco: " + e.getMessage(), e);
        }
    }

    public static RuntimeException traduzir(SQLException e) {
        String estado = e.getSQLState();
        if ("23505".equals(estado)) return new ValidacaoException("Registro duplicado: esse valor ja existe.");
        if ("23503".equals(estado)) return new ValidacaoException("Violacao de integridade: registro relacionado inexistente ou em uso.");
        if ("23513".equals(estado)) return new ValidacaoException("Valor nao permitido pelas regras do banco.");
        return new DaoException("Erro de banco: " + e.getMessage(), e);
    }
}

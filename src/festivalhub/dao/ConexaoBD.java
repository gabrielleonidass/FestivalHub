package festivalhub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centraliza a conexao JDBC com o MySQL.
 * Ajuste URL, USUARIO e SENHA para o seu ambiente
 * (ou passe -Ddb.url=... -Ddb.user=... -Ddb.password=... ao rodar).
 */
public class ConexaoBD {

    private static final String URL = System.getProperty("db.url",
            "jdbc:mysql://localhost:3306/festivalhub?useSSL=false&serverTimezone=America/Bahia&allowPublicKeyRetrieval=true");
    private static final String USUARIO = System.getProperty("db.user", "root");
    private static final String SENHA   = System.getProperty("db.password", "3571");

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}

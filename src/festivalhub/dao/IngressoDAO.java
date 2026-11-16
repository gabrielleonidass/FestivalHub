package festivalhub.dao;

import java.math.BigDecimal;
import java.sql.*;

/** Venda (procedure), cancelamento (UPDATE) e consulta com JOIN de ingressos. */
public class IngressoDAO {

    /** Chama a procedure sp_vender_ingresso, que aplica a RN3 (capacidade). */
    public int vender(int idParticipante, int idFestival, String tipo, BigDecimal preco) throws SQLException {
        String sql = "{CALL sp_vender_ingresso(?, ?, ?, ?, ?)}";
        try (Connection con = ConexaoBD.getConexao();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idParticipante);
            cs.setInt(2, idFestival);
            cs.setString(3, tipo);
            cs.setBigDecimal(4, preco);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.execute();
            return cs.getInt(5);
        }
    }

    /** UPDATE: cancela o ingresso (ele deixa de contar no faturamento - RN4). */
    public boolean cancelar(int idIngresso) throws SQLException {
        String sql = "UPDATE ingresso SET status = 'cancelado' WHERE id_ingresso = ? AND status = 'ativo'";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idIngresso);
            return ps.executeUpdate() > 0;
        }
    }

    /** SELECT com JOIN: ingressos de um festival com o nome do comprador. */
    public void listarPorFestival(int idFestival) throws SQLException {
        String sql = "SELECT i.id_ingresso, pa.nome_participante, f.nome_festival, i.tipo, i.preco, i.status "
                   + "FROM ingresso i "
                   + "JOIN participante pa ON pa.id_participante = i.id_participante "
                   + "JOIN festival f ON f.id_festival = i.id_festival "
                   + "WHERE i.id_festival = ? "
                   + "ORDER BY pa.nome_participante";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFestival);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.printf("%-5s %-20s %-10s %10s  %s%n", "ID", "Participante", "Tipo", "Preco", "Status");
                while (rs.next()) {
                    System.out.printf("%-5d %-20s %-10s %10.2f  %s%n",
                            rs.getInt("id_ingresso"), rs.getString("nome_participante"),
                            rs.getString("tipo"), rs.getBigDecimal("preco"), rs.getString("status"));
                }
            }
        }
    }
}

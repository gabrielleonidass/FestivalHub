package festivalhub.dao;

import java.math.BigDecimal;
import java.sql.*;

/** Relatorios: GROUP BY / HAVING / ORDER BY e uso das FUNCTIONS. */
public class RelatorioDAO {

    /** Consulta integrada: JOIN + WHERE + GROUP BY + HAVING + ORDER BY. */
    public void faturamentoPorFestival(BigDecimal minimo) throws SQLException {
        String sql = "SELECT f.nome_festival, COUNT(i.id_ingresso) AS ingressos_validos, "
                   + "       SUM(i.preco) AS faturamento, ROUND(AVG(i.preco), 2) AS ticket_medio "
                   + "FROM festival f "
                   + "JOIN ingresso i ON i.id_festival = f.id_festival "
                   + "WHERE i.status <> 'cancelado' "
                   + "GROUP BY f.id_festival, f.nome_festival "
                   + "HAVING SUM(i.preco) >= ? "
                   + "ORDER BY faturamento DESC";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, minimo);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.printf("%-24s %9s %12s %12s%n", "Festival", "Ingressos", "Faturamento", "Ticket medio");
                while (rs.next()) {
                    System.out.printf("%-24s %9d %12.2f %12.2f%n",
                            rs.getString("nome_festival"), rs.getInt("ingressos_validos"),
                            rs.getBigDecimal("faturamento"), rs.getBigDecimal("ticket_medio"));
                }
            }
        }
    }

    /** Artistas com pelo menos N shows (GROUP BY + HAVING + ORDER BY). */
    public void artistasMaisEscalados(int minimoShows) throws SQLException {
        String sql = "SELECT ar.nome_artista, ar.genero_musical, COUNT(a.id_apresentacao) AS total_shows "
                   + "FROM artista ar JOIN apresentacao a ON a.id_artista = ar.id_artista "
                   + "GROUP BY ar.id_artista, ar.nome_artista, ar.genero_musical "
                   + "HAVING COUNT(a.id_apresentacao) >= ? "
                   + "ORDER BY total_shows DESC, ar.nome_artista";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, minimoShows);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.printf("%-22s %-14s %s%n", "Artista", "Genero", "Shows");
                while (rs.next()) {
                    System.out.printf("%-22s %-14s %d%n", rs.getString(1), rs.getString(2), rs.getInt(3));
                }
            }
        }
    }

    /** Uso das FUNCTIONS fn_faturamento_festival e fn_percentual_ocupacao. */
    public void indicadoresFestivais() throws SQLException {
        String sql = "SELECT nome_festival, "
                   + "       fn_faturamento_festival(id_festival) AS faturamento, "
                   + "       fn_percentual_ocupacao(id_festival)  AS ocupacao "
                   + "FROM festival ORDER BY faturamento DESC";
        try (Connection con = ConexaoBD.getConexao();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-24s %12s %10s%n", "Festival", "Faturamento", "Ocupacao");
            while (rs.next()) {
                System.out.printf("%-24s %12.2f %9.2f%%%n",
                        rs.getString(1), rs.getBigDecimal(2), rs.getBigDecimal(3));
            }
        }
    }
}

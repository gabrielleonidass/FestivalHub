package festivalhub.dao;

import java.sql.*;

/** Agenda shows pela procedure e consulta a programacao pela VIEW. */
public class ApresentacaoDAO {

    /** Chama sp_agendar_apresentacao (RN1: sem choque de horario; RN2: dentro das datas). */
    public int agendar(int idArtista, int idPalco, String data, String inicio, String fim) throws SQLException {
        String sql = "{CALL sp_agendar_apresentacao(?, ?, ?, ?, ?, ?)}";
        try (Connection con = ConexaoBD.getConexao();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idArtista);
            cs.setInt(2, idPalco);
            cs.setString(3, data);    // 'AAAA-MM-DD' -> o MySQL converte para DATE
            cs.setString(4, inicio);  // 'HH:MM'      -> o MySQL converte para TIME
            cs.setString(5, fim);
            cs.registerOutParameter(6, Types.INTEGER);
            cs.execute();
            return cs.getInt(6);
        }
    }

    /** Uso da VIEW vw_programacao no Java. */
    public void listarProgramacao(int idFestival) throws SQLException {
        String sql = "SELECT nome_festival, nome_palco, data_apresentacao, hora_inicio, hora_fim, nome_artista "
                   + "FROM vw_programacao WHERE id_festival = ? "
                   + "ORDER BY data_apresentacao, hora_inicio";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFestival);
            try (ResultSet rs = ps.executeQuery()) {
                boolean primeiro = true;
                while (rs.next()) {
                    if (primeiro) {
                        System.out.println("Programacao - " + rs.getString("nome_festival"));
                        primeiro = false;
                    }
                    System.out.printf("%s  %s-%s  %-18s %s%n",
                            rs.getDate("data_apresentacao"),
                            rs.getString("hora_inicio").substring(0, 5),
                            rs.getString("hora_fim").substring(0, 5),
                            rs.getString("nome_palco"), rs.getString("nome_artista"));
                }
                if (primeiro) System.out.println("Nenhum show cadastrado para este festival.");
            }
        }
    }
}

package festivalhub.dao;

import festivalhub.model.Participante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** CRUD completo da tabela PARTICIPANTE. */
public class ParticipanteDAO {

    public int inserir(Participante p) throws SQLException {
        String sql = "INSERT INTO participante (nome_participante, cpf, email) VALUES (?, ?, ?)";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCpf());
            ps.setString(3, p.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
            return p.getId();
        }
    }

    public List<Participante> listar() throws SQLException {
        String sql = "SELECT id_participante, nome_participante, cpf, email FROM participante ORDER BY nome_participante";
        List<Participante> lista = new ArrayList<>();
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Participante(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        }
        return lista;
    }

    public boolean atualizar(Participante p) throws SQLException {
        String sql = "UPDATE participante SET nome_participante = ?, cpf = ?, email = ? WHERE id_participante = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCpf());
            ps.setString(3, p.getEmail());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM participante WHERE id_participante = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

package festivalhub.dao;

import festivalhub.model.Artista;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** CRUD completo da tabela ARTISTA. */
public class ArtistaDAO {

    // CREATE
    public int inserir(Artista a) throws SQLException {
        String sql = "INSERT INTO artista (nome_artista, genero_musical) VALUES (?, ?)";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNome());
            ps.setString(2, a.getGeneroMusical());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1));
            }
            return a.getId();
        }
    }

    // READ (todos)
    public List<Artista> listar() throws SQLException {
        String sql = "SELECT id_artista, nome_artista, genero_musical FROM artista ORDER BY nome_artista";
        List<Artista> lista = new ArrayList<>();
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Artista(rs.getInt("id_artista"),
                                      rs.getString("nome_artista"),
                                      rs.getString("genero_musical")));
            }
        }
        return lista;
    }

    // READ (por id)
    public Artista buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_artista, nome_artista, genero_musical FROM artista WHERE id_artista = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Artista(rs.getInt(1), rs.getString(2), rs.getString(3));
                }
            }
        }
        return null;
    }

    // UPDATE
    public boolean atualizar(Artista a) throws SQLException {
        String sql = "UPDATE artista SET nome_artista = ?, genero_musical = ? WHERE id_artista = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNome());
            ps.setString(2, a.getGeneroMusical());
            ps.setInt(3, a.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // DELETE (a FK com RESTRICT impede apagar artista que tem shows)
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM artista WHERE id_artista = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

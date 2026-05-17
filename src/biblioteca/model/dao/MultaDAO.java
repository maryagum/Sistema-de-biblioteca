package biblioteca.model.dao;

import biblioteca.model.Multa;
import biblioteca.model.Usuario;
import biblioteca.model.Livro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MultaDAO {

    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public List<Multa> buscarMultasPendentesPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT m.id, m.valor, l.nome AS nome_livro " +
                     "FROM Multa m " +
                     "INNER JOIN Livro l ON m.id_livro = l.id " +
                     "WHERE m.id_usuario = ? AND m.isQuitado = FALSE";

        List<Multa> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Multa m = new Multa();
                    m.setId(rs.getInt("id"));
                    m.setValor(rs.getDouble("valor"));

                    Livro l = new Livro();
                    l.setNome(rs.getString("nome_livro"));
                    m.setLivro(l);

                    lista.add(m);
                }
            }
        }
        return lista;
    }

    public void quitarMulta(int idMulta, int idUsuario) throws SQLException {
        String sqlQuitar = "UPDATE Multa SET isQuitado = TRUE WHERE id = ?";
        String sqlVerificarRestantes = "SELECT COUNT(*) FROM Multa WHERE id_usuario = ? AND isQuitado = FALSE";
        String sqlLiberarUsuario = "UPDATE Usuario SET isMultado = FALSE WHERE id = ?";

        PreparedStatement stmtQuitar = null;
        PreparedStatement stmtCheck = null;
        PreparedStatement stmtUsuario = null;

        try {
            connection.setAutoCommit(false); 

            stmtQuitar = connection.prepareStatement(sqlQuitar);
            stmtQuitar.setInt(1, idMulta);
            stmtQuitar.executeUpdate();

            stmtCheck = connection.prepareStatement(sqlVerificarRestantes);
            stmtCheck.setInt(1, idUsuario);
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                stmtUsuario = connection.prepareStatement(sqlLiberarUsuario);
                stmtUsuario.setInt(1, idUsuario);
                stmtUsuario.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (stmtQuitar != null) stmtQuitar.close();
            if (stmtCheck != null) stmtCheck.close();
            if (stmtUsuario != null) stmtUsuario.close();
            connection.setAutoCommit(true);
        }
    }
}
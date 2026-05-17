package biblioteca.model.dao;

import biblioteca.model.GeneroLiterario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneroLiterarioDAO {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public List<GeneroLiterario> listar() {
        String sql = "SELECT * FROM Genero_Literario ORDER BY nome";
        List<GeneroLiterario> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                GeneroLiterario genero = new GeneroLiterario();
                genero.setId(rs.getInt("id"));
                genero.setNome(rs.getString("nome"));
                retorno.add(genero);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeneroLiterarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public boolean inserir(GeneroLiterario genero) {
        String sql = "INSERT INTO Genero_Literario(nome) VALUES(?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, genero.getNome());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(GeneroLiterarioDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
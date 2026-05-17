package biblioteca.model.dao;

import biblioteca.model.Autor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutorDAO {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public List<Autor> listar() {
        String sql = "SELECT * FROM Autor ORDER BY nome";
        List<Autor> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Autor autor = new Autor();
                autor.setId(rs.getInt("id"));
                autor.setNome(rs.getString("nome"));
                autor.setNacionalidade(rs.getString("nacionalidade"));
                retorno.add(autor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AutorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public boolean inserir(Autor autor) {
        String sql = "INSERT INTO Autor(nome, nacionalidade) VALUES(?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, autor.getNome());
            stmt.setString(2, autor.getNacionalidade());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AutorDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
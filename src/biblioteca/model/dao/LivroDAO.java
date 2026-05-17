package biblioteca.model.dao;

import biblioteca.model.Autor;
import biblioteca.model.GeneroLiterario;
import biblioteca.model.Livro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {
    private Connection connection;

    public void setConnection(Connection connection) { this.connection = connection; }
    
    public boolean inserir(Livro livro) {
        String sql = "INSERT INTO livro (nome, isbn, autor_id, genero_id, isdisponivel) VALUES (?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, livro.getNome());
            stmt.setString(2, livro.getIsbn());
            
            stmt.setInt(3, livro.getAutor().getId());
            
            stmt.setInt(4, livro.getGenero().getId());
            
            stmt.setBoolean(5, true);
            
            stmt.execute();
            return true;
            
        } catch (SQLException ex) {
            System.err.println("Erro ao inserir livro: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public List<Livro> listarDisponiveis() {
        String sql = "SELECT l.*, a.nome AS nome_autor, g.nome AS nome_genero " +
                     "FROM Livro l " +
                     "INNER JOIN Autor a ON l.autor_id = a.id " +
                     "INNER JOIN Genero_Literario g ON l.genero_id = g.id " +
                     "WHERE l.isdisponivel = true";
                     
        List<Livro> retorno = new ArrayList<>();
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setNome(rs.getString("nome"));
                livro.setIsbn(rs.getString("isbn"));
                livro.setIsdisponivel(rs.getBoolean("isdisponivel"));

                Autor autor = new Autor();
                autor.setId(rs.getInt("autor_id"));
                autor.setNome(rs.getString("nome_autor"));
                livro.setAutor(autor);

                GeneroLiterario genero = new GeneroLiterario();
                genero.setId(rs.getInt("genero_id"));
                genero.setNome(rs.getString("nome_genero"));
                livro.setGenero(genero);

                retorno.add(livro);
            }
        } catch (SQLException ex) { 
            ex.printStackTrace(); 
        }
        return retorno;
    }
}
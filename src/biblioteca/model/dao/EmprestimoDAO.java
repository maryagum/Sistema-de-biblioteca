/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model.dao;

import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import biblioteca.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmprestimoDAO {
    private Connection connection;

    public void setConnection(Connection connection) { this.connection = connection; }
    
    public List<Emprestimo> buscarEmprestimosAtivosPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT e.id, e.descricao, e.data_emprestimo, e.data_prevista_devolucao, " +
                     "u.id AS id_user, u.nome AS nome_user, l.id AS id_livro, l.nome AS nome_livro " +
                     "FROM Emprestimo e " +
                     "INNER JOIN Usuario u ON e.id_usuario = u.id " +
                     "INNER JOIN Livro l ON e.id_livro = l.id " +
                     "WHERE e.id_usuario = ? AND e.data_devolucao_real IS NULL";
                     
        List<Emprestimo> lista = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Emprestimo e = new Emprestimo();
                    e.setId(rs.getInt("id"));
                    e.setDescricao(rs.getString("descricao"));
                    e.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
                    e.setDataPrevistaDevolucao(rs.getDate("data_prevista_devolucao").toLocalDate());
                    
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id_user"));
                    u.setNome(rs.getString("nome_user"));
                    e.setUsuario(u);
                    
                    Livro l = new Livro();
                    l.setId(rs.getInt("id_livro"));
                    l.setNome(rs.getString("nome_livro"));
                    e.setLivro(l);
                    
                    lista.add(e);
                }
            }
        }
        return lista;
    }

    public void registrarDevolucao(int idEmprestimo, LocalDate dataDevolucaoManual) throws SQLException {
        String sqlBuscarEmprestimo = "SELECT id_usuario, id_livro, data_prevista_devolucao FROM Emprestimo WHERE id = ?";
        String sqlAtualizarEmprestimo = "UPDATE Emprestimo SET data_devolucao_real = ? WHERE id = ?";
        String sqlLiberarLivro = "UPDATE Livro SET isDisponivel = TRUE WHERE id = ?";
        String sqlGerarMulta = "INSERT INTO Multa (valor, id_usuario, id_livro, id_emprestimo) VALUES (?, ?, ?, ?)";
        String sqlMultarUsuario = "UPDATE Usuario SET isMultado = TRUE WHERE id = ?";

        PreparedStatement stmtBusca = null;
        PreparedStatement stmtEmprestimo = null;
        PreparedStatement stmtLivro = null;
        PreparedStatement stmtMulta = null;
        PreparedStatement stmtUsuario = null;

        try {
            connection.setAutoCommit(false);

            stmtBusca = connection.prepareStatement(sqlBuscarEmprestimo);
            stmtBusca.setInt(1, idEmprestimo);
            ResultSet rs = stmtBusca.executeQuery();

            if (rs.next()) {
                int idUsuario = rs.getInt("id_usuario");
                int idLivro = rs.getInt("id_livro");
                LocalDate dataPrevista = rs.getDate("data_prevista_devolucao").toLocalDate();

                stmtEmprestimo = connection.prepareStatement(sqlAtualizarEmprestimo);
                stmtEmprestimo.setDate(1, java.sql.Date.valueOf(dataDevolucaoManual));
                stmtEmprestimo.setInt(2, idEmprestimo);
                stmtEmprestimo.executeUpdate();

                stmtLivro = connection.prepareStatement(sqlLiberarLivro);
                stmtLivro.setInt(1, idLivro);
                stmtLivro.executeUpdate();

                if (dataDevolucaoManual.isAfter(dataPrevista)) {
                    long diasAtraso = ChronoUnit.DAYS.between(dataPrevista, dataDevolucaoManual);
                    double valorMulta = diasAtraso * 1.00;

                    stmtMulta = connection.prepareStatement(sqlGerarMulta);
                    stmtMulta.setDouble(1, valorMulta);
                    stmtMulta.setInt(2, idUsuario);
                    stmtMulta.setInt(3, idLivro);
                    stmtMulta.setInt(4, idEmprestimo);
                    stmtMulta.executeUpdate();

                    stmtUsuario = connection.prepareStatement(sqlMultarUsuario);
                    stmtUsuario.setInt(1, idUsuario);
                    stmtUsuario.executeUpdate();
                }
            }

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (stmtBusca != null) stmtBusca.close();
            if (stmtEmprestimo != null) stmtEmprestimo.close();
            if (stmtLivro != null) stmtLivro.close();
            if (stmtMulta != null) stmtMulta.close();
            if (stmtUsuario != null) stmtUsuario.close();
            connection.setAutoCommit(true);
        }
    }
    
    public boolean realizarEmprestimo(Emprestimo e) {
        String sqlVerificarMulta = "SELECT COUNT(*) FROM Multa WHERE id_usuario = ? AND isQuitado = FALSE";
        String sqlVerificarAtrasoAtivo = "SELECT COUNT(*) FROM Emprestimo WHERE id_usuario = ? AND data_devolucao_real IS NULL AND data_prevista_devolucao < ?";
        String sqlEmprestimo = "INSERT INTO emprestimo(id_usuario, id_livro, descricao, data_emprestimo, data_prevista_devolucao) VALUES(?,?,?,?,?)";
        String sqlUpdateLivro = "UPDATE livro SET isdisponivel = false WHERE id = ?";

        try {
            connection.setAutoCommit(false);
            
            PreparedStatement stmtCheckMulta = connection.prepareStatement(sqlVerificarMulta);
            stmtCheckMulta.setInt(1, e.getUsuario().getId());
            ResultSet rsMulta = stmtCheckMulta.executeQuery();
            
            if (rsMulta.next() && rsMulta.getInt(1) > 0) {
                System.out.println("Empréstimo negado: Usuário possui multas financeiras pendentes.");
                stmtCheckMulta.close();
                connection.rollback();
                return false; 
            }
            stmtCheckMulta.close();
            
            PreparedStatement stmtCheckAtraso = connection.prepareStatement(sqlVerificarAtrasoAtivo);
            stmtCheckAtraso.setInt(1, e.getUsuario().getId());
            stmtCheckAtraso.setDate(2, java.sql.Date.valueOf(e.getDataEmprestimo()));
            ResultSet rsAtraso = stmtCheckAtraso.executeQuery();
            
            if (rsAtraso.next() && rsAtraso.getInt(1) > 0) {
                System.out.println("Empréstimo negado: Usuário possui livros com prazo de devolução estourado.");
                stmtCheckAtraso.close();
                connection.rollback();
                return false;
            }
            stmtCheckAtraso.close();
            
            PreparedStatement stmtE = connection.prepareStatement(sqlEmprestimo);
            stmtE.setInt(1, e.getUsuario().getId());
            stmtE.setInt(2, e.getLivro().getId());
            stmtE.setString(3, e.getDescricao());
            
            stmtE.setDate(4, java.sql.Date.valueOf(e.getDataEmprestimo()));
            stmtE.setDate(5, java.sql.Date.valueOf(e.getDataPrevistaDevolucao()));
            
            stmtE.execute();
            stmtE.close();

            PreparedStatement stmtL = connection.prepareStatement(sqlUpdateLivro);
            stmtL.setInt(1, e.getLivro().getId());
            stmtL.execute();
            stmtL.close();

            connection.commit();
            return true;
        } catch (SQLException ex) {
            try { connection.rollback(); } catch (SQLException e1) {}
            ex.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e1) {}
        }
    }


    public Map<String, Integer> obterQuantidadeEmprestimosPorMes(int ano) throws SQLException {
        String sql = "SELECT TO_CHAR(data_emprestimo, 'MM') as mes, COUNT(*) as total " +
                     "FROM Emprestimo " +
                     "WHERE EXTRACT(YEAR FROM data_emprestimo) = ? " +
                     "GROUP BY mes " +
                     "ORDER BY mes";
                     
        Map<String, Integer> dados = new LinkedHashMap<>();
        String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        
        for (int i = 0; i < 12; i++) {
            dados.put(nomesMeses[i], 0);
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ano);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int mesNum = Integer.parseInt(rs.getString("mes")) - 1;
                    if (mesNum >= 0 && mesNum < 12) {
                        dados.put(nomesMeses[mesNum], rs.getInt("total"));
                    }
                }
            }
        }
        return dados;
    }

    public Map<String, Integer> obterStatusPrazosDevolucao() throws SQLException {
        String sql = "SELECT " +
                     "  COUNT(CASE WHEN data_devolucao_real <= data_prevista_devolucao THEN 1 END) as dentro_prazo, " +
                     "  COUNT(CASE WHEN data_devolucao_real > data_prevista_devolucao THEN 1 END) as fora_prazo " +
                     "FROM Emprestimo " +
                     "WHERE data_devolucao_real IS NOT NULL";

        Map<String, Integer> dados = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                dados.put("Dentro do Prazo", rs.getInt("dentro_prazo"));
                dados.put("Fora do Prazo", rs.getInt("fora_prazo"));
            }
        }
        return dados;
    }
}
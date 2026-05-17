package biblioteca.controller;

import biblioteca.model.dao.EmprestimoDAO;
import biblioteca.model.database.Database;
import biblioteca.model.database.DatabaseFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GraficosDashboardController implements Initializable {

    @FXML private BarChart<String, Integer> barChartEmprestimos;
    @FXML private CategoryAxis xAxisMeses;
    @FXML private PieChart pieChartPrazos;
    @FXML private Button btnVoltar; 
    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        emprestimoDAO.setConnection(connection);
        carregarGraficoEmprestimos();
        carregarGraficoPrazos();
    }

    @FXML
    private void handleVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/PaginaInicial.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Sistema de Biblioteca - Menu Principal");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            if (btnVoltar != null && btnVoltar.getScene() != null) {
                ((Stage) btnVoltar.getScene().getWindow()).close();
            }

        } catch (IOException e) {
            System.err.println("Erro ao retornar para a página inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarGraficoEmprestimos() {
        try {
            Map<String, Integer> dadosBanco = emprestimoDAO.obterQuantidadeEmprestimosPorMes(2026);
            
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName("Livros Emprestados");
            
            for (Map.Entry<String, Integer> entry : dadosBanco.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            
            barChartEmprestimos.getData().add(series);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void carregarGraficoPrazos() {
        try {
            Map<String, Integer> dadosBanco = emprestimoDAO.obterStatusPrazosDevolucao();
            
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("No Prazo (" + dadosBanco.getOrDefault("Dentro do Prazo", 0) + ")", dadosBanco.getOrDefault("Dentro do Prazo", 0)),
                new PieChart.Data("Em Atraso (" + dadosBanco.getOrDefault("Fora do Prazo", 0) + ")", dadosBanco.getOrDefault("Fora do Prazo", 0))
            );
            
            pieChartPrazos.setData(pieChartData);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
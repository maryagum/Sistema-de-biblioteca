package biblioteca.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem; 
import javafx.stage.Stage;

public class PaginaInicialController implements Initializable {

    @FXML
    private Button buttonUsuario;
    @FXML 
    private Button buttonEmprestimo;
    @FXML 
    private Button buttonLivro;
    @FXML 
    private Button buttonAutor;
    @FXML 
    private Button buttonGenero;
    @FXML
    private Button buttonDevolucao;
    @FXML
    private Button buttonMulta;
    @FXML
    private MenuItem menuItemRelatorioDevolucao;
    @FXML
    private MenuItem menuItemGraficos; 
    
    @FXML
    private MenuItem menuItemRelatorioMulta;

    @FXML
    private void handleMenuRelatorioMulta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/RelatorioMulta.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Menu de Relatórios - Multas");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            if (buttonUsuario != null && buttonUsuario.getScene() != null) {
                ((Stage) buttonUsuario.getScene().getWindow()).close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de relatório de multas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMenuRelatorioDevolucao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/RelatorioDevolucao.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Menu de Relatórios - Devoluções");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            if (buttonUsuario != null && buttonUsuario.getScene() != null) {
                ((Stage) buttonUsuario.getScene().getWindow()).close();
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de relatório de devoluções: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
   
    @FXML
    private void handleMenuGraficos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/GraficosDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Dashboard de Estatísticas");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            if (buttonUsuario != null && buttonUsuario.getScene() != null) {
                ((Stage) buttonUsuario.getScene().getWindow()).close();
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de gráficos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleButtonMulta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/Multa.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gerenciamento de multas");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            ((Stage) buttonDevolucao.getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de multas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleButtonDevolucao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/Devolucao.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gerenciamento de devoluções");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            ((Stage) buttonDevolucao.getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de devoluções: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleButtonGenero() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/CadastroGeneroLiterario.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gerenciamento de gêneros literários");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            ((Stage) buttonGenero.getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de gêneros: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleButtonAutor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/CadastroAutor.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gerenciamento de autores");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            ((Stage) buttonAutor.getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de autores: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleButtonLivro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/FXMLCadastrarLivro.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gerenciamento de livros");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            ((Stage) buttonLivro.getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de livros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleButtonUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/CadastroUsuario.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gerenciamento de Usuários");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            ((Stage) buttonUsuario.getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de usuários: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleButtonEmprestimo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/Emprestimo.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Serviço de Empréstimo");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            ((Stage) buttonEmprestimo.getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de empréstimo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
}
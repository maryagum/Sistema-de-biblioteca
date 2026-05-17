/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.Usuario;
import biblioteca.model.dao.UsuarioDAO;
import biblioteca.model.database.Database;
import biblioteca.model.database.DatabaseFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroUsuarioController implements Initializable {
    @FXML private TextField textFieldNome;
    @FXML private TextField textFieldCPF;
    @FXML private TextField textFieldEmail;
    @FXML private TextField textFieldTelefone;
    @FXML private TextField textFieldEndereco;
    @FXML private Button buttonVoltar;

    
    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO.setConnection(connection);
    }

    @FXML
    public void handleButtonCadastrar() {
        Usuario usuario = new Usuario();
        usuario.setNome(textFieldNome.getText());
        usuario.setCpf(textFieldCPF.getText());
        usuario.setEmail(textFieldEmail.getText());
        usuario.setTelefone(textFieldTelefone.getText());
        usuario.setEndereco(textFieldEndereco.getText());
        usuario.setIsMultado(false); 

        if (usuarioDAO.inserir(usuario)) {
            exibirAlerta("Sucesso", "Usuário cadastrado com sucesso!", Alert.AlertType.INFORMATION);
        
       
            voltarParaHome();
        
        } else {
            exibirAlerta("Erro", "Falha ao realizar o cadastro. Verifique se o CPF já existe.", Alert.AlertType.ERROR);
        }
}

    private void voltarParaHome() {
        try {
            
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/biblioteca/view/PaginaInicial.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) textFieldNome.getScene().getWindow();

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.setTitle("Sistema de Biblioteca - Home");
            stage.setResizable(false);
            stage.show();

        } catch (java.io.IOException e) {
            System.err.println("Erro ao voltar para a tela inicial: " + e.getMessage());
        }
    }

    private void limparFormulario() {
        textFieldNome.clear();
        textFieldCPF.clear();
        textFieldEmail.clear();
        textFieldTelefone.clear();
        textFieldEndereco.clear();
        textFieldNome.requestFocus(); 
    }

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    @FXML
    private void handleButtonVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/PaginaInicial.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Sistema de Biblioteca");
            stage.setScene(new Scene(root));
            stage.show();
            Stage stageAtual = (Stage) buttonVoltar.getScene().getWindow();
            stageAtual.close();

        } catch (IOException e) {
            System.err.println("Erro ao voltar para a página inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
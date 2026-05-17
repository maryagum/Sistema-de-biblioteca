package biblioteca.controller;

import biblioteca.model.GeneroLiterario;
import biblioteca.model.dao.GeneroLiterarioDAO;
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

public class CadastroGeneroLiterarioController implements Initializable {

    @FXML private TextField textFieldNome;
    @FXML private Button buttonCadastrar;
    @FXML private Button buttonVoltar;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final GeneroLiterarioDAO generoDAO = new GeneroLiterarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        generoDAO.setConnection(connection);
    }

    @FXML
    public void handleButtonCadastrar() {
        if (validarEntradaDeDados()) {
            GeneroLiterario genero = new GeneroLiterario();
            genero.setNome(textFieldNome.getText());

            if (generoDAO.inserir(genero)) {
                exibirAlerta("Sucesso", "Gênero literário cadastrado com sucesso!", Alert.AlertType.INFORMATION);
                limparCampos();
            } else {
                exibirAlerta("Erro", "Falha ao cadastrar no banco de dados.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleButtonVoltar() {
        try {
          
            Stage stage = (Stage) buttonVoltar.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/biblioteca/view/PaginaInicial.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validarEntradaDeDados() {
        String mensagemErro = "";
        if (textFieldNome.getText() == null || textFieldNome.getText().trim().isEmpty()) {
            mensagemErro += "O nome do gênero é obrigatório!\n";
        }
        
        if (mensagemErro.length() == 0) {
            return true;
        } else {
            exibirAlerta("Campos inválidos", mensagemErro, Alert.AlertType.WARNING);
            return false;
        }
    }

    private void limparCampos() {
        textFieldNome.clear();
    }

    private void exibirAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
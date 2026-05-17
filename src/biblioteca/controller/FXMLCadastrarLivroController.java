package biblioteca.controller;

import biblioteca.model.Autor;
import biblioteca.model.GeneroLiterario;
import biblioteca.model.Livro;
import biblioteca.model.dao.AutorDAO;
import biblioteca.model.dao.GeneroLiterarioDAO;
import biblioteca.model.dao.LivroDAO;
import biblioteca.model.database.Database;
import biblioteca.model.database.DatabaseFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLCadastrarLivroController implements Initializable {

    @FXML private TextField textFieldNome;
    @FXML private TextField textFieldIsbn;
    private TextField textFieldEdicao;
    @FXML private ComboBox<Autor> comboBoxAutor;
    @FXML private ComboBox<GeneroLiterario> comboBoxGenero;
    @FXML private Button buttonCadastrar;
    @FXML private Button buttonVoltar;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final LivroDAO livroDAO = new LivroDAO();
    private final AutorDAO autorDAO = new AutorDAO();
    private final GeneroLiterarioDAO generoDAO = new GeneroLiterarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        livroDAO.setConnection(connection);
        autorDAO.setConnection(connection);
        generoDAO.setConnection(connection);
        
        carregarComboBoxes();
    }

    public void carregarComboBoxes() {
        List<Autor> listAutores = autorDAO.listar();
        List<GeneroLiterario> listGeneros = generoDAO.listar();

        comboBoxAutor.setItems(FXCollections.observableArrayList(listAutores));
        comboBoxGenero.setItems(FXCollections.observableArrayList(listGeneros));
    }

    @FXML
    public void handleButtonCadastrar() {
        Livro livro = new Livro();
        livro.setNome(textFieldNome.getText());
        livro.setIsbn(textFieldIsbn.getText());
        livro.setIsdisponivel(true); 
        livro.setAutor(comboBoxAutor.getSelectionModel().getSelectedItem());
        livro.setGenero(comboBoxGenero.getSelectionModel().getSelectedItem());

        if (validarEntradaDeDados()) {
            if (livroDAO.inserir(livro)) {
                exibirAlerta("Sucesso", "Livro cadastrado com sucesso!", Alert.AlertType.INFORMATION);
                limparCampos();
            } else {
                exibirAlerta("Erro", "Erro ao cadastrar livro no banco.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleButtonVoltar() throws IOException {
        Stage stage = (Stage) buttonVoltar.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/biblioteca/view/PaginaInicial.fxml"));
        stage.setScene(new Scene(root));
    }

    private boolean validarEntradaDeDados() {
        String er = "";
        if (textFieldNome.getText().isEmpty()) er += "Nome inválido!\n";
        if (comboBoxAutor.getValue() == null) er += "Selecione um Autor!\n";
        if (comboBoxGenero.getValue() == null) er += "Selecione um Gênero!\n";
        
        if (er.length() == 0) return true;
        else {
            exibirAlerta("Campos Obrigatórios", er, Alert.AlertType.WARNING);
            return false;
        }
    }

    private void limparCampos() {
        textFieldNome.clear();
        textFieldIsbn.clear();
        textFieldEdicao.clear();
        comboBoxAutor.getSelectionModel().clearSelection();
        comboBoxGenero.getSelectionModel().clearSelection();
    }

    private void exibirAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
package biblioteca.controller;

import biblioteca.model.Multa;
import biblioteca.model.Usuario;
import biblioteca.model.dao.MultaDAO;
import biblioteca.model.dao.UsuarioDAO;
import biblioteca.model.database.Database;
import biblioteca.model.database.DatabaseFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MultaController implements Initializable {

    @FXML private ComboBox<Usuario> comboBoxUsuario;
    @FXML private TableView<Multa> tableViewMultas;
    @FXML private TableColumn<Multa, String> tableColumnLivro;
    @FXML private TableColumn<Multa, Double> tableColumnValor;
    @FXML private Button buttonQuitar;
    @FXML private Button buttonVoltar;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final MultaDAO multaDAO = new MultaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private ObservableList<Usuario> obsUsuarios;
    private ObservableList<Multa> obsMultas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        multaDAO.setConnection(connection);
        usuarioDAO.setConnection(connection);

        configurarColunasTabela();
        carregarUsuariosComboBox();

        comboBoxUsuario.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    carregarTableMultas(newValue.getId());
                }
            }
        );
    }

    private void configurarColunasTabela() {
        tableColumnLivro.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLivro().getNome()));
        
        tableColumnValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
    }

    private void carregarUsuariosComboBox() {
        List<Usuario> lista = usuarioDAO.listar();
        obsUsuarios = FXCollections.observableArrayList(lista);
        comboBoxUsuario.setItems(obsUsuarios);
    }

    private void carregarTableMultas(int idUsuario) {
        try {
            List<Multa> lista = multaDAO.buscarMultasPendentesPorUsuario(idUsuario);
            obsMultas = FXCollections.observableArrayList(lista);
            tableViewMultas.setItems(obsMultas);
        } catch (SQLException e) {
            exibirAlerta("Erro", "Falha ao buscar multas.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleButtonQuitar() {
        Multa multaSelecionada = tableViewMultas.getSelectionModel().getSelectedItem();
        Usuario usuarioSelecionado = comboBoxUsuario.getSelectionModel().getSelectedItem();

        if (multaSelecionada == null || usuarioSelecionado == null) {
            exibirAlerta("Seleção Requerida", "Selecione um usuário e a multa que deseja quitar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            multaDAO.quitarMulta(multaSelecionada.getId(), usuarioSelecionado.getId());
            exibirAlerta("Sucesso", "Multa quitada com sucesso!", Alert.AlertType.INFORMATION);
            
            carregarTableMultas(usuarioSelecionado.getId());
        } catch (SQLException e) {
            exibirAlerta("Erro", "Erro ao processar o pagamento no banco.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleButtonVoltar() {
        try {
            Stage stage = (Stage) buttonVoltar.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/biblioteca/view/PaginaInicial.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            exibirAlerta("Erro", "Não foi possível voltar para a página inicial.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void exibirAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
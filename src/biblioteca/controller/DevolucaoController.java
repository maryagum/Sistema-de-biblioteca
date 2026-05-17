package biblioteca.controller;

import biblioteca.model.Emprestimo;
import biblioteca.model.Usuario;
import biblioteca.model.Livro;
import biblioteca.model.dao.EmprestimoDAO;
import biblioteca.model.dao.UsuarioDAO;
import biblioteca.model.database.Database;
import biblioteca.model.database.DatabaseFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import javafx.scene.control.DatePicker; 
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class DevolucaoController implements Initializable {

    @FXML private ComboBox<Usuario> comboBoxUsuario;
    @FXML private TableView<Emprestimo> tableViewEmprestimos;
    @FXML private TableColumn<Emprestimo, String> tableColumnUsuario;
    @FXML private TableColumn<Emprestimo, String> tableColumnLivro;
    @FXML private TableColumn<Emprestimo, LocalDate> tableColumnDataPrevista;
    @FXML private Button buttonConfirmar; 
    @FXML private Button buttonVoltar;    

    
    @FXML private DatePicker datePickerDevolucaoReal;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final EmprestimoDAO mapEmprestimoDAO = new EmprestimoDAO(); 
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private ObservableList<Usuario> obsUsuarios;
    private ObservableList<Emprestimo> obsEmprestimos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapEmprestimoDAO.setConnection(connection);
        usuarioDAO.setConnection(connection);
        configurarColunasTabela();
        carregarUsuariosComboBox();
        
        comboBoxUsuario.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    carregarTableEmprestimos(newValue.getId());
                }
            }
        );
    }

    private void configurarColunasTabela() {
        tableColumnUsuario.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsuario().getNome()));
        
        tableColumnLivro.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLivro().getNome()));
        
        tableColumnDataPrevista.setCellValueFactory(new PropertyValueFactory<>("dataPrevistaDevolucao"));

        tableColumnDataPrevista.setCellFactory(column -> new TableCell<Emprestimo, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    
                    if (item.isBefore(LocalDate.now())) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle(""); 
                    }
                }
            }
        });
    }

    private void carregarUsuariosComboBox() {
        List<Usuario> lista = usuarioDAO.listar(); 
        obsUsuarios = FXCollections.observableArrayList(lista);
        comboBoxUsuario.setItems(obsUsuarios);
    }

    private void carregarTableEmprestimos(int idUsuario) {
        try {
            List<Emprestimo> lista = mapEmprestimoDAO.buscarEmprestimosAtivosPorUsuario(idUsuario);
            obsEmprestimos = FXCollections.observableArrayList(lista);
            tableViewEmprestimos.setItems(obsEmprestimos);
        } catch (SQLException e) {
            exibirAlerta("Erro", "Falha ao carregar empréstimos do usuário.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleButtonConfirmar() {
        Emprestimo emprestimoSelecionado = tableViewEmprestimos.getSelectionModel().getSelectedItem();

        if (emprestimoSelecionado == null) {
            exibirAlerta("Seleção Requerida", "Por favor, selecione um empréstimo na tabela para prosseguir.", Alert.AlertType.WARNING);
            return;
        }

        try {
            LocalDate hoje = LocalDate.now();
            mapEmprestimoDAO.registrarDevolucao(emprestimoSelecionado.getId(), hoje);

            LocalDate dataPrevista = emprestimoSelecionado.getDataPrevistaDevolucao();

            if (hoje.isAfter(dataPrevista)) {
                long diasAtraso = ChronoUnit.DAYS.between(dataPrevista, hoje);
                double valorMulta = diasAtraso * 1.00;

                String msgMulta = String.format(
                    "Livro devolvido com sucesso!\n\n" +
                    "Status: PRAZO EXCEDIDO (%d dias)\n" +
                    "Uma multa foi gerada no valor de: R$ %.2f", 
                    diasAtraso, valorMulta
                );
                exibirAlerta("Atenção - Multa Gerada", msgMulta, Alert.AlertType.INFORMATION);
            } else {
                exibirAlerta("Sucesso", "Livro devolvido com sucesso e dentro do prazo!", Alert.AlertType.INFORMATION);
            }

            Usuario usuarioAtual = comboBoxUsuario.getSelectionModel().getSelectedItem();
            if (usuarioAtual != null) {
                carregarTableEmprestimos(usuarioAtual.getId());
            }

        } catch (SQLException e) {
            exibirAlerta("Erro de Transação", "Falha crítica ao registrar devolução no banco de dados.", Alert.AlertType.ERROR);
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
            exibirAlerta("Erro de Navegação", "Não foi possível retornar para a página inicial.", Alert.AlertType.ERROR);
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
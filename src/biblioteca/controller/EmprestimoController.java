package biblioteca.controller;

import biblioteca.model.Livro;
import biblioteca.model.Usuario;
import biblioteca.model.Emprestimo;
import biblioteca.model.dao.EmprestimoDAO;
import biblioteca.model.dao.LivroDAO;
import biblioteca.model.dao.UsuarioDAO;
import biblioteca.model.database.Database;
import biblioteca.model.database.DatabaseFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class EmprestimoController implements Initializable {

    @FXML private ComboBox<Usuario> comboBoxUsuario;
    @FXML private TableView<Livro> tableViewLivros;
    @FXML private TableColumn<Livro, String> tableColumnLivroNome;
    @FXML private TableColumn<Livro, String> tableColumnLivroAutor;
    @FXML private Button buttonConfirmarEmprestimo;
    @FXML private Button buttonVoltar;
    @FXML private DatePicker datePickerEmprestimo;
    @FXML private DatePicker datePickerPrevista;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final LivroDAO livroDAO = new LivroDAO();
    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

    private ObservableList<Livro> observableListLivros;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO.setConnection(connection);
        livroDAO.setConnection(connection);
        emprestimoDAO.setConnection(connection);

        configurarColunasTabela();
        carregarDados();
    }

    private void configurarColunasTabela() {
        tableColumnLivroNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tableColumnLivroAutor.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAutor().getNome())
        );
    }

    public void carregarDados() {
        List<Usuario> listUsuarios = usuarioDAO.listar();
        comboBoxUsuario.setItems(FXCollections.observableArrayList(listUsuarios));

        List<Livro> listLivros = livroDAO.listarDisponiveis();
        observableListLivros = FXCollections.observableArrayList(listLivros);
        tableViewLivros.setItems(observableListLivros);
    }

    @FXML
    public void handleButtonConfirmar() {
        try {
            if (datePickerEmprestimo == null || datePickerPrevista == null) {
                System.err.println("[ERRO CRÍTICO]: Os DatePickers não foram injetados! Verifique os fx:id no arquivo FXML.");
                exibirAlerta("Erro de Inicialização", "Os componentes de calendário não foram vinculados corretamente no FXML.");
                return;
            }

            Usuario usuario = comboBoxUsuario.getSelectionModel().getSelectedItem();
            Livro livro = tableViewLivros.getSelectionModel().getSelectedItem();
            
            LocalDate dataEmprestimo = datePickerEmprestimo.getValue();
            LocalDate dataPrevista = datePickerPrevista.getValue();

            if (usuario == null || livro == null || dataEmprestimo == null || dataPrevista == null) {
                exibirAlerta("Aviso", "Por favor, selecione o usuário, o livro na tabela e preencha ambas as datas!");
                return;
            }

            if (dataEmprestimo.isAfter(dataPrevista)) {
                exibirAlerta("Erro nas Datas", "A data do empréstimo não pode ser posterior à data prevista de devolução.");
                return;
            }

            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setUsuario(usuario);
            emprestimo.setLivro(livro);
            emprestimo.setDescricao("Empréstimo via seleção em tabela");
            emprestimo.setDataEmprestimo(dataEmprestimo);
            emprestimo.setDataPrevistaDevolucao(dataPrevista);

            if (emprestimoDAO.realizarEmprestimo(emprestimo)) {
                exibirAlerta("Sucesso", "Empréstimo realizado com sucesso!");
                limparCampos();
                carregarDados(); 
            } else {
                exibirAlerta("Erro", "Falha ao salvar no banco. Verifique se o usuário possui multas pendentes no console.");
            }
            
        } catch (Exception ex) {
            System.err.println("\n>>> O ERRO REAL DENTRO DO CONVERTER FOI:");
            ex.printStackTrace();
            System.err.println("<<<\n");
            exibirAlerta("Erro Interno", "Ocorreu uma falha inesperada: " + ex.getMessage());
        }
    }
    
    private void limparCampos() {
        comboBoxUsuario.getSelectionModel().clearSelection();
        tableViewLivros.getSelectionModel().clearSelection();
        datePickerEmprestimo.setValue(null);
        datePickerPrevista.setValue(null);
    }
    
    @FXML
    private void handleButtonVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/view/PaginaInicial.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Sistema de Biblioteca");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            Stage stageAtual = (Stage) buttonVoltar.getScene().getWindow();
            stageAtual.close();

        } catch (IOException e) {
            System.err.println("Erro ao voltar para a página inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exibirAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
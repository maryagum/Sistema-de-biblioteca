package biblioteca.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sockets.thread.AvisoGrupo;
import sockets.thread.ContadorGrupo;
import sockets.thread.RespostaMural;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class MuralController {

    @FXML private TextField txtIdGrupo;
    @FXML private Label lblNomeGrupo;
    @FXML private Label lblTemaGrupo;
    @FXML private Label lblStatus;
    @FXML private Label lblAvisoTitulo;
    @FXML private Label lblAvisoDestaque;
    @FXML private Label lblAvisoTimestamp;
    @FXML private Label lblContadorAvisos;
    
    @FXML private TableView<ContadorGrupo> tableRanking;
    @FXML private TableColumn<ContadorGrupo, String> colGrupo;
    @FXML private TableColumn<ContadorGrupo, Integer> colAcessos;
    @FXML private ListView<String> listViewMensagens;

    private List<AvisoGrupo> avisosAtuais;
    private Thread threadLoop;

    @FXML
    public void initialize() {
        // Vincula as propriedades dos objetos ContadorGrupo às colunas da tabela
        colGrupo.setCellValueFactory(new PropertyValueFactory<>("nomeGrupo"));
        colAcessos.setCellValueFactory(new PropertyValueFactory<>("quantidadeUtilizacoes"));
    }

    @FXML
    private void conectarServidor() {
        String textoId = txtIdGrupo.getText().trim();
        
        if (textoId.isEmpty()) {
            lblStatus.setText("Informe o número do grupo!");
            return;
        }

        try {
            int id = Integer.parseInt(textoId);
            if (id < 1 || id > 10) {
                lblStatus.setText("Insira um grupo válido (1 a 10).");
                return;
            }

            lblStatus.setText("Conectando ao servidor...");

            // Abre uma Thread de background para a comunicação Socket não travar a tela
            new Thread(() -> {
                try (Socket socket = new Socket("127.0.0.1", 12345);
                     ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

                    // Envia o ID inteiro requisitado
                    saida.writeObject(id);
                    saida.flush();

                    // Captura o objeto unificado do mural vindo do servidor
                    RespostaMural resposta = (RespostaMural) entrada.readObject();

                    // Repassa as atualizações visuais de volta para a Thread do JavaFX
                    Platform.runLater(() -> atualizarInterface(resposta));

                } catch (Exception e) {
                    Platform.runLater(() -> lblStatus.setText("Erro de conexão: " + e.getMessage()));
                }
            }).start();

        } catch (NumberFormatException e) {
            lblStatus.setText("O ID precisa ser um número inteiro.");
        }
    }

    private void atualizarInterface(RespostaMural resposta) {
        // Atualiza cabeçalhos
        lblNomeGrupo.setText("Grupo atual: " + resposta.getIdGrupo() + " - " + resposta.getNomeGrupo());
        lblTemaGrupo.setText("Tema carregado: " + resposta.getNomeGrupo());
        lblStatus.setText("Dados recebidos em: " + resposta.getUltimaAtualizacao());
        
        // Atualiza a tabela do Ranking
        tableRanking.getItems().setAll(resposta.getRanking());
        
        // Guarda e atualiza a lista de mensagens recebidas
        avisosAtuais = resposta.getAvisosGrupo();
        lblContadorAvisos.setText(avisosAtuais.size() + " avisos recebidos");
        
        listViewMensagens.getItems().clear();
        for (AvisoGrupo aviso : avisosAtuais) {
            String itemLista = aviso.getTimestamp() + " - " + aviso.getTitulo() + ": " + aviso.getMensagem();
            listViewMensagens.getItems().add(itemLista);
        }

        // Inicializa o loop concorrente de alternância de avisos em destaque
        iniciarLoopMensagens();
    }

    private void iniciarLoopMensagens() {
        // Interrompe o loop anterior caso mude de grupo
        if (threadLoop != null && threadLoop.isAlive()) {
            threadLoop.interrupt();
        }

        if (avisosAtuais == null || avisosAtuais.isEmpty()) return;

        threadLoop = new Thread(() -> {
            int index = 0;
            while (!Thread.currentThread().isInterrupted()) {
                final int i = index % avisosAtuais.size();
                
                // Joga a alteração textual de volta de forma segura para o JavaFX
                Platform.runLater(() -> {
                    AvisoGrupo aviso = avisosAtuais.get(i);
                    lblAvisoTitulo.setText(aviso.getTitulo());
                    lblAvisoDestaque.setText(aviso.getMensagem());
                    lblAvisoTimestamp.setText("Recebido em: " + aviso.getTimestamp());
                });

                index++;
                try {
                    Thread.sleep(4000); // Aguarda exatamente 4 segundos antes de rodar o loop de novo
                } catch (InterruptedException e) {
                    break; // Sai da thread graciosamente caso sofra interrupção externa
                }
            }
        });
        
        threadLoop.setDaemon(true); // Faz a thread morrer se a aplicação fechar
        threadLoop.start();
    }
}
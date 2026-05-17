package biblioteca.controller;

import biblioteca.model.database.Database;
import biblioteca.model.database.DatabaseFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class RelatorioMultaController implements Initializable {

    @FXML private Button btnImprimir;
    @FXML private Button btnVoltar;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void handleImprimir() {
        try {
            URL arquivo = getClass().getResource("/biblioteca/relatorios/RelatorioMultas.jasper");
            
            if (arquivo == null) {
                System.out.println("Arquivo RelatorioMultas.jasper não encontrado!");
                return;
            }
            
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(arquivo);
            Map<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Relatório de Multas Pendentes");
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
    }
}
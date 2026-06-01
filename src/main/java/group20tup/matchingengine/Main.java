package group20tup.matchingengine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada de la aplicacion JavaFX Matching Engine.
 * <p>
 *     Carga la interfaz de usuario desde el archivo FXML dashboard.fxml
 *     y lanza la ventana principal del simulador de flota de vehiculos.
 * </p>
 * @author Ivan
 * @version 1.0
 */
public class Main extends Application {

    /**
     * Inicializa y muestra la ventana principal de la aplicacion.
     * @param stage Escenario principal proporcionado por JavaFX
     * @throws IOException si no se puede cargar el archivo FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        /**>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
         * Ingresa Cambio, pantalla de inicio sobrepuesta a dashboard
         * 
         * --- Cargar la pantalla de carga unos segundos ---*/
        FXMLLoader cargaLoader = new FXMLLoader(Main.class.getResource("/group20tup/matchingengine/fxml/00-PantallaCarga.fxml"));
        Scene cargaScene = new Scene(cargaLoader.load(), 1200, 800);
        Stage cargaStage = new Stage();
        cargaStage.setScene(cargaScene);
        cargaStage.initOwner(stage);          // vinculado a la ventana principal
        cargaStage.initModality(Modality.APPLICATION_MODAL); // bloquea la principal
        cargaStage.setResizable(false);
        cargaStage.show();
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

    /**
     * Metodo principal que inicia la aplicacion JavaFX.
     * @param args Argumentos de linea de comandos
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}

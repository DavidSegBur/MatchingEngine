package group20tup.matchingengine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/group20tup/matchingengine/fxml/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Matching Engine - Gestión de transporte!");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/group20tup/matchingengine/data/mapa.png")));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metodo principal que inicia la aplicacion JavaFX.
     * @param args Argumentos de linea de comandos
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}

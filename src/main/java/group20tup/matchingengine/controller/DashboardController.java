package group20tup.matchingengine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controlador de la interfaz de usuario del Dashboard principal.
 * <p>
 *     Gestiona los eventos e interacciones de la ventana principal
 *     de la aplicacion Matching Engine. Actualmente es un controlador
 *     esqueletico que sera expandido con la logica de simulacion.
 * </p>
 * @author arc
 * @version 1.0
 */
public class DashboardController {
    @FXML
    private Label welcomeText;

    /**
     * Maneja el evento de clic del boton de bienvenida.
     * Actualiza el texto de la etiqueta con un mensaje de prueba.
     */
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}

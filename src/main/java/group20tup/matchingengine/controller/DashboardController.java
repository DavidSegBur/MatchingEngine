package group20tup.matchingengine.controller;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.view.MapCanvas;
import group20tup.matchingengine.view.ProyeccionMapa;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

/**
 * Controlador de la interfaz principal del simulador de flota de vehiculos.
 * <p>
 *     Inicializa el grafo vial de la ciudad, la proyeccion geografica y el
 *     renderizador del mapa al cargar la vista. Gestiona los eventos de
 *     redimension del canvas y la actualizacion de la escena cuando la
 *     ventana se hace visible por primera vez.
 * </p>
 * @author arc
 * @version 1.0
 */
public class DashboardController {
    @FXML
    private Canvas mapaCanvas;
    @FXML
    private StackPane mapContainer;

    private GrafoMapa grafoMapa;
    private ProyeccionMapa proyeccion;
    private MapCanvas renderizadorMapa;

    /**
     * Inicializa el controlador despues de que el FXML ha sido cargado.
     * <p>
     *     Carga el grafo vial desde los archivos CSV, construye la proyeccion
     *     geografica y el renderizador del mapa, luego vincula el tamano del
     *     canvas al contenedor y programa el primer renderizado cuando la
     *     escena este disponible.
     * </p>
     */
    @FXML
    public void initialize() {
        grafoMapa = new GrafoMapa();
        grafoMapa.cargarGrafo();

        proyeccion = new ProyeccionMapa(grafoMapa.getListaEsquinas());
        renderizadorMapa = new MapCanvas(mapaCanvas, grafoMapa, proyeccion);
        renderizadorMapa.inicializar();

        mapaCanvas.widthProperty().bind(mapContainer.widthProperty());
        mapaCanvas.heightProperty().bind(mapContainer.heightProperty());
        mapaCanvas.widthProperty().addListener((obs, old, n) -> renderizadorMapa.redibujar());
        mapaCanvas.heightProperty().addListener((obs, old, n) -> renderizadorMapa.redibujar());

        mapaCanvas.sceneProperty().addListener((obs, old, scene) -> {
            if (scene != null) {
                Platform.runLater(() -> renderizadorMapa.redibujar());
            }
        });
    }
}

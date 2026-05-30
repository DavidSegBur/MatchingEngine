package group20tup.matchingengine.controller;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.MetadataNodo;
import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import group20tup.matchingengine.model.utilidades.CalculadorRutas;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.FloydWarshallRutas;
import group20tup.matchingengine.model.utilidades.sistema.GestorSimulacion;
import group20tup.matchingengine.model.utilidades.sistema.SistemaViajes;
import group20tup.matchingengine.view.MapCanvas;
import group20tup.matchingengine.view.ProyeccionMapa;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.Random;

/**
 * Controlador de la interfaz principal del simulador de flota de vehiculos.
 * <p>
 *     Inicializa el grafo vial de la ciudad, la proyeccion geografica y el
 *     renderizador del mapa al cargar la vista. Crea el sistema de viajes
 *     con el algoritmo Dijkstra y el gestor de simulacion que orquesta el
 *     movimiento autonomo de vehiculos, la densidad de entidades y el ciclo
 *     de vida de los viajes. Gestiona los eventos de redimension del canvas
 *     y la actualizacion de la escena cuando la ventana se hace visible.
 * </p>
 * @author Ivan
 * @version 2.0
 */
public class DashboardController {
    @FXML
    private Canvas mapaCanvas;
    @FXML
    private StackPane mapContainer;
    @FXML
    private VBox sidePanel;
    @FXML
    private ChoiceBox<String> algoritmoSelector;
    @FXML
    private ProgressIndicator floydProgress;
    @FXML
    private Label lblFloydStatus;

    private GrafoMapa grafoMapa;
    private ProyeccionMapa proyeccion;
    private MapCanvas renderizadorMapa;
    private GestorSimulacion gestor;
    private SistemaViajes sistema;
    private CalculadorRutas dijkstraRuteador;
    private volatile CalculadorRutas floydRuteador;
    private volatile boolean floydListo = false;
    private Label lblInfo;
    private Label lblBusyQueue;
    private double mouseX;
    private double mouseY;
    private boolean dragging;
    private long ultimoRenderDrag;

    private void onMousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        dragging = false;
        ultimoRenderDrag = 0;
    }

    private void onMouseDragged(MouseEvent e) {
        double dx = e.getX() - mouseX;
        double dy = e.getY() - mouseY;
        if (Math.abs(dx) > 2 || Math.abs(dy) > 2) {
            dragging = true;
        }
        proyeccion.pan(dx, dy);
        mouseX = e.getX();
        mouseY = e.getY();
        long now = System.nanoTime();
        if (gestor != null && now - ultimoRenderDrag > 30_000_000) {
            gestor.renderizarFrame();
            ultimoRenderDrag = now;
        }
    }

    private void onCanvasClick(MouseEvent e) {
        if (dragging) return;

        double x = e.getX(), y = e.getY();

        Usuario usuario = renderizadorMapa.hitTestUsuario(x, y, sistema.getListaUsuarios());
        if (usuario != null) {
            solicitarViajeUI(usuario);
            return;
        }

        Vehiculo vehiculo = renderizadorMapa.hitTestVehiculo(x, y, sistema.getListaVehiculos());
        if (vehiculo != null) {
            mostrarInfoVehiculo(vehiculo);
        }
    }

    private void onScroll(ScrollEvent e) {
        double dy = e.getDeltaY();
        if (dy == 0) return;
        double factor = dy > 0 ? 1.1 : 1.0 / 1.1;
        proyeccion.zoom(factor, e.getX(), e.getY());
        if (gestor != null) gestor.renderizarFrame();
        e.consume();
    }

    private void solicitarViajeUI(Usuario usuario) {
        boolean algunDisponible = false;
        boolean algunAlcanzable = false;
        for (int i = 0; i < sistema.totalVehiculos(); i++) {
            Vehiculo v = sistema.getVehiculo(i);
            if (v.isDisponible()) {
                algunDisponible = true;
                double eta = sistema.calcularETA(v.getNodoActual(), usuario.getNodoOrigen());
                if (Double.isFinite(eta)) {
                    algunAlcanzable = true;
                    break;
                }
            }
        }

        if (algunDisponible && !algunAlcanzable) {
            sistema.removerUsuario(usuario);
            lblInfo.setText("El usuario " + usuario.getId() + " es inaccesible.\nFue eliminado del mapa.");
            return;
        }

        Vehiculo aceptado = sistema.solicitarViaje(usuario, new Random());
        if (aceptado == null) {
            lblInfo.setText("No hay vehiculos disponibles\npara el usuario " + usuario.getId() + ".");
            return;
        }

        double eta = sistema.calcularETA(aceptado.getNodoActual(), usuario.getNodoOrigen());
        double distanciaKm = eta * (25.0 / 3.6) / 1000.0;
        double tarifa = sistema.calcularTarifa(eta);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Viaje asignado");
        alert.setHeaderText("El vehiculo se dirige hacia el usuario");
        alert.setContentText(String.format(
                "Vehiculo: %s\nETA: %.0f segundos\nDistancia: %.2f km\nTarifa: $%.2f",
                aceptado.getPatente(), eta, distanciaKm, tarifa));
        alert.showAndWait();

        lblInfo.setText(String.format(
                "Viaje asignado\nVehiculo: %s\nETA: %.0f s\nDist: %.2f km\nTarifa: $%.2f",
                aceptado.getPatente(), eta, distanciaKm, tarifa));
    }

    private void mostrarInfoVehiculo(Vehiculo v) {
        MetadataNodo nodo = (MetadataNodo) grafoMapa.getListaEsquinas().devolver(v.getNodoActual());

        if (v.getEstado() == EstadoVehiculo.DISPONIBLE) {
            lblInfo.setText(String.format(
                    "Vehiculo: %s\nEstado: DISPONIBLE\nPosicion: nodo %d\nUbicacion: %s",
                    v.getPatente(), v.getNodoActual(), nodo.getNombreEsquina()));
            lblBusyQueue.setText("");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("--- Cola Ocupados ---\n");

            int n = sistema.totalVehiculos();
            int[] busyIdx = new int[n];
            double[] busyETA = new double[n];
            int busyCount = 0;

            for (int i = 0; i < n; i++) {
                Vehiculo ocupado = sistema.getVehiculo(i);
                if (ocupado.getEstado() == EstadoVehiculo.EN_VIAJE) {
                    double eta = 0;
                    int[] ruta = ocupado.getRutaActiva();
                    for (int j = ocupado.getIndiceRuta(); j < ruta.length - 1; j++) {
                        eta += grafoMapa.getMatrizCosto().devolver(ruta[j], ruta[j + 1]);
                    }
                    int j = busyCount - 1;
                    while (j >= 0 && busyETA[j] > eta) {
                        busyIdx[j + 1] = busyIdx[j];
                        busyETA[j + 1] = busyETA[j];
                        j--;
                    }
                    busyIdx[j + 1] = i;
                    busyETA[j + 1] = eta;
                    busyCount++;
                }
            }

            for (int i = 0; i < busyCount; i++) {
                Vehiculo ocupado = sistema.getVehiculo(busyIdx[i]);
                double distKm = busyETA[i] * (25.0 / 3.6) / 1000.0;
                sb.append(ocupado.getPatente())
                        .append("  ~").append(String.format("%.0f", busyETA[i])).append("s  ")
                        .append(String.format("%.1f", distKm)).append("km\n");
            }

            lblInfo.setText(String.format(
                    "Vehiculo: %s\nEstado: %s\nPosicion: nodo %d\nUbicacion: %s",
                    v.getPatente(), v.getEstado(), v.getNodoActual(), nodo.getNombreEsquina()));
            lblBusyQueue.setText(sb.toString());
        }
    }

    private void precomputarFloydEnBackground() {
        floydProgress.setVisible(true);
        lblFloydStatus.setText("Precomputando Floyd-Warshall...");
        algoritmoSelector.setDisable(true);

        Task<Void> floydTask = new Task<>() {
            @Override
            protected Void call() {
                floydRuteador = new FloydWarshallRutas(grafoMapa);
                return null;
            }
        };

        floydTask.setOnSucceeded(e -> {
            floydListo = true;
            floydProgress.setVisible(false);
            lblFloydStatus.setText("Floyd-Warshall listo");
            algoritmoSelector.setDisable(false);
        });

        floydTask.setOnFailed(e -> {
            floydProgress.setVisible(false);
            lblFloydStatus.setText("Error precomputando Floyd-Warshall");
            algoritmoSelector.setDisable(false);
            System.err.println("Error en Floyd-Warshall: " + floydTask.getException().getMessage());
        });

        new Thread(floydTask).start();
    }

    private void onAlgoritmoCambiado(String algoritmo) {
        if ("Floyd-Warshall".equals(algoritmo)) {
            if (floydListo) {
                sistema.setRuteador(floydRuteador);
                gestor.setRuteador(floydRuteador);
                lblInfo.setText("Algoritmo cambiado a Floyd-Warshall");
            } else {
                algoritmoSelector.setValue("Dijkstra");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Precomputando");
                alert.setHeaderText("Floyd-Warshall se esta precomputando");
                alert.setContentText("Espere a que termine el calculo inicial...");
                alert.showAndWait();
            }
        } else {
            sistema.setRuteador(dijkstraRuteador);
            gestor.setRuteador(dijkstraRuteador);
            lblInfo.setText("Algoritmo cambiado a Dijkstra");
        }
    }

    /**
     * Inicializa el controlador despues de que el FXML ha sido cargado.
     * <p>
     *     Carga el grafo vial desde los archivos CSV, construye la proyeccion
     *     geografica y el renderizador del mapa. Luego crea el sistema de
     *     viajes con Dijkstra como algoritmo de ruteo, instancia el gestor
     *     de simulacion y lo inicia. Vincula el tamano del canvas al
     *     contenedor y programa el primer renderizado cuando la escena
     *     este disponible.
     * </p>
     */
    @FXML
    public void initialize() {
        ProgressIndicator loader = new ProgressIndicator();
        loader.setMaxSize(50, 50);
        mapContainer.getChildren().add(loader);
        StackPane.setAlignment(loader, Pos.CENTER);

        Task<GrafoMapa> loadTask = new Task<>() {
            @Override
            protected GrafoMapa call() {
                GrafoMapa g = new GrafoMapa();
                g.cargarGrafo();
                return g;
            }
        };

        loadTask.setOnSucceeded(e -> {
            mapContainer.getChildren().remove(loader);
            grafoMapa = loadTask.getValue();

            proyeccion = new ProyeccionMapa(grafoMapa.getListaEsquinas());
            renderizadorMapa = new MapCanvas(mapaCanvas, grafoMapa, proyeccion);
            renderizadorMapa.inicializar();

            dijkstraRuteador = new DijkstraRutas(grafoMapa);
            sistema = new SistemaViajes(grafoMapa, dijkstraRuteador);
            gestor = new GestorSimulacion(sistema, renderizadorMapa, grafoMapa, dijkstraRuteador);

            algoritmoSelector.getItems().addAll("Dijkstra", "Floyd-Warshall");
            algoritmoSelector.setValue("Dijkstra");
            algoritmoSelector.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
                if (val != null) onAlgoritmoCambiado(val);
            });

            precomputarFloydEnBackground();

            mapaCanvas.widthProperty().bind(mapContainer.widthProperty());
            mapaCanvas.heightProperty().bind(mapContainer.heightProperty());
            mapaCanvas.widthProperty().addListener((obs, old, n) -> {
                if (gestor != null) gestor.renderizarFrame();
            });
            mapaCanvas.heightProperty().addListener((obs, old, n) -> {
                if (gestor != null) gestor.renderizarFrame();
            });

            mapaCanvas.setOnMousePressed(DashboardController.this::onMousePressed);
            mapaCanvas.setOnMouseDragged(DashboardController.this::onMouseDragged);
            mapaCanvas.setOnMouseClicked(DashboardController.this::onCanvasClick);
            mapaCanvas.setOnScroll(DashboardController.this::onScroll);

            lblInfo = new Label("Haga clic en un usuario\npara solicitar un viaje,\no en un vehiculo para\nver su informacion.");
            lblInfo.setWrapText(true);
            lblInfo.setStyle("-fx-font-size: 12;");

            lblBusyQueue = new Label("");
            lblBusyQueue.setWrapText(true);
            lblBusyQueue.setStyle("-fx-font-size: 11; -fx-font-family: monospace;");

            ScrollPane busyScroll = new ScrollPane(lblBusyQueue);
            busyScroll.setFitToWidth(true);
            busyScroll.setPrefHeight(200);
            busyScroll.setMaxHeight(250);
            busyScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            busyScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sidePanel.getChildren().addAll(lblInfo, busyScroll);

            if (mapaCanvas.getScene() != null) {
                renderizadorMapa.redibujar();
                gestor.iniciar();
            } else {
                mapaCanvas.sceneProperty().addListener((obs, old, scene) -> {
                    if (scene != null) {
                        Platform.runLater(() -> {
                            renderizadorMapa.redibujar();
                            gestor.iniciar();
                        });
                    }
                });
            }

            if (sidePanel.getScene() != null) {
                sidePanel.getScene().widthProperty().addListener((w, o, n) -> {
                    sidePanel.setPrefWidth(Math.max(180, Math.min(350, n.doubleValue() * 0.2)));
                });
            } else {
                sidePanel.sceneProperty().addListener((obs, old, scene) -> {
                    if (scene != null) {
                        scene.widthProperty().addListener((w, o, n) -> {
                            sidePanel.setPrefWidth(Math.max(180, Math.min(350, n.doubleValue() * 0.2)));
                        });
                    }
                });
            }
        });

        loadTask.setOnFailed(e -> {
            mapContainer.getChildren().remove(loader);
            System.err.println("Error al cargar el grafo: " + loadTask.getException().getMessage());
        });

        new Thread(loadTask).start();
    }
}

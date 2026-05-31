package group20tup.matchingengine.controller;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.MetadataNodo;
import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import group20tup.matchingengine.model.utilidades.CalculadorRutas;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.FloydWarshallRutas;
import group20tup.matchingengine.model.utilidades.sistema.EstadisticasSimulacion;
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
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
    @FXML
    private Button btnResetView;

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
    private Label lblColaDespacho;
    private Label lblStats;
    private SimulacionFXAdapter adaptadorSimulacion;
    private PauseTransition pausaDespacho;
    private Usuario usuarioDespachando;
    private double mouseX;
    private double mouseY;
    private boolean dragging;
    private long ultimoRenderDrag;

    /**
     * Captura la posicion inicial del mouse al presionar el boton.
     * Se usa para detectar arrastres y diferenciar clics de drags.
     * @param e Evento de presion del mouse
     */
    private void onMousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        dragging = false;
        ultimoRenderDrag = 0;
    }

    /**
     * Desplaza la proyeccion del mapa arrastrando el canvas con el mouse.
     * El renderizado se actualiza con un throttle de 30ms para evitar
     * re-renderizados excesivos durante el arrastre.
     * @param e Evento de arrastre del mouse
     */
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

    /**
     * Maneja los clics sobre el canvas del mapa.
     * Detecta si el clic fue sobre un usuario (inicia solicitud de viaje)
     * o sobre un vehiculo (muestra informacion del estado). Ignora clics
     * que ocurren inmediatamente despues de un arrastre.
     * @param e Evento de clic del mouse
     */
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

    /**
     * Aplica zoom a la proyeccion del mapa usando la rueda del mouse.
     * El factor de zoom es 1.1x por cada paso hacia arriba y 0.91x (1/1.1)
     * por cada paso hacia abajo. El zoom se centra en la posicion del cursor.
     * @param e Evento de scroll del mouse
     */
    private void onScroll(ScrollEvent e) {
        double dy = e.getDeltaY();
        if (dy == 0) return;
        double factor = dy > 0 ? 1.1 : 1.0 / 1.1;
        proyeccion.zoom(factor, e.getX(), e.getY());
        if (gestor != null) gestor.renderizarFrame();
        e.consume();
    }

    /**
     * Procesa la solicitud de viaje de un usuario desde la interfaz grafica.
     * Verifica disponibilidad y alcanzabilidad de vehiculos, y si es posible
     * inicia un proceso de despacho asincronico con demora simulada entre
     * candidatos. Si el usuario es inalcanzable lo elimina del mapa.
     * @param usuario Usuario que solicita el viaje
     */
    private void solicitarViajeUI(Usuario usuario) {
        if (pausaDespacho != null) {
            pausaDespacho.stop();
        }
        sistema.cancelarDespacho();

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

        String colaTexto = sistema.obtenerTextoColaDespacho(usuario);
        lblColaDespacho.setText(colaTexto);

        sistema.iniciarDespacho(usuario, new Random());
        if (sistema.getTotalCandidatosDespacho() == 0) {
            lblInfo.setText("No hay vehiculos disponibles\npara el usuario " + usuario.getId() + ".");
            return;
        }

        this.usuarioDespachando = usuario;
        lblInfo.setText("Buscando conductor...\n(0/" + sistema.getTotalCandidatosDespacho() + ")");
        pausaDespacho.play();
    }

    /**
     * Procesa el siguiente candidato en el despacho asincronico.
     * Se ejecuta tras cada pausa de 1500ms. Si el vehiculo acepta muestra
     * el dialogo de confirmacion; si rechaza y hay mas candidatos reinicia
     * la pausa; si se agotaron los candidatos informa que no hay disponibles.
     */
    private void procesarSiguienteDespacho() {
        int proc = sistema.getCandidatosProcesadosDespacho();
        int total = sistema.getTotalCandidatosDespacho();

        Vehiculo aceptado = sistema.procesarSiguienteDespacho();

        if (aceptado != null) {
            lblColaDespacho.setText("");
            double eta = sistema.calcularETA(aceptado.getNodoActual(), usuarioDespachando.getNodoOrigen());
            double distanciaKm = eta * GrafoMapa.VELOCIDAD_PROMEDIO_M_S / 1000.0;
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
        } else if (sistema.hayDespachoActivo()) {
            lblInfo.setText("Buscando conductor...\n(%d/%d)".formatted(proc, total));
            actualizarTextoColaDespacho(proc);
            pausaDespacho.playFromStart();
        } else {
            lblColaDespacho.setText("");
            lblInfo.setText("No hay vehiculos disponibles\npara el usuario " + usuarioDespachando.getId() + ".");
        }
    }

    private void actualizarTextoColaDespacho(int procesados) {
        String actual = lblColaDespacho.getText();
        if (actual == null || actual.isEmpty()) return;
        String[] lineas = actual.split("\n");
        StringBuilder sb = new StringBuilder(lineas[0]).append("\n");
        for (int i = 1; i < lineas.length; i++) {
            String linea = lineas[i];
            int numOrden = i;
            if (numOrden <= procesados) {
                sb.append(linea).append("  ✗\n");
            } else {
                if (numOrden == procesados + 1) {
                    sb.append("→ ").append(linea).append("  ← evaluando\n");
                } else {
                    sb.append("  ").append(linea).append("\n");
                }
            }
        }
        lblColaDespacho.setText(sb.toString());
    }

    /**
     * Muestra en el panel lateral la informacion de un vehiculo seleccionado.
     * Si el vehiculo esta DISPONIBLE muestra telemetria basica (patente, estado,
     * posicion, ubicacion). Si esta ocupado muestra la cola de vehiculos
     * ocupados ordenada por tiempo restante ascendente.
     * @param v Vehiculo a inspeccionar
     */
    private void mostrarInfoVehiculo(Vehiculo v) {
        MetadataNodo nodo = (MetadataNodo) grafoMapa.getListaEsquinas().devolver(v.getNodoActual());

        if (v.getEstado() == EstadoVehiculo.DISPONIBLE) {
            lblInfo.setText(String.format(
                    "Vehiculo: %s\nEstado: DISPONIBLE\nPosicion: nodo %d\nUbicacion: %s",
                    v.getPatente(), v.getNodoActual(), nodo.getNombreEsquina()));
            lblBusyQueue.setText("");
        } else {
            String infoVehiculo = String.format(
                    "Vehiculo: %s\nEstado: %s\nPosicion: nodo %d\nUbicacion: %s",
                    v.getPatente(), v.getEstado(), v.getNodoActual(), nodo.getNombreEsquina());
            lblInfo.setText(infoVehiculo);
            lblBusyQueue.setText(sistema.obtenerTextoColaOcupados());
        }
    }

    /**
     * Lanza un hilo en segundo plano para precomputar todas las rutas
     * con Floyd-Warshall. Mientras se ejecuta, el selector de algoritmo
     * se deshabilita y se muestra un indicador de progreso. Al finalizar,
     * se habilita el selector y se marca Floyd como listo.
     */
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

    /**
     * Cambia el algoritmo de ruteo activo en el sistema y el gestor.
     * Si se selecciona Floyd-Warshall y aun no esta precomputado,
     * muestra un alerta y mantiene Dijkstra como activo.
     * @param algoritmo Nombre del algoritmo seleccionado ("Dijkstra" o "Floyd-Warshall")
     */
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

    @FXML
    public void initialize() {
        ProgressIndicator loader = mostrarLoader();

        Task<GrafoMapa> loadTask = crearTareaCargaGrafo();
        loadTask.setOnSucceeded(e -> {
            ocultarLoader(loader);
            onGrafoCargado(loadTask.getValue());
        });
        loadTask.setOnFailed(e -> {
            ocultarLoader(loader);
            System.err.println("Error al cargar el grafo: " + loadTask.getException().getMessage());
        });
        new Thread(loadTask).start();
    }

    private ProgressIndicator mostrarLoader() {
        ProgressIndicator loader = new ProgressIndicator();
        loader.setMaxSize(50, 50);
        mapContainer.getChildren().add(loader);
        StackPane.setAlignment(loader, Pos.CENTER);
        return loader;
    }

    private void ocultarLoader(ProgressIndicator loader) {
        mapContainer.getChildren().remove(loader);
    }

    private Task<GrafoMapa> crearTareaCargaGrafo() {
        return new Task<>() {
            @Override
            protected GrafoMapa call() {
                GrafoMapa g = new GrafoMapa();
                g.cargarGrafo();
                return g;
            }
        };
    }

    private void onGrafoCargado(GrafoMapa mapa) {
        grafoMapa = mapa;
        inicializarSistema(mapa);
        configurarSelectorAlgoritmo();
        precomputarFloydEnBackground();
        configurarCanvas();
        construirSidePanel();
        iniciarTimelineEstadisticas();
        configurarEscena();
    }

    private void inicializarSistema(GrafoMapa mapa) {
        proyeccion = new ProyeccionMapa(mapa.getListaEsquinas());
        renderizadorMapa = new MapCanvas(mapaCanvas, mapa, proyeccion);
        renderizadorMapa.inicializar();

        btnResetView.setOnAction(evt -> {
            proyeccion.resetView();
            renderizadorMapa.redibujar();
        });

        dijkstraRuteador = new DijkstraRutas(mapa);
        sistema = new SistemaViajes(mapa, dijkstraRuteador);
        gestor = new GestorSimulacion(sistema, renderizadorMapa, mapa, dijkstraRuteador);

        pausaDespacho = new PauseTransition(Duration.millis(1500));
        pausaDespacho.setOnFinished(evt -> procesarSiguienteDespacho());
    }

    private void configurarSelectorAlgoritmo() {
        algoritmoSelector.getItems().addAll("Dijkstra", "Floyd-Warshall");
        algoritmoSelector.setValue("Dijkstra");
        algoritmoSelector.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null) onAlgoritmoCambiado(val);
        });
    }

    private void configurarCanvas() {
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
    }

    private void construirSidePanel() {
        lblInfo = new Label("Haga clic en un usuario\npara solicitar un viaje,\no en un vehiculo para\nver su informacion.");
        lblInfo.setWrapText(true);
        lblInfo.getStyleClass().add("info-label");

        lblColaDespacho = new Label("");
        lblColaDespacho.setWrapText(true);
        lblColaDespacho.getStyleClass().add("mono-label");

        lblBusyQueue = new Label("");
        lblBusyQueue.setWrapText(true);
        lblBusyQueue.getStyleClass().add("mono-label");

        ScrollPane infoScroll = new ScrollPane(lblInfo);
        infoScroll.setFitToWidth(true);
        infoScroll.setPrefHeight(100);
        infoScroll.setMaxHeight(120);
        infoScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        infoScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        ScrollPane colaScroll = new ScrollPane(lblColaDespacho);
        colaScroll.setFitToWidth(true);
        colaScroll.setPrefHeight(120);
        colaScroll.setMaxHeight(200);
        colaScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        colaScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        ScrollPane busyScroll = new ScrollPane(lblBusyQueue);
        busyScroll.setFitToWidth(true);
        busyScroll.setPrefHeight(200);
        busyScroll.setMaxHeight(250);
        busyScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        busyScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        sidePanel.getChildren().addAll(infoScroll, colaScroll, busyScroll);

        Label sep = new Label("─────────────────");
        sep.getStyleClass().add("separator-label");

        Label lblStatsHeader = new Label("Estadisticas");
        lblStatsHeader.getStyleClass().add("stats-header");

        lblStats = new Label("(aun sin datos)");
        lblStats.setWrapText(true);
        lblStats.getStyleClass().add("stats-content");

        sidePanel.getChildren().addAll(sep, lblStatsHeader, lblStats);
    }

    private void iniciarTimelineEstadisticas() {
        Timeline statsTimer = new Timeline(
            new KeyFrame(Duration.millis(500), evt -> actualizarEstadisticas())
        );
        statsTimer.setCycleCount(Timeline.INDEFINITE);
        statsTimer.play();
    }

    private void configurarEscena() {
        configurarSimulacionEnEscena();
        configurarAnchoSidePanel();
    }

    private void configurarSimulacionEnEscena() {
        if (mapaCanvas.getScene() != null) {
            iniciarSimulacion();
        } else {
            mapaCanvas.sceneProperty().addListener((obs, old, scene) -> {
                if (scene != null) {
                    Platform.runLater(this::iniciarSimulacion);
                }
            });
        }
    }

    private void configurarAnchoSidePanel() {
        javafx.beans.value.ChangeListener<Number> widthListener = crearWidthListener();
        if (sidePanel.getScene() != null) {
            sidePanel.getScene().widthProperty().addListener(widthListener);
        } else {
            sidePanel.sceneProperty().addListener((obs, old, scene) -> {
                if (scene != null) {
                    scene.widthProperty().addListener(widthListener);
                }
            });
        }
    }

    private void iniciarSimulacion() {
        renderizadorMapa.redibujar();
        gestor.inicializarEntidades();
        adaptadorSimulacion = new SimulacionFXAdapter(gestor);
        adaptadorSimulacion.iniciar();
    }

    private javafx.beans.value.ChangeListener<Number> crearWidthListener() {
        return (w, o, n) -> {
            double wVal = n.doubleValue();
            sidePanel.setPrefWidth(Math.max(180, Math.min(350, wVal * 0.2)));
            if (wVal < 1000 && !sidePanel.getStyleClass().contains("narrow")) {
                sidePanel.getStyleClass().add("narrow");
            } else if (wVal >= 1000) {
                sidePanel.getStyleClass().remove("narrow");
            }
        };
    }

    private void agregarWidthListener(javafx.scene.Scene scene) {
        scene.widthProperty().addListener(crearWidthListener());
    }

    /**
     * Actualiza los labels de estadisticas en el panel lateral.
     * Se ejecuta cada 500ms via Timeline. Lee los contadores del
     * sistema de viajes y calcula la tasa de uso actual de la flota.
     */
    private void actualizarEstadisticas() {
        if (sistema == null) return;
        EstadisticasSimulacion e = sistema.getEstadisticas();

        int ocupados = 0;
        int total = sistema.totalVehiculos();
        for (int i = 0; i < total; i++) {
            if (sistema.getVehiculo(i).getEstado() != EstadoVehiculo.DISPONIBLE) {
                ocupados++;
            }
        }

        String texto = String.format(
            "Solicitados: %d\n" +
            "Completados: %d\n" +
            "Rechazados: %d\n" +
            "ETA prom: %.0fs\n" +
            "Dist total: %.1f km\n" +
            "Tarifa prom: $%.2f\n" +
            "Uso: %d/%d (%.0f%%)\n" +
            "Viajes/h: %.1f",
            e.getViajesSolicitados(),
            e.getViajesCompletados(),
            e.getViajesRechazados(),
            e.getETAPromedio(),
            e.getSumaDistanciasKm(),
            e.getTarifaPromedio(),
            ocupados, total,
            total > 0 ? ocupados * 100.0 / total : 0,
            e.getViajesPorHora()
        );
        lblStats.setText(texto);
    }
}

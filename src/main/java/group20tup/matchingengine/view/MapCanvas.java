package group20tup.matchingengine.view;

import group20tup.matchingengine.model.estructuras.lineales.listas.ListaDoubleLinkedL;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.MetadataNodo;
import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renderizador del mapa vial sobre un Canvas de JavaFX.
 * <p>
 *     Gestiona el dibujo de la red de calles (aristas), las intersecciones
 *     (nodos), los vehiculos y los usuarios sobre el lienzo de la aplicacion.
 *     Precalcula las aristas existentes a partir de la matriz de costos del
 *     grafo para optimizar el renderizado en tiempo real.
 * </p>
 * @author arc
 * @version 1.0
 */
public class MapCanvas {
    private final Canvas canvas;
    private final GrafoMapa grafo;
    private final ProyeccionMapa proyeccion;
    private int[][] aristas;

    private static final double NODO_RADIO = 2.5;
    private static final double VEHICULO_RADIO = 6.0;
    private static final double USUARIO_RADIO = 5.0;
    private static final double ARISTA_ANCHO = 0.5;
    private static final double PADDING = 30.0;

    /**
     * Construye el renderizador asociado al canvas, grafo y proyeccion dados.
     * @param canvas Lienzo de JavaFX donde se dibuja el mapa
     * @param grafo Grafo vial con los datos de la ciudad
     * @param proyeccion Utilidad de proyeccion geografica a coordenadas de pantalla
     */
    public MapCanvas(Canvas canvas, GrafoMapa grafo, ProyeccionMapa proyeccion) {
        this.canvas = canvas;
        this.grafo = grafo;
        this.proyeccion = proyeccion;
    }

    /**
     * Precalcula el listado de aristas (calles) existentes en el grafo.
     * <p>
     *     Recorre la matriz de costos del grafo y almacena los pares de nodos
     *     que poseen una conexion valida (costo finito), evitando recalcular
     *     esta informacion en cada fotograma de renderizado.
     * </p>
     */
    public void inicializar() {
        int orden = grafo.getOrden();
        int contador = 0;
        for (int i = 0; i < orden; i++) {
            for (int j = 0; j < orden; j++) {
                if (i != j && grafo.getMatrizCosto().devolver(i, j) < Double.POSITIVE_INFINITY) {
                    contador++;
                }
            }
        }
        aristas = new int[contador][2];
        int idx = 0;
        for (int i = 0; i < orden; i++) {
            for (int j = 0; j < orden; j++) {
                if (i != j && grafo.getMatrizCosto().devolver(i, j) < Double.POSITIVE_INFINITY) {
                    aristas[idx][0] = i;
                    aristas[idx][1] = j;
                    idx++;
                }
            }
        }
    }

    /**
     * Calcula el rectangulo destino donde se dibujara el mapa dentro del canvas.
     * <p>
     *     Aplica un margen de separacion (PADDING) alrededor del area de dibujo
     *     para evitar que los elementos del mapa queden pegados a los bordes
     *     del lienzo.
     * </p>
     * @param canvasW Ancho actual del canvas en pixeles
     * @param canvasH Alto actual del canvas en pixeles
     * @return Arreglo de cuatro doubles {x, y, ancho, alto} con el rectangulo destino
     */
    private double[] calcularRectDestino(double canvasW, double canvasH) {
        return new double[]{PADDING, PADDING, canvasW - 2 * PADDING, canvasH - 2 * PADDING};
    }

    /**
     * Redibuja completamente el mapa en el canvas.
     * <p>
     *     Limpia el lienzo y dibuja todas las aristas (calles) como lineas
     *     semitransparentes y todos los nodos (intersecciones) como circulos
     *     azules. Las coordenadas se proyectan utilizando la proyeccion
     *     del grafo al rectangulo destino del canvas.
     * </p>
     */
    public void redibujar() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        double[] rect = calcularRectDestino(w, h);
        double tx = rect[0], ty = rect[1], tw = rect[2], th = rect[3];

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        gc.setLineWidth(ARISTA_ANCHO);
        gc.setStroke(Color.rgb(180, 180, 180, 0.35));

        for (int[] arista : aristas) {
            MetadataNodo a = (MetadataNodo) grafo.getListaEsquinas().devolver(arista[0]);
            MetadataNodo b = (MetadataNodo) grafo.getListaEsquinas().devolver(arista[1]);
            double[] p1 = proyeccion.proyectar(a.getLatitud(), a.getLongitud(), tx, ty, tw, th);
            double[] p2 = proyeccion.proyectar(b.getLatitud(), b.getLongitud(), tx, ty, tw, th);
            gc.strokeLine(p1[0], p1[1], p2[0], p2[1]);
        }

        gc.setFill(Color.rgb(74, 144, 217, 0.7));
        for (int i = 0; i < grafo.getOrden(); i++) {
            MetadataNodo nodo = (MetadataNodo) grafo.getListaEsquinas().devolver(i);
            double[] p = proyeccion.proyectar(nodo.getLatitud(), nodo.getLongitud(), tx, ty, tw, th);
            gc.fillOval(p[0] - NODO_RADIO, p[1] - NODO_RADIO, NODO_RADIO * 2, NODO_RADIO * 2);
        }
    }

    /**
     * Dibuja los vehiculos registrados sobre el mapa.
     * <p>
     *     Cada vehiculo se representa como un circulo coloreado segun su estado:
     *     verde para DISPONIBLE, naranja para APROXIMANDO y rojo para EN_VIAJE.
     *     Los circulos poseen un borde oscuro para mejorar la visibilidad.
     * </p>
     * @param vehiculos Lista enlazada con los objetos Vehiculo a renderizar
     */
    public void renderVehiculos(ListaDoubleLinkedL vehiculos) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        double[] rect = calcularRectDestino(w, h);
        double tx = rect[0], ty = rect[1], tw = rect[2], th = rect[3];

        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < vehiculos.tamanio(); i++) {
            Vehiculo v = (Vehiculo) vehiculos.devolver(i);
            MetadataNodo nodo = (MetadataNodo) grafo.getListaEsquinas().devolver(v.getNodoActual());
            double[] p = proyeccion.proyectar(nodo.getLatitud(), nodo.getLongitud(), tx, ty, tw, th);

            Color color;
            if (v.getEstado() == EstadoVehiculo.DISPONIBLE) {
                color = Color.LIMEGREEN;
            } else if (v.getEstado() == EstadoVehiculo.APROXIMANDO) {
                color = Color.ORANGE;
            } else {
                color = Color.RED;
            }

            gc.setFill(color);
            gc.fillOval(p[0] - VEHICULO_RADIO, p[1] - VEHICULO_RADIO,
                    VEHICULO_RADIO * 2, VEHICULO_RADIO * 2);
            gc.setStroke(Color.color(0.15, 0.15, 0.15));
            gc.setLineWidth(1.5);
            gc.strokeOval(p[0] - VEHICULO_RADIO, p[1] - VEHICULO_RADIO,
                    VEHICULO_RADIO * 2, VEHICULO_RADIO * 2);
        }
    }

    /**
     * Dibuja los usuarios registrados sobre el mapa.
     * <p>
     *     Cada usuario se representa como un circulo color violeta medio con
     *     borde oscuro, ubicado en el nodo de origen del usuario.
     * </p>
     * @param usuarios Lista enlazada con los objetos Usuario a renderizar
     */
    public void renderUsuarios(ListaDoubleLinkedL usuarios) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        double[] rect = calcularRectDestino(w, h);
        double tx = rect[0], ty = rect[1], tw = rect[2], th = rect[3];

        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < usuarios.tamanio(); i++) {
            Usuario u = (Usuario) usuarios.devolver(i);
            MetadataNodo nodo = (MetadataNodo) grafo.getListaEsquinas().devolver(u.getNodoOrigen());
            double[] p = proyeccion.proyectar(nodo.getLatitud(), nodo.getLongitud(), tx, ty, tw, th);

            gc.setFill(Color.MEDIUMVIOLETRED);
            gc.fillOval(p[0] - USUARIO_RADIO, p[1] - USUARIO_RADIO,
                    USUARIO_RADIO * 2, USUARIO_RADIO * 2);
            gc.setStroke(Color.color(0.15, 0.15, 0.15));
            gc.setLineWidth(1.5);
            gc.strokeOval(p[0] - USUARIO_RADIO, p[1] - USUARIO_RADIO,
                    USUARIO_RADIO * 2, USUARIO_RADIO * 2);
        }
    }
}

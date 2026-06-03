package group20tup.matchingengine.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Renderiza una capa de fondo geográfico (calles OSM y parques) sobre un Canvas,
 * leyendo geometrías desde archivos GeoJSON empaquetados en el JAR.
 * No requiere conexión a internet en tiempo de ejecución.
 *
 * @author Generado para MatchingEngine
 * @version 1.0
 */
public class CapaFondoOSM {

    private final Canvas canvas;
    private final ProyeccionMapa proyeccion;

    // Calles: cada segmento es un array de puntos {lat, lon}
    private final List<double[][]> lineasCalles = new ArrayList<>();

    // ── Colores del tema oscuro ──────────────────────────────────────────────
    private static final Color COLOR_FONDO         = Color.rgb(20, 27, 38);
    private static final Color COLOR_CALLE_CONTORNO= Color.rgb(40, 48, 65, 0.8);
    private static final Color COLOR_CALLE_RELLENO = Color.rgb(80, 95, 120, 0.65);

    private static final double ANCHO_CALLE_CONTORNO = 3.5;
    private static final double ANCHO_CALLE_RELLENO  = 1.8;
    private static final double PADDING = 30.0;

    /**
     * Construye la capa y carga los GeoJSON desde recursos.
     * Los archivos deben estar en:
     *   src/main/resources/group20tup/matchingengine/geo/CallesSalta.geojson
     *   src/main/resources/group20tup/matchingengine/geo/salta_parks.geojson
     */
    public CapaFondoOSM(Canvas canvas, ProyeccionMapa proyeccion) {
        this.canvas = canvas;
        this.proyeccion = proyeccion;
        cargarCalles("/group20tup/matchingengine/geo/CallesSalta.geojson");
    }

    // ── Carga de GeoJSON ─────────────────────────────────────────────────────

    private void cargarCalles(String recurso) {
        JSONArray features = leerFeatures(recurso);
        if (features == null) return;

        for (int i = 0; i < features.length(); i++) {
            JSONObject geom = features.getJSONObject(i).optJSONObject("geometry");
            if (geom == null) continue;
            String tipo = geom.getString("type");

            if ("LineString".equals(tipo)) {
                lineasCalles.add(extraerLineString(geom.getJSONArray("coordinates")));

            } else if ("MultiLineString".equals(tipo)) {
                JSONArray lineas = geom.getJSONArray("coordinates");
                for (int l = 0; l < lineas.length(); l++) {
                    lineasCalles.add(extraerLineString(lineas.getJSONArray(l)));
                }
            } else if ("Polygon".equals(tipo)) {
                // Zonas peatonales con area=yes vienen como Polygon; las tratamos como línea
                lineasCalles.add(extraerLineString(geom.getJSONArray("coordinates").getJSONArray(0)));
            }
        }
        System.out.println("CapaFondoOSM: calles cargadas = " + lineasCalles.size());
    }

    private JSONArray leerFeatures(String recurso) {
        try (InputStream is = getClass().getResourceAsStream(recurso)) {
            if (is == null) {
                System.err.println("CapaFondoOSM: no se encontró el recurso " + recurso);
                return null;
            }
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONObject(json).getJSONArray("features");
        } catch (Exception e) {
            System.err.println("CapaFondoOSM: error leyendo " + recurso + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Extrae un LineString: array de coordenadas [lon, lat] → double[][]{lat, lon}
     */
    private double[][] extraerLineString(JSONArray coords) {
        double[][] puntos = new double[coords.length()][2];
        for (int k = 0; k < coords.length(); k++) {
            JSONArray c = coords.getJSONArray(k);
            puntos[k][0] = c.getDouble(1); // lat
            puntos[k][1] = c.getDouble(0); // lon
        }
        return puntos;
    }

    // ── Renderizado ──────────────────────────────────────────────────────────

    /**
     * Dibuja el fondo completo: color base → parques → calles OSM.
     * Debe llamarse ANTES de que MapCanvas dibuje el grafo y las entidades.
     */
    public void dibujar() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Fondo base
        gc.setFill(COLOR_FONDO);
        gc.fillRect(0, 0, w, h);

        double tx = PADDING, ty = PADDING;
        double tw = w - 2 * PADDING;
        double th = h - 2 * PADDING;

        dibujarCalles(gc, tx, ty, tw, th);
    }

    private void dibujarCalles(GraphicsContext gc, double tx, double ty, double tw, double th) {
        // Pasada 1: contorno grueso
        gc.setLineWidth(ANCHO_CALLE_CONTORNO);
        gc.setStroke(COLOR_CALLE_CONTORNO);
        for (double[][] linea : lineasCalles) {
            trazarLinea(gc, linea, tx, ty, tw, th);
        }
        // Pasada 2: relleno fino
        gc.setLineWidth(ANCHO_CALLE_RELLENO);
        gc.setStroke(COLOR_CALLE_RELLENO);
        for (double[][] linea : lineasCalles) {
            trazarLinea(gc, linea, tx, ty, tw, th);
        }
    }

    private void trazarLinea(GraphicsContext gc, double[][] puntos,
                              double tx, double ty, double tw, double th) {
        if (puntos.length < 2) return;
        double[] inicio = proyeccion.proyectar(puntos[0][0], puntos[0][1], tx, ty, tw, th);
        gc.beginPath();
        gc.moveTo(inicio[0], inicio[1]);
        for (int k = 1; k < puntos.length; k++) {
            double[] p = proyeccion.proyectar(puntos[k][0], puntos[k][1], tx, ty, tw, th);
            gc.lineTo(p[0], p[1]);
        }
        gc.stroke();
    }
}

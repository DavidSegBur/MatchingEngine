package group20tup.matchingengine.view;

import group20tup.matchingengine.model.estructuras.lineales.listas.ListaDoubleLinkedL;
import group20tup.matchingengine.model.recursos.MetadataNodo;

/**
 * Utilidad de proyeccion geografica que convierte coordenadas (latitud, longitud)
 * a coordenadas de pantalla (x, y) dentro de un rectangulo destino.
 * <p>
 *     Calcula los limites geograficos del conjunto de nodos del grafo vial y
 *     realiza una interpolacion lineal para mapear cada punto geografico a su
 *     posicion correspondiente en el area de dibujo del canvas. Esta proyeccion
 *     es de tipo equirrectangular simple, adecuada para areas geograficas
 *     reducidas como la ciudad de Salta.
 * </p>
 * @author arc
 * @version 1.0
 */
public class ProyeccionMapa {
    private double minLat;
    private double maxLat;
    private double minLon;
    private double maxLon;

    /**
     * Construye la proyeccion calculando los limites geograficos a partir
     * de la lista de esquinas cargadas desde el grafo.
     * <p>
     *     Recorre todas las esquinas para determinar las coordenadas extremas
     *     (minLat, maxLat, minLon, maxLon) que definen el area geografica
     *     cubierta por la red vial.
     * </p>
     * @param esquinas Lista enlazada con objetos MetadataNodo de todas las
     *                 intersecciones del grafo vial
     */
    public ProyeccionMapa(ListaDoubleLinkedL esquinas) {
        double minLatTmp = Double.MAX_VALUE;
        double maxLatTmp = -Double.MAX_VALUE;
        double minLonTmp = Double.MAX_VALUE;
        double maxLonTmp = -Double.MAX_VALUE;

        for (int i = 0; i < esquinas.tamanio(); i++) {
            MetadataNodo nodo = (MetadataNodo) esquinas.devolver(i);
            double lat = nodo.getLatitud();
            double lon = nodo.getLongitud();
            if (lat < minLatTmp) minLatTmp = lat;
            if (lat > maxLatTmp) maxLatTmp = lat;
            if (lon < minLonTmp) minLonTmp = lon;
            if (lon > maxLonTmp) maxLonTmp = lon;
        }
        this.minLat = minLatTmp;
        this.maxLat = maxLatTmp;
        this.minLon = minLonTmp;
        this.maxLon = maxLonTmp;
    }

    /**
     * Proyecta un punto geografico (latitud, longitud) a coordenadas de pantalla
     * dentro del rectangulo destino especificado.
     * <p>
     *     Aplica interpolacion lineal para convertir las coordenadas geograficas
     *     en pixeles, mapeando el rango de latitudes al eje Y y el rango de
     *     longitudes al eje X del rectangulo destino.
     * </p>
     * @param lat Latitud del punto a proyectar en grados decimales
     * @param lon Longitud del punto a proyectar en grados decimales
     * @param targetX Coordenada X del borde izquierdo del rectangulo destino
     * @param targetY Coordenada Y del borde superior del rectangulo destino
     * @param targetW Ancho del rectangulo destino en pixeles
     * @param targetH Alto del rectangulo destino en pixeles
     * @return Arreglo de dos doubles {x, y} con las coordenadas de pantalla
     */
    public double[] proyectar(double lat, double lon, double targetX, double targetY, double targetW, double targetH) {
        double rangoLat = maxLat - minLat;
        double rangoLon = maxLon - minLon;
        if (rangoLat == 0) rangoLat = 1;
        if (rangoLon == 0) rangoLon = 1;

        double x = (lon - minLon) / rangoLon * targetW + targetX;
        double y = (maxLat - lat) / rangoLat * targetH + targetY;
        return new double[]{x, y};
    }
}

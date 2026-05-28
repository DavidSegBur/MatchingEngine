package group20tup.matchingengine;

import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.MetadataNodo;

/**
 * Prueba de carga de datos y ejecucion del algoritmo Dijkstra sobre el grafo vial de Salta.
 * <p>
 *     Verifica que el motor lea los archivos CSV de metadatos y matriz de adyacencia,
 *     construya el grafo dirigido con pesos ETA, y calcule una ruta valida entre dos
 *     nodos utilizando el algoritmo de Dijkstra implementado con un monticulo binario.
 * </p>
 * @author Ivan
 * @version 2.0
 */
public class MatchingEngineTest {

    /**
     * Punto de entrada de la prueba.
     * <p>
     *     Carga los metadatos de 1665 esquinas, transforma la matriz de adyacencia
     *     en una matriz de costos ETA, y ejecuta Dijkstra desde el nodo 0 al nodo 47.
     *     Imprime la ruta encontrada con los nombres de las esquinas.
     * </p>
     * @param args Argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println("====== INICIANDO TEST LOCAL DE CARGA (FUERA DE GIT) ======");

        try {
            GrafoMapa mapaSalta = new GrafoMapa();

            System.out.println("[Test] Cargando catalogo de metadatos y matriz vial...");
            mapaSalta.cargarGrafo();

            // 3. Verificacion de Integridad de Datos (Muestra de control)
            System.out.println("\n====== CONTROL DE CALIDAD DE DATOS ======");

            // Control 1: Verificar que la lista enlazada contenga los 2000 nodos
            int totalEsquinas = mapaSalta.getListaEsquinas().tamanio();
            System.out.println("-> Esquinas en Lista Enlazada: " + totalEsquinas + " / 1665");

            // Control 2: Recuperar un nodo especifico usando el indice secuencial interno (Ej: Nodo 0)
            MetadataNodo primerNodo = (MetadataNodo) mapaSalta.getListaEsquinas().devolver(0);
            if (primerNodo != null) {
                System.out.println("-> Primer nodo indexado con exito:");
                System.out.println("   ID OSM: " + primerNodo.getIdOSM());
                System.out.println("   Esquina: " + primerNodo.getNombreEsquina());
                System.out.println("   Coordenadas: (" + primerNodo.getLatitud() + ", " + primerNodo.getLongitud() + ")");
                System.out.println("   Indice Interno: " + primerNodo.getIndiceInterno());
            }

            // Control 3: Verificar que el traductor directo de ID funcione
            long[] mapeo = mapaSalta.getMapeoIndicesAIdOSM();
            System.out.println("-> Comprobacion de mapeo inverso en array: Indice [0] apunta a ID OSM " + mapeo[0]);

            System.out.println("\n[EXITO] El motor lee de forma nativa sin dependencias externas.");

            System.out.println("\n====== EJECUTANDO PRUEBA DE DIJKSTRA ======");
            DijkstraRutas buscador = new DijkstraRutas(mapaSalta);

            int indiceOrigen = 0;
            int indiceDestino = 47;

            long tiempoInicio = System.nanoTime();
            int[] rutaCalculada = buscador.calcularRuta(indiceOrigen, indiceDestino);
            long tiempoFin = System.nanoTime();

            if (rutaCalculada.length > 0) {
                System.out.println("[Exito] Ruta encontrada en " + ((tiempoFin - tiempoInicio) / 1_000_000.0) + " ms.");
                System.out.println("-> Trayectoria de esquinas a recorrer:");

                for (int i = 0; i < rutaCalculada.length; i++) {
                    int idx = rutaCalculada[i];
                    MetadataNodo esquina = (MetadataNodo) mapaSalta.getListaEsquinas().devolver(idx);

                    if (i == 0) {
                        System.out.printf("   [INICIO] %s\n", esquina.getNombreEsquina());
                    } else if (i == rutaCalculada.length - 1) {
                        System.out.printf("   [DESTINO] %s\n", esquina.getNombreEsquina());
                    } else {
                        System.out.printf("   [PASO %02d] %s\n", i, esquina.getNombreEsquina());
                    }
                }
            } else {
                System.out.println("[FALLO] No se encontro un camino valido entre los nodos seleccionados.");
            }
            System.out.println("===========================================");

        } catch (Exception e) {
            System.err.println("\n[FALLO] Error detectado durante la simulacion de carga:");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("==========================================================");
    }
}

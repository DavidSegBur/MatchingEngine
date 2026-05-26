package group20.matchingengine;

import group20tup.matchingengine.model.utilidades.BuscadorCaminos;
import group20tup.matchingengine.model.utilidades.CargadorDatos;
import group20tup.matchingengine.model.estructuras.nolineales.GrafoDirigido;
import group20tup.matchingengine.model.recursos.MetadataNodo;

public class MatchingEngineTest {

    public static void main(String[] args) {
        System.out.println("====== INICIANDO TEST LOCAL DE CARGA (FUERA DE GIT) ======");

        try {
            // 1. Instanciar el grafo con el tamaño del dataset de Salta (2000 esquinas)
            GrafoDirigido mapaSalta = new GrafoDirigido(2000);
            CargadorDatos cargador = new CargadorDatos();

            // 2. Definir las rutas relativas hacia la carpeta de recursos del proyecto real
            // Subimos dos niveles desde /tests para entrar a /src/main/resources
            String rutaMetadatos = "/group20tup/matchingengine/data/meta_datos_nodos_2k.csv";
            String rutaMatriz = "/group20tup/matchingengine/data/matriz_nodos_2k.csv";

            System.out.println("[Test] Cargando catálogo de metadatos...");
            cargador.cargarMetadatos(rutaMetadatos);

            System.out.println("[Test] Procesando matriz vial y transformando a pesos ETA...");
            cargador.cargarMatrizVial(rutaMatriz, mapaSalta);

            // 3. Verificación de Integridad de Datos (Muestra de control)
            System.out.println("\n====== CONTROL DE CALIDAD DE DATOS ======");

            // Control 1: Verificar que la lista enlazada contenga los 2000 nodos
            int totalEsquinas = cargador.getListaEsquinas().tamanio(); // O el metodo equivalente de tu lista
            System.out.println("-> Esquinas en Lista Enlazada: " + totalEsquinas + " / 2000");

            // Control 2: Recuperar un nodo específico usando el índice secuencial interno (Ej: Nodo 0)
            MetadataNodo primerNodo = (MetadataNodo) cargador.getListaEsquinas().devolver(0);
            if (primerNodo != null) {
                System.out.println("-> Primer nodo indexado con éxito:");
                System.out.println("   ID OSM: " + primerNodo.getIdOSM());
                System.out.println("   Esquina: " + primerNodo.getNombreEsquina());
                System.out.println("   Coordenadas: (" + primerNodo.getLatitud() + ", " + primerNodo.getLongitud() + ")");
                System.out.println("   Indice Interno: " + primerNodo.getIndiceInterno());
            }

            // Control 3: Verificar que el traductor directo de ID funcione
            long[] mapeo = cargador.getMapeoIndicesAIdOSM();
            System.out.println("-> Comprobación de mapeo inverso en array: Índice [0] apunta a ID OSM " + mapeo[0]);

            System.out.println("\n[ÉXITO] El motor lee de forma nativa sin dependencias externas.");

            System.out.println("\n====== EJECUTANDO PRUEBA DE DIJKSTRA ======");
            BuscadorCaminos buscador = new BuscadorCaminos();

            // Vamos a simular un viaje desde el índice 0 hacia el índice 15 (o los que gustes probar)
            int indiceOrigen = 0;
            int indiceDestino = 6;

            long tiempoInicio = System.nanoTime();
            int[] rutaCalculada = buscador.calcularDijkstra(mapaSalta, indiceOrigen, indiceDestino);
            long tiempoFin = System.nanoTime();

            if (rutaCalculada.length > 0) {
                System.out.println("[Éxito] Ruta encontrada en " + ((tiempoFin - tiempoInicio) / 1_000_000.0) + " ms.");
                System.out.println("-> Trayectoria de esquinas a recorrer:");

                for (int i = 0; i < rutaCalculada.length; i++) {
                    int idx = rutaCalculada[i];
                    // Recuperamos los metadatos desde tu ListaEnlazada usando el índice
                    MetadataNodo esquina = (MetadataNodo) cargador.getListaEsquinas().devolver(idx);

                    if (i == 0) {
                        System.out.printf("   [INICIO] %s\n", esquina.getNombreEsquina());
                    } else if (i == rutaCalculada.length - 1) {
                        System.out.printf("   [DESTINO] %s\n", esquina.getNombreEsquina());
                    } else {
                        System.out.printf("   [PASO %02d] %s\n", i, esquina.getNombreEsquina());
                    }
                }
            } else {
                System.out.println("[FALLO] No se encontró un camino válido entre los nodos seleccionados.");
            }
            System.out.println("===========================================");

        } catch (Exception e) {
            System.err.println("\n[FALLO] Error detectado durante la simulación de carga:");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("==========================================================");


    }
}
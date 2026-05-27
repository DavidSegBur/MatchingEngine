package group20tup.matchingengine.model.utilidades;

import group20tup.matchingengine.model.estructuras.lineales.MonticuloBinario;
import group20tup.matchingengine.model.estructuras.nolineales.GrafoDirigido;

/**
 * Dijkstra's algorithm implementation for single-source shortest paths.
 * Uses a binary min-heap for efficient priority queue operations.
 * Calculates the shortest path from a given origin to destination node.
 */
public class DijkstraRutas implements CalculadorRutas {
    private static final double INFINITO = Double.POSITIVE_INFINITY;
    private final GrafoDirigido grafo;

    /**
     * Constructs a Dijkstra path finder for the given graph.
     * 
     * @param grafo The directed graph to process
     */
    public DijkstraRutas(GrafoDirigido grafo) {
        this.grafo = grafo;
    }

    /**
     * Calculates the shortest path from origin to destination using Dijkstra's algorithm.
     * 
     * @param origen  Origin node index
     * @param destino Destination node index
     * @return Array of node indices representing the path, or empty array if no path exists
     */
    @Override
    public int[] calcularRuta(int origen, int destino) {
        int n = grafo.getOrden();

        double[] distancias = new double[n];
        int[] padres = new int[n];
        boolean[] visitados = new boolean[n];

        for (int i = 0; i < n; i++) {
            distancias[i] = INFINITO;
            padres[i] = -1;
            visitados[i] = false;
        }

        distancias[origen] = 0.0;

        MonticuloBinario colaPrioridad = new MonticuloBinario(n);
        colaPrioridad.insertar(origen, 0.0);

        while (!colaPrioridad.estaVacia()) {
            int u = colaPrioridad.extraerMin();

            if (u == destino) {
                break;
            }

            if (visitados[u]) {
                continue;
            }
            visitados[u] = true;

            // Explore neighbors
            for (int v = 0; v < n; v++) {
                if (!visitados[v]) {
                    double pesoArista = grafo.getMatrizCosto().devolver(u, v);
                    if (pesoArista > 0.0 && pesoArista < INFINITO) {
                        double nuevaDistancia = distancias[u] + pesoArista;

                        if (nuevaDistancia < distancias[v]) {
                            distancias[v] = nuevaDistancia;
                            padres[v] = u;
                            colaPrioridad.insertar(v, nuevaDistancia);
                        }
                    }
                }
            }
        }

        if (distancias[destino] == INFINITO) {
            return new int[0];
        }

        return reconstruirRuta(padres, origen, destino);
    }

    /**
     * Reconstructs the path from parent pointers.
     * 
     * @param padres Array of parent indices
     * @param origen Origin node index
     * @param destino Destination node index
     * @return Path from origin to destination as array of node indices
     */
    private int[] reconstruirRuta(int[] padres, int origen, int destino) {
        // First pass: count path length
        int length = 0;
        int actual = destino;
        while (actual != -1) {
            length++;
            actual = padres[actual];
        }
        
        // Second pass: fill array in reverse
        int[] rutaFinal = new int[length];
        int posicion = length - 1;
        actual = destino;
        while (actual != -1) {
            rutaFinal[posicion--] = actual;
            actual = padres[actual];
        }
        
        return rutaFinal;
    }
}
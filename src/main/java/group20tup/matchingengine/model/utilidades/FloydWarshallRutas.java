package group20tup.matchingengine.model.utilidades;

import group20tup.matchingengine.model.estructuras.nolineales.GrafoDirigido;

/**
 * Floyd-Warshall algorithm implementation for all-pairs shortest paths.
 * Precomputes all shortest paths during construction for O(1) path queries.
 * 
 * <p>Note: Uses raw arrays for storage to comply with project constraints.
 * The path matrix uses int[][] for vertex indices (no casting issues).
 * The cost matrix uses double[] for distances.</p>
 */
public class FloydWarshallRutas implements CalculadorRutas {
    private final double[][] costos;   // Minimum distances between nodes
    private final int[][] siguiente;   // Next hop for path reconstruction
    private final int n;               // Number of nodes
    private static final double INFINITO = Double.POSITIVE_INFINITY;

    /**
     * Constructs the Floyd-Warshall calculator from a graph.
     * Precomputes all-pairs shortest paths in O(n³) time.
     * 
     * @param grafo The directed graph to process
     */
    public FloydWarshallRutas(GrafoDirigido grafo) {
        this.n = grafo.getOrden();
        this.costos = new double[n][n];
        this.siguiente = new int[n][n];
        
        // Initialize cost and next matrices
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                costos[i][j] = grafo.getMatrizCosto().devolver(i, j);
                if (i == j) {
                    siguiente[i][j] = i; // Path from node to itself
                } else if (costos[i][j] < INFINITO) {
                    siguiente[i][j] = j; // Direct edge
                } else {
                    siguiente[i][j] = -1; // No path
                }
            }
        }
        
        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (costos[i][k] + costos[k][j] < costos[i][j]) {
                        costos[i][j] = costos[i][k] + costos[k][j];
                        siguiente[i][j] = siguiente[i][k];
                    }
                }
            }
        }
    }

    /**
     * Calculates the shortest path from origin to destination.
     * Path reconstruction is O(L) where L is path length.
     * 
     * @param origen  Origin node index
     * @param destino Destination node index
     * @return Array of node indices representing the path, or empty array if no path exists
     */
    @Override
    public int[] calcularRuta(int origen, int destino) {
        if (siguiente[origen][destino] == -1) {
            return new int[0]; // No path
        }
        
        // First, compute path length
        int length = 0;
        for (int v = origen; v != destino; v = siguiente[v][destino]) {
            length++;
        }
        length++; // Include destination
        
        // Build path
        int[] path = new int[length];
        int pos = 0;
        for (int v = origen; v != destino; v = siguiente[v][destino]) {
            path[pos++] = v;
        }
        path[pos] = destino;
        
        return path;
    }
}
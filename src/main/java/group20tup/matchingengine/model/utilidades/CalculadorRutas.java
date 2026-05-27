package group20tup.matchingengine.model.utilidades;

import group20tup.matchingengine.model.estructuras.nolineales.GrafoDirigido;

/**
 * Interface for path calculation algorithms.
 * Different implementations can be swapped in/out.
 */
public interface CalculadorRutas {
    /**
     * Calculates the shortest path from origin to destination.
     * 
     * @param origen  Origin node index
     * @param destino Destination node index
     * @return Array of node indices representing the path, or empty array if no path exists
     */
    int[] calcularRuta(int origen, int destino);
}
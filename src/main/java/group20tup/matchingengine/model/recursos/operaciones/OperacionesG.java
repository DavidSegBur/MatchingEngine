package group20tup.matchingengine.model.recursos.operaciones;

/**
 * Interfaz que define las operaciones basicas de recorrido de un grafo.
 * <p>
 *     Proporciona metodos para mostrar el recorrido del grafo en
 *     amplitud (BEA) y profundidad (BPF).
 * </p>
 * @author Catedra de AyED
 * @version 1.0
 */
public interface OperacionesG {

    /**
     * Muestra el recorrido del grafo en amplitud (BFS).
     */
    void muestraBEA();

    /**
     * Muestra el recorrido del grafo en profundidad (DFS).
     */
    void muestraBPF();

}

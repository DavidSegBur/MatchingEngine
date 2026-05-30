package group20tup.matchingengine.model.estructuras.nolineales.grafos;

import group20tup.matchingengine.model.estructuras.lineales.matrices.MatrizGrafo;

/**
 * Clase abstracta que representa un grafo dirigido con matriz de adyacencia.
 * <p>
 *     Proporciona la estructura base para la representacion del grafo
 *     mediante una matriz de costos (MatrizGrafo). Las subclases
 *     concretas deben implementar el metodo {@code cargarGrafo()} segun
 *     su origen de datos especifico.
 * </p>
 * @author Catedra de AyED e Ivan
 * @version 2.0
 */
public abstract class AbsGrafo {
    protected MatrizGrafo matrizCosto;
    protected int ordenGrafo;

    /**
     * Construye un grafo vacio con el orden dado.
     * @param ordenGrafo Cantidad de nodos del grafo
     */
    public AbsGrafo(int ordenGrafo) {
        this.ordenGrafo = ordenGrafo;
        this.matrizCosto = new MatrizGrafo(getOrden());
    }

    /**
     * Establece el orden (cantidad de nodos) del grafo.
     * @param ordenGrafo Nuevo orden del grafo
     */
    public void setOrdenGrafo(int ordenGrafo) {
        this.ordenGrafo = ordenGrafo;
    }

    /**
     * Devuelve el orden (cantidad de nodos) del grafo.
     * @return Cantidad de nodos
     */
    public int getOrden() {
        return this.ordenGrafo;
    }

    /**
     * Valor infinito usado para representar ausencia de conexion.
     */
    protected static double infinito= Double.POSITIVE_INFINITY;

    /**
     * Metodo abstracto para cargar los datos del grafo.
     * Cada subclase debe implementar su propia logica de carga.
     */
    public abstract void cargarGrafo();

    /**
     * Devuelve la matriz de costos del grafo.
     * @return MatrizGrafo con los costos (ETAs) entre nodos
     */
    public MatrizGrafo getMatrizCosto() {
        return matrizCosto;
    }
}

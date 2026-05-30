package group20tup.matchingengine.model.estructuras.nolineales.grafos;

/**
 * Implementacion concreta de un grafo dirigido con matriz de adyacencia.
 * <p>
 *     Extiende la clase abstracta {@code AbsGrafo}. Para uso en produccion
 *     utilizar la subclase {@code GrafoMapa} que se auto-carga desde los
 *     archivos CSV de recursos.
 * </p>
 * @author Ivan
 * @version 1.0
 */
public class GrafoDirigido extends AbsGrafo{

    /**
     * Construye un grafo dirigido con el orden dado.
     * @param ordenGrafo Cantidad de nodos del grafo
     */
    public GrafoDirigido(int ordenGrafo) {
        super(ordenGrafo);
    }

    /**
     * {@inheritDoc}
     * @deprecated Metodo interactivo solo para propositos academicos.
     *             En produccion utilizar {@code GrafoMapa} que se auto-carga
     *             desde los archivos CSV.
     */
    @Override
    @Deprecated
    public void cargarGrafo() {
        // No operation - this implementation is deprecated.
        // Use GrafoMapa.cargarGrafo() for CSV-based loading.
    }
}

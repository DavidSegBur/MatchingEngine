package group20tup.matchingengine.model.recursos.operaciones;

/**
 * Interfaz que completa las operaciones de una lista comun
 * con insercion y reemplazo por posicion.
 * <p>
 *     Complementa a {@code OperacionesCL2} agregando las operaciones
 *     de escritura que modifican la estructura de la lista.
 * </p>
 * @author Catedra de AyED
 * @version 1.0
 */
public interface OperacionesCL3 {
	/**
	 * Inserta un elemento en la posicion indicada.
	 * @param elemento Elemento a insertar
	 * @param posicion Indice donde insertar (0 <= posicion <= tamanio())
	 * @throws IndexOutOfBoundsException si la posicion es invalida
	 */
	void insertar(Object elemento, int posicion);

	/**
	 * Reemplaza el elemento en la posicion indicada por uno nuevo.
	 * @param elemento Nuevo valor
	 * @param posicion Indice del elemento a reemplazar (de 0 a tamaño - 1)
	 * @throws IndexOutOfBoundsException si la posicion es invalida o la lista esta vacia
	 */
	void reemplazar(Object elemento, int posicion);
}

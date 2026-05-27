package group20tup.matchingengine.model.recursos;

/**
 * Interfaz que define las operaciones basicas de una lista enlazada
 * con acceso posicional.
 * <p>
 *     Las operaciones incluyen busqueda, acceso, eliminacion y consulta
 *     de estado de la lista.
 * </p>
 * @author Catedra de AyED
 * @version 1.0
 */
public interface OperacionesCL2 {

	/**
	 * Busca un elemento en la lista y devuelve su posicion.
	 * @param elemento Elemento a buscar
	 * @return Posicion del elemento (0-based), o -1 si no se encuentra
	 */
	int buscar(Object elemento);

	/**
	 * Devuelve el elemento en la posicion indicada.
	 * @param posicion Indice del elemento (0-based)
	 * @return Elemento en la posicion indicada
	 * @throws IndexOutOfBoundsException si la posicion es invalida
	 */
	Object devolver(int posicion);

	/**
	 * Elimina el elemento en la posicion indicada.
	 * @param posicion Indice del elemento a eliminar (0-based)
	 * @throws IndexOutOfBoundsException si la posicion es invalida o la lista esta vacia
	 */
	void eliminar(int posicion);

	/**
	 * Elimina todos los elementos de la lista, dejandola vacia.
	 */
	void limpiar();

	/**
	 * Verifica si la lista esta vacia.
	 * @return true si la lista no contiene elementos, false en caso contrario
	 */
	boolean estaVacia();

	/**
	 * Devuelve la cantidad de elementos en la lista.
	 * @return Tamanio actual de la lista
	 */
	int tamanio();
	
}

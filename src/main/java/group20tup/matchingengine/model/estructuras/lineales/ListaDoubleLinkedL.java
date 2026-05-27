package group20tup.matchingengine.model.estructuras.lineales;

/**
 * Estructura de datos de Lista Doblemente Enlazada
 * <p>
 *     Esta clase hereda de la clase Lista1DLinkedL e
 *     implementa el metodo abstracto iguales().
 * </p>
 * @author Catedra de AyED
 * @version 1.0
 */
public class ListaDoubleLinkedL extends Lista1DLinkedL{

	/**
	 * Compara si dos elementos son iguales
	 * @param elementoL, primer elemento a comparar
	 * @param elemento, segundo elemento a comparar
	 * @return verdadero si son iguales, sino falso
	 */
	@Override
	public boolean iguales(Object elementoL, Object elemento) {
		return (boolean)elementoL==(boolean)elemento;
	}

}
